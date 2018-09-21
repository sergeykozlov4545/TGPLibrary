package com.example.sergey.tgplibrary.telegram.passport

import org.json.JSONObject


class PassportScopeElementOne(
        val type: String,
        private val selfie: Boolean = false,
        private val translation: Boolean = false,
        private val nativeNames: Boolean = false
) : PassportScopeElement {

    override fun toJson(): Any? {
        if (!selfie && !translation && !nativeNames) {
            return type
        }

        return JSONObject().apply {
            put(TYPE_KEY, type)
            if (selfie) put(SELFIE_KEY, true)
            if (translation) put(TRANSLATION_KEY, true)
            if (nativeNames) put(NATIVE_NAMES_KEY, true)
        }
    }

    override fun validate() {
        if (nativeNames && type != PassportScope.PERSONAL_DETAILS) {
            throw ScopeValidationException("nativeNames can only be used with personal_details")
        }

        if (selfie && type in arrayOf(
                        PassportScope.UTILITY_BILL,
                        PassportScope.BANK_STATEMENT,
                        PassportScope.RENTAL_AGREEMENT,
                        PassportScope.PASSPORT_REGISTRATION,
                        PassportScope.TEMPORARY_REGISTRATION)) {
            throw ScopeValidationException("selfie can only be used with identity documents")
        }

        if (type in arrayOf(
                        PassportScope.PHONE_NUMBER,
                        PassportScope.EMAIL,
                        PassportScope.ADDRESS,
                        PassportScope.PERSONAL_DETAILS)) {
            if (selfie) {
                throw ScopeValidationException("selfie can only be used with identity documents")
            }
            if (translation) {
                throw ScopeValidationException("translation can only be used with documents")
            }
            if (nativeNames && type != PassportScope.PERSONAL_DETAILS) {
                throw ScopeValidationException("nativeNames can only be used with personal_details")
            }
        }
    }

    companion object {
        private const val TYPE_KEY = "type"
        private const val SELFIE_KEY = "selfie"
        private const val TRANSLATION_KEY = "translation"
        private const val NATIVE_NAMES_KEY = "native_names"
    }
}