package com.example.medclerkmobile.ui.logbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
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
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@Composable
fun LogbookScreen(container: AppContainer, onAddEntry: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "logbook-entries") { ListViewModel { it.logbookRepository.myEntries() } }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) {
                Icon(Icons.Filled.Add, contentDescription = "Log a new encounter")
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.clinicalSign?.name ?: entry.skill?.name ?: "Clinical encounter",
                style = MaterialTheme.typography.titleMedium,
            )
            entry.rotation?.let {
                Text(text = it.name, style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = formatApiDate(entry.encounterDate), style = MaterialTheme.typography.bodySmall)
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
