package com.example.medclerkmobile.ui.students

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.StateListContent
import com.example.medclerkmobile.ui.appViewModel

@Composable
fun StudentSearchScreen(
    container: AppContainer,
    onBack: () -> Unit,
    onOpenStudent: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = appViewModel(container, key = "student-search") { StudentSearchViewModel(it) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Find a student", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = viewModel.query,
                onValueChange = viewModel::onQueryChange,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Search by registration number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            StateListContent(
                state = state,
                emptyMessage = "No students found. Search by registration number.",
                onRetry = viewModel::search,
            ) { student -> StudentRow(student, onClick = { onOpenStudent(student.id) }) }
        }
    }
}

@Composable
private fun StudentRow(student: User, onClick: () -> Unit) {
    MedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = student.studentNumber ?: "Not set",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            Text(
                text = student.institution?.name ?: "Not set",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
