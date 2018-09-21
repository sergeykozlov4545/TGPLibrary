package com.example.sergey.tgplibrary.telegram.botApi.repository

import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.DecryptedData
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.Email
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.PhoneNumber
import com.example.sergey.tgplibrary.telegram.botApi.remote.Response
import com.example.sergey.tgplibrary.telegram.botApi.remote.ServiceApi
import com.example.sergey.tgplibrary.telegram.passport.PassportScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

interface PassportRepository {
    fun getDecryptedData(payload: String): Deferred<Response<DecryptedData>>
}

class PassportRepositoryImpl(
        private val serviceApi: ServiceApi
) : PassportRepository {

    private val mutex = Mutex()

    override fun getDecryptedData(payload: String): Deferred<Response<DecryptedData>> = async {
        mutex.withLock {
            val updateResponse = serviceApi.getUpdates().await()

            val updates = updateResponse.result
                    ?: return@withLock Response<DecryptedData>(success = false)

            val update = updates.find { update ->
                val passportData = update.message?.passportData ?: return@find false

                val credentials = passportData.credentials.decrypt(PRIMARY_KEY)
                return@find credentials.nonce == payload
            } ?: return@withLock Response<DecryptedData>(success = false)

            val credentials = update.message!!.passportData!!.credentials.decrypt(PRIMARY_KEY)

            val decryptedData: MutableList<DecryptedData> = ArrayList()
            update.message.passportData!!.data.forEach { element ->
                when (element.type) {
                    PassportScope.PERSONAL_DETAILS, PassportScope.ADDRESS -> {
                        val decryptedElement = element.decryptElementData(credentials)
                                ?: return@forEach
                        decryptedData.add(decryptedElement)
                    }
                    PassportScope.PHONE_NUMBER -> {
                        element.phoneNumber?.let {
                            decryptedData.add(PhoneNumber(element.phoneNumber))
                        }
                    }
                    PassportScope.EMAIL -> {
                        element.email?.let {
                            decryptedData.add(Email(element.email))
                        }
                    }
                    else -> {
                    }
                }
            }

            return@withLock Response(success = true, result = decryptedData)
        }
    }

    companion object {
        private const val PRIMARY_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEAsI+fkdgEyufLlU2Zeep5MIbAHasFbeHcZB+uwQ4YX10NUVjW\n" +
                "2eqcw8WJJgoCs/ee3kCoXfxb/iRyUv635baq4eqTuCXLW6V3S4/nDp+/c2kdpgn/\n" +
                "2ibGwharaeGAb6qrdqw7XZGP2rvskMTxE+pJmXUmb2WrauULfuGXkGhKNkX36ayJ\n" +
                "WJqnbFrFLODy/fRK4KOVtMv/vjWdQoyKAMhmjnaY4V5yi7P73/4iV9oLdBZDwAzA\n" +
                "l8G1krBgOnuNFyGaKKy16BxxmhobYTG0OZIhzigJFANrRlvvY3XmZIADNoPcNI77\n" +
                "Cw2wDa2NpzU3GEHDY6BQtjwjwLfswDFwX2ElLwIDAQABAoIBAA5QRm5rZdARRJbh\n" +
                "CJ0yngqKEg8Vq0YCNnzDeMZb3yvz/j1nB1v5QiY44Gp+C0QNwgpgkYW1DLwotj/2\n" +
                "OHBeA7J28uzN25OdcXNlwiSUXM/9aJLqYpyL4jcf4VufFWt4GZhwVAu83j2sWHxQ\n" +
                "Cv9DUh4nuirzwhbTZMAvh7sESWfm17CZ2a5upyAHWqmqtqxHJVsS6FWW5iHWCruU\n" +
                "HJnZu4+as7HRAPzxxtLyLQI5VBkcThuNwK7aKXEfgOaPtyI9AqIYvUTjlGOuD4Yy\n" +
                "cB5y1xG5yqVW2eIag9Fq2qT/BZqBx+6dy31s49T4xlPudegejoc/13s0O+XFdoCQ\n" +
                "sVtIjaECgYEA4POKEh1s2oM+blkfOdyUOopEF6Miv1sN1tGNDbO9f5kgWnHJoCTQ\n" +
                "ZK04ymKeU0BzhA8ZD+C+EG3+sQwqglRg3Mj1d230YmE0+pCAaiTHIprbu4DN91yn\n" +
                "T1PUF0lOt5fyehPnsObnCZEElVLCWAvQTc0Dodr+gNZwX7YKHqDwFjECgYEAyO5B\n" +
                "MX7oufEJtPokGRCFCdJu8JPj8+Jmb0udbE5Hf1jfKuWAWm1PDSpWt0tSQQWyfo28\n" +
                "gjBAfKfBochg3GkHJG8FMAo7qCUR4YmflzqSKdDROVTiIegMUscVTWclac4bT+Uc\n" +
                "kEk772VC/7dU9P4P54ECBr3EHLkptkiXG/jYOV8CgYEAlK7D4sdKLH+04xDK/96Y\n" +
                "pry+1vS/wcT4N7WXyqezp8PZS46MGupaR0DrGXHnfdVKxM2J3iHI3mklf3YwHqaQ\n" +
                "wz2caQznC6N+deLOzdlzOZ8rfxpaiJXYDCm3NJECk3y+CwxfBmEH8h3E7sGdrL5a\n" +
                "NyI7tV5e+19BNTHILIhpR/ECgYA9xaGdSdrab1QAo3Y9jmqYHm2k4JKQA+ZiCOkY\n" +
                "xugMKpJRacUQB9LpxT5rk2hyPQInDgQKMjNhH2HUDvpYSKG9fbQmlL7KbrsUj27U\n" +
                "21jcKipoacQrkF9Zg4L1DOTfplGXOmSRpzIZ4xO58e7YctMI5QxubiDAg9xjAeYS\n" +
                "32n6GQKBgHRHNDvB9jiIkL13cWJ9xaSg5F9BbYFMcYcf4VVMYOZfQveKSlkMV1oW\n" +
                "E99iDcRfP9G6k93Lw+3fOMntIfIW1BKMAgrqts9QIUX9a/tDmSXrbyiMc/lFHfUN\n" +
                "r9xpGbZw3yuyNKAuyRAm21Rl5lcs+uh+wX2AneGWhFIVWtTWXr9G\n" +
                "-----END RSA PRIVATE KEY-----"
    }
}

