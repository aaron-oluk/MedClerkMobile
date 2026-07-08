package com.example.medclerkmobile.data.remote

import com.example.medclerkmobile.data.model.Assessment
import com.example.medclerkmobile.data.model.Feedback
import com.example.medclerkmobile.data.model.LoginRequest
import com.example.medclerkmobile.data.model.LoginResponse
import com.example.medclerkmobile.data.model.LogbookEntry
import com.example.medclerkmobile.data.model.NamedRef
import com.example.medclerkmobile.data.model.NewLogbookEntry
import com.example.medclerkmobile.data.model.PaginatedResponse
import com.example.medclerkmobile.data.model.Rotation
import com.example.medclerkmobile.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
}
