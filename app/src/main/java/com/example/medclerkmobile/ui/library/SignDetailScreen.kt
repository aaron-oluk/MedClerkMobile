package com.example.medclerkmobile.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.ui.BackTopAppBar
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

private val tabs = listOf("Overview", "Technique", "Interpretation")

@Composable
fun SignDetailScreen(container: AppContainer, signId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "sign-detail") { SignDetailViewModel(it.libraryRepository, signId) }
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            BackTopAppBar(title = (state as? UiState.Success)?.data?.name ?: "Sign", onBack = onBack)
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

                is UiState.Success -> Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) },
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        when (selectedTab) {
                            0 -> OverviewTab(s.data)
                            1 -> TechniqueTab(s.data)
                            else -> InterpretationTab(s.data)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OverviewTab(sign: ClinicalSign) {
    sign.eponym?.let {
        Text(text = "Eponym: $it", style = MaterialTheme.typography.bodyMedium)
    }
    sign.description?.let {
        LabeledCard(label = "Summary", body = it)
    }
    sign.diagnosticRelevance?.let {
        LabeledCard(label = "Diagnostic relevance", body = it)
    }
    sign.redFlags?.takeIf { it.isNotEmpty() }?.let { flags ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Red flags",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
                flags.forEach { flag ->
                    Text(
                        text = "• $flag",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
    }
    if (sign.tags.isNotEmpty()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            sign.tags.forEach { tag ->
                SuggestionChip(onClick = {}, label = { Text("#${tag.name}") })
            }
        }
    }
}

@Composable
private fun TechniqueTab(sign: ClinicalSign) {
    LabeledCard(label = "Examination technique", body = sign.technique ?: "Not documented yet.")
}

@Composable
private fun InterpretationTab(sign: ClinicalSign) {
    LabeledCard(label = "Interpretation", body = sign.interpretation ?: "Not documented yet.")
    AssistChip(onClick = {}, label = { Text("Difficulty: ${sign.difficulty}") })
}

@Composable
private fun LabeledCard(label: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(text = body, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
