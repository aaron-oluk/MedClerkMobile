package com.example.medclerkmobile.data.remote

import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.data.model.ClinicalSign
import com.example.medclerkmobile.data.model.ClinicalSystem
import com.example.medclerkmobile.data.model.Feedback
import com.example.medclerkmobile.data.model.LoginRequest
import com.example.medclerkmobile.data.model.LoginResponse
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.data.model.NamedRef
import com.example.medclerkmobile.data.model.NewAssessment
import com.example.medclerkmobile.data.model.NewFeedback
import com.example.medclerkmobile.data.model.NewLogbookEntry
import com.example.medclerkmobile.data.model.PaginatedResponse
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.data.model.Skill
import com.example.medclerkmobile.data.model.UpdateSettingsRequest
import com.example.medclerkmobile.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout()

    @GET("user")
    suspend fun currentUser(): User

    @GET("rotations")
    suspend fun rotations(): PaginatedResponse<Rotation>

    @GET("logbook-entries")
    suspend fun logbookEntries(): PaginatedResponse<LogbookEntry>

    @GET("logbook-entries")
    suspend fun pendingLogbookEntries(@Query("needs_assessment") needsAssessment: Boolean = true): PaginatedResponse<LogbookEntry>

    @POST("logbook-entries")
    suspend fun createLogbookEntry(@Body entry: NewLogbookEntry): LogbookEntry

    @GET("assessments")
    suspend fun assessments(): PaginatedResponse<Assessment>

    @GET("feedback")
    suspend fun feedback(): PaginatedResponse<Feedback>

    @GET("clinical-signs")
    suspend fun clinicalSigns(): PaginatedResponse<NamedRef>

    @GET("skills")
    suspend fun skills(): PaginatedResponse<NamedRef>

    @GET("clinical-systems")
    suspend fun clinicalSystems(): PaginatedResponse<ClinicalSystem>

    @GET("clinical-systems/{id}")
    suspend fun clinicalSystem(@Path("id") id: Int): ClinicalSystem

    @GET("clinical-signs")
    suspend fun clinicalSignsFull(@Query("clinical_system_id") systemId: Int? = null): PaginatedResponse<ClinicalSign>

    @GET("clinical-signs/{id}")
    suspend fun clinicalSign(@Path("id") id: Int): ClinicalSign

    @GET("skills")
    suspend fun skillsFull(): PaginatedResponse<Skill>

    @GET("skills/{id}")
    suspend fun skill(@Path("id") id: Int): Skill

    @PATCH("settings")
    suspend fun updateSettings(@Body request: UpdateSettingsRequest): User

    @GET("students/search")
    suspend fun searchStudents(@Query("q") query: String? = null): PaginatedResponse<User>

    @GET("students/{id}")
    suspend fun student(@Path("id") id: Int): User

    @POST("assessments")
    suspend fun createAssessment(@Body request: NewAssessment): Assessment

    @POST("feedback")
    suspend fun createFeedback(@Body request: NewFeedback): Feedback
}
