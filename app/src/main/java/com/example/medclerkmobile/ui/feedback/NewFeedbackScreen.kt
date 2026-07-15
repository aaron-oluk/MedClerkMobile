package com.example.medclerkmobile.ui.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.ui.DateField
import com.example.medclerkmobile.ui.DropdownField
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFeedbackScreen(container: AppContainer, onSaved: () -> Unit, onCancel: () -> Unit) {
    val viewModel = appViewModel(container, key = "new-feedback") {
        NewFeedbackViewModel(it.feedbackRepository, it.assessmentRepository)
    }
    val optionsState by viewModel.options.collectAsState()

    Scaffold(
        topBar = { ScreenHeader(title = "Give feedback", onBack = onCancel) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = optionsState) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> if (state.data.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "You need to give an assessment before you can leave feedback on it.",
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                } else {
                    NewFeedbackForm(viewModel = viewModel, assessments = state.data, onSaved = onSaved)
                }
            }
        }
    }
}

@Composable
private fun NewFeedbackForm(viewModel: NewFeedbackViewModel, assessments: List<Assessment>, onSaved: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Assessment")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DropdownField(
                        label = "Assessment",
                        options = assessments,
                        selected = viewModel.selectedAssessment,
                        optionLabel = { "${it.skill?.name ?: "Assessment"} — ${formatApiDate(it.assessedAt)}" },
                        onSelected = viewModel::onAssessmentSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Feedback")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = viewModel.strengths,
                        onValueChange = viewModel::onStrengthsChange,
                        label = { Text("Strengths") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = viewModel.areasToImprove,
                        onValueChange = viewModel::onAreasToImproveChange,
                        label = { Text("Areas to improve") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Follow-up")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DateField(
                        label = "Follow-up date (optional)",
                        value = viewModel.followUpDate,
                        onValueChange = viewModel::onFollowUpDateChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.submit(onSaved) },
            enabled = !viewModel.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (viewModel.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Save feedback", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
