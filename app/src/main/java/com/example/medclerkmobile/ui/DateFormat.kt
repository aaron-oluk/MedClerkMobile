package com.example.medclerkmobile.ui

/**
 * The API serializes date/datetime columns as full ISO-8601 strings (e.g.
 * "2026-05-01T00:00:00.000000Z") even for date-only fields, since that's Carbon's
 * default JSON representation. Trim to the yyyy-MM-dd portion for display.
 */
fun formatApiDate(raw: String): String = raw.substringBefore("T")
