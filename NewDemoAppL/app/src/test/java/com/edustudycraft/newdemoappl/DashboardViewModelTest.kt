package com.edustudycraft.newdemoappl

import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.model.Lesson
import com.edustudycraft.newdemoappl.fakes.FakeAuthRepository
import com.edustudycraft.newdemoappl.fakes.FakeCourseRepository
import com.edustudycraft.newdemoappl.presentation.dashboard.DashboardUiState
import com.edustudycraft.newdemoappl.presentation.dashboard.DashboardViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshFailure_withCachedCourses_keepsTheListOffline() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(
                initial = listOf(pythonCourse()),
                refreshResult = Result.failure(IOException("No internet connection.")),
            ),
            authRepository = FakeAuthRepository(),
            isOnline = MutableStateFlow(false),
        )
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value as DashboardUiState.Success
        assertEquals("Python Programming", state.courses.single().title)
        assertEquals(65, state.courses.single().progressPercent)
        assertTrue(state.isOffline)
        assertEquals(null, state.message)
    }

    @Test
    fun refreshFailure_withEmptyCache_showsError() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(
                refreshResult = Result.failure(IOException("No internet connection.")),
            ),
            authRepository = FakeAuthRepository(),
            isOnline = MutableStateFlow(false),
        )
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value as DashboardUiState.Error
        assertEquals("No internet connection.", state.message)
    }

    @Test
    fun emptyCatalog_showsEmptyState() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(),
            authRepository = FakeAuthRepository(),
            isOnline = MutableStateFlow(true),
        )
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(DashboardUiState.Empty, viewModel.uiState.value)
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
