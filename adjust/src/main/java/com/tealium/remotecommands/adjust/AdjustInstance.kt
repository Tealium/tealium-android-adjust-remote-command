package com.tealium.remotecommands.adjust

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.os.Bundle
import com.adjust.sdk.*
import com.tealium.remotecommands.adjust.AdjustRemoteCommand.Companion.toStringList
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
                "suppress" -> LogLevel.SUPPRESS
                else -> null
            }
            level?.let {
                config.setLogLevel(it)
            }
        }

        if (settings.has(Config.PREINSTALL_TRACKING)) {
            val enabled = settings.optBoolean(Config.PREINSTALL_TRACKING)
            if (enabled) {
                config.enablePreinstallTracking()
            }
        }

        if (settings.has(Config.SEND_IN_BACKGROUND)) {
            val enabled = settings.optBoolean(Config.SEND_IN_BACKGROUND)
            if (enabled) {
                config.enableSendingInBackground()
            }
        }

        if (settings.has(Config.DEFAULT_TRACKER)) {
            val defaultTracker = settings.optString(Config.DEFAULT_TRACKER, "")
            if (defaultTracker.isNotEmpty()) {
                config.defaultTracker = defaultTracker
            }
        }

        val strategyKey = settings.optString(Config.URL_STRATEGY)
        val strategy = UrlStrategy.defaultStrategies[strategyKey]
        if (strategy != null) {
            config.setUrlStrategy(
                strategy.domains,
                strategy.useSubdomains,
                strategy.isDataResidency
            )
        } else {
            val domains = settings.optJSONArray(Config.URL_STRATEGY_DOMAINS)?.toStringList()
            val useSubdomains = settings.optBoolean(Config.URL_STRATEGY_USE_SUBDOMAIN)
            val isDataResidency = settings.optBoolean(Config.URL_STRATEGY_IS_RESIDENCY)
            config.setUrlStrategy(domains, useSubdomains, isDataResidency)
        }

        if (settings.has(Config.COPPA_COMPLIANT)) {
            val coppaCompliant = settings.optBoolean(Config.COPPA_COMPLIANT)
            if (coppaCompliant) {
                config.enableCoppaCompliance()
            }
        }

        if (settings.has(Config.PLAY_STORE_KIDS_ENABLED)) {
            val playStoreKidsEnabled = settings.optBoolean(Config.PLAY_STORE_KIDS_ENABLED)
            if (playStoreKidsEnabled) {
                config.enablePlayStoreKidsCompliance()
            }
        }

        if (settings.has(Config.DEDUPLICATION_ID_MAX_SIZE)) {
            val deduplicationIdMaxSize = settings.optInt(Config.DEDUPLICATION_ID_MAX_SIZE)
                config.eventDeduplicationIdsMaxSize = deduplicationIdMaxSize
        }

        initialize(config)
    }

    override fun initialize(config: AdjustConfig) {
        if (initialized) return

        Adjust.initSdk(config)
        initialized = true

        if (needsResume) Adjust.onResume()
    }

    override fun sendEvent(
        eventToken: String,
        orderId: String?,
        deduplicationId: String?,
        revenue: Double?,
        currency: String?,
        callbackParams: Map<String, String>?,
        partnerParams: Map<String, String>?,
        callbackId: String?
    ) {
        val event = AdjustEvent(eventToken)
        orderId?.let {
            event.orderId = it
        }

        event.deduplicationId = deduplicationId ?: orderId
        
        revenue?.let {
            event.setRevenue(it, currency)
        }
        callbackId?.let {
            event.callbackId = it
        }
        callbackParams?.forEach {
            event.addCallbackParameter(it.key, it.value)
        }
        partnerParams?.forEach {
            event.addPartnerParameter(it.key, it.value)
        }

        Adjust.trackEvent(event)
    }

    override fun appWillOpenURL(url: Uri) {
        Adjust.processDeeplink(AdjustDeeplink(url), application.applicationContext)
    }

    override fun addGlobalCallbackParams(params: Map<String, String>) {
        params.entries.forEach {
            Adjust.addGlobalCallbackParameter(it.key, it.value)
        }
    }

    override fun removeGlobalCallbackParams(paramNames: List<String>) {
        paramNames.forEach {
            Adjust.removeGlobalCallbackParameter(it)
        }
    }

    override fun resetGlobalCallbackParams() {
        Adjust.removeGlobalCallbackParameters()
    }

    override fun addGlobalPartnerParams(params: Map<String, String>) {
        params.entries.forEach {
            Adjust.addGlobalPartnerParameter(it.key, it.value)
        }
    }

    override fun removeGlobalPartnerParams(paramNames: List<String>) {
        paramNames.forEach {
            Adjust.removeGlobalPartnerParameter(it)
        }
    }

    override fun resetGlobalPartnerParams() {
        Adjust.removeGlobalPartnerParameters()
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

        subscription.purchaseTime = purchaseTime

        callbackParams?.forEach {
            subscription.addCallbackParameter(it.key, it.value)
        }

        partnerParams?.forEach {
            subscription.addPartnerParameter(it.key, it.value)
        }

        Adjust.trackPlayStoreSubscription(subscription)
    }

    override fun trackAdRevenue(adRevenue: AdjustAdRevenue) {
        Adjust.trackAdRevenue(adRevenue)
    }

    override fun setPushToken(token: String) {
        Adjust.setPushToken(token, application.applicationContext)
    }

    override fun setEnabled(enabled: Boolean) {
        if (enabled) {
            Adjust.enable()
        } else {
            Adjust.disable()
        }
    }

    override fun setOfflineMode(enabled: Boolean) {
        if (enabled) {
            Adjust.switchToOfflineMode()
        } else {
            Adjust.switchBackToOnlineMode()
        }
    }

    override fun gdprForgetMe() {
        Adjust.gdprForgetMe(application.applicationContext)
    }

    override fun setThirdPartySharing(enabled: Boolean?, granularOptions: JSONObject?) {
        if (enabled == null && granularOptions == null) return

        val sharing = AdjustThirdPartySharing(enabled)

        granularOptions?.let { opts ->
            for (thirdParty in opts.keys()) {
                val thirdPartyOpts = opts.optJSONObject(thirdParty) ?: continue

                for (thirdPartyOption in thirdPartyOpts.keys()) {
                    val option = thirdPartyOpts.getString(thirdPartyOption)
                    sharing.addGranularOption(thirdParty, thirdPartyOption, option)
                }
            }
        }

        Adjust.trackThirdPartySharing(sharing)
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
