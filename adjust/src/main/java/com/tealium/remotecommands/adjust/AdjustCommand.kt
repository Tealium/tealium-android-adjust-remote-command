package com.tealium.remotecommands.adjust

import android.net.Uri
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import org.json.JSONObject

/**
 * Helper interface for passing all data points onto the Adjust SDK.
 */
interface AdjustCommand {

    /**
     * Initializes the AdjustConfig object with supplied options
     */
    fun initialize(apiToken: String, sandbox: Boolean, settings: JSONObject)

    /**
     * Initializes the Adjust SDK with the provided AdjustConfig
     */
    fun initialize(config: AdjustConfig)

    /**
     * Tracks and event for the given [eventToken] - optional data will be set on the event if
     * provided
     */
    fun sendEvent(
        eventToken: String,
        orderId: String?,
        deduplicationId: String?,
        revenue: Double?,
        currency: String?,
        callbackParams: Map<String, String>?,
        partnerParams: Map<String, String>?,
        callbackId: String?
    )

    /**
     * Tracks a new subscription.
     */
    fun trackSubscription(
        price: Long,
        currency: String,
        sku: String,
        orderId: String,
        signature: String,
        purchaseToken: String,
        purchaseTime: Long,
        callbackParams: Map<String, String>?,
        partnerParams: Map<String, String>?
    )

    /**
     * Tracks Ad Revenue
     */
    fun trackAdRevenue(source: String, payload: JSONObject)

    /**
     * Sets the push messaging token
     */
    fun setPushToken(token: String)

    /**
     * Sets the Adjust SDK to enabled/disabled
     */
    fun setEnabled(enabled: Boolean)

    /**
     * Sets the Adjust SDK to offline mode
     */
    fun setOfflineMode(enabled: Boolean)

    /**
     * Tracks deep link opening
     * */
    fun appWillOpenURL(url: Uri)

    /**
     * Requests for the user to be forgotten
     */
    fun gdprForgetMe()

    /**
     * Enables/disables third party sharing
     */
    fun setThirdPartySharing(enabled: Boolean?, granularOptions: JSONObject?)

    /**
     * Tracks measurement consent
     */
    fun trackMeasurementConsent(consented: Boolean)

    /**
     * Adds the provided params as a Global Callback parameter.
     */
    fun addGlobalCallbackParams(params: Map<String, String>)

    /**
     * Removes Global Callback parameters using the list of key names.
     */
    fun removeGlobalCallbackParams(paramNames: List<String>)

    /**
     * Resets all Global Callback parameters
     */
    fun resetGlobalCallbackParams()

    /**
     * Adds the provided params as a Global Partner parameter.
     */
    fun addGlobalPartnerParams(params: Map<String, String>)

    /**
     * Removes Global Partner parameters using the list of key names.
     */
    fun removeGlobalPartnerParams(paramNames: List<String>)

    /**
     * Resets all Global Partner parameters
     */
    fun resetGlobalPartnerParams()
}