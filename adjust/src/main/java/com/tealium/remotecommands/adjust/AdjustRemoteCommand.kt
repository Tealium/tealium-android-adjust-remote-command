package com.tealium.remotecommands.adjust

import android.app.Application
import android.util.Log
import com.adjust.sdk.AdjustConfig
import com.tealium.remotecommands.RemoteCommand
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

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
) : RemoteCommand(commandId, description) {

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

    internal fun splitCommands(payload: JSONObject): Array<String> {
        val commandString = payload.optString(Commands.COMMAND_NAME, "")
        return commandString.split(AdjustConstants.SEPARATOR).map {
            it.trim().toLowerCase(Locale.ROOT)
        }.toTypedArray()
    }

    internal fun parseCommands(commands: Array<String>, payload: JSONObject) {
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
                    Commands.SET_PUSH_TOKEN -> {
                        setPushToken(payload)
                    }
                    Commands.SET_ENABLED -> {
                        setEnabled(payload)
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
                    Commands.ADD_SESSION_CALLBACK_PARAMS -> {
                        addSessionCallbackParams(payload)
                    }
                    Commands.REMOVE_SESSION_CALLBACK_PARAMS -> {
                        removeSessionCallbackParams(payload)
                    }
                    Commands.RESET_SESSION_CALLBACK_PARAMS -> {
                        resetSessionCallbackParams(payload)
                    }
                    Commands.ADD_SESSION_PARTNER_PARAMS -> {
                        addPartnerSessionCallbackParams(payload)
                    }
                    Commands.REMOVE_SESSION_PARTNER_PARAMS -> {
                        removePartnerSessionCallbackParams(payload)
                    }
                    Commands.RESET_SESSION_PARTNER_PARAMS -> {
                        resetPartnerSessionCallbackParams(payload)
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
        val orderId: String? = payload.optString(Events.ORDER_ID, "").nullIfBlank()
        val callbackId: String? = payload.optString(Events.CALLBACK_ID, "").nullIfBlank()
        val callbackParams: Map<String, String>? =
            payload.optJSONObject(Events.CALLBACK_PARAMETERS)?.toTypedMap()
        val partnerParams: Map<String, String>? =
            payload.optJSONObject(Events.PARTNER_PARAMETERS)?.toTypedMap()

        adjustCommand.sendEvent(
            eventToken,
            orderId,
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
            revenue, currency, sku, orderId, signature, purchaseToken, purchaseTime, callbackParams, partnerParams
        )
    }

    private fun trackAdRevenue(payload: JSONObject) {
        val adSource: String? = payload.optString(Events.AD_REVENUE_SOURCE, "").nullIfBlank()
        val adPayload: JSONObject? = payload.optJSONObject(Events.AD_REVENUE_PAYLOAD)
        if (adSource != null && adPayload != null) {
            adjustCommand.trackAdRevenue(adSource, adPayload)
        }
    }

    private fun setPushToken(payload: JSONObject) {
        payload.getString(Misc.PUSH_TOKEN).let { token ->
            adjustCommand.setPushToken(token)
        }
    }

    private fun setEnabled(payload: JSONObject) {
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
        if (payload.has(Misc.THIRD_PARTY_SHARING_ENABLED)) {
            val enabled = payload.getBoolean(Misc.THIRD_PARTY_SHARING_ENABLED)
            adjustCommand.setThirdPartySharing(enabled)
        }
    }

    private fun trackMeasurementConsent(payload: JSONObject) {
        if (payload.has(Misc.MEASUREMENT_CONSENT)) {
            val enabled = payload.getBoolean(Misc.MEASUREMENT_CONSENT)
            adjustCommand.trackMeasurementConsent(enabled)
        }
    }

    private fun addSessionCallbackParams(payload: JSONObject) {
        val sessionParams: Map<String, String>? =
            payload.optJSONObject(Events.SESSION_CALLBACK_PARAMETERS)?.toTypedMap()
        sessionParams?.let {
            adjustCommand.addSessionCallbackParams(it)
        }
    }

    private fun removeSessionCallbackParams(payload: JSONObject) {
        val sessionParams: List<String>? =
            payload.optJSONArray(Events.REMOVE_SESSION_CALLBACK_PARAMETERS)?.toStringList()
        sessionParams?.let {
            adjustCommand.removeSessionCallbackParams(it)
        }
    }

    private fun resetSessionCallbackParams(payload: JSONObject) {
        adjustCommand.resetSessionCallbackParams()
    }

    private fun addPartnerSessionCallbackParams(payload: JSONObject) {
        val partnerSessionParams: Map<String, String>? =
            payload.optJSONObject(Events.SESSION_CALLBACK_PARAMETERS)?.toTypedMap()
        partnerSessionParams?.let {
            adjustCommand.addSessionPartnerParams(it)
        }
    }

    private fun removePartnerSessionCallbackParams(payload: JSONObject) {
        val partnerSessionParams: List<String>? =
            payload.optJSONArray(Events.REMOVE_SESSION_PARTNER_PARAMETERS)?.toStringList()
        partnerSessionParams?.let {
            adjustCommand.removeSessionPartnerParams(it)
        }
    }

    private fun resetPartnerSessionCallbackParams(payload: JSONObject) {
        adjustCommand.resetSessionPartnerParams()
    }

    companion object {
        private const val DEFAULT_COMMAND_ID = "adjust"
        private const val DEFAULT_COMMAND_DESC = "Tealium-Adjust Remote Command"
        private const val INVALID_REVENUE = -1.0
    }
}

private inline fun <reified T> JSONObject.toTypedMap(): Map<String, T> {
    val map = HashMap<String, T>()
    keys().forEach { key ->
        val value = this[key]
        (value as? T)?.let {
            map[key] = value
        }
    }
    return map
}

private fun JSONArray.toStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until length()) {
        getString(i)?.let {
            list.add(it)
        }
    }
    return list
}

private fun String.nullIfBlank(): String? {
    return if (isBlank()) null else this
}