package com.example.studynote.data.repository

import com.example.studynote.App
import com.example.studynote.data.network.AuthService
import com.example.studynote.data.network.dto.LoginRequest
import com.example.studynote.data.network.dto.LoginResponse
import com.example.studynote.data.network.dto.RegisterRequest
import com.example.studynote.data.network.dto.UserDto

class AuthRepository {

    private val service = App.retrofit.create(AuthService::class.java)
    private val prefs = App.prefs

    /** 注册新用户 */
    suspend fun register(username: String, email: String, password: String) {
        val req = RegisterRequest(username, email, password)
        service.register(req)
    }

    /** 登录，成功后保存 Token */
    suspend fun login(usernameOrEmail: String, password: String): Boolean {
        val resp: LoginResponse = service.login(LoginRequest(usernameOrEmail, password))
        val token = resp.token
        // 保存到 SharedPreferences
        prefs.edit().putString("token", token).apply()
        return true
    }

    /** 获取当前登录用户信息 */
    suspend fun me(): UserDto {
        val token = prefs.getString("token", null)
            ?: throw Exception("未登录或token为空")
        return service.me("Bearer $token")
    }

    /** 清除本地 Token */
    fun logout() {
        prefs.edit().remove("token").apply()
    }
}
