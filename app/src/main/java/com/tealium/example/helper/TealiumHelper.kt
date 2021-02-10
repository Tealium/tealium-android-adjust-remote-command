package com.tealium.example.helper

import android.app.Activity
import android.app.Application
import android.webkit.WebView
import com.tealium.core.*
import com.tealium.dispatcher.TealiumEvent
import com.tealium.dispatcher.TealiumView
import com.tealium.example.BuildConfig
import com.tealium.remotecommanddispatcher.RemoteCommands
import com.tealium.remotecommanddispatcher.remoteCommands
import com.tealium.remotecommands.adjust.AdjustRemoteCommand

object TealiumHelper {
    private const val TAG = "TealiumHelper"

    const val TEALIUM_MAIN = "main"

    @JvmStatic
    fun initialize(application: Application) {
        WebView.setWebContentsDebuggingEnabled(true)

        val config = TealiumConfig(
            application,
            BuildConfig.TEALIUM_ACCOUNT,
            BuildConfig.TEALIUM_PROFILE,
            Environment.valueOf(BuildConfig.TEALIUM_ENVIRONMENT)
        ).apply {
            useRemoteLibrarySettings = true

            // TagManagement controlled RemoteCommand
            // dispatchers.add(Dispatchers.TagManagement)

            // JSON controlled RemoteCommand
            dispatchers.add(Dispatchers.RemoteCommands)
        }

        val adjustRemoteCommand = AdjustRemoteCommand(application)

        Tealium.create(TEALIUM_MAIN, config) {
            // Remote Command Tag - requires TiQ
            // remoteCommands?.add(adjustRemoteCommand)

            // JSON Remote Command - requires filename
            remoteCommands?.add(adjustRemoteCommand, "adjust.json")
        }
    }

    @JvmStatic
    fun trackView(viewName: String, data: Map<String, Any>? = null) {
        val instance: Tealium? = Tealium[TEALIUM_MAIN]

        // Instance can be remotely destroyed through publish settings
        instance?.track(TealiumView(viewName, data))
    }

    @JvmStatic
    fun trackEvent(eventName: String, data: Map<String, Any>? = null) {
        val instance: Tealium? = Tealium[TEALIUM_MAIN]

        // Instance can be remotely destroyed through publish settings
        instance?.track(TealiumEvent(eventName, data))
    }

    @JvmStatic
    fun trackScreen(activity: Activity, screenName: String, data: Map<String, Any>? = null) {
        trackView(
            "screen_view",
            mutableMapOf<String, Any>(
                DataLayer.SCREEN_NAME to screenName,
                DataLayer.SCREEN_CLASS to activity.javaClass.simpleName
            ).apply {
                data?.let {
                    putAll(it)
                }
            }
        )
    }
}