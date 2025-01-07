package com.tealium.remotecommands.adjust

data class UrlStrategy(
    val domains: List<String>,
    val useSubdomains: Boolean,
    val isDataResidency: Boolean
) {
    companion object {
        val defaultStrategies: Map<String, UrlStrategy> = mapOf(
            "DataResidencyEU" to UrlStrategy(
                domains = listOf("eu.adjust.com"),
                useSubdomains = true,
                isDataResidency = true
            ),
            "DataResidencyTR" to UrlStrategy(
                domains = listOf("tr.adjust.com"),
                useSubdomains = true,
                isDataResidency = true
            ),
            "ADJDataResidencyUS" to UrlStrategy(
                domains = listOf("us.adjust.com"),
                useSubdomains = true,
                isDataResidency = true
            ),
            "UrlStrategyChina" to UrlStrategy(
                domains = listOf("adjust.world", "adjust.com"),
                useSubdomains = true,
                isDataResidency = false
            ),
            "UrlStrategyCn" to UrlStrategy(
                domains = listOf("adjust.cn", "adjust.com"),
                useSubdomains = true,
                isDataResidency = false
            ),
            "UrlStrategyCnOnly" to UrlStrategy(
                domains = listOf("adjust.cn"),
                useSubdomains = true,
                isDataResidency = false
            ),
            "UrlStrategyIndia" to UrlStrategy(
                domains = listOf("adjust.net.in", "adjust.com"),
                useSubdomains = true,
                isDataResidency = false
            )
        )
    }
}
