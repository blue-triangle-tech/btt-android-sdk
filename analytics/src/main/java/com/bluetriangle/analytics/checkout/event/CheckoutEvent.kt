package com.bluetriangle.analytics.checkout.event

sealed class CheckoutEvent {

    class ClassEvent(val className: String): CheckoutEvent()

    class NetworkEvent(val url: String, val statusCode: Int): CheckoutEvent()

}