package com.tealium.remotecommands.adjust

data class UrlStrategy(
    val domains: List<String>,
    val useSubdomains: Boolean,
    val isDataResidency: Boolean
) {
    companion object {
        val defaultStrategies: Map<String, UrlStrategy> = mapOf(
            "DataResidencyEU" to UrlStrategy(listOf("eu.adjust.com"), true, true),
            "DataResidencyTR" to UrlStrategy(listOf("tr.adjust.com"), true, true),
            "ADJDataResidencyUS" to UrlStrategy(listOf("us.adjust.com"), true, true),
            "UrlStrategyChina" to UrlStrategy(listOf("adjust.world", "adjust.com"), true, false),
            "UrlStrategyCn" to UrlStrategy(listOf("adjust.cn", "adjust.com"), true, false),
            "UrlStrategyCnOnly" to UrlStrategy(listOf("adjust.cn"), true, false),
            "UrlStrategyIndia" to UrlStrategy(listOf("adjust.net.in", "adjust.com"), true, false)
        )
    }
}
