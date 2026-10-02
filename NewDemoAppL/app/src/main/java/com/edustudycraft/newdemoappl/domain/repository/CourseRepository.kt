package com.edustudycraft.newdemoappl.domain.repository

import com.edustudycraft.newdemoappl.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun observeCourses(): Flow<List<Course>>

    fun observeCourse(courseId: Int): Flow<Course?>

    suspend fun refreshCourses(): Result<Unit>

    suspend fun hasCachedCourses(): Boolean

    suspend fun markLessonCompleted(lessonId: Int)
}
