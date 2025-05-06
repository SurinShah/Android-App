package com.example.csci571hw4.network.model

data class RegisterRequest(
    val fullname: String,
    val email: String,
    val password: String
)
