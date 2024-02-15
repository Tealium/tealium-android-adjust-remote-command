package com.tealium.example.helper

object DataLayer {
    // Screen Properties
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"

    // Event Properties
    const val EVENT_TOKEN = "event_token"
    const val EVENT_PARAM_1 = "event_param_1"
    const val EVENT_PARAM_2 = "event_param_2"
    const val PARTNER_PARAM_1 = "partner_param_1"
    const val PARTNER_PARAM_2 = "partner_param_2"
    const val CALLBACK_ID = "callback_id"

    // Order Properties
    const val ORDER_CURRENCY = "order_currency"
    const val ORDER_ID = "order_id"
    const val ORDER_TOTAL = "order_total"

    // Ad
    const val SOURCE = "source"
    const val PAYLOAD = "payload"

    const val DEEPLINK_URL = "deeplink_url"

    // Subscription
    const val SKU = "sku"
    const val SIGNATURE = "signature"
    const val PURCHASE_TOKEN = "purchase_token"

    const val CONSENT_GRANTED = "consent_granted"
    const val THIRD_PARTY_SHARING_OPTS = "consent_opts"
    const val ENABLED = "enabled"

    object EventTokens {
        const val LAUNCH = "f35pky"
        const val PURCHASE = "37obwl"
        const val CONTACT = "t54mvx"
        const val EVENT = "gtb7m2"
    }
}