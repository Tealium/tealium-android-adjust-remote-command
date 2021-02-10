package com.tealium.remotecommands.adjust

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.adjust.sdk.*
import org.json.JSONException
import org.json.JSONObject


class AdjustInstance(
    private val application: Application
) : AdjustCommand {

    @Volatile
    private var initialized = false
    @Volatile
    private var needsResume = false

    init {
        application.registerActivityLifecycleCallbacks(AdjustActivityCallbacks())
    }

    override fun initialize(apiToken: String, sandbox: Boolean, settings: JSONObject) {
        if (initialized) return

        val environment =
            if (sandbox) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(application, apiToken, environment)

        settings.optString(Config.LOG_LEVEL, "").let { logLevel ->
            val level = when (logLevel) {
                "verbose" -> LogLevel.VERBOSE
                "debug" -> LogLevel.DEBUG
                "info" -> LogLevel.INFO
                "warn" -> LogLevel.WARN
                "error" -> LogLevel.ERROR
                "assert" -> LogLevel.ASSERT
                "suppress" -> LogLevel.SUPRESS
                else -> null
            }
            level?.let {
                config.setLogLevel(it)
            }
        }

        try {
            val appSecret = settings.getLong(Config.SECRET_ID)
            val info1 = settings.getLong(Config.SECRET_INFO_1)
            val info2 = settings.getLong(Config.SECRET_INFO_2)
            val info3 = settings.getLong(Config.SECRET_INFO_3)
            val info4 = settings.getLong(Config.SECRET_INFO_4)

            config.setAppSecret(appSecret, info1, info2, info3, info4)
        } catch (jex: JSONException) {

        }

        if (settings.has(Config.DELAY_START)) {
            val delay = settings.optDouble(Config.DELAY_START, -1.0)
            if (delay != -1.0) {
                config.setDelayStart(delay)
            }
        }

        if (settings.has(Config.PREINSTALL_TRACKING)) {
            val enabled = settings.optBoolean(Config.PREINSTALL_TRACKING)
            config.setPreinstallTrackingEnabled(enabled)
        }

        if (settings.has(Config.EVENT_BUFFERING_ENABLED)) {
            val enabled = settings.optBoolean(Config.EVENT_BUFFERING_ENABLED)
            config.setEventBufferingEnabled(enabled)
        }

        if (settings.has(Config.SEND_IN_BACKGROUND)) {
            val enabled = settings.optBoolean(Config.SEND_IN_BACKGROUND)
            config.setSendInBackground(enabled)
        }

        if (settings.has(Config.DEFAULT_TRACKER)) {
            val defaultTracker = settings.optString(Config.DEFAULT_TRACKER, "")
            if (defaultTracker.isNotEmpty()) {
                config.setDefaultTracker(defaultTracker)
            }
        }

        initialize(config)
    }

    override fun initialize(config: AdjustConfig) {
        if (initialized) return

        Adjust.onCreate(config)
        initialized = true

        if (needsResume) Adjust.onResume()
    }

    override fun sendEvent(
        eventToken: String,
        orderId: String?,
        revenue: Double?,
        currency: String?,
        callbackParams: Map<String, String>?,
        partnerParams: Map<String, String>?,
        callbackId: String?
    ) {
        val event = AdjustEvent(eventToken)
        orderId?.let {
            event.setOrderId(it)
        }
        revenue?.let {
            event.setRevenue(it, currency)
        }
        callbackId?.let {
            event.setCallbackId(it)
        }
        callbackParams?.forEach {
            event.addCallbackParameter(it.key, it.value)
        }
        partnerParams?.forEach {
            event.addPartnerParameter(it.key, it.value)
        }

        Adjust.trackEvent(event)
    }

    override fun addSessionCallbackParams(params: Map<String, String>) {
        params.entries.forEach {
            Adjust.addSessionCallbackParameter(it.key, it.value)
        }
    }

    override fun removeSessionCallbackParams(paramNames: List<String>) {
        paramNames.forEach {
            Adjust.removeSessionCallbackParameter(it)
        }
    }

    override fun resetSessionCallbackParams() {
        Adjust.resetSessionCallbackParameters()
    }

    override fun addSessionPartnerParams(params: Map<String, String>) {
        params.entries.forEach {
            Adjust.addSessionPartnerParameter(it.key, it.value)
        }
    }

    override fun removeSessionPartnerParams(paramNames: List<String>) {
        paramNames.forEach {
            Adjust.removeSessionPartnerParameter(it)
        }
    }

    override fun resetSessionPartnerParams() {
        Adjust.resetSessionPartnerParameters()
    }

    override fun trackSubscription(
        price: Long,
        currency: String,
        sku: String,
        orderId: String,
        signature: String,
        purchaseToken: String,
        purchaseTime: Long,
        callbackParams: Map<String, String>?,
        partnerParams: Map<String, String>?
    ) {
        val subscription = AdjustPlayStoreSubscription(
            price,
            currency,
            sku,
            orderId,
            signature,
            purchaseToken
        )
        subscription.setPurchaseTime(purchaseTime)

        callbackParams?.forEach {
            subscription.addCallbackParameter(it.key, it.value)
        }

        partnerParams?.forEach {
            subscription.addPartnerParameter(it.key, it.value)
        }

        Adjust.trackPlayStoreSubscription(subscription)
    }

    override fun trackAdRevenue(source: String, payload: JSONObject) {
        Adjust.trackAdRevenue(source, payload)
    }

    override fun setPushToken(token: String) {
        Adjust.setPushToken(token, application.applicationContext)
    }

    override fun setEnabled(enabled: Boolean) {
        Adjust.setEnabled(enabled)
    }

    override fun setOfflineMode(enabled: Boolean) {
        Adjust.setOfflineMode(enabled)
    }

    override fun gdprForgetMe() {
        Adjust.gdprForgetMe(application.applicationContext)
    }

    override fun setThirdPartySharing(enabled: Boolean) {
        if (!enabled) {
            Adjust.disableThirdPartySharing(application)
        } else {
            val sharing = AdjustThirdPartySharing(true)
            //  sharing.addGranularOption()
            // TODO (how best to map granular options)
            Adjust.trackThirdPartySharing(sharing)
        }
    }

    override fun trackMeasurementConsent(consented: Boolean) {
        Adjust.trackMeasurementConsent(consented)
    }

    private inner class AdjustActivityCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
            if (initialized) Adjust.onPause()
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityResumed(activity: Activity) {
            if (initialized) {
                Adjust.onResume()
            } else {
                // not initialized yet
                needsResume = true
            }
        }
    }
}
