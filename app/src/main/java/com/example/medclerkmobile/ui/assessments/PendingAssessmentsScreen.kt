package com.example.medclerkmobile.ui.assessments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@Composable
fun PendingAssessmentsScreen(container: AppContainer, onBack: () -> Unit, onOpenEntry: (Int) -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "pending-assessments") {
        ListViewModel { it.logbookRepository.pendingAssessments() }
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Pending encounters", onBack = onBack) },
    ) { innerPadding ->
        StateListContent(
            state = state,
            emptyMessage = "No unscored encounters from your students right now.",
            onRetry = viewModel::refresh,
            modifier = Modifier.padding(innerPadding),
        ) { entry -> PendingEntryCard(entry, onClick = { onOpenEntry(entry.id) }) }
    }
}

@Composable
private fun PendingEntryCard(entry: LogbookEntry, onClick: () -> Unit) {
    MedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.student?.name ?: "Student",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = entry.skill?.name ?: "Skill",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            entry.rotation?.let {
                MedChip(text = it.name, color = ChipColor.Teal, modifier = Modifier.padding(top = 6.dp))
            }
            Text(
                text = formatApiDate(entry.encounterDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
