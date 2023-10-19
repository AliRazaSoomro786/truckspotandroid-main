package com.truckspot.models

data class LoginResponse(
    val code: Int,
    val message: String,
    val results: Results,
    val status: Boolean
)
data class Results(
    val email: String,
    val token: String,
    val username: String
)

