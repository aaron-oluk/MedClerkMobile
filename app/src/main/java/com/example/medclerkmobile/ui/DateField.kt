package com.example.medclerkmobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A read-only text field that opens a Material3 date picker and reports the
 * selected date back as a "yyyy-MM-dd" string (the API's date format).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = ymdToMillis(value)?.let { millisToDisplay(it) } ?: value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
        modifier = modifier.clickable { showPicker = true },
    )

    if (showPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = ymdToMillis(value))
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onValueChange(millisToYmd(it)) }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun dateFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

private fun displayFormat(): SimpleDateFormat =
    SimpleDateFormat("d MMM yyyy", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

fun ymdToMillis(value: String): Long? = runCatching { dateFormat().parse(value)?.time }.getOrNull()

fun millisToYmd(millis: Long): String = dateFormat().format(Date(millis))

fun millisToDisplay(millis: Long): String = displayFormat().format(Date(millis))
