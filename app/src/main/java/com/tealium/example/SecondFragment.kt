package com.tealium.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.adjust.sdk.AdjustConfig
import com.tealium.example.helper.DataLayer
import com.tealium.example.helper.TealiumHelper
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), View.OnClickListener {

    private lateinit var paramNameEditText: EditText
    private lateinit var paramValueEditText: EditText
    private lateinit var purchaseButton: Button
    private lateinit var adRevenueButton: Button
    private lateinit var deepLinkButton: Button
    private lateinit var subscriptionButton: Button
    private lateinit var sessionCallbackButton: Button
    private lateinit var sessionPartnerButton: Button

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        paramNameEditText = view.findViewById(R.id.edit_param_name)
        paramValueEditText = view.findViewById(R.id.edit_param_value)
        purchaseButton = view.findViewById(R.id.button_purchase)
        adRevenueButton = view.findViewById(R.id.button_ad_revenue)
        deepLinkButton = view.findViewById(R.id.button_deeplink)
        subscriptionButton = view.findViewById(R.id.button_subscription)
        sessionCallbackButton = view.findViewById(R.id.button_session_callback)
        sessionPartnerButton = view.findViewById(R.id.button_session_partner)

        deepLinkButton.setOnClickListener(this)
        paramNameEditText.setOnClickListener(this)
        paramValueEditText.setOnClickListener(this)
        purchaseButton.setOnClickListener(this)
        adRevenueButton.setOnClickListener(this)
        subscriptionButton.setOnClickListener(this)
        sessionCallbackButton.setOnClickListener(this)
        sessionPartnerButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.id.let { id ->
            when (id) {
                R.id.button_session_callback -> { addSessionCallbackParams() }
                R.id.button_session_partner -> { addSessionPartnerParams() }
                R.id.button_purchase -> { trackPurchase() }
                R.id.button_ad_revenue -> { trackAdRevenue() }
                R.id.button_deeplink -> { deepLink() }
                R.id.button_subscription -> { trackSubscription() }
            }
        }
    }

    private fun getParamData(): Map<String, String>? {
        val name = paramNameEditText.text.toString()
        val value = paramValueEditText.text.toString()
        if (name.isBlank() || value.isBlank()) return null

        return mapOf(name to value)
    }

    private fun addSessionCallbackParams() {
        getParamData()?.let {
            TealiumHelper.trackEvent("add_session_callback", mapOf( "session_callback_params" to it))
        }
    }

    private fun addSessionPartnerParams() {
        getParamData()?.let {
            TealiumHelper.trackEvent("add_session_partner", mapOf( "session_partner_params" to it))
        }
    }

    private fun trackPurchase() {
        TealiumHelper.trackEvent("purchase",
            mapOf(
                DataLayer.EVENT_TOKEN to DataLayer.EventTokens.PURCHASE,
                DataLayer.ORDER_ID to "order123",
                DataLayer.ORDER_TOTAL to 10.0,
                DataLayer.ORDER_CURRENCY to "USD"
            )
        )
    }

    private fun trackAdRevenue() {
        TealiumHelper.trackEvent("ad_revenue",
            mapOf(
                DataLayer.SOURCE to "admob_sdk",
                DataLayer.PAYLOAD to mapOf<String, String>()
            )
        )
    }

    private fun deepLink() {
        TealiumHelper.trackEvent("track_deeplink",
            mapOf(
                DataLayer.DEEPLINK_URL to "app://someurl?hello=world"
            )
        )
    }

    private fun trackSubscription() {
        TealiumHelper.trackEvent("subscribe",
            mapOf(
                DataLayer.ORDER_ID to "order123",
                DataLayer.ORDER_TOTAL to 10.0,
                DataLayer.ORDER_CURRENCY to "USD",
                DataLayer.SKU to "sku123",
                DataLayer.SIGNATURE to "signature",
                DataLayer.PURCHASE_TOKEN to UUID.randomUUID().toString()
            )
        )
    }
}