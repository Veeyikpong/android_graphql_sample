package com.example.rocketreserver

import android.content.Context
import com.example.rocketreserver.User
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", User.getToken(context) ?: "")
            .build()

        return chain.proceed(request)
    }
}