package com.example.sergey.tgplibrary.telegram.passport

import android.content.Intent
import android.util.Base64
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

data class AuthParams(
        val botID: Int,
        val scope: PassportScope,
        val publicKey: String,
        val nonce: String
) {

    fun toIntent(): Intent {
        validate()

        return Intent(ACTION_AUTHORIZE).apply {
            putExtra(BOT_ID, botID)
            putExtra(SCOPE, scope.toJson().toString())
            putExtra(PUBLIC_KEY, publicKey)
            putExtra(PAYLOAD, "nonce")
            putExtra(NONCE, nonce)
        }
    }

    private fun validate() {
        if (botID <= 0) {
            throw IllegalArgumentException("botID must be >= 0")
        }
        if (scope.elements.isEmpty()) {
            throw IllegalArgumentException("Нужно выбрать хотя бы один пункт")
        }
        try {
            scope.validate()
        } catch (e: ScopeValidationException) {
            throw IllegalArgumentException("scope is invalid", e)
        }
        try {
            val cleanedPublicKey = publicKey.replace("\\n", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
            val spec = X509EncodedKeySpec(Base64.decode(cleanedPublicKey, 0))


            KeyFactory.getInstance("RSA").generatePublic(spec)
        } catch (e: Exception) {
            throw IllegalArgumentException("publicKey has invalid format", e)
        }
        if (nonce.isEmpty()) {
            throw IllegalArgumentException("nonce must not be empty")
        }
    }

    class Builder(
            private val botID: Int,
            private val publicKey: String,
            private val nonce: String
    ) {

        private val scopes: MutableList<PassportScopeElement> = ArrayList()

        fun addScope(scope: String): Builder {
            return addScope(PassportScopeElementOne(scope))
        }

        fun addScope(scope: PassportScopeElement): Builder {
            scopes.add(scope)
            return this
        }

        fun build(): AuthParams {
            return AuthParams(
                    botID = botID,
                    publicKey = publicKey,
                    nonce = nonce,
                    scope = PassportScope(scopes)
            )
        }
    }

    companion object {
        private const val ACTION_AUTHORIZE = "org.telegram.passport.AUTHORIZE"
        private const val BOT_ID = "bot_id"
        private const val SCOPE = "scope"
        private const val PUBLIC_KEY = "public_key"
        private const val PAYLOAD = "payload"
        private const val NONCE = "nonce"
    }
}