package com.example.sergey.tgplibrary.telegram.botApi.remote

import com.example.sergey.tgplibrary.telegram.botApi.pojo.Update
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET

interface ServiceApi {
    @GET("getUpdates?allowed_updates=[passport_data]")
    fun getUpdates(): Deferred<Response<Update>>
}