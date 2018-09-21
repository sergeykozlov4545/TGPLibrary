package com.example.sergey.tgplibrary.telegram.botApi.pojo

import com.google.gson.annotations.SerializedName

data class Update(
        @SerializedName("update_id") val id: Long,
        @SerializedName("message") val message: Message? = null
)