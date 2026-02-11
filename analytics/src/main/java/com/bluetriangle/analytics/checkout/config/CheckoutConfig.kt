package com.bluetriangle.analytics.checkout.config

class CheckoutConfig(
    val isEnabled: Boolean,
    val className: String?,
    val networkUrl: String?,
    val checkoutAmount: Double,
    val cartCount: Int,
    val cartCountCheckout: Int,
    val orderNumber: String?,
    val timerValue: Int
) {
    override fun equals(other: Any?): Boolean {
        if(other !is CheckoutConfig) return false
        return other.isEnabled == isEnabled &&
                other.className == className &&
                other.networkUrl == networkUrl &&
                other.checkoutAmount == checkoutAmount &&
                other.cartCount == cartCount &&
                other.cartCountCheckout == cartCountCheckout &&
                other.orderNumber == orderNumber &&
                other.timerValue == timerValue
    }

    override fun hashCode(): Int {
        var result = isEnabled.hashCode()
        result = 31 * result + className.hashCode()
        result = 31 * result + networkUrl.hashCode()
        result = 31 * result + checkoutAmount.hashCode()
        result = 31 * result + cartCount.hashCode()
        result = 31 * result + cartCountCheckout.hashCode()
        result = 31 * result + orderNumber.hashCode()
        result = 31 * result + timerValue.hashCode()
        return result
    }
}