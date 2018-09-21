package com.example.sergey.tgplibrary.telegram.passport

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PassportScopeElementOneOfSeveral(
        private val elements: MutableList<PassportScopeElementOne> = ArrayList(),
        private val selfie: Boolean = false,
        private val translation: Boolean = false
) : PassportScopeElement {

    constructor(vararg types: String) : this() {
        types.forEach { elements.add(PassportScopeElementOne(it)) }
    }

    override fun toJson(): Any? {
        if (elements.isEmpty()) {
            return null
        }

        return try {
            JSONObject().apply {
                put(ONE_OF_KEY, JSONArray().apply { elements.forEach { put(it.toJson()) } })
                if (selfie) put(SELFIE_KEY, true)
                if (translation) put(TRANSLATION_KEY, true)
            }
        } catch (ignore: JSONException) {
            null
        }
    }

    override fun validate() {
        var type = 0
        elements.forEach { element ->
            element.validate()
            if (element.type in arrayOf(
                            PassportScope.PERSONAL_DETAILS,
                            PassportScope.PHONE_NUMBER,
                            PassportScope.EMAIL,
                            PassportScope.ADDRESS)) {
                throw ScopeValidationException("one_of can only be used with documents")
            }
            if (element.type in arrayOf(
                            PassportScope.ID_DOCUMENT, PassportScope.ADDRESS_DOCUMENT)) {
                throw ScopeValidationException("one_of can only be used when exact document" +
                        " types are specified")
            }

            var docType = 0
            if (element.type in arrayOf(
                            PassportScope.PASSPORT,
                            PassportScope.IDENTITY_CARD,
                            PassportScope.INTERNAL_PASSPORT,
                            PassportScope.DRIVER_LICENSE)) {
                docType = 1
            } else if (element.type in arrayOf(
                            PassportScope.UTILITY_BILL,
                            PassportScope.BANK_STATEMENT,
                            PassportScope.RENTAL_AGREEMENT,
                            PassportScope.PASSPORT_REGISTRATION,
                            PassportScope.TEMPORARY_REGISTRATION)) {
                docType = 2
            }
            if (type == 0) {
                type = docType
            }
            if (docType != 0 && type != docType) {
                throw ScopeValidationException("One PassportScopeElementOneOfSeveral object can " +
                        "only contain documents of one type")
            }
        }
    }

    companion object {
        private const val ONE_OF_KEY = "one_of"
        private const val SELFIE_KEY = "selfie"
        private const val TRANSLATION_KEY = "translation"
    }
}