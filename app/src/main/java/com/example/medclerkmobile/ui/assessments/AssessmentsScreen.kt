package com.example.medclerkmobile.ui.assessments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.ProgressRing
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate
import com.example.medclerkmobile.ui.theme.Amber600
import com.example.medclerkmobile.ui.theme.Red600
import com.example.medclerkmobile.ui.theme.Teal600

private val canGiveAssessment = setOf("lecturer", "superadmin")

@Composable
fun AssessmentsScreen(container: AppContainer, onAddAssessment: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "assessments") { ListViewModel { it.assessmentRepository.myAssessments() } }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            val role = container.currentUserRole
            if (role != null && role in canGiveAssessment) {
                FloatingActionButton(onClick = onAddAssessment) {
                    Icon(Icons.Filled.Add, contentDescription = "Give an assessment")
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val s = state) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = s.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::refresh, modifier = Modifier.padding(top = 12.dp)) {
                            Text("Retry")
                        }
                    }
                }

                is UiState.Success -> if (s.data.isEmpty()) {
                    EmptyResults()
                } else {
                    AssessmentsContent(s.data)
                }
            }
        }
    }
}

@Composable
private fun EmptyResults() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.AutoMirrored.Filled.Assignment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = "No assessments recorded yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AssessmentsContent(assessments: List<Assessment>) {
    val groups = assessments
        .groupBy { it.rotation?.name ?: "Other" }
        .toList()
        .sortedByDescending { (_, group) -> group.maxOf { it.assessedAt } }
        .map { (name, group) -> name to group.sortedByDescending { it.assessedAt } }

    val average = assessments.map { scorePercent(it) }.average().toFloat()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item { SummaryCard(average = average, count = assessments.size) }

        groups.forEach { (rotationName, group) ->
            item { SectionTitle(text = rotationName, modifier = Modifier.padding(top = 8.dp)) }
            items(group) { assessment -> AssessmentCard(assessment) }
        }
    }
}

@Composable
private fun SummaryCard(average: Float, count: Int) {
    val (label, color) = resultBand(average)

    MedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(percent = average, color = ringColor(average), size = 60.dp, label = "avg")
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "Average score",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Across $count assessment${if (count == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            MedChip(text = label, color = color, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun AssessmentCard(assessment: Assessment) {
    val pct = scorePercent(assessment)
    val (label, chipColor) = resultBand(pct)

    MedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assessment.skill?.name ?: "Skill assessment",
                    style = MaterialTheme.typography.titleMedium,
                )
                assessment.rotation?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                assessment.assessor?.let {
                    Text(
                        text = "Assessed by ${it.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = formatApiDate(assessment.assessedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                MedChip(text = label, color = chipColor, modifier = Modifier.padding(top = 6.dp))
            }
            ProgressRing(percent = pct, size = 52.dp, color = ringColor(pct))
        }
    }
}

private fun scorePercent(assessment: Assessment): Float {
    val score = assessment.score.toFloatOrNull() ?: 0f
    val maxScore = assessment.maxScore.toFloatOrNull()?.takeIf { it > 0f } ?: 100f
    return (score / maxScore) * 100f
}

private fun ringColor(pct: Float) = when {
    pct >= 75f -> Teal600
    pct >= 60f -> Amber600
    else -> Red600
}

private fun resultBand(pct: Float): Pair<String, ChipColor> = when {
    pct >= 75f -> "Strong" to ChipColor.Teal
    pct >= 60f -> "Developing" to ChipColor.Amber
    else -> "Needs focus" to ChipColor.Red
}
