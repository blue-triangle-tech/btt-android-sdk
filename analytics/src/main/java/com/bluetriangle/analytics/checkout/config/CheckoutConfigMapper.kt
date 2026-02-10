package com.bluetriangle.analytics.checkout.config

import com.bluetriangle.analytics.Constants.DEFAULT_CART_COUNT
import com.bluetriangle.analytics.Constants.DEFAULT_CART_COUNT_CHECKOUT
import com.bluetriangle.analytics.Constants.DEFAULT_CHECKOUT_AMOUNT
import com.bluetriangle.analytics.Constants.DEFAULT_TIMER_VALUE
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfigurationMapper
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import com.bluetriangle.analytics.utility.getStringOrNull
import org.json.JSONObject

object CheckoutConfigMapper {
    private const val CLASS_NAME = "className"
    private const val GROUP_NAME = "groupName"
    private const val CHECKOUT_AMOUNT = "checkoutAmount"
    private const val CART_COUNT = "cartCount"
    private const val CART_COUNT_CHECKOUT = "cartCountCheckout"
    private const val ORDER_NUMBER = "orderNumber"
    private const val TIMER_VALUE = "timerValue"

    fun loadFromJsonObject(jsonObject: JSONObject): CheckoutConfig {
        val className = jsonObject.getStringOrNull(CLASS_NAME)
        val groupName = jsonObject.getStringOrNull(GROUP_NAME)
        val checkoutAmount = jsonObject.getDoubleOrNull(CHECKOUT_AMOUNT)?:DEFAULT_CHECKOUT_AMOUNT
        val cartCount = jsonObject.getIntOrNull(CART_COUNT)?:DEFAULT_CART_COUNT
        val cartCountCheckout = jsonObject.getIntOrNull(CART_COUNT_CHECKOUT)?:DEFAULT_CART_COUNT_CHECKOUT
        val orderNumber = jsonObject.getStringOrNull(ORDER_NUMBER)
        val timerValue = jsonObject.getIntOrNull(TIMER_VALUE)?:DEFAULT_TIMER_VALUE

        return CheckoutConfig(
            true,
            className,
            "",
            "",
            checkoutAmount,
            cartCount,
            cartCountCheckout,
            orderNumber,
            timerValue
        )
    }

    fun loadIntoJsonObject(jsonObject: JSONObject, config: CheckoutConfig) {
        jsonObject.put(CLASS_NAME, config.className)
        jsonObject.put(GROUP_NAME, config.groupName)
        jsonObject.put(CHECKOUT_AMOUNT, config.checkoutAmount)
        jsonObject.put(CART_COUNT, config.cartCount)
        jsonObject.put(CART_COUNT_CHECKOUT, config.cartCountCheckout)
        jsonObject.put(ORDER_NUMBER, config.orderNumber)
        jsonObject.put(TIMER_VALUE, config.timerValue)
    }
}