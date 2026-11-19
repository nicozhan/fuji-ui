package com.vendsolution.fuji.coffee.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Compose 单 Activity 内部的页面路由，对应 Web 原型的 7 个横屏页面。 */
enum class AppScreen {
    WELCOME,
    PRODUCTS,
    PAYMENT,
    QR_PAYMENT,
    PREPARATION,
    OUT_OF_SERVICE,
    SETUP,
}

data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val options: String = "",
)

/**
 * 全局会话状态。Web 原型里分散在 localStorage / sessionStorage / URL 参数中的
 * 订单、语言、设备档位、支付状态，在这里集中为可观察状态，便于研发替换为
 * Repository + ViewModel 后接真实后端。
 */
object AppState {
    var screen by mutableStateOf(AppScreen.WELCOME)
        private set

    var language by mutableStateOf("zh")
    var devicePreset by mutableStateOf("m21")
    var salesMode by mutableStateOf("cart")
    var orderId by mutableStateOf("FUJI-DEMO-0001")
    var paymentState by mutableStateOf("waiting") // waiting / channels / paid / failed
    /** 调试用：paid 态是否冻结不自动跳制作页（adb -e freezePaid 1）。 */
    var freezePaid by mutableStateOf(false)
    var selectedChannel by mutableStateOf("alipay")
    /** 启用的支付方式数量 1/2/3，对应 Web 的 .payment-grid.count-N 与调试浮窗 1/2/3 切换。 */
    var paymentMethodCount by mutableIntStateOf(2)
    /** 制作页调试动作（start/taken/last），由调试浮窗发出、制作页消费；序号保证重复点击可触发。 */
    var prepDemoAction by mutableStateOf("" to 0)
    var debugOpen by mutableStateOf(false)
    var helpOpen by mutableStateOf(false)
    var cartOpen by mutableStateOf(false)
    var customizeProductId by mutableStateOf<Int?>(null)

    val cart = mutableStateListOf<CartItem>()

    val cartCount: Int get() = cart.sumOf { it.quantity }
    val cartTotal: Int get() = cart.sumOf { it.product.priceYen * it.quantity }

    fun navigate(target: AppScreen) {
        screen = target
        debugOpen = false
        cartOpen = false
        helpOpen = false
        customizeProductId = null
    }

    /** 供 Android Studio / adb 以 -e screen=XXX 直接打开指定页面调试。 */
    fun setInitialScreen(target: AppScreen) {
        screen = target
    }

    fun addToCart(product: Product, options: String = "") {
        val index = cart.indexOfFirst { it.product.id == product.id && it.options == options }
        if (index >= 0) {
            cart[index] = cart[index].copy(quantity = cart[index].quantity + 1)
        } else {
            cart.add(CartItem(product, 1, options))
        }
    }

    fun removeFromCart(productId: Int) {
        val index = cart.indexOfFirst { it.product.id == productId }
        if (index >= 0) cart.removeAt(index)
    }

    fun clearSession() {
        cart.clear()
        orderId = "FUJI-DEMO-0001"
        paymentState = "waiting"
        selectedChannel = "alipay"
        freezePaid = false
    }

    fun newOrderId(): String {
        orderId = "FUJI-${System.currentTimeMillis()}"
        return orderId
    }
}
