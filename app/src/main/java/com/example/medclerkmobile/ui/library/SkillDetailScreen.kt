package com.example.medclerkmobile.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.medclerkmobile.data.model.Skill
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.HeaderTone
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

@Composable
fun SkillDetailScreen(container: AppContainer, skillId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "skill-detail") { SkillDetailViewModel(it.libraryRepository, skillId) }
    val state by viewModel.state.collectAsState()
    val successState = state as? UiState.Success

    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(
                title = successState?.data?.name ?: "Skill",
                tone = HeaderTone.Dark,
                onBack = onBack,
            )
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
            skill.estMinutes?.let { MedChip(text = "$it min", color = ChipColor.Neutral) }
            skill.competencyCodes?.firstOrNull()?.let { MedChip(text = it, color = ChipColor.Neutral) }
        }

        if (!skill.equipment.isNullOrEmpty()) {
            Text(text = "Equipment", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                skill.equipment.forEach { item -> MedChip(text = item, color = ChipColor.Neutral) }
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
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = activeStep.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = activeStep.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    activeStep.caution?.let { caution ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .background(
                                    MaterialTheme.colorScheme.errorContainer,
                                    androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                )
                                .padding(10.dp),
                        ) {
                            Text(
                                text = caution,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
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
                    MedCard(
                        onClick = { viewModel.goToStep(index) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val isDone = index < activeIndex
                            val isActive = index == activeIndex
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(
                                        when {
                                            isActive -> MaterialTheme.colorScheme.primary
                                            isDone -> MaterialTheme.colorScheme.primaryContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (isDone) "✓" else "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isActive) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}
