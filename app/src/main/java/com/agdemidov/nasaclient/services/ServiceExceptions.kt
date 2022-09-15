package com.agdemidov.nasaclient.services

class HttpServiceException(val errorCode: Int, val errorMessage: String) : Exception()

class GeneralServiceException(val title: String, val errorMessage: String) : Exception()

class DataConvertServiceException(val className: String, val errorMessage: String) : Exception()

object CancellationServiceException : Exception()

object NoNetworkServiceException : Exception()
