package com.vendsolution.fuji.coffee.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 设计画布基准：横屏 1920 × 1080，1rem = 16 设计像素。
 *
 * Web 版本通过 `html { font-size: min(100vw/120, 100vh/67.5) }` 做等比缩放；
 * Android 端用 [LocalDesignScale] 复刻同一套机制：所有固定尺寸按
 * `min(实际宽 / 1920, 实际高 / 1080)` 缩放，因此在任意分辨率 / 密度下
 * 与 Web 稿保持像素级一致，后续迁移其它尺寸时无需逐项改数值。
 */
object DesignSpec {
    const val CANVAS_WIDTH = 1920f
    const val CANVAS_HEIGHT = 1080f
    const val BASE_REM_PX = 16f
}

/** 缩放系数，由 [ProductGridScreen] 在 BoxWithConstraints 中注入。 */
val LocalDesignScale = staticCompositionLocalOf { 1f }

/** 设计像素 → Dp（缩放后）。例：`74.sdp()` 对应 CSS 的 4.625rem。 */
@Composable
fun Int.sdp(): Dp = (this * LocalDesignScale.current).dp

@Composable
fun Float.sdp(): Dp = (this * LocalDesignScale.current).dp

@Composable
fun Double.sdp(): Dp = (this.toFloat() * LocalDesignScale.current).dp

/** 设计像素 → Sp（缩放后）。例：`24.ssp()` 对应 CSS 的 1.5rem。 */
@Composable
fun Int.ssp(): TextUnit = (this * LocalDesignScale.current).sp

@Composable
fun Float.ssp(): TextUnit = (this * LocalDesignScale.current).sp

@Composable
fun Double.ssp(): TextUnit = (this.toFloat() * LocalDesignScale.current).sp
