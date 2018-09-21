package com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo

import android.util.Log
import com.example.sergey.tgplibrary.telegram.passport.PassportScope
import com.google.gson.annotations.SerializedName

data class PersonalDetails(
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("first_name_native") val firstNameNative: String,
        @SerializedName("last_name_native") val lastNameNative: String,
        @SerializedName("gender") val gender: String
) : DecryptedData {
    fun log(tag: String) {
        Log.e(tag, "it's ${PassportScope.PERSONAL_DETAILS}")
        Log.e(tag, "first name: $firstName")
        Log.e(tag, "last name: $lastName")
        Log.e(tag, "first name (Native): $firstNameNative")
        Log.e(tag, "last name (Native): $lastNameNative")
        Log.e(tag, "gender: $gender")
        Log.e(tag, "--------------------------------------------------------")
    }
}