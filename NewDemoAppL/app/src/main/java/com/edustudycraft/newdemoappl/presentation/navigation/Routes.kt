package com.edustudycraft.newdemoappl.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val COURSE_ID = "courseId"
    const val COURSE_DETAIL = "course/{$COURSE_ID}"

    fun courseDetail(courseId: Int): String = "course/$courseId"
}
