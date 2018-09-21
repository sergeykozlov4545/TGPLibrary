package com.example.sergey.tgplibrary.telegram.botApi.decrypt.util

import java.security.MessageDigest

class SecretHash(secret: ByteArray, hash: ByteArray) {
    private val secretHash = sha512(secret + hash)
    val key = secretHash.copyOfRange(0, 32)
    val iv = secretHash.copyOfRange(32, 48)

    private fun sha512(data: ByteArray) =
            MessageDigest.getInstance("SHA-512").digest(data)
}