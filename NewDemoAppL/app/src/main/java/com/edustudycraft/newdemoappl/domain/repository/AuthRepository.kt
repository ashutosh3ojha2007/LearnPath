package com.edustudycraft.newdemoappl.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>

    fun logout()
}
