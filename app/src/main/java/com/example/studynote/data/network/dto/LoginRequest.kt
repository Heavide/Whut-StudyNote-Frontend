// LoginRequest.kt
package com.example.studynote.data.network.dto

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
