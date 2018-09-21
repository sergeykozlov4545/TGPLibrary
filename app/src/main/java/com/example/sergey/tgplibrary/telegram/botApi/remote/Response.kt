package com.example.sergey.tgplibrary.telegram.botApi.remote

import com.google.gson.annotations.SerializedName

data class Response<T>(
        @SerializedName("ok") val success: Boolean,
        @SerializedName("error_code") val errorCode: Int? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("result") val result: List<T>? = null
)