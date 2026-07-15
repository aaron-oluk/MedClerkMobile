package com.example.medclerkmobile.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.ui.MedCard
import com.example.medclerkmobile.ui.ScreenHeader
import com.example.medclerkmobile.ui.SectionTitle
import com.example.medclerkmobile.ui.UiState
import com.example.medclerkmobile.ui.appViewModel

private const val FEEDBACK_EMAIL = "hello@example.com"

@Composable
fun SettingsScreen(container: AppContainer, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = appViewModel(container, key = "settings") { SettingsViewModel(it) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Settings", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (val s = state) {
                is UiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize().padding(48.dp),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                is UiState.Error -> Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(text = s.message, color = MaterialTheme.colorScheme.error) }

                is UiState.Success -> SettingsContent(
                    emailNotificationsEnabled = s.data.emailNotificationsEnabled,
                    onNotificationsChanged = viewModel::setNotificationsEnabled,
                )
            }
        }
    }
}

@Composable
private fun SettingsContent(emailNotificationsEnabled: Boolean, onNotificationsChanged: (Boolean) -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Notifications")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Email me about updates to my rotations, logbook and feedback",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f).padding(end = 12.dp),
                    )
                    Switch(checked = emailNotificationsEnabled, onCheckedChange = onNotificationsChanged)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Rate us")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Coming soon.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(text = "Send feedback")
            MedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Tell us what's working, and what isn't.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = { sendFeedbackEmail(context) },
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            Text("Send feedback", fontWeight = FontWeight.SemiBold)
                        },
                    )
                }
            }
        }
    }
}

private fun sendFeedbackEmail(context: android.content.Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$FEEDBACK_EMAIL")
        putExtra(Intent.EXTRA_SUBJECT, "MedClerk feedback")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // No email app available on this device; nothing more we can do here.
    }
}
