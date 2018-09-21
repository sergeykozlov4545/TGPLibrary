package com.example.sergey.tgplibrary.telegram.passport

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PassportScope(
        val elements: MutableList<PassportScopeElement> = ArrayList()
) : PassportScopeElement {

    override fun toJson() = try {
        JSONObject().apply {
            put(SCOPE_VERSION_KEY, 1)
            put(DATA_KEY, JSONArray().apply {
                elements.forEach { element ->
                    put(element.toJson() ?: return@forEach)
                }
            })
        }
    } catch (ignore: JSONException) {
        null
    }

    override fun validate() {
        for (element in elements) {
            element.validate()
        }
    }

    companion object {
        private const val SCOPE_VERSION_KEY = "v"
        private const val DATA_KEY = "data"

        const val PERSONAL_DETAILS = "personal_details"
        const val PASSPORT = "passport"
        const val INTERNAL_PASSPORT = "internal_passport"
        const val DRIVER_LICENSE = "driver_license"
        const val IDENTITY_CARD = "identity_card"
        const val ID_DOCUMENT = "id_document"
        const val ADDRESS = "address"
        const val UTILITY_BILL = "utility_bill"
        const val BANK_STATEMENT = "bank_statement"
        const val RENTAL_AGREEMENT = "rental_agreement"
        const val PASSPORT_REGISTRATION = "passport_registration"
        const val TEMPORARY_REGISTRATION = "temporary_registration"
        const val ADDRESS_DOCUMENT = "address_document"
        const val PHONE_NUMBER = "phone_number"
        const val EMAIL = "email"
    }
}