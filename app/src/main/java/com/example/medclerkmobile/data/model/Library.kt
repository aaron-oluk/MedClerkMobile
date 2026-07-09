package com.example.medclerkmobile.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class ClinicalSystem(
    val id: Int,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    @SerialName("clinical_signs_count") val signCount: Int = 0,
    @SerialName("mastery_pct") val masteryPct: Float? = null,
    val tags: List<Tag> = emptyList(),
)

@Serializable
data class Tag(
    val id: Int,
    val name: String,
    val slug: String,
)

@Serializable
data class ClinicalSign(
    val id: Int,
    val name: String,
    val eponym: String? = null,
    val description: String? = null,
    val interpretation: String? = null,
    val technique: String? = null,
    @SerialName("diagnostic_relevance") val diagnosticRelevance: String? = null,
    @SerialName("red_flags") val redFlags: List<String>? = null,
    val difficulty: String = "core",
    @SerialName("last_reviewed") val lastReviewed: String? = null,
    @SerialName("media_type") val mediaType: String = "text",
    @SerialName("media_duration") val mediaDuration: String? = null,
    @SerialName("clinical_system") val clinicalSystem: NamedRef? = null,
    val tags: List<Tag> = emptyList(),
)

/**
 * Older skill records (authored before the step shape became {title, detail, caution})
 * still store procedure_steps as plain strings. Decode both shapes rather than crashing
 * on whichever content hasn't been migrated to the richer format yet.
 */
@Serializable(with = ProcedureStepSerializer::class)
data class ProcedureStep(
    val title: String,
    val detail: String,
    val caution: String? = null,
)

object ProcedureStepSerializer : KSerializer<ProcedureStep> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ProcedureStep") {
        element<String>("title")
        element<String>("detail")
        element<String>("caution")
    }

    override fun deserialize(decoder: Decoder): ProcedureStep {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        return if (element is JsonPrimitive) {
            ProcedureStep(title = element.content, detail = "")
        } else {
            val obj = element.jsonObject
            ProcedureStep(
                title = obj["title"]?.jsonPrimitive?.content ?: "",
                detail = obj["detail"]?.jsonPrimitive?.content ?: "",
                caution = obj["caution"]?.jsonPrimitive?.content,
            )
        }
    }

    override fun serialize(encoder: Encoder, value: ProcedureStep) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.title)
            encodeStringElement(descriptor, 1, value.detail)
        }
    }
}

@Serializable
data class Skill(
    val id: Int,
    val name: String,
    val description: String? = null,
    @SerialName("procedure_steps") val procedureSteps: List<ProcedureStep> = emptyList(),
    @SerialName("competency_codes") val competencyCodes: List<String>? = null,
    val equipment: List<String>? = null,
    @SerialName("est_minutes") val estMinutes: Int? = null,
    @SerialName("mastery_pct") val masteryPct: Float? = null,
    @SerialName("clinical_system") val clinicalSystem: NamedRef? = null,
    val tags: List<Tag> = emptyList(),
)
