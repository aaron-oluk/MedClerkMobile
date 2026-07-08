package com.example.medclerkmobile.ui.logbook

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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.NamedRef
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.DropdownField
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLogbookEntryScreen(container: AppContainer, onSaved: () -> Unit, onCancel: () -> Unit) {
    val viewModel = appViewModel(container) {
        NewLogbookEntryViewModel(it.logbookRepository, it.rotationRepository)
    }
    val optionsState by viewModel.options.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Log an encounter") }) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = optionsState) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> if (state.data.rotations.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("You need an active rotation before you can log an encounter.")
                    }
                } else {
                    NewLogbookEntryForm(
                        viewModel = viewModel,
                        rotations = state.data.rotations,
                        clinicalSigns = state.data.clinicalSigns,
                        skills = state.data.skills,
                        onSaved = onSaved,
                        onCancel = onCancel,
                    )
                }
            }
        }
    }
}

@Composable
private fun NewLogbookEntryForm(
    viewModel: NewLogbookEntryViewModel,
    rotations: List<Rotation>,
    clinicalSigns: List<NamedRef>,
    skills: List<NamedRef>,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DropdownField(
            label = "Rotation",
            options = rotations,
            selected = viewModel.selectedRotation,
            optionLabel = { it.name },
            onSelected = viewModel::onRotationSelected,
            modifier = Modifier.fillMaxWidth(),
        )

        DropdownField(
            label = "Clinical sign (optional)",
            options = clinicalSigns,
            selected = viewModel.selectedClinicalSign,
            optionLabel = { it.name },
            onSelected = viewModel::onClinicalSignSelected,
            modifier = Modifier.fillMaxWidth(),
        )

        DropdownField(
            label = "Skill (optional)",
            options = skills,
            selected = viewModel.selectedSkill,
            optionLabel = { it.name },
            onSelected = viewModel::onSkillSelected,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = viewModel.encounterDate,
            onValueChange = viewModel::onEncounterDateChange,
            label = { Text("Encounter date (yyyy-mm-dd)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = viewModel.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text("Notes") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

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
                Text("Save entry")
            }
        }

        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}
