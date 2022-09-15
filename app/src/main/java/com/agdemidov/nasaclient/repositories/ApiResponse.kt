package com.agdemidov.nasaclient.repositories

import retrofit2.Response
import java.util.concurrent.CancellationException

class SuccessResponse<T>(val body: T) : ApiResponse<T>()
class HttpErrorResponse<T>(val errorCode: Int, val message: String) : ErrorResponse<T>()
class ApiErrorResponse<T>(val title: String, val errorMessage: String) : ErrorResponse<T>()
class NoNetworkResponse<T> : ErrorResponse<T>()
class CancelledResponse<T> : ErrorResponse<T>()

sealed class ErrorResponse<T> : ApiResponse<T>()
sealed class ApiResponse<T> {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> createResponse(response: Response<T>): ApiResponse<T> {
            return when (val code = response.code()) {
                in 200..299 -> {
                    SuccessResponse(
                        response.body() as T
                    )
                }
                else -> {
                    HttpErrorResponse(
                        code, "Http error : ${response.message()}"
                    )
                }
            }
        }

        fun <T> createError(t: Throwable): ApiResponse<T> {
            if (t is CancellationException) {
                return CancelledResponse()
            }
            return ApiErrorResponse("General failure", t.localizedMessage ?: t.toString())
        }
    }
}
