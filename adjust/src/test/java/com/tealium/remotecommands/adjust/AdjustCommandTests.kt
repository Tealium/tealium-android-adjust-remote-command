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

        every { anyConstructed<AdjustEvent>().orderId = any() } just Runs

        adjustCommand = AdjustInstance(mockApp)
    }

    @Test
    fun initialize_Initializes_WhenRequiredParamsAvailable() {
        every { Adjust.initSdk(any()) } just Runs

        adjustCommand.initialize(
            "token",
            false,
            JSONObject()
        )

        verify(exactly = 0) {
            anyConstructed<AdjustConfig>().setLogLevel(any())
            anyConstructed<AdjustConfig>().enablePreinstallTracking()
            anyConstructed<AdjustConfig>().enableSendingInBackground()
            anyConstructed<AdjustConfig>().defaultTracker = any()
            anyConstructed<AdjustConfig>().setUrlStrategy(any(), any(), any())
            anyConstructed<AdjustConfig>().enableCoppaCompliance()
            anyConstructed<AdjustConfig>().enablePlayStoreKidsCompliance()
        }

        verify {
            Adjust.initSdk(any())
        }
    }

    @Test
    fun initialize_Initializes_WithOptionals_WhenAvailable() {
        every { Adjust.initSdk(any()) } just Runs

        adjustCommand.initialize("token",
            false,
            JSONObject().apply {
                put(ConstConfig.LOG_LEVEL, "verbose")
                put(ConstConfig.PREINSTALL_TRACKING, true)
                put(ConstConfig.DEFAULT_TRACKER, "tracker")
                put(ConstConfig.SEND_IN_BACKGROUND, true)
                put(ConstConfig.URL_STRATEGY, "UrlStrategyChina")
                put(ConstConfig.COPPA_COMPLIANT, true)
                put(ConstConfig.PLAY_STORE_KIDS_ENABLED, true)
                put(ConstConfig.DEDUPLICATION_ID_MAX_SIZE, 3)
            }
        )

        verify {
            anyConstructed<AdjustConfig>().setLogLevel(LogLevel.VERBOSE)
            anyConstructed<AdjustConfig>().enablePreinstallTracking()
            anyConstructed<AdjustConfig>().enableSendingInBackground()
            anyConstructed<AdjustConfig>().defaultTracker = "tracker"
            anyConstructed<AdjustConfig>().setUrlStrategy(listOf("adjust.world", "adjust.com"), true, false)
            anyConstructed<AdjustConfig>().enableCoppaCompliance()
            anyConstructed<AdjustConfig>().enablePlayStoreKidsCompliance()
            anyConstructed<AdjustConfig>().eventDeduplicationIdsMaxSize = 3
        }

        verify {
            Adjust.initSdk(any())
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
            null,
            null
        )

        verify(exactly = 0) {
            anyConstructed<AdjustEvent>().orderId = any()
            anyConstructed<AdjustEvent>().setRevenue(any(), any())
            anyConstructed<AdjustEvent>().callbackId = any()
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
            "dedupId",
            1.0,
            "USD",
            mapOf("callback_1" to "value_1", "callback_2" to "value_2"),
            mapOf("partner_1" to "value_1", "partner_2" to "value_2"),
            "id"
        )

        verify {
            anyConstructed<AdjustEvent>().orderId = "orderId"
            anyConstructed<AdjustEvent>().deduplicationId = "dedupId"
            anyConstructed<AdjustEvent>().setRevenue(1.0, "USD")
            anyConstructed<AdjustEvent>().callbackId = "id"
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
    fun addGlobalCallbackParams_AddsAllParams() {
        every { Adjust.addGlobalCallbackParameter(any(), any()) } just Runs

        adjustCommand.addGlobalCallbackParams(
            mapOf(
                "string_1" to "value_1",
                "string_2" to "value_2"
            )
        )

        verify {
            Adjust.addGlobalCallbackParameter("string_1", "value_1")
            Adjust.addGlobalCallbackParameter("string_2", "value_2")
        }
    }

    @Test
    fun addPartnerCallbackParams_AddsAllParams() {
        every { Adjust.addGlobalPartnerParameter(any(), any()) } just Runs

        adjustCommand.addGlobalPartnerParams(
            mapOf(
                "string_1" to "value_1",
                "string_2" to "value_2"
            )
        )

        verify {
            Adjust.addGlobalPartnerParameter("string_1", "value_1")
            Adjust.addGlobalPartnerParameter("string_2", "value_2")
        }
    }

    @Test
    fun removeGlobalCallbackParams_RemovesAllParams() {
        every { Adjust.removeGlobalCallbackParameter(any()) } just Runs

        adjustCommand.removeGlobalCallbackParams(
            listOf(
                "string_1",
                "string_2"
            )
        )

        verify {
            Adjust.removeGlobalCallbackParameter("string_1")
            Adjust.removeGlobalCallbackParameter("string_2")
        }
    }

    @Test
    fun addPartnerCallbackParams_RemovesAllParams() {
        every { Adjust.removeGlobalCallbackParameter(any()) } just Runs

        adjustCommand.removeGlobalPartnerParams(
            listOf(
                "string_1",
                "string_2"
            )
        )

        verify {
            Adjust.removeGlobalPartnerParameter("string_1")
            Adjust.removeGlobalPartnerParameter("string_2")
        }
    }

    @Test
    fun resetGlobalCallbackParams_ResetsParams() {
        every { Adjust.removeGlobalCallbackParameters() } just Runs

        adjustCommand.resetGlobalCallbackParams()

        verify {
            Adjust.removeGlobalCallbackParameters()
        }
    }

    @Test
    fun resetPartnerCallbackParams_ResetsParams() {
        every { Adjust.removeGlobalPartnerParameters() } just Runs

        adjustCommand.resetGlobalPartnerParams()

        verify {
            Adjust.removeGlobalPartnerParameters()
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
        every { Adjust.enable() } just Runs
        every { Adjust.disable() } just Runs

        adjustCommand.setEnabled(true)
        adjustCommand.setEnabled(false)

        verifyOrder {
            Adjust.enable()
            Adjust.disable()
        }
    }

    @Test
    fun setOfflineMode_SetsOffline() {
        every { Adjust.switchToOfflineMode() } just Runs
        every { Adjust.switchBackToOnlineMode() } just Runs

        adjustCommand.setOfflineMode(true)
        adjustCommand.setOfflineMode(false)

        verifyOrder {
            Adjust.switchToOfflineMode()
            Adjust.switchBackToOnlineMode()
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
    fun setThirdPartySharing_NullDoesNotCallAdjust() {
        adjustCommand.setThirdPartySharing(null, null)

        verify(inverse = true) {
            Adjust.trackThirdPartySharing(any())
        }
    }

    @Test
    fun setThirdPartySharing_Disables() {
        adjustCommand.setThirdPartySharing(false, null)

        verifyOrder {
            Adjust.trackThirdPartySharing(match {
                it.enabled == false
            })
        }
    }

    @Test
    fun setThirdPartySharing_Enables() {
        adjustCommand.setThirdPartySharing(true, null)

        verifyOrder {
            Adjust.trackThirdPartySharing(match {
                it.enabled == true
            })
        }
    }

    @Test
    fun setThirdPartySharing_WithOptions_MapsOptions() {
        val options = JSONObject().apply {
            put("facebook", JSONObject().apply {
                put("data_processing_options_country", "1")
                put("data_processing_options_state", 1000)
            })
            put("google_dma", JSONObject().apply {
                put("eea", 1)
                put("ad_personalization", 1)
                put("ad_user_data", "0")
            })
        }

        adjustCommand.setThirdPartySharing(null, options)

        verify {
            Adjust.trackThirdPartySharing(match {
                val facebook = it.granularOptions["facebook"]!!
                val google = it.granularOptions["google_dma"]!!

                facebook["data_processing_options_country"] == "1"
                        && facebook["data_processing_options_state"] == "1000"
                        && google["eea"] == "1"
                        && google["ad_personalization"] == "1"
                        && google["ad_user_data"] == "0"
            })
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