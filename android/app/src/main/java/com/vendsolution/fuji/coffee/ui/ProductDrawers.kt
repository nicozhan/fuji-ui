package com.vendsolution.fuji.coffee.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.data.Product
import com.vendsolution.fuji.coffee.data.ProductCatalog
import com.vendsolution.fuji.coffee.design.FujiColor
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

private val TEMPERATURES = listOf("热" to "hot", "冰" to "iced")
private val SIZES = listOf("小杯" to "small", "中杯" to "medium", "大杯" to "large")
private val SWEETS = listOf("无糖" to "none", "少糖" to "less", "半糖" to "half", "标准" to "standard")
private val MILKS = listOf("全脂" to "milk", "低脂" to "lowfat", "燕麦" to "oat", "豆奶" to "soy")

/** 定制饮品抽屉，对应 Web 的 #customizationLayer.
 *  从屏幕下端向上滑入（translateY 105%→0，0.34s），背景页面仍可透出，不铺满。 */
@Composable
fun CustomizationDrawer() {
    val productId = AppState.customizeProductId ?: return
    val product = ProductCatalog.ALL.firstOrNull { it.id == productId } ?: return

    var temperature by remember(productId) { mutableStateOf("iced") }
    var size by remember(productId) { mutableStateOf("medium") }
    var sweetness by remember(productId) { mutableStateOf("standard") }
    var milk by remember(productId) { mutableStateOf("milk") }
    var quantity by remember(productId) { mutableIntStateOf(1) }

    // 进入动画：从底部滑入 + 遮罩淡入
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

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF091C2A).copy(alpha = 0.44f * scrimAlpha))
            .clickable { AppState.customizeProductId = null },
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
            // 头：居中标题，右侧关闭；两侧留边
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(92.sdp()),
                contentAlignment = Alignment.Center,
            ) {
                Text("定制饮品", color = FujiColor.Text, fontSize = 28.ssp(), fontWeight = FontWeight(750))
                Text(
                    "✕",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 44.sdp())
                        .clip(CircleShape)
                        .clickable { AppState.customizeProductId = null }
                        .padding(12.sdp()),
                    color = FujiColor.TextSecondary,
                    fontSize = 22.ssp(),
                )
            }

            // 身：左侧商品 380 + 间距 56，右侧选项区；整体左右留白 64
            Row(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 64.sdp()),
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
                    Text(product.name, color = FujiColor.Text, fontSize = 28.ssp(), fontWeight = FontWeight(750))
                    Text("¥${product.priceYen}", color = FujiColor.Accent, fontSize = 26.ssp(), fontWeight = FontWeight(800))
                }
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    OptionRow("温度", TEMPERATURES, temperature) { temperature = it }
                    OptionRow("杯型", SIZES, size) { size = it }
                    OptionRow("甜度", SWEETS, sweetness) { sweetness = it }
                    OptionRow("奶类", MILKS, milk) { milk = it }
                }
            }

            // 尾：数量 / 小计 / 加入订单，与 Web 的 .customization-footer 同高
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(116.sdp())
                    .padding(horizontal = 64.sdp()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("数量", color = FujiColor.Text, fontSize = 18.ssp(), fontWeight = FontWeight(700))
                Spacer(Modifier.width(16.sdp()))
                PillButton("−", { if (quantity > 1) quantity-- }, height = 48, fontSize = 20)
                Spacer(Modifier.width(10.sdp()))
                Text("$quantity", color = FujiColor.Text, fontSize = 22.ssp(), fontWeight = FontWeight(800))
                Spacer(Modifier.width(10.sdp()))
                PillButton("+", { if (quantity < 3) quantity++ }, height = 48, fontSize = 20)
                Spacer(Modifier.weight(1f))
                Text(
                    "¥${product.priceYen * quantity}",
                    color = FujiColor.AccentDark,
                    fontSize = 36.ssp(),
                    fontWeight = FontWeight(800),
                )
                Spacer(Modifier.width(36.sdp()))
                PillButton(
                    "加入订单",
                    onClick = {
                        val options = listOf(temperature, size, sweetness, milk).joinToString("/")
                        repeat(quantity) { AppState.addToCart(product, options) }
                        AppState.customizeProductId = null
                        AppState.cartOpen = true
                    },
                    modifier = Modifier.width(350.sdp()),
                    height = 72,
                    fontSize = 20,
                )
            }
        }
    }
}

@Composable
private fun OptionRow(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(90.sdp()), color = FujiColor.Text, fontSize = 18.ssp(), fontWeight = FontWeight(700))
        Row(horizontalArrangement = Arrangement.spacedBy(12.sdp())) {
            options.forEach { (text, value) ->
                val active = selected == value
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(if (active) FujiColor.Accent else Color.White)
                        .clickable { onSelect(value) }
                        .padding(horizontal = 23.sdp(), vertical = 13.sdp()),
                ) {
                    Text(
                        text,
                        color = if (active) Color.White else FujiColor.Text,
                        fontSize = 16.ssp(),
                        fontWeight = FontWeight(650),
                    )
                }
            }
        }
    }
}
