package com.example.remotecompose.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

// Retrofit interface
// 回傳 ResponseBody 讓我們手動拿 ByteArray，不走 JSON converter
interface RemoteUiApi {
    @GET("ui/{version}")
    suspend fun getUiDocument(
        @Path("version") version: String
    ): ResponseBody
}

// Singleton，spike 用 object 就夠
object ApiClient {
    // 模擬器連本機 server 固定用 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    val api: RemoteUiApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
        .create(RemoteUiApi::class.java)
}
