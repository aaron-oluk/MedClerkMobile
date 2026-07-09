package com.example.medclerkmobile.ui.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

fun systemIconFor(name: String?): ImageVector = when (name) {
    "heart" -> Icons.Filled.Favorite
    "lungs" -> Icons.Filled.Air
    "stomach" -> Icons.Filled.Science
    "brain" -> Icons.Filled.Psychology
    "bone" -> Icons.Filled.Accessibility
    "gland" -> Icons.Filled.Spa
    "kidney" -> Icons.Filled.WaterDrop
    "droplet" -> Icons.Filled.Bloodtype
    "skin" -> Icons.Filled.Spa
    "ear" -> Icons.Filled.Hearing
    else -> Icons.Filled.MedicalServices
}
