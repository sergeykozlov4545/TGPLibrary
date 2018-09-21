package com.example.sergey.tgplibrary.telegram.botApi.pojo

import com.google.gson.annotations.SerializedName

data class PassportData(
        @SerializedName("data") val data: List<EncryptedPassportElement>,
        @SerializedName("credentials") val credentials: EncryptedCredentials
)