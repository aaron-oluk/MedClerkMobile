package com.example.medclerkmobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Assessment(
    val id: Int,
    val score: String,
    @SerialName("max_score") val maxScore: String,
    @SerialName("curriculum_version") val curriculumVersion: String? = null,
    @SerialName("assessed_at") val assessedAt: String,
    val skill: NamedRef? = null,
    val rotation: NamedRef? = null,
    val assessor: NamedRef? = null,
)
