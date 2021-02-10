package com.tealium.example.helper

object DataLayer {
    // Screen Properties
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"

    // Event Properties
    const val EVENT_TOKEN = "event_token"

    // User Properties
    const val CUSTOMER_ID = "customer_id"
    const val USERNAME = "username"

    // Product Properties
    const val PRODUCT_ID = "product_id"
    const val PRODUCT_NAME = "product_name"
    const val PRODUCT_CATEGORY = "product_category"
    const val PRODUCT_QUANTITY = "product_quantity"
    const val PRODUCT_PRICE = "product_price"

    // Order Properties
    const val ORDER_CURRENCY = "order_currency"
    const val ORDER_ID = "order_id"
    const val ORDER_TOTAL = "order_total"

    object EventTokens {
        const val LAUNCH = "f35pky"
        const val PURCHASE = "37obwl"
    }
}