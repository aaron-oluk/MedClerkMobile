package com.example.medclerkmobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class LogbookEntry(
    val id: Int = 0,
    @SerialName("rotation_id") val rotationId: Int,
    @SerialName("clinical_sign_id") val clinicalSignId: Int? = null,
    @SerialName("skill_id") val skillId: Int? = null,
    @SerialName("encounter_date") val encounterDate: String,
    val findings: Map<String, JsonElement>? = null,
    val notes: String? = null,
    val rotation: NamedRef? = null,
    @SerialName("clinical_sign") val clinicalSign: NamedRef? = null,
    val skill: NamedRef? = null,
    val student: NamedRef? = null,
    @SerialName("consent_confirmed") val consentConfirmed: Boolean = false,
)

@Serializable
data class NewLogbookEntry(
    @SerialName("rotation_id") val rotationId: Int,
    @SerialName("clinical_sign_id") val clinicalSignId: Int? = null,
    @SerialName("skill_id") val skillId: Int? = null,
    @SerialName("encounter_date") val encounterDate: String,
    val notes: String? = null,
    @SerialName("consent_confirmed") val consentConfirmed: Boolean,
)
