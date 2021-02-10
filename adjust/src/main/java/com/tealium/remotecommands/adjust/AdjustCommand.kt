package com.tealium.remotecommands.adjust

import com.adjust.sdk.AdjustConfig
import org.json.JSONObject

interface AdjustCommand {

    fun initialize(apiToken: String, sandbox: Boolean, settings: JSONObject)
    fun initialize(config: AdjustConfig)
    fun sendEvent(
        eventToken: String,
        orderId: String?,
        revenue: Double?,
        currency: String?,
        callbackParams: Map<String, String>?,
        partnerParams: Map<String, String>?,
        callbackId: String?
    )
    fun trackSubscription(price: Long,
                          currency: String,
                          sku: String,
                          orderId: String,
                          signature: String,
                          purchaseToken: String,
                          purchaseTime: Long,
                          callbackParams: Map<String, String>?,
                          partnerParams: Map<String, String>?)
    fun trackAdRevenue(source: String, payload: JSONObject)
    fun setPushToken(token: String)
    fun setEnabled(enabled: Boolean)
    fun setOfflineMode(enabled: Boolean)
    fun gdprForgetMe()
    fun setThirdPartySharing(enabled: Boolean)
    fun trackMeasurementConsent(consented: Boolean)
    fun addSessionCallbackParams(params: Map<String, String>)
    fun removeSessionCallbackParams(paramNames: List<String>)
    fun resetSessionCallbackParams()
    fun addSessionPartnerParams(params: Map<String, String>)
    fun removeSessionPartnerParams(paramNames: List<String>)
    fun resetSessionPartnerParams()
}