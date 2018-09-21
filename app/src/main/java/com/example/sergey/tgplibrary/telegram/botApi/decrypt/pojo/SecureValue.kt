package com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo

import com.google.gson.annotations.SerializedName

data class SecureValue(
       @SerializedName("data") val data: DataCredentials? = null
)