package com.bluetriangle.analytics.checkout.event

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.checkout.config.CheckoutConfig
import com.bluetriangle.analytics.utility.logD
import com.bluetriangle.analytics.utility.logV

class CheckoutEventReporter(private var _config: CheckoutConfig) {

    val config: CheckoutConfig
        get() = _config

    companion object {
        const val TAG = "CheckoutEventReporter"
    }

    private var lastEvent: CheckoutEvent? = null

    fun onCheckoutEvent(event: CheckoutEvent) {
        if(!config.isEnabled) {
            logV(TAG, "sdk-event-ignored (reason:checkout-config-disabled)")
            return
        }

        logV(TAG, "checkout-event-received (data: ${event::class.java.simpleName}())")
        val shouldReport: Boolean = event.shouldReport(config, lastEvent)

        if(shouldReport) {
            lastEvent = event
            reportCheckout()
        }
    }

    fun updateConfig(config: CheckoutConfig) {
        this._config = config
        logD(TAG, "received-updated-checkout-config (data: ${config})")
    }

    private fun reportCheckout() {
        val checkoutTimer = Timer("PurchaseConfirmation", null)
        checkoutTimer.startWithoutPerformanceMonitor()
        checkoutTimer.setCartCount(config.cartCount)
        checkoutTimer.setCartCountCheckout(config.cartCountCheckout)
        config.orderNumber?.let {
            checkoutTimer.setOrderNumber(it)
        }
        checkoutTimer.setCartValue(config.checkoutAmount)
        checkoutTimer.submit()
        logD(TAG, "checkout-event-submitted")
    }

}