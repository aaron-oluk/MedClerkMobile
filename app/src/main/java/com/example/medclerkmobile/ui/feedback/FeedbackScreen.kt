package com.example.medclerkmobile.ui.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Feedback
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate
import com.example.medclerkmobile.ui.theme.Amber700
import com.example.medclerkmobile.ui.theme.Emerald700

private val canGiveFeedback = setOf("lecturer", "superadmin")

@Composable
fun FeedbackScreen(container: AppContainer, onBack: () -> Unit, onAddFeedback: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "feedback") { ListViewModel { it.feedbackRepository.myFeedback() } }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Feedback", onBack = onBack) },
        floatingActionButton = {
            val role = container.currentUserRole
            if (role != null && role in canGiveFeedback) {
                FloatingActionButton(onClick = onAddFeedback) {
                    Icon(Icons.Filled.Add, contentDescription = "Give feedback")
                }
            }
        },
    ) { innerPadding ->
        StateListContent(
            state = state,
            emptyMessage = "No feedback yet.",
            onRetry = viewModel::refresh,
            modifier = Modifier.padding(innerPadding),
        ) { feedback -> FeedbackCard(feedback) }
    }
}

@Composable
fun FeedbackCard(feedback: Feedback) {
    MedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            feedback.givenBy?.let {
                Text(text = "From ${it.name}", style = MaterialTheme.typography.titleMedium)
            }
            feedback.strengths?.let {
                Text(
                    text = "STRENGTHS",
                    color = Emerald700,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 2.dp))
            }
            feedback.areasToImprove?.let {
                Text(
                    text = "AREAS TO IMPROVE",
                    color = Amber700,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 2.dp))
            }
            feedback.followUpDate?.let {
                Text(
                    text = "Follow up: ${formatApiDate(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}
