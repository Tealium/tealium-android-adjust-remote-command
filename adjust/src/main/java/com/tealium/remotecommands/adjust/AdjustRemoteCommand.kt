package com.tealium.remotecommands.adjust

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.tealium.remotecommands.RemoteCommand
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

/**
 * Remote Command implementation for the Adjust SDK.
 *
 * @param application   Application instance
 * @param adjustConfig  Optional AdjustConfig instance; use this if you need to add any Adjust
 *                      event listeners before initialization. Note. supplying this will initialize
 *                      the Adjust SDK immediately, ignoring any future initialization config from
 *                      the Remote Command
 * @param adjustCommand Optional AdjustCommand implementation for overriding
 */
class AdjustRemoteCommand @JvmOverloads constructor(
    private val application: Application,
    adjustConfig: AdjustConfig? = null,
    private val adjustCommand: AdjustCommand = AdjustInstance(application),
    commandId: String = DEFAULT_COMMAND_ID,
    description: String = DEFAULT_COMMAND_DESC
) : RemoteCommand(commandId, description, BuildConfig.TEALIUM_ADJUST_VERSION) {

    init {
        adjustConfig?.let {
            adjustCommand.initialize(it)
        }
    }

    public override fun onInvoke(response: Response?) {
        response?.apply {
            val commandList = splitCommands(requestPayload)
            parseCommands(commandList, requestPayload)
            send()
        }
    }

    private fun splitCommands(payload: JSONObject): Array<String> {
        val commandString = payload.optString(Commands.COMMAND_NAME, "")
        return commandString.split(AdjustConstants.SEPARATOR).map {
            it.trim().toLowerCase(Locale.ROOT)
        }.toTypedArray()
    }

    private fun parseCommands(commands: Array<String>, payload: JSONObject) {
        commands.forEach { command ->
            try {
                when (command) {
                    Commands.INITIALIZE -> {
                        initialize(payload)
                    }

                    Commands.TRACK_EVENT -> {
                        logEvent(payload)
                    }

                    Commands.TRACK_SUBSCRIPTION -> {
                        trackSubscription(payload)
                    }

                    Commands.TRACK_AD_REVENUE -> {
                        trackAdRevenue(payload)
                    }

                    Commands.TRACK_DEEPLINK -> {
                        appWillOpenUrl(payload)
                    }

                    Commands.SET_PUSH_TOKEN -> {
                        setPushToken(payload)
                    }

                    Commands.SET_ENABLED -> {
                        toggleEnabled(payload)
                    }

                    Commands.SET_OFFLINE_MODE -> {
                        setOfflineMode(payload)
                    }

                    Commands.GDPR_FORGET_ME -> {
                        gdprForgetMe(payload)
                    }

                    Commands.SET_THIRD_PARTY_SHARING -> {
                        setThirdPartySharing(payload)
                    }

                    Commands.TRACK_MEASUREMENT_CONSENT -> {
                        trackMeasurementConsent(payload)
                    }

                    Commands.ADD_GLOBAL_CALLBACK_PARAMS, Commands.ADD_SESSION_CALLBACK_PARAMS -> {
                        addGlobalCallbackParams(payload)
                    }

                    Commands.REMOVE_GLOBAL_CALLBACK_PARAMS, Commands.REMOVE_SESSION_CALLBACK_PARAMS -> {
                        removeGlobalCallbackParams(payload)
                    }

                    Commands.RESET_GLOBAL_CALLBACK_PARAMS, Commands.RESET_SESSION_CALLBACK_PARAMS -> {
                        resetGlobalCallbackParams(payload)
                    }

                    Commands.ADD_GLOBAL_PARTNER_PARAMS, Commands.ADD_SESSION_PARTNER_PARAMS -> {
                        addPartnerGlobalCallbackParams(payload)
                    }

                    Commands.REMOVE_GLOBAL_PARTNER_PARAMS, Commands.REMOVE_SESSION_PARTNER_PARAMS -> {
                        removePartnerGlobalCallbackParams(payload)
                    }

                    Commands.RESET_GLOBAL_PARTNER_PARAMS, Commands.RESET_SESSION_PARTNER_PARAMS -> {
                        resetPartnerGlobalCallbackParams()
                    }

                    else -> {
                        Log.d(AdjustConstants.TAG, "Invalid command name.")
                    }
                }
            } catch (ex: Exception) {
                Log.w(AdjustConstants.TAG, "Error processing command: $command", ex)
            }
        }
    }

    private fun initialize(payload: JSONObject) {
        val apiToken = payload.getString(Config.API_TOKEN)
        val sandbox = payload.optBoolean(Config.SANDBOX, false)
        val settings: JSONObject? = payload.optJSONObject(Config.SETTINGS)

        adjustCommand.initialize(apiToken, sandbox, settings ?: JSONObject())
    }

    private fun logEvent(payload: JSONObject) {
        val eventToken = payload.optString(Events.EVENT_TOKEN, "")
        if (eventToken.isBlank()) return

        val revenue: Double? = payload.optDouble(Events.REVENUE, INVALID_REVENUE)
            .let { if (it == INVALID_REVENUE) null else it }
        val currency: String? = payload.optString(Events.CURRENCY).nullIfBlank()
        val deduplicationId: String? = payload.optString(Events.DEDUPLICATION_ID, "").nullIfBlank()
        val orderId: String? = payload.optString(Events.ORDER_ID, "").nullIfBlank()
        val callbackId: String? = payload.optString(Events.CALLBACK_ID, "").nullIfBlank()
        val callbackParams: Map<String, String>? =
            payload.optJSONObject(Events.CALLBACK_PARAMETERS)?.toTypedMap()
        val partnerParams: Map<String, String>? =
            payload.optJSONObject(Events.PARTNER_PARAMETERS)?.toTypedMap()

        adjustCommand.sendEvent(
            eventToken,
            orderId,
            deduplicationId,
            revenue,
            currency,
            callbackParams,
            partnerParams,
            callbackId
        )
    }

    private fun trackSubscription(payload: JSONObject) {
        val revenue: Long = payload.getLong(Events.REVENUE)
        val currency: String = payload.getString(Events.CURRENCY)
        val sku: String = payload.getString(Events.SKU)
        val orderId: String = payload.getString(Events.ORDER_ID)
        val signature: String = payload.getString(Events.SIGNATURE)
        val purchaseToken: String = payload.getString(Events.PURCHASE_TOKEN)
        val purchaseTime: Long = payload.optLong(Events.PURCHASE_TIME, System.currentTimeMillis())
        val callbackParams: Map<String, String>? =
            payload.optJSONObject(Events.CALLBACK_PARAMETERS)?.toTypedMap()
        val partnerParams: Map<String, String>? =
            payload.optJSONObject(Events.PARTNER_PARAMETERS)?.toTypedMap()

        adjustCommand.trackSubscription(
            revenue,
            currency,
            sku,
            orderId,
            signature,
            purchaseToken,
            purchaseTime,
            callbackParams,
            partnerParams
        )
    }

    private fun trackAdRevenue(payload: JSONObject) {
        val adSource: String? = payload.optString(Events.AD_REVENUE_SOURCE, "").nullIfBlank()
        val adPayload: JSONObject? = payload.optJSONObject(Events.AD_REVENUE_PAYLOAD)
        if (adSource != null && adPayload != null) {
            adjustCommand.trackAdRevenue(adSource, adPayload)
        } else {
            Log.d(AdjustConstants.TAG, "${Events.AD_REVENUE_SOURCE} and ${Events.AD_REVENUE_PAYLOAD} keys are required ")
            return
        }
    }

    private fun appWillOpenUrl(payload: JSONObject) {
        val url: Uri? = payload.optString(Events.DEEPLINK_URL, "").nullIfBlank()?.toUri()
        if (url != null) {
            adjustCommand.appWillOpenURL(url)
        }
    }

    private fun setPushToken(payload: JSONObject) {
        payload.getString(Misc.PUSH_TOKEN).let { token ->
            adjustCommand.setPushToken(token)
        }
    }

    private fun toggleEnabled(payload: JSONObject) {
        if (payload.has(Misc.ENABLED)) {
            val enabled = payload.getBoolean(Misc.ENABLED)
            adjustCommand.setEnabled(enabled)
        }
    }

    private fun setOfflineMode(payload: JSONObject) {
        if (payload.has(Misc.OFFLINE)) {
            val enabled = payload.getBoolean(Misc.OFFLINE)
            adjustCommand.setOfflineMode(enabled)
        }
    }

    private fun gdprForgetMe(payload: JSONObject) {
        adjustCommand.gdprForgetMe()
    }

    private fun setThirdPartySharing(payload: JSONObject) {
        // optBoolean, does not allow null returns
        val enabled: Boolean? = try {
            payload.getBoolean(Misc.THIRD_PARTY_SHARING_ENABLED)
        } catch (ex: JSONException) {
            null
        }
        val options = payload.optJSONObject(Misc.THIRD_PARTY_SHARING_OPTIONS)

        adjustCommand.setThirdPartySharing(enabled, options)
    }

    private fun trackMeasurementConsent(payload: JSONObject) {
        if (payload.has(Misc.MEASUREMENT_CONSENT)) {
            val enabled = payload.getBoolean(Misc.MEASUREMENT_CONSENT)
            adjustCommand.trackMeasurementConsent(enabled)
        }
    }

    private fun addGlobalCallbackParams(payload: JSONObject) {
        val globalParams: Map<String, String>? =
            payload.optJSONObject(Events.GLOBAL_CALLBACK_PARAMETERS)?.toTypedMap()
        val sessionParams: Map<String, String>? =
            payload.optJSONObject(Events.SESSION_CALLBACK_PARAMETERS)?.toTypedMap()

        val params = globalParams ?: sessionParams
        params?.let {
            adjustCommand.addGlobalCallbackParams(it)
        }
    }

    private fun removeGlobalCallbackParams(payload: JSONObject) {
        val globalParams: List<String>? =
            payload.optJSONArray(Events.REMOVE_GLOBAL_CALLBACK_PARAMETERS)?.toStringList()
        val sessionParams: List<String>? =
            payload.optJSONArray(Events.REMOVE_SESSION_CALLBACK_PARAMETERS)?.toStringList()

        val params = globalParams ?: sessionParams
        params?.let {
            adjustCommand.removeGlobalCallbackParams(it)
        }
    }

    private fun resetGlobalCallbackParams(payload: JSONObject) {
        adjustCommand.resetGlobalCallbackParams()
    }

    private fun addPartnerGlobalCallbackParams(payload: JSONObject) {
        val partnerGlobalParams: Map<String, String>? =
            payload.optJSONObject(Events.GLOBAL_PARTNER_PARAMETERS)?.toTypedMap()
        val partnerSessionParams: Map<String, String>? =
            payload.optJSONObject(Events.SESSION_PARTNER_PARAMETERS)?.toTypedMap()

        val partnerParams = partnerGlobalParams ?: partnerSessionParams
        partnerParams?.let {
            adjustCommand.addGlobalPartnerParams(it)
        }
    }

    private fun removePartnerGlobalCallbackParams(payload: JSONObject) {
        val partnerGlobalParams: List<String>? =
            payload.optJSONArray(Events.REMOVE_GLOBAL_PARTNER_PARAMETERS)?.toStringList()
        val partnerSessionParams: List<String>? =
            payload.optJSONArray(Events.REMOVE_SESSION_PARTNER_PARAMETERS)?.toStringList()

        val partnerParams = partnerGlobalParams ?: partnerSessionParams
        partnerParams?.let {
            adjustCommand.removeGlobalPartnerParams(it)
        }
    }

    private fun resetPartnerGlobalCallbackParams() {
        adjustCommand.resetGlobalPartnerParams()
    }

    companion object {
        private const val DEFAULT_COMMAND_ID = "adjust"
        private const val DEFAULT_COMMAND_DESC = "Tealium-Adjust Remote Command"
        private const val INVALID_REVENUE = -1.0

        fun JSONArray.toStringList(): List<String> {
            val list = mutableListOf<String>()
            for (i in 0 until length()) {
                getString(i)?.let {
                    list.add(it)
                }
            }
            return list
        }

        inline fun <reified T> JSONObject.toTypedMap(): Map<String, T> {
            val map = HashMap<String, T>()
            keys().forEach { key ->
                val value = this[key]
                (value as? T)?.let {
                    map[key] = value
                }
            }
            return map
        }
    }

    private fun String.nullIfBlank(): String? {
        return if (isBlank()) null else this
    }
}