package com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo

import com.google.gson.annotations.SerializedName

data class DataCredentials(
        @SerializedName("data_hash") val dataHash: String,
        @SerializedName("secret") val secret: String
)