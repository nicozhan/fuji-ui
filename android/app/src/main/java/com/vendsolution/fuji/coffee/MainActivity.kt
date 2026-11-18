package com.vendsolution.fuji.coffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vendsolution.fuji.coffee.design.FujiCoffeeTheme
import com.vendsolution.fuji.coffee.ui.ProductGridScreen

/**
 * 横屏咖啡商品展示页入口。
 * 方向锁定在 Manifest 的 `android:screenOrientation="landscape"`；
 * 此处负责全屏沉浸式：内容延伸到系统栏下方并隐藏状态栏 / 导航栏。
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()

        setContent {
            FujiCoffeeTheme {
                ProductGridScreen()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // 用户从边缘滑出系统栏后，重新获得焦点时恢复沉浸式
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
