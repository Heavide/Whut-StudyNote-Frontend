// AuthService.kt
package com.example.studynote.data.network

import com.example.studynote.data.network.dto.LoginRequest
import com.example.studynote.data.network.dto.LoginResponse
import com.example.studynote.data.network.dto.RegisterRequest
import com.example.studynote.data.network.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface AuthService {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest)

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("auth/me")
        suspend fun me(@Header("Authorization") token: String): UserDto

}
