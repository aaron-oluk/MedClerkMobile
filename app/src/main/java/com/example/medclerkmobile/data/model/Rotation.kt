package com.example.medclerkmobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rotation(
    val id: Int,
    val name: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String? = null,
    val status: String,
    @SerialName("required_encounters") val requiredEncounters: Int? = null,
    @SerialName("logbook_entries_count") val completedEncounters: Int = 0,
    val department: NamedRef? = null,
    val student: NamedRef? = null,
    val supervisor: NamedRef? = null,
)
