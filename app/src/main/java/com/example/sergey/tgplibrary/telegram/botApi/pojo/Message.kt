package com.example.sergey.tgplibrary.telegram.botApi.pojo

import com.google.gson.annotations.SerializedName

data class Message(
        @SerializedName("message_id") val id: Int,
        @SerializedName("passport_data") val passportData: PassportData? = null
)