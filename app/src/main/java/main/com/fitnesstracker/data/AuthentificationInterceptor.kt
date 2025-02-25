package main.com.fitnesstracker.data

import okhttp3.Interceptor
import okhttp3.Response

    class AuthentificationInterceptor(val authToken:String) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val builder = original.newBuilder().header("Authorization", authToken)
            val request = builder.build()

            return chain.proceed(request)
        }
    }