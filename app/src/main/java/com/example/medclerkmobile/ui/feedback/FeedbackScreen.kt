package com.example.medclerkmobile.ui.feedback

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
import com.example.medclerkmobile.data.model.Feedback
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@Composable
fun FeedbackScreen(container: AppContainer, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "feedback") { ListViewModel { it.feedbackRepository.myFeedback() } }
    val state by viewModel.state.collectAsState()

    StateListContent(
        state = state,
        emptyMessage = "No feedback yet.",
        onRetry = viewModel::refresh,
        modifier = modifier,
    ) { feedback -> FeedbackCard(feedback) }
}

@Composable
private fun FeedbackCard(feedback: Feedback) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            feedback.givenBy?.let {
                Text(text = "From ${it.name}", style = MaterialTheme.typography.titleMedium)
            }
            feedback.strengths?.let {
                Text(text = "Strengths", style = MaterialTheme.typography.labelMedium)
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            feedback.areasToImprove?.let {
                Text(text = "Areas to improve", style = MaterialTheme.typography.labelMedium)
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            feedback.followUpDate?.let {
                Text(text = "Follow up: ${formatApiDate(it)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
