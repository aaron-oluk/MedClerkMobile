package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.Feedback
import com.example.medclerkmobile.data.model.NewFeedback
import com.example.medclerkmobile.data.remote.ApiService

class FeedbackRepository(private val api: ApiService) {
    suspend fun myFeedback(): Result<List<Feedback>> = safeCall { api.feedback().data }

    suspend fun createFeedback(feedback: NewFeedback): Result<Feedback> = safeCall { api.createFeedback(feedback) }
}
