package com.example.medclerkmobile.ui.assessments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.ProgressRing
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.feedback.FeedbackCard
import com.example.medclerkmobile.ui.formatApiDate

@Composable
fun AssessmentDetailScreen(container: AppContainer, assessmentId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "assessment-detail") {
        AssessmentDetailViewModel(it.assessmentRepository, assessmentId)
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Assessment", onBack = onBack) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val s = state) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> AssessmentDetailContent(s.data)
            }
        }
    }
}

@Composable
private fun AssessmentDetailContent(assessment: Assessment) {
    val pct = scorePercent(assessment)
    val (label, chipColor) = resultBand(pct)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MedCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assessment.skill?.name ?: "Skill assessment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
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
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Text(
                        text = formatApiDate(assessment.assessedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    MedChip(text = label, color = chipColor, modifier = Modifier.padding(top = 8.dp))
                }
                ProgressRing(percent = pct, size = 72.dp, color = ringColor(pct))
            }
        }

        Text(
            text = "Score: ${assessment.score.toFloatOrNull()?.let { formatScore(it) } ?: assessment.score} / ${assessment.maxScore.toFloatOrNull()?.let { formatScore(it) } ?: assessment.maxScore}",
            style = MaterialTheme.typography.bodyMedium,
        )

        if (assessment.feedback.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(text = "Feedback")
                assessment.feedback.forEach { feedback -> FeedbackCard(feedback) }
            }
        }
    }
}

private fun formatScore(value: Float): String =
    if (value == value.toInt().toFloat()) value.toInt().toString() else value.toString()
