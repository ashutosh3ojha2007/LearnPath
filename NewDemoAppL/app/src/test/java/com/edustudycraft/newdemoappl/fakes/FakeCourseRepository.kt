package com.edustudycraft.newdemoappl.fakes

import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCourseRepository(
    initial: List<Course> = emptyList(),
    private val refreshResult: Result<Unit> = Result.success(Unit),
) : CourseRepository {
    private val courses = MutableStateFlow(initial)

    override fun observeCourses(): Flow<List<Course>> = courses

    override fun observeCourse(courseId: Int): Flow<Course?> {
        return courses.map { list -> list.find { it.id == courseId } }
    }

    override suspend fun refreshCourses(): Result<Unit> = refreshResult

    override suspend fun hasCachedCourses(): Boolean = courses.value.isNotEmpty()

    override suspend fun markLessonCompleted(lessonId: Int) {
        courses.value = courses.value.map { course ->
            course.copy(
                lessons = course.lessons.map { lesson ->
                    if (lesson.id == lessonId) lesson.copy(isCompleted = true) else lesson
                },
            )
        }
    }
}
