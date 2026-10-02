package com.edustudycraft.newdemoappl

import com.edustudycraft.newdemoappl.fakes.FakeAuthRepository
import com.edustudycraft.newdemoappl.presentation.login.LoginEvent
import com.edustudycraft.newdemoappl.presentation.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun invalidEmail_showsFieldError_andDoesNotCallApi() {
        val repository = FakeAuthRepository()
        val viewModel = LoginViewModel(repository)

        viewModel.onEmailChange("not-an-email")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        assertEquals(LoginViewModel.EMAIL_INVALID, viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.passwordError)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun shortPassword_showsFieldError_andDoesNotCallApi() {
        val repository = FakeAuthRepository()
        val viewModel = LoginViewModel(repository)

        viewModel.onEmailChange("student@learn.app")
        viewModel.onPasswordChange("123")
        viewModel.login()

        assertEquals(LoginViewModel.PASSWORD_SHORT, viewModel.uiState.value.passwordError)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun rejectedCredentials_showApiError() = runTest {
        val repository = FakeAuthRepository { _, _ ->
            Result.failure(IllegalArgumentException("Invalid email or password."))
        }
        val viewModel = LoginViewModel(repository)

        viewModel.onEmailChange("student@learn.app")
        viewModel.onPasswordChange("wrongpass")
        viewModel.login()
        advanceUntilIdle()

        assertEquals("Invalid email or password.", viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(1, repository.loginCalls)
    }

    @Test
    fun validCredentials_emitSuccess() = runTest {
        val viewModel = LoginViewModel(FakeAuthRepository())
        val events = mutableListOf<LoginEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.onEmailChange("  student@learn.app ")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        advanceUntilIdle()

        assertEquals(listOf(LoginEvent.Succeeded), events)
    }
}
