package com.bluetriangle.analytics.checkout.event

import com.bluetriangle.analytics.checkout.config.CheckoutConfig
import com.bluetriangle.analytics.utility.logD

sealed class CheckoutEvent {

    class ClassEvent(val className: String): CheckoutEvent() {
        override fun toString(): String {
            return "${super.toString()}(className: \"$className\")"
        }

        override fun shouldReport(
            config: CheckoutConfig,
            lastEvent: CheckoutEvent?
        ): Boolean {
            val configClassName = config.className?: return false
            val classNameIsSameAsConfig = { configClassName == className }
            val classNameIsNotSameAsLastEvent = { !(lastEvent is ClassEvent && className == lastEvent.className) }

            return classNameIsSameAsConfig() and classNameIsNotSameAsLastEvent()
        }
    }

    class NetworkEvent(val url: String?, val statusCode: Int?): CheckoutEvent() {
        companion object {
            val SUCCESS_STATUS_CODE_RANGE = 200..299
        }

        val isSuccess: Boolean
            get() = statusCode in SUCCESS_STATUS_CODE_RANGE

        override fun toString(): String {
            return "${super.toString()}(url: \"$url\", statusCode: $statusCode)"
        }

        override fun shouldReport(
            config: CheckoutConfig,
            lastEvent: CheckoutEvent?
        ): Boolean {
            if(url == null || statusCode == null) {
                logD(message = "not-reporting-checkout-for-network-call (reason: data-is-incomplete, data: ${this})")
                return false
            }
            val networkUrlPattern = config.networkUrlPattern?: return false

            val urlMatchesPattern = {
                url.matches(networkUrlPattern.replace("*", ".*").toRegex())
            }

            return isSuccess && urlMatchesPattern()
        }
    }

    override fun toString(): String {
        return "${this::class.java.simpleName}"
    }

    abstract fun shouldReport(config: CheckoutConfig, lastEvent: CheckoutEvent?): Boolean

}