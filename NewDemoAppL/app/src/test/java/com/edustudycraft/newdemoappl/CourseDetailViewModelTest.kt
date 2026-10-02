package com.edustudycraft.newdemoappl

import androidx.lifecycle.SavedStateHandle
import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.model.Lesson
import com.edustudycraft.newdemoappl.fakes.FakeCourseRepository
import com.edustudycraft.newdemoappl.presentation.detail.CourseDetailUiState
import com.edustudycraft.newdemoappl.presentation.detail.CourseDetailViewModel
import com.edustudycraft.newdemoappl.presentation.navigation.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CourseDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun markingPendingLessonComplete_updatesStatusAndCourseProgress() = runTest {
        val repository = FakeCourseRepository(initial = listOf(pythonCourse()))
        val viewModel = CourseDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(Routes.COURSE_ID to 1)),
            courseRepository = repository,
            isOnline = MutableStateFlow(true),
        )
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val before = viewModel.uiState.value as CourseDetailUiState.Success
        assertEquals(65, before.course.progressPercent)
        val pending = before.course.lessons.first { !it.isCompleted }

        viewModel.markLessonCompleted(pending.id)
        advanceUntilIdle()

        val after = viewModel.uiState.value as CourseDetailUiState.Success
        assertTrue(after.course.lessons.first { it.id == pending.id }.isCompleted)
        assertEquals(14, after.course.completedCount)
        assertEquals(70, after.course.progressPercent)
    }

    private fun pythonCourse(): Course {
        val lessons = List(20) { index ->
            Lesson(
                id = index + 1,
                title = "Lesson ${index + 1}",
                isCompleted = index < 13,
            )
        }
        return Course(
            id = 1,
            title = "Python Programming",
            instructor = "John Smith",
            lessons = lessons,
        )
    }
}
