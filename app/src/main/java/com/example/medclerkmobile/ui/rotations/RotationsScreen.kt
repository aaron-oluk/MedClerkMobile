package com.example.medclerkmobile.ui.rotations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@Composable
fun RotationsScreen(container: AppContainer, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "rotations") { ListViewModel { it.rotationRepository.myRotations() } }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Rotations", onBack = onBack) },
    ) { innerPadding ->
        StateListContent(
            state = state,
            emptyMessage = "You don't have any rotations yet.",
            onRetry = viewModel::refresh,
            modifier = Modifier.padding(innerPadding),
        ) { rotation -> RotationCard(rotation) }
    }
}

private fun statusChipColor(status: String): ChipColor = when (status) {
    "active" -> ChipColor.Green
    "completed" -> ChipColor.Neutral
    else -> ChipColor.Amber
}

@Composable
private fun RotationCard(rotation: Rotation) {
    MedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = rotation.name, style = MaterialTheme.typography.titleMedium)
            rotation.department?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Text(
                text = "${formatApiDate(rotation.startDate)} – ${rotation.endDate?.let(::formatApiDate) ?: "present"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            MedChip(
                text = rotation.status.replaceFirstChar { it.uppercase() },
                color = statusChipColor(rotation.status),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
