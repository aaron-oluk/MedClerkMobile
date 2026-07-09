package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.local.TokenStore
import com.example.medclerkmobile.data.model.LoginRequest
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.data.remote.ApiService

class AuthRepository(
    private val api: ApiService,
    private val tokenStore: TokenStore,
    private val onLoginStateChanged: (Boolean) -> Unit,
) {
    suspend fun login(email: String, password: String): Result<User> = safeCall {
        val response = api.login(LoginRequest(email = email, password = password, deviceName = "android"))
        tokenStore.token = response.token
        tokenStore.userId = response.user.id
        tokenStore.userName = response.user.name
        tokenStore.userRole = response.user.role
        onLoginStateChanged(true)
        response.user
    }

    suspend fun logout() {
        safeCall { api.logout() }
        tokenStore.clear()
        onLoginStateChanged(false)
    }

    suspend fun currentUser(): Result<User> = safeCall { api.currentUser() }
}
