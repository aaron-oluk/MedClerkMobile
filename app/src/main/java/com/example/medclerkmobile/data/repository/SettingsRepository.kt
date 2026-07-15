package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.UpdateSettingsRequest
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.data.remote.ApiService

class SettingsRepository(private val api: ApiService) {
    suspend fun updateNotifications(enabled: Boolean): Result<User> = safeCall {
        api.updateSettings(UpdateSettingsRequest(emailNotificationsEnabled = enabled))
    }
}
