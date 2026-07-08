package com.example.medclerkmobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    @SerialName("device_name") val deviceName: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User,
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerialName("student_number") val studentNumber: String? = null,
    @SerialName("institution_id") val institutionId: Int? = null,
    @SerialName("department_id") val departmentId: Int? = null,
)
