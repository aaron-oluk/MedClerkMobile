package com.example.medclerkmobile.ui.assessments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ProgressRing
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate
import com.example.medclerkmobile.ui.theme.Amber600
import com.example.medclerkmobile.ui.theme.Red600
import com.example.medclerkmobile.ui.theme.Teal600

@Composable
fun AssessmentsScreen(container: AppContainer, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "assessments") { ListViewModel { it.assessmentRepository.myAssessments() } }
    val state by viewModel.state.collectAsState()

    StateListContent(
        state = state,
        emptyMessage = "No assessments recorded yet.",
        onRetry = viewModel::refresh,
        modifier = modifier,
    ) { assessment -> AssessmentCard(assessment) }
}

@Composable
private fun AssessmentCard(assessment: Assessment) {
    val score = assessment.score.toFloatOrNull() ?: 0f
    val maxScore = assessment.maxScore.toFloatOrNull()?.takeIf { it > 0 } ?: 100f
    val pct = (score / maxScore) * 100f
    val ringColor = when {
        pct >= 75f -> Teal600
        pct >= 60f -> Amber600
        else -> Red600
    }

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
            }
            ProgressRing(percent = pct, size = 52.dp, color = ringColor)
        }
    }
}
