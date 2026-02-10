package com.bluetriangle.analytics.checkout.config

class CheckoutConfig(
    val isEnabled: Boolean,
    val className: String?,
    val groupName: String?,
    val networkUrl: String?,
    val checkoutAmount: Double,
    val cartCount: Int,
    val cartCountCheckout: Int,
    val orderNumber: String?,
    val timerValue: Int
) {
    override fun equals(other: Any?): Boolean {
        if(other !is CheckoutConfig) return false
        return other.className == className &&
                other.groupName == groupName &&
                other.checkoutAmount == checkoutAmount &&
                other.cartCount == cartCount &&
                other.cartCountCheckout == cartCountCheckout &&
                other.orderNumber == orderNumber &&
                other.timerValue == timerValue
    }

//    “checkoutTrackingEnabled” : true
//    “checkoutClassName” : “checkout”
//    “checkoutFragmentName” : “checkout”
//    “checkoutURL”: “”
//    “checkOutAmount” : 1.0
//    “checkoutCartCount” : 1
//    “checkoutCartCountCheckout” 1
//    “checkoutOrderNumber” = “#ORD3344”
//    “checkoutTimeValue” = 100
    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + groupName.hashCode()
        result = 31 * result + checkoutAmount.hashCode()
        result = 31 * result + cartCount.hashCode()
        result = 31 * result + cartCountCheckout.hashCode()
        result = 31 * result + orderNumber.hashCode()
        result = 31 * result + timerValue.hashCode()
        return result
    }
}