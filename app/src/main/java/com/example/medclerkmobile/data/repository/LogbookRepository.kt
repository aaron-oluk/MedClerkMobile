package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.data.model.NamedRef
import com.example.medclerkmobile.data.model.NewLogbookEntry
import com.example.medclerkmobile.data.remote.ApiService

class LogbookRepository(private val api: ApiService) {
    suspend fun myEntries(): Result<List<LogbookEntry>> = safeCall { api.logbookEntries().data }

    suspend fun pendingAssessments(): Result<List<LogbookEntry>> = safeCall { api.pendingLogbookEntries().data }

    suspend fun entry(id: Int): Result<LogbookEntry> = safeCall { api.logbookEntry(id) }

    suspend fun createEntry(entry: NewLogbookEntry): Result<LogbookEntry> = safeCall { api.createLogbookEntry(entry) }

    suspend fun clinicalSigns(): Result<List<NamedRef>> = safeCall { api.clinicalSigns().data }

    suspend fun skills(): Result<List<NamedRef>> = safeCall { api.skills().data }
}
