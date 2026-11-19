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

/** 购物车抽屉，对应 Web 的 #cartPanel：从右侧滑入，标题栏通栏，宽 668。 */
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
                .background(Color.White),
        ) {
            // 标题栏通栏（不受内容左右留白影响），内含 38 内边距
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.sdp())
                    .background(FujiColor.AccentDark)
                    .padding(horizontal = 38.sdp()),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("购物车", color = Color.White, fontSize = 26.ssp(), fontWeight = FontWeight(720))
                    Spacer(Modifier.width(14.sdp()))
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(horizontal = 12.sdp(), vertical = 4.sdp()),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "${AppState.cartCount}",
                            color = FujiColor.AccentDark,
                            fontSize = 15.ssp(),
                            fontWeight = FontWeight(800),
                        )
                    }
                }
                Text(
                    "✕",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .clickable { AppState.cartOpen = false }
                        .padding(12.sdp()),
                    color = Color.White,
                    fontSize = 20.ssp(),
                )
            }

            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 38.sdp()),
                verticalArrangement = Arrangement.spacedBy(12.sdp()),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 20.sdp()),
            ) {
                items(AppState.cart, key = { it.product.id }) { item ->
                    CartItemRow(item.product, item.quantity)
                }
            }

            Column(Modifier.padding(horizontal = 38.sdp(), vertical = 24.sdp())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("合计", color = FujiColor.Text, fontSize = 22.ssp(), fontWeight = FontWeight(650))
                    Spacer(Modifier.weight(1f))
                    Text(
                        "¥${AppState.cartTotal}",
                        color = FujiColor.Accent,
                        fontSize = 34.ssp(),
                        fontWeight = FontWeight(800),
                    )
                }
                Spacer(Modifier.height(18.sdp()))
                PillButton(
                    text = "去结算",
                    onClick = {
                        if (AppState.cart.isNotEmpty()) {
                            AppState.newOrderId()
                            AppState.navigate(AppScreen.PAYMENT)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 64,
                    fontSize = 17,
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(product: Product, quantity: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(150.sdp())
            .clip(RoundedCornerShape(14.sdp()))
            .background(FujiColor.Surface2)
            .padding(12.sdp()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        product.imageRes?.let { res ->
            Image(
                painter = painterResource(res),
                contentDescription = product.name,
                modifier = Modifier.size(122.sdp()),
                contentScale = ContentScale.Fit,
            )
        }
        Spacer(Modifier.width(14.sdp()))
        Column(Modifier.weight(1f)) {
            Text(product.name, color = FujiColor.Text, fontSize = 21.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.height(6.sdp()))
            Text("¥${product.priceYen}", color = FujiColor.AccentDark, fontSize = 18.ssp(), fontWeight = FontWeight(700))
            Text("数量 × $quantity", color = FujiColor.TextSecondary, fontSize = 15.ssp())
        }
        Spacer(Modifier.width(10.sdp()))
        Box(
            Modifier
                .clip(RoundedCornerShape(10.sdp()))
                .background(Color(0xFFF2F6F9))
                .clickable { AppState.removeFromCart(product.id) }
                .padding(horizontal = 14.sdp(), vertical = 12.sdp()),
        ) {
            Text("移除", color = FujiColor.TextSecondary, fontSize = 15.ssp())
        }
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
private fun CloseGlyph(modifier: Modifier) {
    Canvas(modifier) {
        val d = this.size.minDimension
        val sw = d * 0.06f
        drawLine(FujiColor.TextSecondary, Offset(d * 0.27f, d * 0.27f), Offset(d * 0.73f, d * 0.73f), sw, StrokeCap.Round)
        drawLine(FujiColor.TextSecondary, Offset(d * 0.73f, d * 0.27f), Offset(d * 0.27f, d * 0.73f), sw, StrokeCap.Round)
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
