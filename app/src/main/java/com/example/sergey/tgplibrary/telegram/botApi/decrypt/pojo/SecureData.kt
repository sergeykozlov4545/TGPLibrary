package com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo

import com.example.sergey.tgplibrary.telegram.passport.PassportScope
import com.google.gson.annotations.SerializedName

data class SecureData(
        @SerializedName("personal_details") val personalDetails: SecureValue? = null,
        @SerializedName("address") val address: SecureValue? = null
) {
    fun getSecureValue(type: String) = when (type) {
        PassportScope.PERSONAL_DETAILS -> personalDetails
        PassportScope.ADDRESS -> address
        else -> null
    }
}