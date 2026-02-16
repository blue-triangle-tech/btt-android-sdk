package com.bluetriangle.analytics.checkout.config

import com.bluetriangle.analytics.Constants.DEFAULT_CART_COUNT
import com.bluetriangle.analytics.Constants.DEFAULT_CART_COUNT_CHECKOUT
import com.bluetriangle.analytics.Constants.DEFAULT_CHECKOUT_AMOUNT
import com.bluetriangle.analytics.Constants.DEFAULT_CHECKOUT_TRACKING_ENABLED
import com.bluetriangle.analytics.Constants.DEFAULT_TIMER_VALUE
import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import com.bluetriangle.analytics.utility.getStringOrNull
import org.json.JSONObject

object CheckoutConfigMapper {
    private const val CHECKOUT_TRACKING_ENABLED = "checkoutTrackingEnabled"
    private const val CLASS_NAME = "checkoutClassName"
    private const val CHECKOUT_AMOUNT = "checkoutAmount"
    private const val CHECKOUT_URL = "checkoutURL"
    private const val CART_COUNT = "checkoutCartCount"
    private const val CART_COUNT_CHECKOUT = "checkoutCartCountCheckout"
    private const val ORDER_NUMBER = "checkoutOrderNumber"
    private const val TIMER_VALUE = "checkoutTimeValue"

    fun loadFromJsonObject(jsonObject: JSONObject): CheckoutConfig {
        val isEnabled = jsonObject.getBooleanOrNull(CHECKOUT_TRACKING_ENABLED)?:DEFAULT_CHECKOUT_TRACKING_ENABLED
        val className = jsonObject.getStringOrNull(CLASS_NAME)
        val networkUrlPattern = jsonObject.getStringOrNull(CHECKOUT_URL)
        val checkoutAmount = jsonObject.getDoubleOrNull(CHECKOUT_AMOUNT)?:DEFAULT_CHECKOUT_AMOUNT
        val cartCount = jsonObject.getIntOrNull(CART_COUNT)?:DEFAULT_CART_COUNT
        val cartCountCheckout = jsonObject.getIntOrNull(CART_COUNT_CHECKOUT)?:DEFAULT_CART_COUNT_CHECKOUT
        val orderNumber = jsonObject.getStringOrNull(ORDER_NUMBER)
        val timerValue = jsonObject.getIntOrNull(TIMER_VALUE)?:DEFAULT_TIMER_VALUE

        return CheckoutConfig(
            isEnabled,
            className,
            networkUrlPattern,
            checkoutAmount,
            cartCount,
            cartCountCheckout,
            orderNumber,
            timerValue
        )
    }

    fun loadIntoJsonObject(jsonObject: JSONObject, config: CheckoutConfig) {
        jsonObject.put(CHECKOUT_TRACKING_ENABLED, config.isEnabled)
        jsonObject.put(CLASS_NAME, config.className)
        jsonObject.put(CHECKOUT_URL, config.networkUrlPattern)
        jsonObject.put(CHECKOUT_AMOUNT, config.checkoutAmount)
        jsonObject.put(CART_COUNT, config.cartCount)
        jsonObject.put(CART_COUNT_CHECKOUT, config.cartCountCheckout)
        jsonObject.put(ORDER_NUMBER, config.orderNumber)
        jsonObject.put(TIMER_VALUE, config.timerValue)
    }
}