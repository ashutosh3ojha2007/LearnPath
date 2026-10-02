package com.edustudycraft.newdemoappl.data.repository

import com.edustudycraft.newdemoappl.data.remote.AuthRemoteDataSource
import com.edustudycraft.newdemoappl.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException

class AuthRepositoryImpl(
    private val remote: AuthRemoteDataSource,
) : AuthRepository {

    private var accessToken: String? = null

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val token = remote.login(email.trim(), password)
            check(token.isNotBlank()) { "Auth service returned an empty token." }
            accessToken = token
            Result.success(Unit)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            accessToken = null
            Result.failure(error)
        }
    }

    override fun logout() {
        accessToken = null
    }
}
