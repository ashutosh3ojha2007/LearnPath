package com.edustudycraft.newdemoappl.data.repository

import com.edustudycraft.newdemoappl.data.local.CourseDao
import com.edustudycraft.newdemoappl.data.local.entity.CourseEntity
import com.edustudycraft.newdemoappl.data.local.entity.LessonEntity
import com.edustudycraft.newdemoappl.data.mapper.toDomain
import com.edustudycraft.newdemoappl.data.remote.CourseRemoteDataSource
import com.edustudycraft.newdemoappl.data.remote.LessonCatalog
import com.edustudycraft.newdemoappl.data.remote.dto.CourseDto
import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.model.LessonMerge
import com.edustudycraft.newdemoappl.domain.repository.CourseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class CourseRepositoryImpl(
    private val remote: CourseRemoteDataSource,
    private val courseDao: CourseDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CourseRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val mutex = Mutex()
    private val courses = MutableStateFlow<List<Course>?>(null)

    init {
        scope.launch {
            mutex.withLock {
                if (courses.value == null) {
                    courses.value = loadCourses()
                }
            }
        }
    }

    override fun observeCourses(): Flow<List<Course>> = courses.filterNotNull()

    override fun observeCourse(courseId: Int): Flow<Course?> {
        return observeCourses().map { list -> list.find { it.id == courseId } }
    }

    override suspend fun refreshCourses(): Result<Unit> {
        return try {
            val remoteCourses = withContext(ioDispatcher) { remote.fetchCourses() }
            mutex.withLock {
                withContext(ioDispatcher) {
                    val preserved = courseDao.completedLessonIds().toSet()
                    val (courseEntities, lessonEntities) = toEntities(remoteCourses, preserved)
                    courseDao.replaceCatalog(courseEntities, lessonEntities)
                    courses.value = loadCourses()
                }
            }
            Result.success(Unit)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun hasCachedCourses(): Boolean {
        courses.value?.let { return it.isNotEmpty() }
        return withContext(ioDispatcher) { courseDao.courseCount() > 0 }
    }

    override suspend fun markLessonCompleted(lessonId: Int) {
        mutex.withLock {
            withContext(ioDispatcher) {
                courseDao.markLessonCompleted(lessonId)
                courses.value = loadCourses()
            }
        }
    }

    private suspend fun loadCourses(): List<Course> {
        return courseDao.getCourses().map { it.toDomain() }
    }

    private fun toEntities(
        remoteCourses: List<CourseDto>,
        locallyCompletedIds: Set<Int>,
    ): Pair<List<CourseEntity>, List<LessonEntity>> {
        val courseEntities = remoteCourses.map { course ->
            CourseEntity(
                id = course.id,
                title = course.title,
                instructor = course.instructor,
            )
        }
        val lessonEntities = remoteCourses.flatMap { course ->
            LessonCatalog.lessonsFor(course).mapIndexed { index, lesson ->
                LessonEntity(
                    id = lesson.id,
                    courseId = course.id,
                    title = lesson.title,
                    sortOrder = index,
                    completed = LessonMerge.isCompleted(
                        remoteCompleted = lesson.completed,
                        lessonId = lesson.id,
                        locallyCompletedIds = locallyCompletedIds,
                    ),
                )
            }
        }
        return courseEntities to lessonEntities
    }
}
