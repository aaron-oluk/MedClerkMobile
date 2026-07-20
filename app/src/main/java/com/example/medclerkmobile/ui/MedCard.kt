package com.example.medclerkmobile.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, elevation = elevation) {
            content()
        }
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, elevation = elevation) {
            content()
        }
    }
}
