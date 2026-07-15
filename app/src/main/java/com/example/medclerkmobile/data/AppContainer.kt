package com.example.medclerkmobile.data

import android.content.Context
import com.example.medclerkmobile.BuildConfig
import com.example.medclerkmobile.data.local.TokenStore
import com.example.medclerkmobile.data.remote.ApiService
import com.example.medclerkmobile.data.remote.AuthInterceptor
import com.example.medclerkmobile.data.remote.SessionExpiredInterceptor
import com.example.medclerkmobile.data.repository.AssessmentRepository
import com.example.medclerkmobile.data.repository.AuthRepository
import com.example.medclerkmobile.data.repository.FeedbackRepository
import com.example.medclerkmobile.data.repository.LibraryRepository
import com.example.medclerkmobile.data.repository.LogbookRepository
import com.example.medclerkmobile.data.repository.RotationRepository
import com.example.medclerkmobile.data.repository.SettingsRepository
import com.example.medclerkmobile.data.repository.StudentLookupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Small hand-rolled service locator. The app has a handful of singletons (HTTP client,
 * token store, repositories), which doesn't justify pulling in a DI framework like Hilt.
 */
class AppContainer(context: Context) {
    private val tokenStore = TokenStore(context.applicationContext)

    private val _isLoggedIn = MutableStateFlow(tokenStore.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenStore))
        .addInterceptor(SessionExpiredInterceptor { onSessionExpired() })
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            },
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    val authRepository = AuthRepository(apiService, tokenStore) { loggedIn -> _isLoggedIn.value = loggedIn }
    val rotationRepository = RotationRepository(apiService)
    val logbookRepository = LogbookRepository(apiService)
    val assessmentRepository = AssessmentRepository(apiService)
    val feedbackRepository = FeedbackRepository(apiService)
    val libraryRepository = LibraryRepository(apiService)
    val settingsRepository = SettingsRepository(apiService)
    val studentLookupRepository = StudentLookupRepository(apiService)

    val currentUserName: String? get() = tokenStore.userName
    val currentUserRole: String? get() = tokenStore.userRole

    private fun onSessionExpired() {
        tokenStore.clear()
        _isLoggedIn.value = false
    }
}
