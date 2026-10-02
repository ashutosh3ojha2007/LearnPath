package com.edustudycraft.newdemoappl.data.remote

import kotlinx.coroutines.delay

/**
 * Stand-in for a real auth API. Any well-formed login succeeds except the
 * password [FAILURE_PASSWORD], which returns the same error a server would.
 */
class MockAuthRemoteDataSource : AuthRemoteDataSource {
    override suspend fun login(email: String, password: String): String {
        delay(LOGIN_DELAY_MS)
        if (password == FAILURE_PASSWORD) throw InvalidCredentialsException()
        return "mock-access-token"
    }

    private companion object {
        const val LOGIN_DELAY_MS = 700L
        const val FAILURE_PASSWORD = "wrongpass"
    }
}
