package com.example.medclerkmobile.ui.rotations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.ui.ListViewModel
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel
import com.example.medclerkmobile.ui.formatApiDate

@Composable
fun RotationsScreen(container: AppContainer, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "rotations") { ListViewModel { it.rotationRepository.myRotations() } }
    val state by viewModel.state.collectAsState()

    StateListContent(
        state = state,
        emptyMessage = "You don't have any rotations yet.",
        onRetry = viewModel::refresh,
        modifier = modifier,
    ) { rotation -> RotationCard(rotation) }
}

@Composable
private fun RotationCard(rotation: Rotation) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = rotation.name, style = MaterialTheme.typography.titleMedium)
            rotation.department?.let {
                Text(text = it.name, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "${formatApiDate(rotation.startDate)} – ${rotation.endDate?.let(::formatApiDate) ?: "present"}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = rotation.status.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
