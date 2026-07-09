package com.example.medclerkmobile.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
fun SystemDetailScreen(
    container: AppContainer,
    systemId: Int,
    onBack: () -> Unit,
    onOpenSign: (Int) -> Unit,
    onOpenSkill: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = appViewModel(container, key = "system-detail") {
        SystemDetailViewModel(it.libraryRepository, systemId)
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            BackTopAppBar(title = (state as? UiState.Success)?.data?.system?.name ?: "System", onBack = onBack)
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

                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (s.data.skills.isNotEmpty()) {
                        item {
                            Text(text = "Skills", style = MaterialTheme.typography.titleSmall)
                        }
                        items(s.data.skills) { skill -> SkillRow(skill, onClick = { onOpenSkill(skill.id) }) }
                    }
                    item {
                        Text(
                            text = "Signs",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    items(s.data.signs) { sign -> SignRow(sign, onClick = { onOpenSign(sign.id) }) }
                }
            }
        }
    }
}

@Composable
internal fun SkillRow(skill: Skill, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(text = skill.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = buildString {
                    append("${skill.procedureSteps.size} steps")
                    skill.estMinutes?.let { append(", $it min") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
