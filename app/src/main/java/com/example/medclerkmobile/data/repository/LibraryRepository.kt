package com.example.medclerkmobile.data.repository

import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.data.model.Skill
import com.example.medclerkmobile.data.remote.ApiService

class LibraryRepository(private val api: ApiService) {
    suspend fun systems(): Result<List<ClinicalSystem>> = safeCall { api.clinicalSystems().data }

    suspend fun system(id: Int): Result<ClinicalSystem> = safeCall { api.clinicalSystem(id) }

    suspend fun signsBySystem(systemId: Int): Result<List<ClinicalSign>> =
        safeCall { api.clinicalSignsFull(systemId).data }

    suspend fun allSigns(): Result<List<ClinicalSign>> = safeCall { api.clinicalSignsFull().data }

    suspend fun sign(id: Int): Result<ClinicalSign> = safeCall { api.clinicalSign(id) }

    suspend fun skillsBySystem(systemId: Int): Result<List<Skill>> =
        safeCall { api.skillsFull().data.filter { it.clinicalSystem?.id == systemId } }

    suspend fun skill(id: Int): Result<Skill> = safeCall { api.skill(id) }
}
