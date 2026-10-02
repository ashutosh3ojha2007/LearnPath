package com.edustudycraft.newdemoappl.fakes

import com.edustudycraft.newdemoappl.domain.repository.AuthRepository

class FakeAuthRepository(
    private val loginResult: (email: String, password: String) -> Result<Unit> = { _, _ ->
        Result.success(Unit)
    },
) : AuthRepository {
    var loginCalls: Int = 0
        private set

    override suspend fun login(email: String, password: String): Result<Unit> {
        loginCalls += 1
        return loginResult(email, password)
    }

    override fun logout() = Unit
}
