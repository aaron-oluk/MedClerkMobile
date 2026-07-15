package com.example.medclerkmobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feedback(
    val id: Int,
    val strengths: String? = null,
    @SerialName("areas_to_improve") val areasToImprove: String? = null,
    @SerialName("follow_up_date") val followUpDate: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val assessment: AssessmentRef? = null,
    @SerialName("given_by") val givenBy: NamedRef? = null,
)

@Serializable
data class AssessmentRef(
    val id: Int,
    val skill: NamedRef? = null,
)

@Serializable
data class NewFeedback(
    @SerialName("assessment_id") val assessmentId: Int,
    val strengths: String? = null,
    @SerialName("areas_to_improve") val areasToImprove: String? = null,
    @SerialName("follow_up_date") val followUpDate: String? = null,
)
