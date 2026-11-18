package com.vendsolution.fuji.coffee.data

import androidx.compose.ui.graphics.Color

/** 促销标签，色值与 index.html `.product-tag.is-*` 一致。 */
enum class PromoTag(val label: String, val start: Color, val end: Color) {
    BEST("冬日畅销", Color(0xFFE2761F), Color(0xFFB9480A)),
    BOGO("第二件半价", Color(0xFFDD3F46), Color(0xFFA81A22)),
    LIMITED("本店限量", Color(0xFFB98A2E), Color(0xFF8A5C06)),
    NEW("新品上市", Color(0xFF2F9E64), Color(0xFF0F6F40)),
    TOP("人气 No.1", Color(0xFF8A71D8), Color(0xFF5730B0)),
}

enum class Temperature(val label: String) {
    ICED("冷饮"),
    HOT("热饮"),
}

/**
 * 商品图占位配色。本期不引入任何图片素材，杯型由 Compose Canvas 绘制；
 * 后续拿到真实商品图时，把资源放进 res/drawable 并填写 [Product.imageRes] 即可。
 */
enum class CupStyle(
    val bottom: Color,
    val middle: Color,
    val top: Color,
    val lid: Color,
    val foam: Boolean = false,
) {
    AMERICANO(Color(0xFF2B1A12), Color(0xFF5B3622), Color(0xFF9A6A44), Color(0xFFE8EEF2)),
    LATTE(Color(0xFFF2E5CF), Color(0xFFC08A54), Color(0xFFF8F1E4), Color(0xFFE8EEF2), foam = true),
    MOCHA(Color(0xFF4B2A1C), Color(0xFF7A4A2E), Color(0xFFF3E6D2), Color(0xFFE8EEF2), foam = true),
    CARAMEL(Color(0xFFC98B45), Color(0xFFE7BB7A), Color(0xFFFAF0DC), Color(0xFFE8EEF2), foam = true),
    MATCHA(Color(0xFF6E8C46), Color(0xFFA8C077), Color(0xFFF4F6E8), Color(0xFFE8EEF2), foam = true),
    ESPRESSO(Color(0xFF3A2014), Color(0xFF6B3B22), Color(0xFFB58253), Color(0xFFE8EEF2)),
}

/**
 * 商品数据模型。
 *
 * @param imageRes 真实商品图资源 ID；为 null 时渲染 [CupPlaceholder] 占位杯型。
 * @param showStrikePrice 是否显示划线原价（Web 稿中由调试开关控制，默认关闭）。
 */
data class Product(
    val id: Int,
    val name: String,
    val priceYen: Int,
    val temperature: Temperature,
    val tag: PromoTag? = null,
    val soldOut: Boolean = false,
    val cup: CupStyle = CupStyle.LATTE,
    val imageRes: Int? = null,
    val showStrikePrice: Boolean = false,
)
