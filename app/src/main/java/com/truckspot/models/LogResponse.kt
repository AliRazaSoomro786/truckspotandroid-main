package com.truckspot.models

data class LogResponse(
    val code: Int,
    val message: String,
    val results: String,
    val status: Boolean
)

