package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState

/** 单 Activity 页面容器：统一设计画布 + 七个横屏页面路由 + 全局调试浮窗。 */
@Composable
fun AppRoot() {
    DesignFrame {
        when (AppState.screen) {
            AppScreen.WELCOME -> WelcomeScreen()
            AppScreen.PRODUCTS -> ProductGridScreen()
            AppScreen.PAYMENT -> PaymentScreen()
            AppScreen.QR_PAYMENT -> QrPaymentScreen()
            AppScreen.PREPARATION -> PreparationScreen()
            AppScreen.OUT_OF_SERVICE -> OutOfServiceScreen()
            AppScreen.SETUP -> SetupScreen()
        }

        if (AppState.debugOpen) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                DebugDock()
            }
        }
    }
}
