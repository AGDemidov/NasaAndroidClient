package com.agdemidov.nasaclient.services

import com.agdemidov.nasaclient.repositories.*

object ErrorResponseProcessor {
    fun <T> processError(apiResponse: ErrorResponse<T>): Nothing = when (apiResponse) {
        is HttpErrorResponse ->
            throw HttpServiceException(apiResponse.errorCode, apiResponse.message)
        is ApiErrorResponse ->
            throw GeneralServiceException(apiResponse.title, apiResponse.errorMessage)
        is NoNetworkResponse ->
            throw NoNetworkServiceException
        is CancelledResponse ->
            throw CancellationServiceException
    }
}
