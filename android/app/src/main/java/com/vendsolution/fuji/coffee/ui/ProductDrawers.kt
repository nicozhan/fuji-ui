package com.vendsolution.fuji.coffee.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.data.Product
import com.vendsolution.fuji.coffee.data.ProductCatalog
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.FujiDimens
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp

/** 购物车抽屉，对应 Web 的 #cartPanel：从右侧滑入，左圆角 22，宽 668。 */
@Composable
fun CartDrawer() {
    Scrim(onDismiss = { AppState.cartOpen = false })

    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val offsetX by animateDpAsState(
        targetValue = if (entered) 0.sdp() else 720.sdp(),
        animationSpec = tween(280),
        label = "cart-slide",
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        Column(
            Modifier
                .fillMaxHeight()
                .width(668.sdp())
                .offset(x = offsetX)
                .clip(RoundedCornerShape(topStart = 22.sdp(), bottomStart = 22.sdp()))
                .background(Color.White),
        ) {
            // 头：100 高，购物车图标 + 标题 + 白底徽章；右侧 Canvas 关闭图标
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(100.sdp())
                    .background(FujiColor.AccentDark)
                    .padding(horizontal = 38.sdp()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CartGlyph(Modifier.size(27.sdp()))
                Spacer(Modifier.width(13.sdp()))
                Text("购物车", color = Color.White, fontSize = 26.ssp(), fontWeight = FontWeight(720))
                Spacer(Modifier.width(13.sdp()))
                Box(
                    Modifier
                        .heightIn(min = 29.sdp())
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(horizontal = 8.sdp(), vertical = 2.sdp()),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${AppState.cartCount}",
                        color = FujiColor.AccentDark,
                        fontSize = 15.ssp(),
                        fontWeight = FontWeight(800),
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(40.sdp())
                        .clip(CircleShape)
                        .clickable { AppState.cartOpen = false },
                    contentAlignment = Alignment.Center,
                ) {
                    CloseGlyph(Modifier.size(26.sdp()), tint = Color.White)
                }
            }

            LazyColumn(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.sdp()),
            ) {
                items(AppState.cart, key = { "${it.product.id}-${it.options}" }) { item ->
                    CartItemRow(item.product, item.quantity, item.options)
                }
            }

            // 尾：合计（N 件）+ 千分位金额，去结算 → 胶囊
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.sdp(), FujiColor.Border, RoundedCornerShape(0))
                    .padding(start = 38.sdp(), top = 25.sdp(), end = 38.sdp(), bottom = 34.sdp()),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "合计（${AppState.cartCount} 件）",
                        color = FujiColor.Text,
                        fontSize = 19.ssp(),
                        fontWeight = FontWeight(650),
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        yen(AppState.cartTotal),
                        color = FujiColor.Text,
                        fontSize = 34.ssp(),
                        fontWeight = FontWeight(800),
                    )
                }
                Spacer(Modifier.height(23.sdp()))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(64.sdp())
                        .clip(CircleShape)
                        .background(FujiColor.Accent)
                        .clickable {
                            if (AppState.cart.isNotEmpty()) {
                                AppState.newOrderId()
                                AppState.navigate(AppScreen.PAYMENT)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.sdp()),
                    ) {
                        Text("去结算", color = Color.White, fontSize = 17.ssp(), fontWeight = FontWeight(700))
                        Canvas(Modifier.size(21.sdp())) {
                            val d = this.size.minDimension
                            val y = center.y
                            val sw = d * 0.085f
                            drawLine(Color.White, Offset(d * 0.08f, y), Offset(d * 0.92f, y), sw, StrokeCap.Round)
                            drawLine(Color.White, Offset(d * 0.60f, y - d * 0.32f), Offset(d * 0.92f, y), sw, StrokeCap.Round)
                            drawLine(Color.White, Offset(d * 0.92f, y), Offset(d * 0.60f, y + d * 0.32f), sw, StrokeCap.Round)
                        }
                    }
                }
            }
        }
    }
}

/** 购物车三列条目：thumb / 名称+配置小字 / 价格+删除。 */
@Composable
private fun CartItemRow(product: Product, quantity: Int, options: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 38.sdp(), vertical = 16.sdp())
            .border(1.sdp(), Color(0xFFEDF1F4), RoundedCornerShape(0)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        product.imageRes?.let { res ->
            Image(
                painter = painterResource(res),
                contentDescription = product.name,
                modifier = Modifier.size(150.sdp()),
                contentScale = ContentScale.Fit,
            )
        }
        Spacer(Modifier.width(24.sdp()))
        Column(Modifier.weight(1f)) {
            Text(
                product.name,
                color = FujiColor.Text,
                fontSize = 22.ssp(),
                fontWeight = FontWeight(750),
                maxLines = 2,
            )
            Spacer(Modifier.height(9.sdp()))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cartVariantLabel(product, options), color = FujiColor.TextSecondary, fontSize = 17.ssp())
                if (quantity > 1) {
                    Spacer(Modifier.width(8.sdp()))
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFEAF2FA))
                            .padding(horizontal = 8.sdp(), vertical = 2.sdp()),
                    ) {
                        Text("× $quantity", color = FujiColor.AccentDark, fontSize = 14.ssp(), fontWeight = FontWeight(800))
                    }
                }
            }
        }
        Spacer(Modifier.width(24.sdp()))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(yen(product.priceYen * quantity), color = FujiColor.Text, fontSize = 26.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.width(18.sdp()))
            Box(
                Modifier
                    .size(56.sdp())
                    .clip(RoundedCornerShape(14.sdp()))
                    .background(Color(0xFFF2F6F9))
                    .clickable { AppState.removeFromCart(product.id) },
                contentAlignment = Alignment.Center,
            ) {
                TrashGlyph(Modifier.size(28.sdp()))
            }
        }
    }
}

/** ¥1,070 千分位格式，与 Web 的 yen() 一致。 */
private fun yen(value: Int): String = "¥" + String.format(java.util.Locale.US, "%,d", value)

/** 与 Web 的 variantLabel() 对应：温度 / 杯型 / 甜度 / 奶类。 */
private fun cartVariantLabel(product: Product, options: String): String {
    if (options.isBlank()) return "${product.temperature.label} / 标准杯"
    val labels = mapOf(
        "hot" to "热饮", "iced" to "冷饮",
        "small" to "小杯", "medium" to "中杯", "large" to "大杯", "regular" to "标准杯",
        "none" to "无糖", "light" to "微糖", "half" to "半糖", "less" to "少糖", "standard" to "标准糖",
        "milk" to "牛奶", "lowfat" to "低脂奶", "oat" to "燕麦奶", "soy" to "豆奶",
    )
    return options.split("/").mapNotNull { labels[it.trim()] }.joinToString(" / ")
}

@Composable
private fun CartGlyph(modifier: Modifier) {
    Canvas(modifier) {
        val d = this.size.minDimension
        val sw = d * 0.075f
        val color = Color.White
        drawLine(color, Offset(d * 0.12f, d * 0.16f), Offset(d * 0.22f, d * 0.16f), sw, StrokeCap.Round)
        drawLine(color, Offset(d * 0.22f, d * 0.16f), Offset(d * 0.34f, d * 0.83f), sw, StrokeCap.Round)
        drawLine(color, Offset(d * 0.34f, d * 0.83f), Offset(d * 0.88f, d * 0.83f), sw, StrokeCap.Round)
        drawLine(color, Offset(d * 0.88f, d * 0.83f), Offset(d * 0.96f, d * 0.32f), sw, StrokeCap.Round)
        drawLine(color, Offset(d * 0.96f, d * 0.32f), Offset(d * 0.24f, d * 0.32f), sw, StrokeCap.Round)
        drawCircle(color, radius = d * 0.055f, center = Offset(d * 0.43f, d * 0.93f))
        drawCircle(color, radius = d * 0.055f, center = Offset(d * 0.79f, d * 0.93f))
    }
}

@Composable
private fun TrashGlyph(modifier: Modifier) {
    Canvas(modifier) {
        val d = this.size.minDimension
        val c = Color(0xFF4C6172)
        val sw = d * 0.065f
        drawLine(c, Offset(d * 0.20f, d * 0.30f), Offset(d * 0.80f, d * 0.30f), sw, StrokeCap.Round)
        drawLine(c, Offset(d * 0.42f, d * 0.30f), Offset(d * 0.42f, d * 0.20f), sw, StrokeCap.Round)
        drawLine(c, Offset(d * 0.58f, d * 0.30f), Offset(d * 0.58f, d * 0.20f), sw, StrokeCap.Round)
        drawLine(c, Offset(d * 0.28f, d * 0.30f), Offset(d * 0.34f, d * 0.82f), sw, StrokeCap.Round)
        drawLine(c, Offset(d * 0.72f, d * 0.30f), Offset(d * 0.66f, d * 0.82f), sw, StrokeCap.Round)
        drawLine(c, Offset(d * 0.34f, d * 0.82f), Offset(d * 0.66f, d * 0.82f), sw, StrokeCap.Round)
    }
}

private val TEMPERATURES = listOf("热饮" to "hot", "冷饮" to "iced")
private val SIZES = listOf("小杯" to "small", "中杯" to "medium", "大杯" to "large")
private val SWEETS = listOf(
    "无糖" to "none",
    "微糖" to "light",
    "半糖" to "half",
    "少糖" to "less",
    "标准糖" to "standard",
)
private val MILKS = listOf("牛奶" to "milk", "低脂奶" to "lowfat", "燕麦奶" to "oat", "豆奶" to "soy")

/** 定制饮品弹窗，严格对应 index.html 的 .customization-sheet。 */
@Composable
fun CustomizationDrawer() {
    val productId = AppState.customizeProductId ?: return
    val product = ProductCatalog.ALL.firstOrNull { it.id == productId } ?: return

    var temperature by remember(productId) { mutableStateOf("hot") }
    var size by remember(productId) { mutableStateOf("medium") }
    var sweetness by remember(productId) { mutableStateOf("standard") }
    var milk by remember(productId) { mutableStateOf("milk") }
    var quantity by remember(productId) { mutableIntStateOf(1) }

    var entered by remember(productId) { mutableStateOf(false) }
    LaunchedEffect(productId) { entered = true }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(300),
        label = "custom-scrim",
    )
    val sheetOffset by animateDpAsState(
        targetValue = if (entered) 0.sdp() else 900.sdp(),
        animationSpec = tween(340),
        label = "custom-slide",
    )
    val close = { AppState.customizeProductId = null }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF091C2A).copy(alpha = 0.44f * scrimAlpha))
            .clickable(onClick = close),
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            Modifier
                .width(1240.sdp())
                .height(820.sdp())
                .offset(y = sheetOffset)
                .clip(RoundedCornerShape(topStart = 32.sdp(), topEnd = 32.sdp()))
                .background(Color.White),
        ) {
            // 头：92 高 + 下分隔线；左收起、中标题、右关闭，两侧各 44
            Box(Modifier.fillMaxWidth().height(92.sdp())) {
                SheetCircleButton(
                    Modifier.align(Alignment.CenterStart).padding(start = 44.sdp()),
                    onClick = close,
                ) { ChevronDownGlyph(Modifier.size(30.sdp())) }
                Text(
                    "定制饮品",
                    Modifier.align(Alignment.Center),
                    color = FujiColor.Text,
                    fontSize = 28.ssp(),
                    fontWeight = FontWeight(750),
                )
                SheetCircleButton(
                    Modifier.align(Alignment.CenterEnd).padding(end = 44.sdp()),
                    onClick = close,
                ) { CloseGlyph(Modifier.size(30.sdp())) }
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(1.sdp())
                        .background(FujiColor.Border),
                )
            }

            // 身：padding 26/64/28，左商品 380 + gap 56 + 右选项区
            Row(
                Modifier
                    .weight(1f)
                    .padding(start = 64.sdp(), top = 26.sdp(), end = 64.sdp(), bottom = 28.sdp()),
                horizontalArrangement = Arrangement.spacedBy(56.sdp()),
            ) {
                Column(Modifier.width(380.sdp())) {
                    product.imageRes?.let { res ->
                        Image(
                            painter = painterResource(res),
                            contentDescription = product.name,
                            modifier = Modifier.size(380.sdp()),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Spacer(Modifier.height(20.sdp()))
                    Text(product.name, color = FujiColor.Text, fontSize = 28.ssp(), fontWeight = FontWeight(750))
                    Spacer(Modifier.height(6.sdp()))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "¥${product.priceYen}",
                            color = FujiColor.AccentDark,
                            fontSize = 26.ssp(),
                            fontWeight = FontWeight(800),
                        )
                        Spacer(Modifier.width(7.sdp()))
                        Text(
                            "¥${product.priceYen + FujiDimens.STRIKE_DELTA}",
                            color = FujiColor.PriceWas,
                            fontSize = 17.ssp(),
                            fontWeight = FontWeight(650),
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                }
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    OptionGroup("温度", TEMPERATURES, temperature) { temperature = it }
                    OptionGroup("杯型", SIZES, size) { size = it }
                    OptionGroup("甜度", SWEETS, sweetness) { sweetness = it }
                    OptionGroup("奶类", MILKS, milk, last = true) { milk = it }
                }
            }

            // 尾：116 高 + 上分隔线；数量 / 合计 / 加入订单
            Box(Modifier.fillMaxWidth().height(116.sdp())) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(116.sdp())
                        .padding(horizontal = 64.sdp()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("数量", color = FujiColor.Text, fontSize = 19.ssp(), fontWeight = FontWeight(700))
                    Spacer(Modifier.width(20.sdp()))
                    RoundStepper("−", enabled = quantity > 1) { quantity-- }
                    Spacer(Modifier.width(20.sdp()))
                    Text(
                        "$quantity",
                        modifier = Modifier.width(32.sdp()),
                        color = FujiColor.Text,
                        fontSize = 24.ssp(),
                        fontWeight = FontWeight(700),
                    )
                    Spacer(Modifier.width(20.sdp()))
                    RoundStepper("+", enabled = quantity < 3) { quantity++ }
                    Spacer(Modifier.weight(1f))
                    Text("合计", color = FujiColor.TextSecondary, fontSize = 18.ssp())
                    Spacer(Modifier.width(12.sdp()))
                    Text(
                        "¥${product.priceYen * quantity}",
                        color = FujiColor.Text,
                        fontSize = 36.ssp(),
                        fontWeight = FontWeight(800),
                    )
                    Spacer(Modifier.width(7.sdp()))
                    Text(
                        "¥${(product.priceYen + FujiDimens.STRIKE_DELTA) * quantity}",
                        color = FujiColor.PriceWas,
                        fontSize = 17.ssp(),
                        fontWeight = FontWeight(650),
                        textDecoration = TextDecoration.LineThrough,
                    )
                    Spacer(Modifier.width(28.sdp()))
                    Box(
                        Modifier
                            .width(350.sdp())
                            .height(72.sdp())
                            .clip(CircleShape)
                            .background(FujiColor.Accent)
                            .clickable {
                                val options = listOf(temperature, size, sweetness, milk).joinToString("/")
                                repeat(quantity) { AppState.addToCart(product, options) }
                                close()
                                AppState.cartOpen = true
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.sdp()),
                        ) {
                            Text("加入订单", color = Color.White, fontSize = 20.ssp(), fontWeight = FontWeight(700))
                            Canvas(Modifier.size(24.sdp())) {
                                val d = this.size.minDimension
                                val y = center.y
                                val sw = d * 0.075f
                                drawLine(FujiColor.Surface, Offset(d * 0.08f, y), Offset(d * 0.86f, y), sw, StrokeCap.Round)
                                drawLine(FujiColor.Surface, Offset(d * 0.55f, y - d * 0.30f), Offset(d * 0.86f, y), sw, StrokeCap.Round)
                                drawLine(FujiColor.Surface, Offset(d * 0.86f, y), Offset(d * 0.55f, y + d * 0.30f), sw, StrokeCap.Round)
                            }
                        }
                    }
                }
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(1.sdp())
                        .background(FujiColor.Border),
                )
            }
        }
    }
}

@Composable
private fun SheetCircleButton(modifier: Modifier, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Box(
        modifier
            .size(64.sdp())
            .clip(CircleShape)
            .background(Color.White)
            .border(1.sdp(), Color(0xFFD9E6ED), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { icon() }
}

@Composable
private fun ChevronDownGlyph(modifier: Modifier) {
    Canvas(modifier) {
        val d = this.size.minDimension
        val sw = d * 0.065f
        drawLine(FujiColor.AccentDark, Offset(d * 0.22f, d * 0.40f), Offset(d * 0.50f, d * 0.66f), sw, StrokeCap.Round)
        drawLine(FujiColor.AccentDark, Offset(d * 0.50f, d * 0.66f), Offset(d * 0.78f, d * 0.40f), sw, StrokeCap.Round)
    }
}

@Composable
private fun CloseGlyph(modifier: Modifier, tint: Color = FujiColor.TextSecondary) {
    Canvas(modifier) {
        val d = this.size.minDimension
        val sw = d * 0.06f
        drawLine(tint, Offset(d * 0.27f, d * 0.27f), Offset(d * 0.73f, d * 0.73f), sw, StrokeCap.Round)
        drawLine(tint, Offset(d * 0.73f, d * 0.27f), Offset(d * 0.27f, d * 0.73f), sw, StrokeCap.Round)
    }
}

@Composable
private fun RoundStepper(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(52.sdp())
            .clip(CircleShape)
            .background(Color.White)
            .border(1.sdp(), Color(0xFFC9DFE9), CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (enabled) FujiColor.Accent else FujiColor.Border,
            fontSize = 28.ssp(),
            fontWeight = FontWeight(500),
        )
    }
}

@Composable
private fun OptionGroup(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    last: Boolean = false,
    onSelect: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(top = 8.sdp())) {
        Text(label, color = FujiColor.Text, fontSize = 18.ssp(), fontWeight = FontWeight(700))
        Spacer(Modifier.height(16.sdp()))
        Row(horizontalArrangement = Arrangement.spacedBy(12.sdp())) {
            options.forEach { (text, value) ->
                val active = selected == value
                Box(
                    Modifier
                        .heightIn(min = 52.sdp())
                        .widthIn(min = 108.sdp())
                        .clip(CircleShape)
                        .background(if (active) FujiColor.Accent else Color.White)
                        .border(1.sdp(), if (active) FujiColor.Accent else Color(0xFFCEDFE8), CircleShape)
                        .clickable { onSelect(value) }
                        .padding(horizontal = 23.sdp(), vertical = 6.sdp()),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text,
                        color = if (active) Color.White else FujiColor.Text,
                        fontSize = 16.ssp(),
                        fontWeight = FontWeight(500),
                    )
                }
            }
        }
        Spacer(Modifier.height(if (last) 8.sdp() else 22.sdp()))
        if (!last) {
            Box(Modifier.fillMaxWidth().height(1.sdp()).background(FujiColor.Border))
        }
    }
}
