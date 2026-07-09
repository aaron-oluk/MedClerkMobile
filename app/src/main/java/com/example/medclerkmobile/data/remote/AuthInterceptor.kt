package com.example.medclerkmobile.data.remote

import com.example.medclerkmobile.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Always ask for JSON, even on unauthenticated requests like login: without this
        // header Laravel treats the request as a browser form post and returns an HTML
        // redirect (with flashed session errors) instead of a JSON error body.
        val builder = original.newBuilder().header("Accept", "application/json")
        tokenStore.token?.let { builder.header("Authorization", "Bearer $it") }

        return chain.proceed(builder.build())
    }
}

class SessionExpiredInterceptor(private val onSessionExpired: () -> Unit) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            onSessionExpired()
        }
        return response
    }
}
