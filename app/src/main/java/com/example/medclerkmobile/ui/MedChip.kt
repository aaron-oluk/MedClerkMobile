package com.example.medclerkmobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medclerkmobile.ui.theme.Amber100
import com.example.medclerkmobile.ui.theme.Amber800
import com.example.medclerkmobile.ui.theme.Blue100
import com.example.medclerkmobile.ui.theme.Blue700
import com.example.medclerkmobile.ui.theme.Emerald100
import com.example.medclerkmobile.ui.theme.Emerald800
import com.example.medclerkmobile.ui.theme.Neutral100
import com.example.medclerkmobile.ui.theme.Neutral700
import com.example.medclerkmobile.ui.theme.Red100
import com.example.medclerkmobile.ui.theme.Red700
import com.example.medclerkmobile.ui.theme.Teal100
import com.example.medclerkmobile.ui.theme.Teal800
import com.example.medclerkmobile.ui.theme.Violet100
import com.example.medclerkmobile.ui.theme.Violet700

enum class ChipColor(val background: Color, val content: Color) {
    Neutral(Neutral100, Neutral700),
    Teal(Teal100, Teal800),
    Amber(Amber100, Amber800),
    Red(Red100, Red700),
    Violet(Violet100, Violet700),
    Blue(Blue100, Blue700),
    Green(Emerald100, Emerald800),
}

@Composable
fun MedChip(text: String, color: ChipColor = ChipColor.Neutral, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = color.content,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .background(color.background, RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}
