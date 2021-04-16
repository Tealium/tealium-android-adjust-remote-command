package com.tealium.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.adjust.sdk.Adjust
import com.tealium.core.Tealium
import com.tealium.core.consent.ConsentStatus
import com.tealium.example.helper.DataLayer
import com.tealium.example.helper.TealiumHelper

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var eventButton: Button
    private lateinit var eventWithParamsButton: Button
    private lateinit var optInButton: Button
    private lateinit var optOutButton: Button
    private lateinit var disableSdkButton: Button
    private lateinit var setOfflineButton: Button

    private var isOffline = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        eventButton = view.findViewById(R.id.button_event)
        eventWithParamsButton = view.findViewById(R.id.button_event_with_params)
        optInButton = view.findViewById(R.id.button_opt_in)
        optOutButton = view.findViewById(R.id.button_opt_out)
        disableSdkButton = view.findViewById(R.id.button_disable_sdk)
        setOfflineButton = view.findViewById(R.id.button_offline_mode)

        eventButton.setOnClickListener(this)
        eventWithParamsButton.setOnClickListener(this)
        optInButton.setOnClickListener(this)
        optOutButton.setOnClickListener(this)
        disableSdkButton.setOnClickListener(this)
        setOfflineButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.id.let { id ->
            when (id) {
                R.id.button_event -> { trackEvent() }
                R.id.button_event_with_params -> { trackEventWithParams() }
                R.id.button_opt_in -> { optIn() }
                R.id.button_opt_out -> { optOut() }
                R.id.button_disable_sdk -> { toggleSdkEnabled() }
                R.id.button_offline_mode -> { setOffline() }
            }
        }
    }

    private fun trackEvent() {
        TealiumHelper.trackEvent("event",
            mapOf(
                DataLayer.EVENT_TOKEN to DataLayer.EventTokens.EVENT
            )
        )
    }

    private fun trackEventWithParams() {
        TealiumHelper.trackEvent("event",
            mapOf(
                DataLayer.EVENT_TOKEN to DataLayer.EventTokens.EVENT,
                DataLayer.EVENT_PARAM_1 to "value_1",
                DataLayer.PARTNER_PARAM_1 to "value_1"
            )
        )
    }

    private fun optIn() {
        Tealium[BuildConfig.TEALIUM_INSTANCE]?.consentManager?.userConsentStatus = ConsentStatus.CONSENTED
    }

    private fun optOut() {
        Tealium[BuildConfig.TEALIUM_INSTANCE]?.consentManager?.userConsentStatus = ConsentStatus.NOT_CONSENTED
    }

    private fun toggleSdkEnabled() {
        val wasEnabled = Adjust.isEnabled()

        TealiumHelper.trackEvent("disable",
            mapOf(
                DataLayer.ENABLED to !wasEnabled
            )
        )

        if (!wasEnabled) {
            disableSdkButton.text = "Disable SDK"
        } else {
            disableSdkButton.text = "Enable SDK"
        }
    }

    private fun setOffline() {
        TealiumHelper.trackEvent("offline",
            mapOf(
                DataLayer.ENABLED to !isOffline
            )
        )
        isOffline = !isOffline
    }
}
