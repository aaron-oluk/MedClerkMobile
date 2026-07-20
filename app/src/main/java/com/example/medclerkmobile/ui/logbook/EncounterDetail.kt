package com.example.medclerkmobile.ui.logbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.formatApiDate
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonPrimitive

fun LogbookEntry.findingText(key: String): String? {
    val element = findings?.get(key) ?: return null
    if (element is JsonNull) return null
    return element.jsonPrimitive.content.takeIf { it.isNotBlank() }
}

@Composable
fun EncounterDetailCard(entry: LogbookEntry, modifier: Modifier = Modifier) {
    val chiefComplaint = entry.findingText("chief_complaint")
    val examinationFindings = entry.findingText("examination_findings")
    val impression = entry.findingText("impression")
    val plan = entry.findingText("plan")
    val hasNothingToShow = chiefComplaint == null && examinationFindings == null && impression == null && plan == null && entry.notes == null

    MedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "${entry.student?.name ?: "Student"} — ${entry.skill?.name ?: "Skill"}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${formatApiDate(entry.encounterDate)} · ${entry.rotation?.name ?: "Rotation"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            FindingBlock("Chief complaint", chiefComplaint)
            FindingBlock("Examination findings", examinationFindings)
            FindingBlock("Impression", impression)
            FindingBlock("Plan", plan)
            FindingBlock("Notes", entry.notes)

            if (hasNothingToShow) {
                Text(
                    text = "No structured findings recorded for this encounter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FindingBlock(label: String, value: String?) {
    if (value == null) return

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
