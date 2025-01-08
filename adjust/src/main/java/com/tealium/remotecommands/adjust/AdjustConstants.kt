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

    const val ADD_GLOBAL_CALLBACK_PARAMS = "addglobalcallbackparams"
    const val REMOVE_GLOBAL_CALLBACK_PARAMS = "removeglobalcallbackparams"
    const val RESET_GLOBAL_CALLBACK_PARAMS = "resetglobalcallbackparams"
    const val ADD_GLOBAL_PARTNER_PARAMS = "addglobalpartnerparams"
    const val REMOVE_GLOBAL_PARTNER_PARAMS = "removeglobalpartnerparams"
    const val RESET_GLOBAL_PARTNER_PARAMS = "resetglobalpartnerparams"
}

object Config {

    const val API_TOKEN = "api_token"
    const val SANDBOX = "sandbox"
    const val SETTINGS = "settings"
    const val LOG_LEVEL = "log_level"
    const val PREINSTALL_TRACKING = "preinstall_tracking"
    const val DEFAULT_TRACKER = "default_tracker"
    const val SEND_IN_BACKGROUND = "send_in_background"
    const val URL_STRATEGY = "url_strategy"
    const val URL_STRATEGY_DOMAINS = "url_strategy_domains"
    const val URL_STRATEGY_USE_SUBDOMAIN = "url_strategy_use_subdomain"
    const val URL_STRATEGY_IS_RESIDENCY = "url_strategy_is_residency"
    const val COPPA_COMPLIANT = "coppa_compliant"
    const val PLAY_STORE_KIDS_ENABLED = "play_store_kids_enabled"
    const val DEDUPLICATION_ID_MAX_SIZE = "deduplication_id_max_size"
}

object Misc {
    const val ENABLED = "enabled"
    const val OFFLINE = "offline"
    const val PUSH_TOKEN = "push_token"
    const val MEASUREMENT_CONSENT = "measurement_consent"
    const val THIRD_PARTY_SHARING_ENABLED = "third_party_sharing_enabled"
    const val THIRD_PARTY_SHARING_OPTIONS = "third_party_sharing_options"
}

object Events {
    const val EVENT_TOKEN = "event_token"
    const val REVENUE = "revenue"
    const val CURRENCY = "currency"
    const val ORDER_ID = "order_id"
    const val DEDUPLICATION_ID = "deduplication_id"
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

    const val GLOBAL_CALLBACK_PARAMETERS = "global_callback"
    const val GLOBAL_PARTNER_PARAMETERS = "global_partner"
    const val REMOVE_GLOBAL_CALLBACK_PARAMETERS = "remove_global_callback_params"
    const val REMOVE_GLOBAL_PARTNER_PARAMETERS = "remove_global_partner_params"

    const val DEEPLINK_URL = "deeplink_open_url"
    const val AD_REVENUE_SOURCE = "ad_revenue_source"
    const val AD_REVENUE_PAYLOAD = "ad_revenue_payload"
    const val AD_REVENUE_UNIT = "unit"
    const val AD_REVENUE_NETWORK = "network"
    const val AD_REVENUE_AMOUNT = "amount"
    const val AD_REVENUE_CURRENCY = "currency"
    const val AD_REVENUE_PLACEMENT = "placement"
    const val AD_REVENUE_IMPRESSIONS_COUNT = "impressions_count"
}