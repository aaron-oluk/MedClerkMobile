package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.data.remote.ApiService

class AssessmentRepository(private val api: ApiService) {
    suspend fun myAssessments(): Result<List<Assessment>> = safeCall { api.assessments().data }
}
