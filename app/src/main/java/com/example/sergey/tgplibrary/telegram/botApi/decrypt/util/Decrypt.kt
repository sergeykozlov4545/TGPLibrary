package com.example.sergey.tgplibrary.telegram.botApi.decrypt.util

import android.util.Base64
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.Credentials
import com.google.gson.Gson


fun decryptCredentials(privateKey: String, data: String, hash: String, secret: String): Credentials {
    val secretHash = SecretHash(
            secret = RsaOaep.decrypt(privateKey, base64(secret)),
            hash = base64(hash)
    )
    val decryptedData = String(decryptAes256Cbc(secretHash, base64(data)))
    return Gson().fromJson(decryptedData, Credentials::class.java)

}

fun decryptData(data: String, dataHash: String, secret: String) =
        String(decryptFile(base64(data), dataHash, secret))

fun decryptFile(data: ByteArray, fileHash: String, secret: String) = decryptAes256Cbc(
        secretHash = SecretHash(base64(secret), base64(fileHash)),
        data = data
)

private fun decryptAes256Cbc(secretHash: SecretHash, data: ByteArray): ByteArray {
    val decryptedData = Aes256Cbc(secretHash.key, secretHash.iv).decrypt(data)
    val padding = decryptedData[0].toInt() and 0xFF
    return decryptedData.copyOfRange(padding, decryptedData.size)
}

private fun base64(value: String) = Base64.decode(value, 0)