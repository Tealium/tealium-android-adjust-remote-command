package com.tealium.remotecommands.adjust

import android.app.Application
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
import com.tealium.remotecommands.adjust.Config as ConstConfig
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21, 28])
class AdjustCommandTests {

    @RelaxedMockK
    lateinit var mockApp: Application

    lateinit var adjustCommand: AdjustCommand

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockApp.applicationContext } returns mockApp

        mockkStatic(Adjust::class)
        mockkConstructor(AdjustConfig::class)
        mockkConstructor(AdjustEvent::class)

        every { anyConstructed<AdjustConfig>().setAppSecret(any(),any(),any(),any(),any()) } just Runs
        every { anyConstructed<AdjustEvent>().setOrderId(any()) } just Runs

        adjustCommand = AdjustInstance(mockApp)
    }

    @Test
    fun initialize_Initializes_WhenRequiredParamsAvailable() {
        every { Adjust.onCreate(any()) } just Runs

        adjustCommand.initialize(
            "token",
            false,
            JSONObject()
        )

        verify(exactly = 0) {
            anyConstructed<AdjustConfig>().setAppSecret(any(), any(), any(), any(), any())
            anyConstructed<AdjustConfig>().setLogLevel(any())
            anyConstructed<AdjustConfig>().setDelayStart(any())
            anyConstructed<AdjustConfig>().setPreinstallTrackingEnabled(any())
            anyConstructed<AdjustConfig>().setEventBufferingEnabled(any())
            anyConstructed<AdjustConfig>().setSendInBackground(any())
            anyConstructed<AdjustConfig>().setDefaultTracker(any())
            anyConstructed<AdjustConfig>().setUrlStrategy(any())
            anyConstructed<AdjustConfig>().setCoppaCompliantEnabled(any())
            anyConstructed<AdjustConfig>().setPlayStoreKidsAppEnabled(any())
        }

        verify {
            Adjust.onCreate(any())
        }
    }

    @Test
    fun initialize_Initializes_WithOptionals_WhenAvailable() {
        every { Adjust.onCreate(any()) } just Runs

        adjustCommand.initialize("token",
            false,
            JSONObject().apply {
                put(ConstConfig.DELAY_START, 5)
                put(ConstConfig.LOG_LEVEL, "verbose")
                put(ConstConfig.SECRET_ID, 0)
                put(ConstConfig.SECRET_INFO_1, 1)
                put(ConstConfig.SECRET_INFO_2, 2)
                put(ConstConfig.SECRET_INFO_3, 3)
                put(ConstConfig.SECRET_INFO_4, 4)
                put(ConstConfig.PREINSTALL_TRACKING, true)
                put(ConstConfig.DEFAULT_TRACKER, "tracker")
                put(ConstConfig.EVENT_BUFFERING_ENABLED, true)
                put(ConstConfig.SEND_IN_BACKGROUND, true)
                put(ConstConfig.URL_STRATEGY, "url_strategy_china")
                put(ConstConfig.COPPA_COMPLIANT, true)
                put(ConstConfig.PLAY_STORE_KIDS_ENABLED, true)
            }
        )

        verify {
            anyConstructed<AdjustConfig>().setAppSecret(0, 1, 2, 3, 4)
            anyConstructed<AdjustConfig>().setLogLevel(LogLevel.VERBOSE)
            anyConstructed<AdjustConfig>().setDelayStart(5.0)
            anyConstructed<AdjustConfig>().setPreinstallTrackingEnabled(true)
            anyConstructed<AdjustConfig>().setEventBufferingEnabled(true)
            anyConstructed<AdjustConfig>().setSendInBackground(true)
            anyConstructed<AdjustConfig>().setDefaultTracker("tracker")
            anyConstructed<AdjustConfig>().setUrlStrategy("url_strategy_china")
            anyConstructed<AdjustConfig>().setCoppaCompliantEnabled(true)
            anyConstructed<AdjustConfig>().setPlayStoreKidsAppEnabled(true)
        }

        verify {
            Adjust.onCreate(any())
        }
    }

    @Test
    fun sendEvent_DoesNotAddNullParams() {
        every { Adjust.trackEvent(any()) } just Runs

        adjustCommand.sendEvent(
            "token",
            null,
            null,
            null,
            null,
            null,
            null
        )

        verify(exactly = 0) {
            anyConstructed<AdjustEvent>().setOrderId(any())
            anyConstructed<AdjustEvent>().setRevenue(any(), any())
            anyConstructed<AdjustEvent>().setCallbackId(any())
            anyConstructed<AdjustEvent>().addCallbackParameter(any(), any())
            anyConstructed<AdjustEvent>().addPartnerParameter(any(), any())
        }

        verify {
            Adjust.trackEvent(any())
        }
    }

    @Test
    fun sendEvent_AddsNonNullParams() {
        every { Adjust.trackEvent(any()) } just Runs

        adjustCommand.sendEvent(
            "token",
            "orderId",
            1.0,
            "USD",
            mapOf("callback_1" to "value_1", "callback_2" to "value_2"),
            mapOf("partner_1" to "value_1", "partner_2" to "value_2"),
            "id"
        )

        verify {
            anyConstructed<AdjustEvent>().setOrderId("orderId")
            anyConstructed<AdjustEvent>().setRevenue(1.0, "USD")
            anyConstructed<AdjustEvent>().setCallbackId("id")
            anyConstructed<AdjustEvent>().addCallbackParameter("callback_1", "value_1")
            anyConstructed<AdjustEvent>().addCallbackParameter("callback_2", "value_2")
            anyConstructed<AdjustEvent>().addPartnerParameter("partner_1", "value_1")
            anyConstructed<AdjustEvent>().addPartnerParameter("partner_2", "value_2")
        }

        verify {
            Adjust.trackEvent(any())
        }
    }

    @Test
    fun addSessionCallbackParams_AddsAllParams() {
        every { Adjust.addSessionCallbackParameter(any(), any()) } just Runs

        adjustCommand.addSessionCallbackParams(
            mapOf(
                "string_1" to "value_1",
                "string_2" to "value_2"
            )
        )

        verify {
            Adjust.addSessionCallbackParameter("string_1", "value_1")
            Adjust.addSessionCallbackParameter("string_2", "value_2")
        }
    }

    @Test
    fun addPartnerCallbackParams_AddsAllParams() {
        every { Adjust.addSessionPartnerParameter(any(), any()) } just Runs

        adjustCommand.addSessionPartnerParams(
            mapOf(
                "string_1" to "value_1",
                "string_2" to "value_2"
            )
        )

        verify {
            Adjust.addSessionPartnerParameter("string_1", "value_1")
            Adjust.addSessionPartnerParameter("string_2", "value_2")
        }
    }

    @Test
    fun removeSessionCallbackParams_RemovesAllParams() {
        every { Adjust.removeSessionCallbackParameter(any()) } just Runs

        adjustCommand.removeSessionCallbackParams(
            listOf(
                "string_1",
                "string_2"
            )
        )

        verify {
            Adjust.removeSessionCallbackParameter("string_1")
            Adjust.removeSessionCallbackParameter("string_2")
        }
    }

    @Test
    fun addPartnerCallbackParams_RemovesAllParams() {
        every { Adjust.removeSessionCallbackParameter(any()) } just Runs

        adjustCommand.removeSessionPartnerParams(
            listOf(
                "string_1",
                "string_2"
            )
        )

        verify {
            Adjust.removeSessionPartnerParameter("string_1")
            Adjust.removeSessionPartnerParameter("string_2")
        }
    }

    @Test
    fun resetSessionCallbackParams_ResetsParams() {
        every { Adjust.resetSessionCallbackParameters() } just Runs

        adjustCommand.resetSessionCallbackParams()

        verify {
            Adjust.resetSessionCallbackParameters()
        }
    }

    @Test
    fun resetPartnerCallbackParams_ResetsParams() {
        every { Adjust.resetSessionPartnerParameters() } just Runs

        adjustCommand.resetSessionPartnerParams()

        verify {
            Adjust.resetSessionPartnerParameters()
        }
    }

    @Test
    fun setPushToken_SetsPushToken() {
        every { Adjust.setPushToken(any(), any()) } just Runs

        adjustCommand.setPushToken("token")

        verify {
            Adjust.setPushToken("token", mockApp)
        }
    }

    @Test
    fun setEnabled_EnablesOrDisables() {
        every { Adjust.setEnabled(any()) } just Runs

        adjustCommand.setEnabled(true)
        adjustCommand.setEnabled(false)

        verifyOrder {
            Adjust.setEnabled(true)
            Adjust.setEnabled(false)
        }
    }

    @Test
    fun setOfflineMode_SetsOffline() {
        every { Adjust.setOfflineMode(any()) } just Runs

        adjustCommand.setOfflineMode(true)
        adjustCommand.setOfflineMode(false)

        verifyOrder {
            Adjust.setOfflineMode(true)
            Adjust.setOfflineMode(false)
        }
    }

    @Test
    fun trackMeasurementConsent_EnablesOrDisables() {
        every { Adjust.trackMeasurementConsent(any()) } just Runs

        adjustCommand.trackMeasurementConsent(true)
        adjustCommand.trackMeasurementConsent(false)

        verifyOrder {
            Adjust.trackMeasurementConsent(true)
            Adjust.trackMeasurementConsent(false)
        }
    }

    @Test
    fun setThirdPartySharing_Disables() {
        every { Adjust.disableThirdPartySharing(any()) } just Runs

        adjustCommand.setThirdPartySharing(false)

        verifyOrder {
            Adjust.disableThirdPartySharing(mockApp)
        }
    }

    @Test
    fun setThirdPartySharing_Enables() {
        every { Adjust.disableThirdPartySharing(any()) } just Runs

        adjustCommand.setThirdPartySharing(true)

        verifyOrder {
            Adjust.trackThirdPartySharing(any())
        }
    }

    @Test
    fun gdprForgetMe_Forgets() {
        every { Adjust.gdprForgetMe(any()) } just Runs

        adjustCommand.gdprForgetMe()

        verifyOrder {
            Adjust.gdprForgetMe(mockApp)
        }
    }
}