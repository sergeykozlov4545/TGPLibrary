package com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo

import android.util.Log
import com.example.sergey.tgplibrary.telegram.passport.PassportScope
import com.google.gson.annotations.SerializedName

data class ResidentialAddress(
        @SerializedName("street_line1") val firstStreetLine: String,
        @SerializedName("street_line2") val secondStreetLine: String? = null,
        @SerializedName("city") val city: String,
        @SerializedName("state") val state: String? = null,
        @SerializedName("post_code") val postCode: String,
        @SerializedName("country_code") val countryCode: String
) : DecryptedData {
    fun log(tag: String) {
        Log.e(tag, "it's ${PassportScope.ADDRESS}")
        Log.e(tag, "street_line1: $firstStreetLine")
        Log.e(tag, "street_line2: $secondStreetLine")
        Log.e(tag, "city: $city")
        Log.e(tag, "state: $state")
        Log.e(tag, "postCode: $postCode")
        Log.e(tag, "countryCode: $countryCode")
        Log.e(tag, "--------------------------------------------------------")
    }
}