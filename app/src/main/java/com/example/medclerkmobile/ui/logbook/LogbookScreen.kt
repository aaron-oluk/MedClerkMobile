package com.example.medclerkmobile.ui.logbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

private val canReview = setOf("lecturer", "superadmin")

@Composable
fun LogbookScreen(container: AppContainer, onAddEntry: () -> Unit, onOpenPendingReview: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "logbook-entries") { ListViewModel { it.logbookRepository.myEntries() } }
    val state by viewModel.state.collectAsState()
    val role = container.currentUserRole

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (role == "student") {
                FloatingActionButton(onClick = onAddEntry) {
                    Icon(Icons.Filled.Add, contentDescription = "Log a new encounter")
                }
            } else if (role != null && role in canReview) {
                ExtendedFloatingActionButton(onClick = onOpenPendingReview) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Text(text = "Pending review", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            StateListContent(
                state = state,
                emptyMessage = "No logbook entries yet. Tap + to log your first encounter.",
                onRetry = viewModel::refresh,
            ) { entry -> LogbookEntryCard(entry) }
        }
    }
}

@Composable
private fun LogbookEntryCard(entry: LogbookEntry) {
    MedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.clinicalSign?.name ?: entry.skill?.name ?: "Clinical encounter",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(modifier = Modifier.padding(top = 6.dp)) {
                entry.rotation?.let {
                    MedChip(text = it.name, color = ChipColor.Teal)
                }
                if (entry.signedOffAt != null) {
                    MedChip(text = "Signed off", color = ChipColor.Teal, modifier = Modifier.padding(start = 6.dp))
                } else {
                    MedChip(text = "Awaiting sign-off", color = ChipColor.Amber, modifier = Modifier.padding(start = 6.dp))
                }
            }
            Text(
                text = formatApiDate(entry.encounterDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
            entry.notes?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
