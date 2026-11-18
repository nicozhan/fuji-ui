package com.vendsolution.fuji.coffee.design

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/** 取自 index.html `:root`，色值与 Web 稿逐项一致。 */
object FujiColor {
    val Bg = Color(0xFFF4F7FA)
    val Surface = Color(0xFFFFFFFF)
    val CardTop = Color(0xFFF4F8FB)
    val Surface2 = Color(0xFFEEF5FA)
    val Border = Color(0xFFE4EBF0)
    val CardBorder = Color(0xE6D6E1E9)          // rgba(214,225,233,.9)
    val Text = Color(0xFF102D43)
    val TextSecondary = Color(0xFF6C7E8C)
    val Accent = Color(0xFF0877C9)
    val AccentDark = Color(0xFF075B9E)
    val AccentDeep = Color(0xFF064B83)
    val AccentWeak = Color(0xFFE8F4FD)
    val PriceWas = Color(0xFF7C8F9F)
    val StatusBarBg = Color(0xFFE9EFF5)
    val StatusBarBorder = Color(0xFFDAE5EE)
    val StatusText = Color(0xFF4A6478)
    val MachineId = Color(0xFF6D8598)
    val MachineIdStrong = Color(0xFF35566E)
    val PageBorder = Color(0xFFC6DBE8)
    val AdPlaceholderTop = Color(0xFFEAF3F9)
    val AdPlaceholderBottom = Color(0xFFD7E7F1)

    // 售罄态（方案 A）
    val SoldOutVeil = Color(0xB8F7FBFD)          // rgba(247,251,253,.72)
    val SoldOutMark = Color(0xFF526B7D)
    val SoldOutName = Color(0xFF8296A5)
    val SoldOutPrice = Color(0xFFA3B4C0)
    val SoldOutButtonBg = Color(0xFFF2F6F9)
    val SoldOutButtonBorder = Color(0xFFBCCBD6)
    val SoldOutButtonIcon = Color(0xFFAABDC9)
}

/**
 * 尺寸常量，单位为「设计像素」（= CSS 中的 rem × 16）。
 * 所有尺寸都通过 sdp() / ssp() 缩放，勿直接当 dp 使用。
 */
object FujiDimens {
    const val STATUS_BAR_H = 56          // --status-h 3.5rem
    const val HEADER_H = 104             // --header-h 6.5rem
    const val PAGER_H = 64               // --pager-h 4rem
    const val GRID_GAP = 12              // --grid-gap 0.75rem
    const val STAGE_PADDING_TOP = 28     // 1.75rem
    const val STAGE_PADDING_H = 74       // 4.625rem（多页时左右箭头通道）
    const val CARD_RADIUS = 14           // --radius 0.875rem
    const val CARD_BORDER = 1            // 0.0625rem
    const val CARD_MEDIA_RATIO = 0.68f
    const val CARD_INFO_RATIO = 0.32f
    const val INFO_PADDING_H = 19        // clamp(11, 1vw, 19)
    const val INFO_PADDING_V = 15        // clamp(8, .78vw, 15)
    const val CARD_NAME_SIZE = 24        // clamp(16, 1.3vw, 24)
    const val PRICE_CURRENCY_SIZE = 15   // clamp(11, .8vw, 15)
    const val PRICE_AMOUNT_SIZE = 21     // clamp(16, 1.14vw, 21)
    const val PRICE_WAS_SIZE = 17        // clamp(12, .88vw, 17)
    const val ADD_BUTTON_SIZE = 46       // clamp(34, 2.5vw, 46)
    const val ADD_BUTTON_BORDER = 2      // 0.125rem
    const val TAG_H = 30                 // 1.875rem
    const val TAG_OFFSET = 12            // 0.75rem
    const val TAG_TEXT_SIZE = 13         // clamp(10, .68vw, 13)
    const val SOLD_OUT_MARK_SIZE = 98    // clamp(68, 6vw, 98)
    const val SOLD_OUT_MARK_CENTER_Y = 0.36f
    const val STRIKE_DELTA = 20          // 划线原价 = 现价 + 20
    const val PAGE_SIZE = 6              // 广告开启时一屏 3×2
    const val AD_POSTER_W = 848
    const val AD_POSTER_H = 1855
}

/**
 * Material3 默认 Typography 的 lineHeight 是绝对 sp，不随 ssp() 缩放，
 * 会把小容器（如 30px 促销标签）里的文字撑出边界被裁切。
 * 这里统一改成无预设行高的 TextStyle，所有文字仅由各组件显式指定的
 * fontSize / lineHeight 控制，保证与 Web 稿逐像素对齐。
 */
@Composable
fun FujiCoffeeTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(LocalTextStyle provides TextStyle.Default) {
            content()
        }
    }
}
