package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.data.remote.ApiService

class RotationRepository(private val api: ApiService) {
    suspend fun myRotations(): Result<List<Rotation>> = safeCall { api.rotations().data }
}
