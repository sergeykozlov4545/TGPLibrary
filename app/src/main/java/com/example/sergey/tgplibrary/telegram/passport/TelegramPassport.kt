package com.example.sergey.tgplibrary.telegram.passport

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.sergey.tgplibrary.R

object TelegramPassport {
    const val RESULT_OK = Activity.RESULT_OK
    const val RESULT_CANCELED = Activity.RESULT_CANCELED
    const val RESULT_ERROR = Activity.RESULT_FIRST_USER

    fun request(activity: Activity, params: AuthParams, requestCode: Int) {
        val intent = params.toIntent()
        intent.resolveActivity(activity.packageManager) ?: run {
            showAppInstallAlert(activity)
            return@request
        }
        activity.startActivityForResult(intent, requestCode)
    }


    @SuppressLint("InflateParams")
    private fun showAppInstallAlert(activity: Activity) {
        val dialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_telegram_install, null)

        val appName = try {
            val applicationInfo = activity.packageManager.getApplicationInfo(activity.packageName, 0)
            val applicationLabel = activity.packageManager.getApplicationLabel(applicationInfo)

            applicationLabel.toString().replace("<", "&lt;")
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        dialogView.findViewById<TextView>(R.id.messageView).apply {
            val str = activity.getString(R.string.TelegramInstallDialogMessage, appName)
            text = Html.fromHtml(str.replace("\\*\\*([^*]+)\\*\\*", "<b>$1</b>"))
        }

        val dialog = AlertDialog.Builder(activity, R.style.Theme_Telegram_Alert)
                .setView(dialogView)
                .setPositiveButton(R.string.OpenGooglePlay) { _, _ ->
                    val uri = Uri.parse("market://details?id=org.telegram.messenger")
                    activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                .setNegativeButton(R.string.Cancel, null)
                .show()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val titleDividerId = activity.resources
                    .getIdentifier("titleDivider", "id", "android")
            dialog.findViewById<View>(titleDividerId)?.visibility = View.GONE
        }
    }
}