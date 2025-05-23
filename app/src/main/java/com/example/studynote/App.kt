package com.example.studynote

import android.app.Application
import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var prefs: android.content.SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化 SharedPreferences，用于存储和读取 token
        prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)

        // 日志拦截器，打印请求和响应的完整信息
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 身份验证拦截器：如果 SharedPreferences 中有 token，就自动添加到请求头
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = prefs.getString("token", null)
            val request = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        }

        // 构建 OkHttpClient，添加拦截器
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(authInterceptor)
            .build()

        // 初始化 Retrofit
        retrofit = Retrofit.Builder()
            // 在模拟器中访问本机后端：10.0.2.2
            .baseUrl("http://10.0.2.2:8080/api/")
            .client(client)
            // 使用 Gson 进行 JSON 转换
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
