package com.example.medclerkmobile.ui.home

import com.example.medclerkmobile.ui.formatApiDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class RotationProgress(val weeksElapsed: Int, val weeksTotal: Int)

private const val MILLIS_PER_DAY = 86_400_000L
private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

fun rotationProgress(startDate: String, endDate: String?): RotationProgress? {
    val start = runCatching { dateFormat.parse(formatApiDate(startDate)) }.getOrNull() ?: return null
    val end = endDate?.let { runCatching { dateFormat.parse(formatApiDate(it)) }.getOrNull() }
    val today = Date()

    val totalDays = if (end != null) (end.time - start.time) / MILLIS_PER_DAY else 7L
    val elapsedDays = (today.time - start.time) / MILLIS_PER_DAY

    val weeksTotal = max(1, ceil(totalDays / 7.0).toInt())
    val weeksElapsed = min(weeksTotal, max(0, ceil(elapsedDays / 7.0).toInt()))

    return RotationProgress(weeksElapsed, weeksTotal)
}
