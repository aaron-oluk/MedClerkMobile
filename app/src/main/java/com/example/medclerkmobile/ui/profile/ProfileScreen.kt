package com.example.medclerkmobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.ProgressRing
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.theme.Neutral500
import com.example.medclerkmobile.ui.theme.Red100
import com.example.medclerkmobile.ui.theme.Red700
import com.example.medclerkmobile.ui.theme.Teal100
import com.example.medclerkmobile.ui.theme.Teal600
import com.example.medclerkmobile.ui.theme.Teal700
import com.example.medclerkmobile.ui.theme.Teal800
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    container: AppContainer,
    onOpenRotations: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStudentSearch: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = appViewModel(container, key = "profile") { ProfileViewModel(it) }
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
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

            is UiState.Success -> ProfileContent(
                data = s.data,
                onOpenRotations = onOpenRotations,
                onOpenSettings = onOpenSettings,
                onOpenStudentSearch = onOpenStudentSearch,
                onSignOut = {
                    scope.launch {
                        container.authRepository.logout()
                        onLoggedOut()
                    }
                },
            )
        }
    }
}

@Composable
private fun ProfileContent(
    data: ProfileData,
    onOpenRotations: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStudentSearch: () -> Unit,
    onSignOut: () -> Unit,
) {
    Column {
        ProfileHeader(data)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(text = "Overall competency")
                MedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        ProgressRing(
                            percent = data.overallCompetencyPct ?: 0f,
                            color = Teal600,
                            size = 60.dp,
                            label = "overall",
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = "Tracking ${data.competencyCount} competencies",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Average performance across your tracked clinical systems. Higher is better.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }

            data.user.institution?.let { institution ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle(text = "Institution")
                    MedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Teal100, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = Teal700)
                            }
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text(text = institution.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                institution.country?.let {
                                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                data.user.department?.let {
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(
                    text = "Rotations",
                    action = {
                        Text(
                            text = "All",
                            style = MaterialTheme.typography.labelMedium,
                            color = Teal700,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onOpenRotations),
                        )
                    },
                )
                if (data.rotations.isEmpty()) {
                    MedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No rotations yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        data.rotations.take(3).forEach { rotation -> RotationRow(rotation, onClick = onOpenRotations) }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (data.user.role == "lecturer") {
                    NavigationRow(
                        label = "Find a student",
                        icon = Icons.Filled.PersonSearch,
                        onClick = onOpenStudentSearch,
                    )
                }
                NavigationRow(label = "Settings", icon = Icons.Filled.Settings, onClick = onOpenSettings)
            }

            Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(containerColor = Red100, contentColor = Red700),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Sign out")
            }
        }
    }
}

@Composable
private fun NavigationRow(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    MedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Teal700)
                Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Neutral500)
        }
    }
}

@Composable
private fun ProfileHeader(data: ProfileData) {
    val user = data.user
    val initials = user.name.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Teal700, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = initials.ifBlank { "?" }, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(text = user.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = user.email, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.padding(top = 6.dp)) {
                    HeaderChip(text = user.role.replaceFirstChar { it.uppercase() })
                    user.studentNumber?.let {
                        HeaderChip(text = it, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.08f))
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            HeaderStat(value = data.averageScorePct?.let { "${it.toInt()}" } ?: "—", label = "Average")
            HeaderStat(value = "${data.encounterCount}", label = "Encounters")
            HeaderStat(value = "${data.feedbackCount}", label = "Feedback")
            HeaderStat(value = "${data.rotations.size}", label = "Rotations")
        }
    }
}

@Composable
private fun HeaderChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(Teal800, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    )
}

@Composable
private fun HeaderStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(text = label, color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.labelSmall)
    }
}

private fun statusChipColor(status: String): ChipColor = when (status) {
    "active" -> ChipColor.Green
    "completed" -> ChipColor.Neutral
    else -> ChipColor.Amber
}

@Composable
private fun RotationRow(rotation: Rotation, onClick: () -> Unit) {
    MedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .background(if (rotation.status == "active") Teal600 else Neutral500, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = rotation.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    rotation.department?.let {
                        Text(text = it.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                MedChip(text = rotation.status.replaceFirstChar { it.uppercase() }, color = statusChipColor(rotation.status))
            }
        }
    }
}
