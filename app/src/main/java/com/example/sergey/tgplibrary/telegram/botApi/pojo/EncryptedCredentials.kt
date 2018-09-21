package com.example.sergey.tgplibrary.telegram.botApi.pojo

import com.example.sergey.tgplibrary.telegram.botApi.decrypt.util.decryptCredentials
import com.google.gson.annotations.SerializedName

data class EncryptedCredentials(
        @SerializedName("data") val data: String,
        @SerializedName("hash") val hash: String,
        @SerializedName("secret") val secret: String
) {
    fun decrypt(privateKey: String) = decryptCredentials(privateKey, data, hash, secret)
}