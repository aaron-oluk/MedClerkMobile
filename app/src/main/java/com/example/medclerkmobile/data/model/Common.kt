package com.example.medclerkmobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T> = emptyList(),
    @kotlinx.serialization.SerialName("current_page") val currentPage: Int = 1,
    @kotlinx.serialization.SerialName("last_page") val lastPage: Int = 1,
    val total: Int = 0,
)

@Serializable
data class NamedRef(
    val id: Int,
    val name: String,
)
