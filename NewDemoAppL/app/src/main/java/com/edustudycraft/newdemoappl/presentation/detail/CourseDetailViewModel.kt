package com.edustudycraft.newdemoappl.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.repository.CourseRepository
import com.edustudycraft.newdemoappl.presentation.navigation.Routes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CourseDetailUiState {
    data object Loading : CourseDetailUiState

    data class Success(
        val course: Course,
        val isOffline: Boolean,
    ) : CourseDetailUiState

    data class Error(val message: String) : CourseDetailUiState
}

class CourseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository,
    isOnline: Flow<Boolean>,
) : ViewModel() {

    private val courseId: Int = checkNotNull(savedStateHandle.get<Int>(Routes.COURSE_ID)) {
        "Missing ${Routes.COURSE_ID} argument"
    }

    val uiState: StateFlow<CourseDetailUiState> = combine(
        courseRepository.observeCourse(courseId),
        isOnline,
    ) { course, online ->
        if (course == null) {
            CourseDetailUiState.Error("This course is no longer available.")
        } else {
            CourseDetailUiState.Success(course = course, isOffline = !online)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CourseDetailUiState.Loading,
    )

    fun markLessonCompleted(lessonId: Int) {
        viewModelScope.launch {
            courseRepository.markLessonCompleted(lessonId)
        }
    }
}
