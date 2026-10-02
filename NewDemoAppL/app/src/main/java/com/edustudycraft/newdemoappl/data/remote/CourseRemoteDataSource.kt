package com.edustudycraft.newdemoappl.data.remote

import com.edustudycraft.newdemoappl.data.remote.dto.CourseDto

interface CourseRemoteDataSource {
    suspend fun fetchCourses(): List<CourseDto>
}
