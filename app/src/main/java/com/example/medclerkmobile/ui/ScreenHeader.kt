package com.example.medclerkmobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class HeaderTone { Light, Tinted, Dark }

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    tone: HeaderTone = HeaderTone.Light,
    accentColor: Color? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val background = when (tone) {
        HeaderTone.Dark -> accentColor ?: MaterialTheme.colorScheme.primary
        HeaderTone.Tinted -> MaterialTheme.colorScheme.secondaryContainer
        HeaderTone.Light -> MaterialTheme.colorScheme.surface
    }
    val onBackgroundColor = if (tone == HeaderTone.Dark) Color.White else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (tone == HeaderTone.Dark) {
        Color.White.copy(alpha = 0.75f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onBackgroundColor)
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = if (onBack == null) 12.dp else 0.dp)) {
                Text(
                    text = title,
                    color = onBackgroundColor,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
                subtitle?.let {
                    Text(text = it, color = subtitleColor, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }
            trailing?.invoke()
        }
    }
}
