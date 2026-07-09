package com.example.medclerkmobile.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.ui.ChipColor
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.MedChip
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

@Composable
fun LibraryScreen(
    container: AppContainer,
    onOpenSystem: (Int) -> Unit,
    onOpenSign: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = appViewModel(container, key = "library") { LibraryViewModel(it.libraryRepository) }
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = s.message, color = MaterialTheme.colorScheme.error)
            }

            is UiState.Success -> LibraryContent(
                viewModel = viewModel,
                systems = s.data.systems,
                recentSigns = s.data.recentSigns,
                onOpenSystem = onOpenSystem,
                onOpenSign = onOpenSign,
            )
        }
    }
}

@Composable
private fun LibraryContent(
    viewModel: LibraryViewModel,
    systems: List<ClinicalSystem>,
    recentSigns: List<ClinicalSign>,
    onOpenSystem: (Int) -> Unit,
    onOpenSign: (Int) -> Unit,
) {
    val matchingSigns = viewModel.matchingSigns(recentSigns)
    val matchingSystems = viewModel.matchingSystems(systems)

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = viewModel::onQueryChange,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text("Search signs, systems, tags") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        if (viewModel.query.isBlank()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(matchingSystems) { system -> SystemCard(system, onClick = { onOpenSystem(system.id) }) }
            }

            SectionTitle(
                text = "Recently added signs",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).padding(top = 8.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(recentSigns.take(4)) { sign -> SignRow(sign, onClick = { onOpenSign(sign.id) }) }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(matchingSigns) { sign -> SignRow(sign, onClick = { onOpenSign(sign.id) }) }
            }
        }
    }
}

@Composable
private fun SystemCard(system: ClinicalSystem, onClick: () -> Unit) {
    val color = parseHexColor(system.color, MaterialTheme.colorScheme.primary)
    MedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(systemIconFor(system.icon), contentDescription = null, tint = color)
            }
            Text(
                text = system.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "${system.signCount} signs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun SignRow(sign: ClinicalSign, onClick: () -> Unit) {
    MedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = sign.name, style = MaterialTheme.typography.titleSmall)
            MedChip(
                text = sign.difficulty.replaceFirstChar { it.uppercase() },
                color = difficultyChipColor(sign.difficulty),
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
