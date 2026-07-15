package com.example.medclerkmobile.ui.students

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

@Composable
fun StudentProfileScreen(container: AppContainer, studentId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "student-profile") {
        StudentProfileViewModel(it.studentLookupRepository, studentId)
    }
    val state by viewModel.state.collectAsState()
    val successState = state as? UiState.Success

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = successState?.data?.name ?: "Student", onBack = onBack) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val s = state) {
                is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = MaterialTheme.colorScheme.error)
                }

                is UiState.Success -> StudentProfileContent(s.data)
            }
        }
    }
}

@Composable
private fun StudentProfileContent(student: User) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        MedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailField(label = "Name", value = student.name)
                DetailField(label = "Registration number", value = student.studentNumber ?: "Not set")
                DetailField(label = "Email address", value = student.email)
                DetailField(label = "University", value = student.institution?.name ?: "Not set")
                DetailField(label = "Course", value = student.programme ?: "Not set")
                DetailField(label = "Current placement", value = student.currentPlacement ?: "Not set")
                Column {
                    Text(
                        text = "Year of study",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Not yet available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
