package com.bluetriangle.analytics.checkout.event

import com.bluetriangle.analytics.checkout.config.CheckoutConfig
import com.bluetriangle.analytics.utility.logD
import com.bluetriangle.analytics.utility.logV
import com.bluetriangle.analytics.utility.onFalse

sealed class CheckoutEvent {

    class ClassEvent(val className: String): CheckoutEvent() {
        override fun toString(): String {
            return "${super.toString()}(className: \"$className\")"
        }

        override val eventID: Int
            get() =  0

        override fun shouldReport(
            config: CheckoutConfig,
            lastEvent: CheckoutEvent?
        ): Boolean {
            val configClassNames = config.classNames
            var notReportingReason: String? = null

            val classNameIsSameAsConfig = {
                configClassNames.contains(className).onFalse {
                    notReportingReason = "class-name-not-in-list"
                }
            }
            val classNameIsNotSameAsLastEvent = {
                (!(lastEvent is ClassEvent && className == lastEvent.className)).onFalse {
                    notReportingReason = "duplicate-class-name"
                }
            }

            return (classNameIsSameAsConfig() and classNameIsNotSameAsLastEvent()).onFalse {
                logV(message = "not-reporting-checkout-for-class-name (reason: $notReportingReason)")
            }
        }
    }

    class NetworkEvent(val url: String?, val statusCode: Int?): CheckoutEvent() {
        companion object {
            val SUCCESS_STATUS_CODE_RANGE = 200..299
        }

        val isSuccess: Boolean
            get() = statusCode in SUCCESS_STATUS_CODE_RANGE

        override val eventID: Int
            get() =  1

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

            var notReportingReason: String? = null

            if(!isSuccess) {
                notReportingReason = "network-call-not-success"
            }

            val urlMatchesPattern = {
                url.matches(networkUrlPattern.replace("*", ".*").toRegex()).onFalse {
                    notReportingReason = "network-url-not-matches"
                }
            }

            val urlIsNotSameAsLastEvent = {
                (!(lastEvent is NetworkEvent && lastEvent.url == url && lastEvent.isSuccess)).onFalse {
                    notReportingReason = "duplicate-network-call"
                }
            }

            return (isSuccess and urlMatchesPattern() and urlIsNotSameAsLastEvent()).onFalse {
                logV(message = "not-reporting-checkout-for-network-call (reason: $notReportingReason)")
            }
        }
    }

    override fun toString(): String {
        return "${this::class.java.simpleName}"
    }

    abstract val eventID: Int

    abstract fun shouldReport(config: CheckoutConfig, lastEvent: CheckoutEvent?): Boolean

}