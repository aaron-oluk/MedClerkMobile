package com.example.medclerkmobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateSettingsRequest(
    @SerialName("email_notifications_enabled") val emailNotificationsEnabled: Boolean,
)
