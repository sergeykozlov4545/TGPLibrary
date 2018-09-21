package com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo

import com.google.gson.annotations.SerializedName

data class Credentials(
        @SerializedName("secure_data") val secureData: SecureData,
        @SerializedName("nonce") val nonce: String
)