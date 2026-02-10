package com.bluetriangle.analytics.checkout

import com.bluetriangle.analytics.checkout.config.CheckoutConfig
import com.bluetriangle.analytics.networkcapture.CapturedRequest

class NetworkObserver(private var config: CheckoutConfig) {

    fun onNetworkRequestReceived(request: CapturedRequest) {
    }

}