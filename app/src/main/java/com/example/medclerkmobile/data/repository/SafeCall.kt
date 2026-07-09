package com.example.medclerkmobile.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

@Serializable
private data class ApiErrorResponse(
    val message: String? = null,
    val errors: Map<String, List<String>>? = null,
)

private val errorJson = Json { ignoreUnknownKeys = true }

suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    Result.failure(Exception(e.friendlyMessage()))
} catch (e: IOException) {
    Result.failure(Exception("Couldn't reach the server. Check your connection and try again."))
} catch (e: SerializationException) {
    Result.failure(Exception("The server sent back something unexpected. Please try again."))
}

private fun HttpException.friendlyMessage(): String {
    val body = response()?.errorBody()?.string()
    val parsed = body?.let { runCatching { errorJson.decodeFromString<ApiErrorResponse>(it) }.getOrNull() }

    return parsed?.errors?.values?.firstOrNull()?.firstOrNull()
        ?: parsed?.message
        ?: "Request failed (${code()})"
}
