package com.tealium.remotecommands.adjust

import android.app.Application
import com.tealium.remotecommands.RemoteCommand
import com.tealium.remotecommands.adjust.Config as ConstConfig
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21, 28])
class AdjustRemoteCommandTests {

    @RelaxedMockK
    lateinit var mockApp: Application

    @RelaxedMockK
    lateinit var mockAdjustCommand: AdjustCommand

    @RelaxedMockK
    lateinit var mockResponse: RemoteCommand.Response

    lateinit var payload: JSONObject
    lateinit var adjustRemoteCommand: AdjustRemoteCommand

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockApp.applicationContext } returns mockApp

        payload = JSONObject()
        every { mockResponse.requestPayload } returns payload

        adjustRemoteCommand = AdjustRemoteCommand(mockApp, adjustCommand = mockAdjustCommand)
    }

    @Test
    fun initialize_Initialzes_WhenAllRequiredAvailable() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.INITIALIZE
            )
            put(ConstConfig.API_TOKEN, "token")
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.initialize("token", false, any())
        }
    }

    @Test
    fun initialize_WithOptionals() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.INITIALIZE
            )
            put(ConstConfig.API_TOKEN, "token")
            put(ConstConfig.SANDBOX, true)
            put(ConstConfig.SETTINGS, JSONObject("{}"))
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.initialize("token", true, match {
                it.toString() == "{}"
            })
        }
    }

    @Test
    fun trackEvent_TracksEvent_WhenEventTokenSet() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.TRACK_EVENT
            )
            put(Events.EVENT_TOKEN, "token")
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.sendEvent("token", any(), any(), any(), any(), any(), any(),any())
        }
    }

    @Test
    fun trackEvent_DoesNotTrackEvent_WhenEventTokenNotSet() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.TRACK_EVENT
            )
            // Missing Token
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify(exactly = 0) {
            mockAdjustCommand.sendEvent(any(), any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun trackEvent_AddsOptionalData_WhenAvailable() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.TRACK_EVENT
            )
            put(Events.EVENT_TOKEN, "token")
            put(Events.REVENUE, 100)
            put(Events.CURRENCY, "USD")
            put(Events.ORDER_ID, "order123")
            put(Events.DEDUPLICATION_ID, "dedup123")
            put(Events.CALLBACK_ID, "callbackId")
            put(Events.CALLBACK_PARAMETERS, JSONObject(mapOf("callback" to "value")))
            put(Events.PARTNER_PARAMETERS, JSONObject(mapOf("partner" to "value")))
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.sendEvent(
                "token",
                "order123",
                "dedup123",
                100.0,
                "USD",
                mapOf("callback" to "value"),
                mapOf("partner" to "value"),
                "callbackId"
            )
        }
    }

    @Test
    fun trackSubscription_TracksSubscription_WhenAllRequiredAvailable() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.TRACK_SUBSCRIPTION
            )
            put(Events.REVENUE, 10L)
            put(Events.CURRENCY, "USD")
            put(Events.SKU, "sku123")
            put(Events.ORDER_ID, "order123")
            put(Events.SIGNATURE, "signature")
            put(Events.PURCHASE_TOKEN, "token")
            put(Events.PURCHASE_TIME, 100L)
            put(Events.CALLBACK_PARAMETERS, JSONObject(mapOf("callback" to "value")))
            put(Events.PARTNER_PARAMETERS, JSONObject(mapOf("partner" to "value")))
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.trackSubscription(
                10L,
                "USD",
                "sku123",
                "order123",
                "signature",
                "token",
                100L,
                mapOf("callback" to "value"),
                mapOf("partner" to "value")
            )
        }
    }

    fun trackSubscription_DoesNotTrackSubscription_WhenMissingRequired() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.TRACK_SUBSCRIPTION
            )
            // Missing Revenue && Currency
            put(Events.SKU, "sku123")
            put(Events.ORDER_ID, "order123")
            put(Events.SIGNATURE, "signature")
            put(Events.PURCHASE_TOKEN, "token")
            // Optionals missing (time, callback and partner params
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify(exactly = 0) {
            mockAdjustCommand.trackSubscription(
                any(),
                any(),
                "sku123",
                "order123",
                "signature",
                "token",
                100L,
                mapOf("callback" to "value"),
                mapOf("partner" to "value")
            )
        }
    }

    @Test
    fun setAdRevenue_SetsAdRevenue() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.TRACK_AD_REVENUE
            )
            put(Events.AD_REVENUE_SOURCE, "admob_sdk")
            put(Events.AD_REVENUE_PAYLOAD, JSONObject("{}"))
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.trackAdRevenue("admob_sdk", match {
                it.toString() == "{}"
            })
        }
    }

    @Test
    fun setCommands_SetAllOptions_True() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                listOf(
                    Commands.SET_ENABLED,
                    Commands.SET_OFFLINE_MODE,
                    Commands.TRACK_MEASUREMENT_CONSENT,
                    Commands.SET_THIRD_PARTY_SHARING
                ).joinToString()
            )
            put(Misc.ENABLED, true)
            put(Misc.OFFLINE, false)
            put(Misc.MEASUREMENT_CONSENT, true)
            put(Misc.THIRD_PARTY_SHARING_ENABLED, false)
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.setEnabled(true)
            mockAdjustCommand.setOfflineMode(false)
            mockAdjustCommand.trackMeasurementConsent(true)
            mockAdjustCommand.setThirdPartySharing(false, null)
        }
    }

    @Test
    fun gdprForgetMe_Forgets() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.GDPR_FORGET_ME
            )
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.gdprForgetMe()
        }
    }

    @Test
    fun setPushToken_SetsPushToken() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                Commands.SET_PUSH_TOKEN
            )
            put(Misc.PUSH_TOKEN, "token")
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.setPushToken("token")
        }
    }

    @Test
    fun addGlobalParams_GetAdded() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                listOf(
                    Commands.ADD_GLOBAL_CALLBACK_PARAMS,
                    Commands.ADD_GLOBAL_PARTNER_PARAMS
                ).joinToString()
            )
            put(
                Events.GLOBAL_CALLBACK_PARAMETERS,
                JSONObject(mapOf("key_1" to "value_1", "key_2" to "value_2"))
            )
            put(
                Events.GLOBAL_PARTNER_PARAMETERS,
                JSONObject(mapOf("key_1" to "value_1", "key_2" to "value_2"))
            )
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.addGlobalCallbackParams(match {
                it["key_1"] == "value_1" &&
                it["key_2"] == "value_2"
            })
            mockAdjustCommand.addGlobalPartnerParams(match {
                it["key_1"] == "value_1" &&
                it["key_2"] == "value_2"
            })
        }
    }

    @Test
    fun removeGlobalParams_GetRemoved() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                listOf(
                    Commands.REMOVE_GLOBAL_CALLBACK_PARAMS,
                    Commands.REMOVE_GLOBAL_PARTNER_PARAMS
                ).joinToString()
            )
            put(Events.REMOVE_GLOBAL_CALLBACK_PARAMETERS, JSONArray(listOf("key_1", "key_2")))
            put(Events.REMOVE_GLOBAL_PARTNER_PARAMETERS, JSONArray(listOf("key_1", "key_2")))
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.removeGlobalPartnerParams(match {
                it.contains("key_1") && it.contains("key_2")
            })
            mockAdjustCommand.removeGlobalPartnerParams(match {
                it.contains("key_1") && it.contains("key_2")
            })
        }
    }

    @Test
    fun resetGlobalParams_ResetsParams() {
        payload.apply {
            put(
                Commands.COMMAND_NAME,
                listOf(
                    Commands.RESET_GLOBAL_CALLBACK_PARAMS,
                    Commands.RESET_GLOBAL_PARTNER_PARAMS
                ).joinToString()
            )
        }

        adjustRemoteCommand.onInvoke(mockResponse)

        verify {
            mockAdjustCommand.resetGlobalCallbackParams()
            mockAdjustCommand.resetGlobalPartnerParams()
        }
    }
}