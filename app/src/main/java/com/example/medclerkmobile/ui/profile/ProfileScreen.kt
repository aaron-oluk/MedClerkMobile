package com.example.medclerkmobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.ui.theme.Red100
import com.example.medclerkmobile.ui.theme.Red700
import com.example.medclerkmobile.ui.theme.Teal700
import kotlinx.coroutines.launch

/**
 * Minimal placeholder for the Profile tab. The full profile (stats, competencies,
 * institution card, rotations, quick links to audit trail/settings) is a later stage;
 * this exists so sign-out has a home now that it's no longer in the top bar.
 */
@Composable
fun ProfileScreen(container: AppContainer, onLoggedOut: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val name = container.currentUserName ?: "Your account"
    val role = container.currentUserRole?.replaceFirstChar { it.uppercase() }
    val initials = name.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Teal700, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = initials.ifBlank { "?" }, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
            )
            role?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = {
                    scope.launch {
                        container.authRepository.logout()
                        onLoggedOut()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Red100, contentColor = Red700),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            ) {
                Text("Sign out")
            }
        }
    }
}
