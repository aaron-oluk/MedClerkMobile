package com.example.medclerkmobile.ui.assessments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel

@Composable
fun AssessmentsScreen(container: AppContainer, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container) { ListViewModel { it.assessmentRepository.myAssessments() } }
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = assessment.skill?.name ?: "Skill assessment",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${assessment.score} / ${assessment.maxScore}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            assessment.rotation?.let {
                Text(text = it.name, style = MaterialTheme.typography.bodyMedium)
            }
            assessment.assessor?.let {
                Text(text = "Assessed by ${it.name}", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = assessment.assessedAt, style = MaterialTheme.typography.bodySmall)
        }
    }
}
