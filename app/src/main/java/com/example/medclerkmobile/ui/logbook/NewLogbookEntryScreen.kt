package com.example.medclerkmobile.ui.logbook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.NamedRef
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.DropdownField
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLogbookEntryScreen(container: AppContainer, onSaved: () -> Unit, onCancel: () -> Unit) {
    val viewModel = appViewModel(container, key = "new-logbook-entry") {
        NewLogbookEntryViewModel(it.logbookRepository, it.rotationRepository)
    }
    val optionsState by viewModel.options.collectAsState()

    Scaffold(
        topBar = { ScreenHeader(title = "Log an encounter", onBack = onCancel) },
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
                        Text(
                            text = "You need an active rotation before you can log an encounter.",
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                } else {
                    NewLogbookEntryForm(
                        viewModel = viewModel,
                        rotations = state.data.rotations,
                        clinicalSigns = state.data.clinicalSigns,
                        skills = state.data.skills,
                        onSaved = onSaved,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewLogbookEntryForm(
    viewModel: NewLogbookEntryViewModel,
    rotations: List<Rotation>,
    clinicalSigns: List<NamedRef>,
    skills: List<NamedRef>,
    onSaved: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Encounter")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DropdownField(
                        label = "Rotation",
                        options = rotations,
                        selected = viewModel.selectedRotation,
                        optionLabel = { it.name },
                        onSelected = viewModel::onRotationSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = ymdToMillis(viewModel.encounterDate)?.let { millisToDisplay(it) } ?: viewModel.encounterDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Encounter date") },
                        trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Clinical detail")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Notes")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.notes,
                    onValueChange = viewModel::onNotesChange,
                    label = { Text("Additional notes") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Consent")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Checkbox(checked = viewModel.consentConfirmed, onCheckedChange = viewModel::onConsentChange)
                    Text(
                        text = "I confirm that verbal consent was obtained from the patient before this entry was recorded.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 14.dp, end = 8.dp),
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
                Text("Save entry", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = ymdToMillis(viewModel.encounterDate))
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onEncounterDateChange(millisToYmd(it)) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun dateFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

private fun displayFormat(): SimpleDateFormat =
    SimpleDateFormat("d MMM yyyy", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

private fun ymdToMillis(value: String): Long? = runCatching { dateFormat().parse(value)?.time }.getOrNull()

private fun millisToYmd(millis: Long): String = dateFormat().format(Date(millis))

private fun millisToDisplay(millis: Long): String = displayFormat().format(Date(millis))
