package com.example.csci571hw4.network.model

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val message: String? = null
)
