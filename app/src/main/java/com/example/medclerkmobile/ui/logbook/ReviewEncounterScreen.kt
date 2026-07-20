package com.example.medclerkmobile.ui.logbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.ui.DateField
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewEncounterScreen(container: AppContainer, logbookEntryId: Int, onDone: () -> Unit, onCancel: () -> Unit) {
    val viewModel = appViewModel(container, key = "review-encounter") {
        ReviewEncounterViewModel(it.logbookRepository, it.assessmentRepository, logbookEntryId)
    }
    val entryState by viewModel.entry.collectAsState()

    Scaffold(
        topBar = { ScreenHeader(title = "Review encounter", onBack = onCancel) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = entryState) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> ReviewEncounterContent(viewModel = viewModel, entry = state.data, onDone = onDone)
            }
        }
    }
}

@Composable
private fun ReviewEncounterContent(viewModel: ReviewEncounterViewModel, entry: LogbookEntry, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Encounter")
            EncounterDetailCard(entry = entry)
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Sign-off")
            if (entry.needsSignOff) {
                MedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Signing off confirms you supervised this encounter as recorded.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        viewModel.signOffError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                        Button(
                            onClick = { viewModel.signOff(onDone) },
                            enabled = !viewModel.isSigningOff,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            if (viewModel.isSigningOff) {
                                CircularProgressIndicator(modifier = Modifier.padding(2.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Sign off this encounter", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            } else {
                MedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Signed off by ${entry.signedOffBy?.name ?: "you"}${entry.signedOffAt?.let { " on ${formatApiDate(it)}" } ?: ""}.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        if (entry.skillId != null) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(text = "Score")
                if (entry.needsAssessment) {
                    MedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = viewModel.score,
                                    onValueChange = viewModel::onScoreChange,
                                    label = { Text("Score") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f),
                                )

                                OutlinedTextField(
                                    value = viewModel.maxScore,
                                    onValueChange = viewModel::onMaxScoreChange,
                                    label = { Text("Out of") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            DateField(
                                label = "Assessed on",
                                value = viewModel.assessedAt,
                                onValueChange = viewModel::onAssessedAtChange,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            viewModel.assessmentError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }

                            Button(
                                onClick = { viewModel.submitAssessment(onDone) },
                                enabled = !viewModel.isSubmittingAssessment,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                if (viewModel.isSubmittingAssessment) {
                                    CircularProgressIndicator(modifier = Modifier.padding(2.dp), color = MaterialTheme.colorScheme.onPrimary)
                                } else {
                                    Text("Save assessment", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                } else {
                    MedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "This encounter has already been scored.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}
