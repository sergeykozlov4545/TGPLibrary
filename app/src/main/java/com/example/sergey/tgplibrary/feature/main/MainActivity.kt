package com.example.sergey.tgplibrary.feature.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.sergey.tgplibrary.R
import com.example.sergey.tgplibrary.extansion.toast
import com.example.sergey.tgplibrary.telegram.botApi.decrypt.pojo.*
import com.example.sergey.tgplibrary.telegram.botApi.remote.ServiceApiFactory
import com.example.sergey.tgplibrary.telegram.botApi.repository.PassportRepositoryImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_address.*
import kotlinx.android.synthetic.main.activity_main_contact_data.*
import kotlinx.android.synthetic.main.activity_main_personal_details.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    private lateinit var payload: String

    private var job: Job? = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.findViewById<TextView>(R.id.title_toolbar).apply {
            text = getString(R.string.mainActivityTitle)
        }
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }

        payload = savedInstanceState?.getString(PAYLOAD_EXTRA)
                ?: intent.getStringExtra(PAYLOAD_EXTRA)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(PAYLOAD_EXTRA, payload)
        }
    }

    private fun loadData() {
        progressView.visibility = View.VISIBLE
        dataContainer.visibility = View.GONE

        val serviceApi = ServiceApiFactory.create()
        val passportRepository = PassportRepositoryImpl(serviceApi)

        launch(UI, parent = job) {
            try {
                val decryptedDataResponse = passportRepository.getDecryptedData(payload).await()

                if (decryptedDataResponse.success) {
                    showData(decryptedDataResponse.result!!)
                    return@launch
                }

                progressView.visibility = View.GONE
                decryptedDataResponse.description?.let {
                    toast("is not success: ${decryptedDataResponse.description}")
                }
            } catch (e: Exception) {
                progressView.visibility = View.GONE
                toast("exception: ${e.localizedMessage}")
                Log.e(TAG, "exception", e)
            }
        }
    }

    private fun showData(data: List<DecryptedData>) {
        resetView()
        data.forEach { value ->
            when (value) {
                is PersonalDetails -> showPersonalDetails(value)
                is ResidentialAddress -> showAddress(value)
                is PhoneNumber -> showPhoneNumber(value)
                is Email -> showEmail(value)
                else -> {
                }
            }
        }
        progressView.visibility = View.GONE
        dataContainer.visibility = View.VISIBLE
    }

    private fun resetView() {
        firstName.text = "-"
        lastName.text = "-"
        firstNameNative.text = "-"
        lastNameNative.text = "-"
        gender.text = "-"
        firstStreetLine.text = "-"
        secondStreetLine.text = "-"
        city.text = "-"
        state.text = "-"
        postCode.text = "-"
        countryCode.text = "-"
        phoneNumber.text = "-"
        email.text = "-"
    }

    private fun showPersonalDetails(personalDetails: PersonalDetails?) {
        firstName.text = personalDetails?.firstName ?: "-"
        lastName.text = personalDetails?.lastName ?: "-"
        firstNameNative.text = personalDetails?.firstNameNative ?: "-"
        lastNameNative.text = personalDetails?.lastNameNative ?: "-"
        gender.text = when (personalDetails?.gender) {
            "male" -> getString(R.string.male)
            "female" -> getString(R.string.female)
            else -> "-"
        }
    }

    private fun showAddress(address: ResidentialAddress?) {
        firstStreetLine.text = address?.firstStreetLine ?: "-"
        secondStreetLine.text = address?.secondStreetLine ?: "-"
        city.text = address?.city ?: "-"
        state.text = address?.state ?: "-"
        postCode.text = address?.postCode ?: "-"
        countryCode.text = address?.countryCode ?: "-"
    }

    private fun showPhoneNumber(phoneNumberValue: PhoneNumber?) {
        phoneNumber.text = phoneNumberValue?.number ?: "-"
    }

    private fun showEmail(emailValue: Email?) {
        email.text = emailValue?.email ?: "-"
    }

    companion object {
        private const val TAG = "PassportData"
        private const val PAYLOAD_EXTRA = "payload"

        fun startForResult(activity: FragmentActivity, payload: String, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, MainActivity::class.java).apply {
                putExtra(PAYLOAD_EXTRA, payload)
            }, requestCode)
        }
    }
}
