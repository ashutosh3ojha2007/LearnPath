package com.edustudycraft.newdemoappl.di

import android.content.Context
import com.edustudycraft.newdemoappl.data.local.LearningDatabase
import com.edustudycraft.newdemoappl.data.network.NetworkMonitor
import com.edustudycraft.newdemoappl.data.remote.MockAuthRemoteDataSource
import com.edustudycraft.newdemoappl.data.remote.MockCourseRemoteDataSource
import com.edustudycraft.newdemoappl.data.repository.AuthRepositoryImpl
import com.edustudycraft.newdemoappl.data.repository.CourseRepositoryImpl
import com.edustudycraft.newdemoappl.domain.repository.AuthRepository
import com.edustudycraft.newdemoappl.domain.repository.CourseRepository

/**
 * Manual graph for a single-module app. The same boundaries are what Hilt
 * would provide once the project is split into features.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val networkMonitor = NetworkMonitor(appContext)
    private val database = LearningDatabase.create(appContext)

    val authRepository: AuthRepository = AuthRepositoryImpl(
        remote = MockAuthRemoteDataSource(),
    )

    val courseRepository: CourseRepository = CourseRepositoryImpl(
        remote = MockCourseRemoteDataSource(appContext, networkMonitor),
        courseDao = database.courseDao(),
    )
}
