package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.User
import com.example.medclerkmobile.data.remote.ApiService

class StudentLookupRepository(private val api: ApiService) {
    suspend fun search(query: String): Result<List<User>> = safeCall {
        api.searchStudents(query.ifBlank { null }).data
    }

    suspend fun show(studentId: Int): Result<User> = safeCall { api.student(studentId) }
}
