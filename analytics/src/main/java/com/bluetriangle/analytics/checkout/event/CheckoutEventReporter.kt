package com.bluetriangle.analytics.checkout.event

import java.net.HttpURLConnection

class CheckoutEventReporter {

    fun onCheckoutEvent(event: CheckoutEvent) {
        when(event) {
            is CheckoutEvent.ClassEvent -> {

            }
            is CheckoutEvent.NetworkEvent -> {
                if(event.statusCode in 200..220) {

                }
            }
        }
    }

}