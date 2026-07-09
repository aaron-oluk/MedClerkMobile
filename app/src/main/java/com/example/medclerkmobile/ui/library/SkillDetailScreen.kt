package com.example.medclerkmobile.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Skill
import com.example.medclerkmobile.ui.BackTopAppBar
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

@Composable
fun SkillDetailScreen(container: AppContainer, skillId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "skill-detail") { SkillDetailViewModel(it.libraryRepository, skillId) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            BackTopAppBar(title = (state as? UiState.Success)?.data?.name ?: "Skill", onBack = onBack)
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val s = state) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> SkillContent(viewModel, s.data)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillContent(viewModel: SkillDetailViewModel, skill: Skill) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        skill.description?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            skill.estMinutes?.let { SuggestionChip(onClick = {}, label = { Text("$it min") }) }
            skill.competencyCodes?.firstOrNull()?.let { SuggestionChip(onClick = {}, label = { Text(it) }) }
        }

        if (!skill.equipment.isNullOrEmpty()) {
            Text(text = "Equipment", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                skill.equipment.forEach { item -> SuggestionChip(onClick = {}, label = { Text(item) }) }
            }
        }

        if (skill.procedureSteps.isNotEmpty()) {
            val steps = skill.procedureSteps
            val activeIndex = viewModel.activeStep.coerceIn(0, steps.lastIndex)
            val activeStep = steps[activeIndex]

            Text(
                text = "Step ${activeIndex + 1} of ${steps.size}",
                style = MaterialTheme.typography.labelLarge,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = activeStep.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = activeStep.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    activeStep.caution?.let { caution ->
                        Card(modifier = Modifier.padding(top = 12.dp)) {
                            Text(
                                text = caution,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.goToStep(activeIndex - 1) },
                            enabled = activeIndex > 0,
                        ) { Text("Previous") }
                        Button(
                            onClick = { viewModel.goToStep(activeIndex + 1) },
                            enabled = activeIndex < steps.lastIndex,
                        ) { Text("Next step") }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                steps.forEachIndexed { index, step ->
                    Card(
                        onClick = { viewModel.goToStep(index) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = if (index < activeIndex) "✓" else "${index + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (index == activeIndex) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                            Text(text = step.title, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
