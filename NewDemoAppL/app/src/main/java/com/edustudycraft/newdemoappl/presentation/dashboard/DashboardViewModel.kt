package com.edustudycraft.newdemoappl.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.repository.AuthRepository
import com.edustudycraft.newdemoappl.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Success(
        val courses: List<Course>,
        val isOffline: Boolean,
        val isRefreshing: Boolean,
        val message: String? = null,
    ) : DashboardUiState

    data object Empty : DashboardUiState

    data class Error(val message: String) : DashboardUiState
}

sealed interface DashboardEvent {
    data object LoggedOut : DashboardEvent
}

class DashboardViewModel(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository,
    isOnline: Flow<Boolean>,
) : ViewModel() {

    private val request = MutableStateFlow(RequestState())

    private val _events = MutableSharedFlow<DashboardEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DashboardEvent> = _events.asSharedFlow()

    val uiState: StateFlow<DashboardUiState> = combine(
        courseRepository.observeCourses(),
        request,
        isOnline,
    ) { courses, requestState, online ->
        toUiState(courses, requestState, online)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState.Loading,
    )

    init {
        refresh()
    }

    fun refresh() {
        if (request.value.isRefreshing) return
        request.update { it.copy(isRefreshing = true, fatalError = null) }
        viewModelScope.launch {
            courseRepository.refreshCourses()
                .onSuccess {
                    request.update {
                        it.copy(
                            isRefreshing = false,
                            hasAttemptedRefresh = true,
                            fatalError = null,
                            message = null,
                        )
                    }
                }
                .onFailure { error ->
                    val cached = courseRepository.hasCachedCourses()
                    request.update {
                        it.copy(
                            isRefreshing = false,
                            hasAttemptedRefresh = true,
                            fatalError = if (cached) null else errorMessage(error),
                            message = if (cached) CACHED_REFRESH_FAILED else null,
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _events.emit(DashboardEvent.LoggedOut)
        }
    }

    private fun errorMessage(error: Throwable): String {
        return when (error) {
            is IOException -> error.message ?: OFFLINE_ERROR
            else -> GENERIC_ERROR
        }
    }

    private data class RequestState(
        val isRefreshing: Boolean = false,
        val hasAttemptedRefresh: Boolean = false,
        val fatalError: String? = null,
        val message: String? = null,
    )

    companion object {
        const val CACHED_REFRESH_FAILED = "Couldn't refresh. Showing saved courses."
        const val OFFLINE_ERROR = "No internet connection. Connect to the internet and try again."
        const val GENERIC_ERROR = "Couldn't load courses. Please try again."

        private fun toUiState(
            courses: List<Course>,
            request: RequestState,
            online: Boolean,
        ): DashboardUiState {
            return when {
                courses.isEmpty() && request.fatalError != null -> DashboardUiState.Error(request.fatalError)
                courses.isEmpty() && !request.hasAttemptedRefresh -> DashboardUiState.Loading
                courses.isEmpty() -> DashboardUiState.Empty
                else -> DashboardUiState.Success(
                    courses = courses,
                    isOffline = !online,
                    isRefreshing = request.isRefreshing,
                    message = if (online) request.message else null,
                )
            }
        }
    }
}
