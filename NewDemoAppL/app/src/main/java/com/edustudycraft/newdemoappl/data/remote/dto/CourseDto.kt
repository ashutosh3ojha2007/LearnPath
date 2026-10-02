package com.edustudycraft.newdemoappl.data.remote.dto

data class CourseDto(
    val id: Int,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonCount: Int,
)

data class RemoteLesson(
    val id: Int,
    val title: String,
    val completed: Boolean,
)
