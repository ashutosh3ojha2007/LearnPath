package com.edustudycraft.newdemoappl.data.remote

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): String
}

class InvalidCredentialsException : Exception("Invalid email or password.")
