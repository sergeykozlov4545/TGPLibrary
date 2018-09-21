package com.example.sergey.tgplibrary.feature.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.sergey.tgplibrary.feature.main.MainActivity
import com.example.sergey.tgplibrary.R
import com.example.sergey.tgplibrary.extansion.toast
import com.example.sergey.tgplibrary.telegram.passport.AuthParams
import com.example.sergey.tgplibrary.telegram.passport.PassportScope
import com.example.sergey.tgplibrary.telegram.passport.PassportScopeElementOne
import com.example.sergey.tgplibrary.telegram.passport.TelegramPassport
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.Exception
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var payload: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        payload = savedInstanceState?.getString(PAYLOAD_EXTRA) ?: UUID.randomUUID().toString()

        loginWithTelegramButton.setOnClickListener {
            val paramsBuilder = AuthParams.Builder(BOT_ID, PUBLIC_KEY, payload)

            if (personalDetailsSwitch.isChecked) {
                paramsBuilder.addScope(PassportScopeElementOne(
                        PassportScope.PERSONAL_DETAILS, nativeNames = true))
            }
            if (addressSwitch.isChecked) {
                paramsBuilder.addScope(PassportScope.ADDRESS)
            }
            if (phoneNumberSwitch.isChecked) {
                paramsBuilder.addScope(PassportScope.PHONE_NUMBER)
            }
            if (emailSwitch.isChecked) {
                paramsBuilder.addScope(PassportScope.EMAIL)
            }

            try {
                TelegramPassport.request(this@LoginActivity,
                        paramsBuilder.build(), TELEGRAM_PASSPORT_REQUEST_CODE)
            } catch (e: Exception) {
                toast(e.localizedMessage)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(PAYLOAD_EXTRA, payload)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TELEGRAM_PASSPORT_REQUEST_CODE) {
            when (resultCode) {
                TelegramPassport.RESULT_OK -> {
                    MainActivity.startForResult(this, payload, MAIN_ACTIVITY_REQUEST_CODE)
                }
                TelegramPassport.RESULT_ERROR -> {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(data?.getStringExtra("error"))
                            .setPositiveButton(R.string.Ok, null)
                            .show()
                }
                else -> {
                }
            }
            return
        }

        if (requestCode == MAIN_ACTIVITY_REQUEST_CODE) {
            payload = UUID.randomUUID().toString()
        }
    }

    companion object {
        private const val PAYLOAD_EXTRA = "payload"

        private const val BOT_ID = 668466943
        private const val PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsI+fkdgEyufLlU2Zeep5\n" +
                "MIbAHasFbeHcZB+uwQ4YX10NUVjW2eqcw8WJJgoCs/ee3kCoXfxb/iRyUv635baq\n" +
                "4eqTuCXLW6V3S4/nDp+/c2kdpgn/2ibGwharaeGAb6qrdqw7XZGP2rvskMTxE+pJ\n" +
                "mXUmb2WrauULfuGXkGhKNkX36ayJWJqnbFrFLODy/fRK4KOVtMv/vjWdQoyKAMhm\n" +
                "jnaY4V5yi7P73/4iV9oLdBZDwAzAl8G1krBgOnuNFyGaKKy16BxxmhobYTG0OZIh\n" +
                "zigJFANrRlvvY3XmZIADNoPcNI77Cw2wDa2NpzU3GEHDY6BQtjwjwLfswDFwX2El\n" +
                "LwIDAQAB\n" +
                "-----END PUBLIC KEY-----"
        private const val TELEGRAM_PASSPORT_REQUEST_CODE = 105
        private const val MAIN_ACTIVITY_REQUEST_CODE = 106
    }
}