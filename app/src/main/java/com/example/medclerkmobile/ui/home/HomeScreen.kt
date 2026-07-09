package com.example.medclerkmobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ProgressRing
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.theme.Amber600
import com.example.medclerkmobile.ui.theme.Teal600
import com.example.medclerkmobile.ui.theme.Teal700
import com.example.medclerkmobile.ui.theme.Teal800

@Composable
fun HomeScreen(
    container: AppContainer,
    onAddLogbookEntry: () -> Unit,
    onOpenRotations: () -> Unit,
    onOpenFeedback: () -> Unit,
    onSwitchToResults: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = appViewModel(container, key = "home") { HomeViewModel(it) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HomeHeader(name = container.currentUserName ?: "there")

        when (val s = state) {
            is UiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is UiState.Error -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) { Text(text = s.message, color = MaterialTheme.colorScheme.error) }

            is UiState.Success -> HomeContent(
                data = s.data,
                onAddLogbookEntry = onAddLogbookEntry,
                onOpenRotations = onOpenRotations,
                onOpenFeedback = onOpenFeedback,
                onSwitchToResults = onSwitchToResults,
            )
        }
    }
}

@Composable
private fun HomeHeader(name: String) {
    val firstName = name.trim().substringBefore(" ")
    val initials = name.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Teal700, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Good to see you,", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                Text(
                    text = firstName.ifBlank { "there" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = initials.ifBlank { "?" }, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onAddLogbookEntry: () -> Unit,
    onOpenRotations: () -> Unit,
    onOpenFeedback: () -> Unit,
    onSwitchToResults: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        data.activeRotation?.let { rotation ->
            ActiveRotationCard(rotation, onAddLogbookEntry, onOpenRotations)
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Today")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                QuickActionCard(
                    count = data.assessmentCount,
                    label = "Assessments",
                    sub = "View results",
                    modifier = Modifier.weight(1f),
                    onClick = onSwitchToResults,
                )
                QuickActionCard(
                    count = data.feedbackCount,
                    label = "Feedback",
                    sub = "From your supervisors",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenFeedback,
                )
            }
        }

        if (data.strongestSystem != null || data.weakestSystem != null) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(text = "Competency snapshot")
                MedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        data.strongestSystem?.let {
                            CompetencyRow(label = "Strongest area", system = it, color = Teal600)
                        }
                        data.weakestSystem?.let {
                            CompetencyRow(label = "Needs focus", system = it, color = Amber600)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveRotationCard(rotation: Rotation, onAddLogbookEntry: () -> Unit, onOpenRotations: () -> Unit) {
    val progress = rotationProgress(rotation.startDate, rotation.endDate)

    MedCard(onClick = onOpenRotations, modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACTIVE ROTATION",
                        color = Teal700,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(text = rotation.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 2.dp))
                    rotation.department?.let {
                        Text(text = it.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    rotation.supervisor?.let {
                        Text(text = "With ${it.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (progress != null) {
                    ProgressRing(
                        percent = (progress.weeksElapsed.toFloat() / progress.weeksTotal.toFloat()) * 100f,
                        color = Teal800,
                        size = 56.dp,
                    )
                }
            }

            val required = rotation.requiredEncounters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RotationStat(label = "Encounters", value = "${rotation.completedEncounters}", sub = required?.let { "of $it" } ?: "recorded")
                if (required != null) {
                    RotationStat(label = "To go", value = "${(required - rotation.completedEncounters).coerceAtLeast(0)}", sub = "this rotation")
                }
                if (progress != null) {
                    RotationStat(label = "Week", value = "${progress.weeksElapsed}", sub = "of ${progress.weeksTotal}")
                }
            }

            Button(
                onClick = onAddLogbookEntry,
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Record new encounter")
            }
        }
    }
}

@Composable
private fun RotationStat(label: String, value: String, sub: String) {
    Column {
        Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun QuickActionCard(count: Int, label: String, sub: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    MedCard(onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "$count", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CompetencyRow(label: String, system: ClinicalSystem, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ProgressRing(percent = system.masteryPct ?: 0f, color = color, size = 52.dp)
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = system.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        }
    }
}
