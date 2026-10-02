package com.edustudycraft.newdemoappl.domain.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<Lesson>,
) {
    val lessonCount: Int
        get() = lessons.size

    val completedCount: Int
        get() = lessons.count { it.isCompleted }

    val progressPercent: Int
        get() = CourseProgress.percent(completedCount, lessonCount)
}
