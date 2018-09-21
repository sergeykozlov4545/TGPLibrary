package com.example.sergey.tgplibrary.telegram.botApi.pojo

import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.Credentials
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.DecryptedData
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.PersonalDetails
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.ResidentialAddress
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.util.decryptData
import com.example.sergey.tgplibrary.telegram.passport.PassportScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class EncryptedPassportElement(
        @SerializedName("type") val type: String,
        @SerializedName("data") val data: String? = null,
        @SerializedName("phone_number") val phoneNumber: String? = null,
        @SerializedName("address") val address: String? = null,
        @SerializedName("email") val email: String? = null,
        @SerializedName("hash") val hash: String
) {
    private val dataClass: Class<out DecryptedData>?
        get() = when (type) {
            PassportScope.PERSONAL_DETAILS -> PersonalDetails::class.java
            PassportScope.ADDRESS -> ResidentialAddress::class.java
            else -> null
        }

    fun decryptElementData(credentials: Credentials): DecryptedData? {
        dataClass ?: return null
        data ?: return null

        val secureValue = credentials.secureData.getSecureValue(type) ?: return null
        val dataCredentials = secureValue.data ?: return null
        val decryptedData = decryptData(data, dataCredentials.dataHash, dataCredentials.secret)
        return Gson().fromJson(decryptedData, dataClass)
    }
}