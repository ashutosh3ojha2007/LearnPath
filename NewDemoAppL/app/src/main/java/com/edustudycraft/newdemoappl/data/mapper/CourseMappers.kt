package com.edustudycraft.newdemoappl.data.mapper

import com.edustudycraft.newdemoappl.data.local.CourseWithLessons
import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.model.Lesson

fun CourseWithLessons.toDomain(): Course {
    return Course(
        id = course.id,
        title = course.title,
        instructor = course.instructor,
        lessons = lessons
            .sortedBy { it.sortOrder }
            .map { lesson ->
                Lesson(
                    id = lesson.id,
                    title = lesson.title,
                    isCompleted = lesson.completed,
                )
            },
    )
}
