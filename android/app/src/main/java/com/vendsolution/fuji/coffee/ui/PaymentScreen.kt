package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vendsolution.fuji.coffee.R
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.data.ProductCatalog
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp

/** 支付方式页：订单摘要 + 两种支付方式 + 帮助弹窗 + 返回主页确认。 */
@Composable
fun PaymentScreen() {
    val total = if (AppState.cart.isEmpty()) 1040 else AppState.cartTotal
    val count = if (AppState.cart.isEmpty()) 3 else AppState.cartCount
    var confirmExit by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        StatusBar()
        Row(
            Modifier
                .fillMaxWidth()
                .height(104.sdp())
                .background(Color.White)
                .padding(horizontal = 48.sdp()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PillButton(
                "返回",
                { confirmExit = true },
                modifier = Modifier.width(132.sdp()),
                height = 54,
                fontSize = 16,
            )
            Spacer(Modifier.width(24.sdp()))
            BrandMark(size = 33)
            Spacer(Modifier.width(10.sdp()))
            Text("Fuji Electric", color = Color(0xFF101C26), fontSize = 22.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.weight(1f))
            PillButton("帮助", { AppState.helpOpen = true }, modifier = Modifier.width(132.sdp()), height = 54, fontSize = 16)
            Spacer(Modifier.width(20.sdp()))
            PillButton("简体中文", {}, modifier = Modifier.width(166.sdp()), height = 54, fontSize = 16)
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.sdp(), vertical = 36.sdp()),
        ) {
            Text("选择支付方式", color = FujiColor.Text, fontSize = 32.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.height(24.sdp()))

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(170.sdp())
                    .clip(RoundedCornerShape(18.sdp()))
                    .border(1.sdp(), FujiColor.Border, RoundedCornerShape(18.sdp()))
                    .background(Color.White)
                    .padding(horizontal = 34.sdp(), vertical = 24.sdp()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("应付金额", color = FujiColor.TextSecondary, fontSize = 16.ssp())
                    Text("¥$total", color = FujiColor.Text, fontSize = 42.ssp(), fontWeight = FontWeight(800))
                }
                Spacer(Modifier.weight(1f))
                Text("共 $count 件商品", color = FujiColor.TextSecondary, fontSize = 18.ssp())
                Spacer(Modifier.width(28.sdp()))
                repeat(3) { index ->
                    val product = ProductCatalog.ALL[index]
                    product.imageRes?.let { res ->
                        Image(
                            painter = painterResource(res),
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(82.sdp())
                                .clip(RoundedCornerShape(12.sdp()))
                                .background(FujiColor.Surface2),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Spacer(Modifier.width(12.sdp()))
                }
            }

            Spacer(Modifier.height(28.sdp()))
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                Row(
                    Modifier
                        .fillMaxHeight()
                        .widthIn(max = 1400.sdp()),
                    horizontalArrangement = Arrangement.spacedBy(28.sdp()),
                ) {
                    PaymentCard(
                        title = "扫码支付",
                        subtitle = "微信 / 支付宝",
                        imageRes = R.drawable.payment_methods_square,
                        onClick = { AppState.selectedChannel = "alipay"; AppState.navigate(AppScreen.QR_PAYMENT) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    PaymentCard(
                        title = "POS 机支付",
                        subtitle = "请到收银台完成",
                        imageRes = null,
                        onClick = {},
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            }
        }
    }

    if (AppState.helpOpen) HelpDialog()
    if (confirmExit) ExitConfirmDialog(onCancel = { confirmExit = false })
}

@Composable
private fun PaymentCard(
    title: String,
    subtitle: String,
    imageRes: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(20.sdp()))
            .background(
                Brush.verticalGradient(listOf(Color.White, Color(0xFFF8FBFD))),
            )
            .border(1.sdp(), FujiColor.Border, RoundedCornerShape(20.sdp()))
            .clickable(onClick = onClick)
            .padding(
                start = 36.sdp(),
                top = 28.sdp(),
                end = 36.sdp(),
                bottom = 32.sdp(),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, color = FujiColor.Text, fontSize = 26.ssp(), fontWeight = FontWeight(700))
        Text(subtitle, color = FujiColor.TextSecondary, fontSize = 16.ssp())
        Spacer(Modifier.height(18.sdp()))
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.sdp()))
                .background(FujiColor.Surface2),
            contentAlignment = Alignment.Center,
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = subtitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Text("POS", color = FujiColor.AccentDark, fontSize = 64.ssp(), fontWeight = FontWeight(800))
            }
        }
        Spacer(Modifier.height(18.sdp()))
        PillButton("立即支付", onClick, modifier = Modifier.fillMaxWidth(), height = 64, fontSize = 18)
    }
}

@Composable
private fun ExitConfirmDialog(onCancel: () -> Unit) {
    Scrim(onDismiss = onCancel)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .width(544.sdp())
                .clip(RoundedCornerShape(22.sdp()))
                .background(Color.White)
                .padding(36.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(60.sdp())
                    .clip(CircleShape)
                    .background(Color(0xFFFFF0F0)),
                contentAlignment = Alignment.Center,
            ) {
                Text("!", color = Color(0xFFC64747), fontSize = 30.ssp(), fontWeight = FontWeight(800))
            }
            Spacer(Modifier.height(18.sdp()))
            Text("确定返回主页？", color = FujiColor.Text, fontSize = 24.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.height(10.sdp()))
            Text(
                "当前订单将被清除，返回后需要重新选择商品。",
                color = FujiColor.TextSecondary,
                fontSize = 17.ssp(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.sdp()))
            PillButton(
                "继续支付",
                onCancel,
                modifier = Modifier.fillMaxWidth(),
                height = 56,
                fontSize = 17,
            )
            Spacer(Modifier.height(12.sdp()))
            PillButton(
                "返回主页",
                {
                    AppState.clearSession()
                    AppState.navigate(AppScreen.WELCOME)
                },
                modifier = Modifier.fillMaxWidth(),
                height = 56,
                fontSize = 17,
                filled = false,
                background = Color(0xFFFFF7F7),
                foreground = Color(0xFFB03A3A),
                borderColor = Color(0xFFE6C6C6),
            )
        }
    }
}

@Composable
private fun HelpDialog() {
    Scrim(onDismiss = { AppState.helpOpen = false })
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .width(928.sdp())
                .clip(RoundedCornerShape(22.sdp()))
                .background(Color.White)
                .padding(30.sdp()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.sdp())
                        .clip(CircleShape)
                        .background(FujiColor.AccentWeak),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("?", color = FujiColor.Accent, fontSize = 24.ssp(), fontWeight = FontWeight(800))
                }
                Spacer(Modifier.width(14.sdp()))
                Column {
                    Text("帮助与客服", color = FujiColor.Text, fontSize = 24.ssp(), fontWeight = FontWeight(750))
                    Text("扫码联系客服，或拨打下方电话", color = FujiColor.TextSecondary, fontSize = 15.ssp())
                }
                Spacer(Modifier.weight(1f))
                PillButton("✕", { AppState.helpOpen = false }, height = 38, fontSize = 18)
            }
            Spacer(Modifier.height(24.sdp()))
            Row {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.sdp())) {
                    InfoRow("订单号", AppState.orderId)
                    InfoRow("机器 ID", "Fuji12345678")
                    InfoRow("时间", "2026-11-18 23:18:00")
                    InfoRow("电话", "800-1234-5678")
                    InfoRow("邮箱", "service@fuji.example.com")
                }
                QrCode(
                    "https://service.fuji.example.com/support?order=${AppState.orderId}&machine=Fuji12345678",
                    modifier = Modifier
                        .size(160.sdp())
                        .clip(RoundedCornerShape(10.sdp()))
                        .background(Color.White),
                )
            }
            Spacer(Modifier.height(24.sdp()))
            PillButton(
                "完成",
                { AppState.helpOpen = false },
                modifier = Modifier.width(320.sdp()).align(Alignment.CenterHorizontally),
                height = 54,
                fontSize = 17,
            )
        }
    }
}
