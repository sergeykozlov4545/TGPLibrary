package com.example.sergey.tgplibrary.telegram.botApi.remote

import com.example.sergey.tgplibrary.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceApiFactory {
    // TODO: 668466943:AAG6WXTQY1jxCUE25Ous37Ig-6upRRnEawg - token. Надо запихнуть чтоб было динамически
    private const val BASE_URL = "https://api.telegram.org/bot668466943:AAG6WXTQY1jxCUE25Ous37Ig-6upRRnEawg/"

    fun create(): ServiceApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()

        return retrofit.create(ServiceApi::class.java)
    }
}