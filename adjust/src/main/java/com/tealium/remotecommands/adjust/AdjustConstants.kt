package com.tealium.remotecommands.adjust

object AdjustConstants {
    const val TAG = "Tealium-Adjust"
    const val SEPARATOR = ","
}

object Commands {
    const val COMMAND_NAME = "command_name"

    const val INITIALIZE = "initialize"
    const val TRACK_EVENT = "trackevent"
    const val TRACK_SUBSCRIPTION = "tracksubscription"
    const val TRACK_AD_REVENUE = "trackadrevenue"
    const val TRACK_DEEPLINK = "appwillopenurl"
    const val SET_PUSH_TOKEN = "setpushtoken"
    const val SET_ENABLED = "setenabled"
    const val SET_OFFLINE_MODE = "setofflinemode"
    const val GDPR_FORGET_ME = "gdprforgetme"
    const val SET_THIRD_PARTY_SHARING = "setthirdpartysharing"
    const val TRACK_MEASUREMENT_CONSENT = "trackmeasurementconsent"

    const val ADD_SESSION_CALLBACK_PARAMS = "addsessioncallbackparams"
    const val REMOVE_SESSION_CALLBACK_PARAMS = "removesessioncallbackparams"
    const val RESET_SESSION_CALLBACK_PARAMS = "resetsessioncallbackparams"
    const val ADD_SESSION_PARTNER_PARAMS = "addsessionpartnerparams"
    const val REMOVE_SESSION_PARTNER_PARAMS = "removesessionpartnerparams"
    const val RESET_SESSION_PARTNER_PARAMS = "resetsessionpartnerparams"
}

object Config {

    const val API_TOKEN = "api_token"
    const val SANDBOX = "sandbox"
    const val SETTINGS = "settings"

    const val SECRET_ID = "app_secret"
    const val SECRET_INFO_1 = "app_secret_info_1"
    const val SECRET_INFO_2 = "app_secret_info_2"
    const val SECRET_INFO_3 = "app_secret_info_3"
    const val SECRET_INFO_4 = "app_secret_info_4"
    const val LOG_LEVEL = "log_level"
    const val DELAY_START = "delay_start"
    const val PREINSTALL_TRACKING = "preinstall_tracking"
    const val DEFAULT_TRACKER = "default_tracker"
    const val EVENT_BUFFERING_ENABLED = "event_buffering_enabled"
    const val SEND_IN_BACKGROUND = "send_in_background"
}

object Misc {
    const val ENABLED = "enabled"
    const val OFFLINE = "offline"
    const val PUSH_TOKEN = "push_token"
    const val MEASUREMENT_CONSENT = "measurement_consent"
    const val THIRD_PARTY_SHARING_ENABLED = "third_party_sharing_enabled"
}

object Events {
    const val EVENT_TOKEN = "event_token"
    const val REVENUE = "revenue"
    const val CURRENCY = "currency"
    const val ORDER_ID = "order_id"
    const val SKU = "sku"
    const val SIGNATURE = "signature"
    const val PURCHASE_TOKEN = "purchase_token"
    const val PURCHASE_TIME = "purchase_time"

    const val CALLBACK_ID = "callback_id"
    const val CALLBACK_PARAMETERS = "callback"
    const val PARTNER_PARAMETERS = "partner"
    const val SESSION_CALLBACK_PARAMETERS = "session_callback"
    const val SESSION_PARTNER_PARAMETERS = "session_partner"
    const val REMOVE_SESSION_CALLBACK_PARAMETERS = "remove_session_callback_params"
    const val REMOVE_SESSION_PARTNER_PARAMETERS = "remove_session_partner_params"

    const val DEEPLINK_URL = "deeplink_open_url"
    const val AD_REVENUE_SOURCE = "ad_revenue_source"
    const val AD_REVENUE_PAYLOAD = "ad_revenue_payload"
}