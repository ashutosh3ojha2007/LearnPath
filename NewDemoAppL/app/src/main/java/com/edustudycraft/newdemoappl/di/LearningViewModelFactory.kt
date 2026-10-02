package com.edustudycraft.newdemoappl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.edustudycraft.newdemoappl.presentation.dashboard.DashboardViewModel
import com.edustudycraft.newdemoappl.presentation.detail.CourseDetailViewModel
import com.edustudycraft.newdemoappl.presentation.login.LoginViewModel

class LearningViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val viewModel = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(container.authRepository)
            DashboardViewModel::class.java -> DashboardViewModel(
                courseRepository = container.courseRepository,
                authRepository = container.authRepository,
                isOnline = container.networkMonitor.isOnline,
            )
            CourseDetailViewModel::class.java -> CourseDetailViewModel(
                savedStateHandle = extras.createSavedStateHandle(),
                courseRepository = container.courseRepository,
                isOnline = container.networkMonitor.isOnline,
            )
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return viewModel as T
    }
}
