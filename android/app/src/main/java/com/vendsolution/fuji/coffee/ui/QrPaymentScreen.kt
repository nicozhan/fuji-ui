package com.vendsolution.fuji.coffee.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vendsolution.fuji.coffee.R
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp
import kotlinx.coroutines.delay

private val CHANNELS = listOf(
    Triple("alipay", "支付宝", Color(0xFF1677FF)),
    Triple("wechat", "微信支付", Color(0xFF07C160)),
    Triple("wechatqr", "微信扫码", Color(0xFF0B8F66)),
)

/** 扫码支付页：4 步进度、三渠道切换、90 秒倒计时、支付结果。 */
@Composable
fun QrPaymentScreen() {
    var seconds by remember { mutableIntStateOf(90) }
    var showHomeConfirm by remember { mutableStateOf(false) }
    val state = AppState.paymentState
    val total = if (AppState.cart.isEmpty()) 1040 else AppState.cartTotal

    LaunchedEffect(state) {
        if (state == "paid" && !AppState.freezePaid) {
            delay(1400)
            AppState.navigate(AppScreen.PREPARATION)
        }
    }
    LaunchedEffect(state, seconds) {
        if ((state == "waiting" || state == "channels") && seconds > 0) {
            delay(1000)
            seconds -= 1
            if (seconds == 0) AppState.paymentState = "failed"
        }
    }

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
            BrandMark(size = 33)
            Spacer(Modifier.width(10.sdp()))
            Text("Fuji Electric", color = Color(0xFF101C26), fontSize = 22.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(20.sdp())) {
                listOf("选择商品", "口味定制", "结账", "开始制作").forEachIndexed { index, label ->
                    val current = if (state == "paid") index == 3 else index == 2
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(32.sdp())
                                .clip(RoundedCornerShape(50))
                                .background(if (current || index < 2) FujiColor.Accent else FujiColor.Surface2),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                if (index < 2) "✓" else "${index + 1}",
                                color = if (current || index < 2) Color.White else FujiColor.TextSecondary,
                                fontSize = 14.ssp(),
                                fontWeight = FontWeight(750),
                            )
                        }
                        Spacer(Modifier.width(8.sdp()))
                        Text(
                            label,
                            color = if (current) FujiColor.AccentDark else FujiColor.TextSecondary,
                            fontSize = 16.ssp(),
                            fontWeight = if (current) FontWeight(750) else FontWeight(500),
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            PillButton("主页", { showHomeConfirm = true }, modifier = Modifier.width(126.sdp()), height = 54, fontSize = 15)
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.sdp(), vertical = 24.sdp()),
        ) {
            Text("应付 ¥$total", color = FujiColor.Text, fontSize = 36.ssp(), fontWeight = FontWeight(800))
            Spacer(Modifier.height(20.sdp()))
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                // 左右两框等宽，在 1500 设计像素内居中分布
                Row(
                    Modifier
                        .fillMaxHeight()
                        .widthIn(max = 1500.sdp()),
                    horizontalArrangement = Arrangement.spacedBy(28.sdp()),
                ) {
                    // 左侧：支付渠道图 + 三渠道选择
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(22.sdp()))
                            .background(Color.White)
                            .border(1.sdp(), FujiColor.Border, RoundedCornerShape(22.sdp()))
                            .padding(20.sdp()),
                    ) {
                    Image(
                        painter = painterResource(R.drawable.payment_methods),
                        contentDescription = "支付方式",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.sdp()))
                            .background(FujiColor.Surface2),
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(Modifier.height(16.sdp()))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.sdp())) {
                        CHANNELS.forEach { (id, label, color) ->
                            val active = AppState.selectedChannel == id
                            Column(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.sdp()))
                                    .background(if (active) color.copy(alpha = 0.10f) else FujiColor.Surface2)
                                    .border(
                                        2.sdp(),
                                        if (active) color else FujiColor.Border,
                                        RoundedCornerShape(14.sdp()),
                                    )
                                    .clickable { AppState.selectedChannel = id as String }
                                    .padding(12.sdp()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(label, color = color, fontSize = 17.ssp(), fontWeight = FontWeight(750))
                            }
                        }
                    }
                }

                // 右侧：二维码 / 支付结果原地切换（与左框等高等宽）
                // 对应 Web qr-payment.html：waiting/channels/paid/failed 共用同一容器，
                // 状态变化时只切换框内内容，不新开页面、不遮挡状态栏与顶部进度条。
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.sdp()))
                        .background(Color.White)
                        .border(1.sdp(), FujiColor.Border, RoundedCornerShape(22.sdp()))
                        .padding(24.sdp()),
                    contentAlignment = Alignment.Center,
                ) {
                    val panel = if (state == "waiting" || state == "channels") "qr" else state
                    Crossfade(targetState = panel, label = "qr-state") { target ->
                        when (target) {
                            "paid" -> ResultPanel(success = true)
                            "failed" -> ResultPanel(success = false)
                            else -> QrPanel(seconds = seconds)
                        }
                    }
                }
            }
        }
    }
    }

    if (showHomeConfirm) {
        Scrim(onDismiss = { showHomeConfirm = false })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .width(544.sdp())
                    .clip(RoundedCornerShape(22.sdp()))
                    .background(Color.White)
                    .padding(36.sdp()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("清除订单并返回？", color = FujiColor.Text, fontSize = 24.ssp(), fontWeight = FontWeight(750))
                Spacer(Modifier.height(16.sdp()))
                PillButton("继续支付", { showHomeConfirm = false }, modifier = Modifier.fillMaxWidth(), height = 56)
                Spacer(Modifier.height(10.sdp()))
                PillButton(
                    "返回主页",
                    { AppState.clearSession(); AppState.navigate(AppScreen.WELCOME) },
                    modifier = Modifier.fillMaxWidth(),
                    height = 56,
                    filled = false,
                )
            }
        }
    }
}

/** 二维码面板（waiting / channels），严格对照 qr-payment.html 的扫码 state-panel。 */
@Composable
private fun QrPanel(seconds: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("请扫码支付", color = FujiColor.Text, fontSize = 30.ssp(), fontWeight = FontWeight(750))
        Spacer(Modifier.height(8.sdp()))
        Text("订单号 ${AppState.orderId}", color = FujiColor.TextSecondary, fontSize = 14.ssp())
        Spacer(Modifier.height(16.sdp()))
        Box(
            Modifier
                .size(260.sdp())
                .clip(RoundedCornerShape(16.sdp()))
                .background(Color.White)
                .border(1.sdp(), FujiColor.Border, RoundedCornerShape(16.sdp()))
                .padding(12.sdp()),
        ) {
            QrCode(
                when (AppState.selectedChannel) {
                    "wechat" -> "weixin://wxpay/bizpayurl?pr=${AppState.orderId}"
                    "wechatqr" -> "https://pay.example.com/fuji/wxscan?order=${AppState.orderId}"
                    else -> "https://qr.alipay.com/fuji-${AppState.orderId}"
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.height(16.sdp()))
        Text("剩余时间", color = FujiColor.TextSecondary, fontSize = 15.ssp())
        Text(
            "%02d:%02d".format(seconds / 60, seconds % 60),
            color = FujiColor.Accent,
            fontSize = 30.ssp(),
            fontWeight = FontWeight(750),
        )
        Spacer(Modifier.height(12.sdp()))
    }
}

/** 支付结果面板（paid / failed），在二维码框内原地切换，对照 qr-payment.html 的 .result-panel。 */
@Composable
private fun ResultPanel(success: Boolean) {
    val green = Color(0xFF168565)
    val greenWeak = Color(0xFFEAF7F2)
    val red = Color(0xFFC64747)
    val redWeak = Color(0xFFFCEDED)
    val tint = if (success) green else red
    val tintWeak = if (success) greenWeak else redWeak

    // 三个跳动圆点（Web .making-dots，1.2s 循环错相）
    val transition = rememberInfiniteTransition(label = "making-dots")
    val dots = (0..2).map { i ->
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, delayMillis = i * 200),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "dot-$i",
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 圆形结果图标：淡色圆底 + 对勾/叉（Canvas 绘制，对应 Web .state-icon svg）
            Canvas(Modifier.size(146.sdp())) {
                drawCircle(tintWeak, radius = size.minDimension / 2f, center = center)
                val c = center
                val d = size.minDimension
                if (success) {
                    drawCircle(
                        tint,
                        radius = d * 0.43f / 2f,
                        center = c,
                        style = Stroke(width = d * 0.052f),
                    )
                    drawLine(tint, Offset(c.x - d * 0.20f, c.y + d * 0.01f), Offset(c.x - d * 0.05f, c.y + d * 0.16f), d * 0.072f, StrokeCap.Round)
                    drawLine(tint, Offset(c.x - d * 0.05f, c.y + d * 0.16f), Offset(c.x + d * 0.22f, c.y - d * 0.14f), d * 0.072f, StrokeCap.Round)
                } else {
                    drawCircle(
                        tint,
                        radius = d * 0.43f / 2f,
                        center = c,
                        style = Stroke(width = d * 0.052f),
                    )
                    drawLine(tint, Offset(c.x - d * 0.13f, c.y - d * 0.13f), Offset(c.x + d * 0.13f, c.y + d * 0.13f), d * 0.072f, StrokeCap.Round)
                    drawLine(tint, Offset(c.x + d * 0.13f, c.y - d * 0.13f), Offset(c.x - d * 0.13f, c.y + d * 0.13f), d * 0.072f, StrokeCap.Round)
                }
            }
            Spacer(Modifier.height(28.sdp()))
            Text(
                if (success) "支付成功" else "支付失败",
                color = FujiColor.Text,
                fontSize = 34.ssp(),
                fontWeight = FontWeight(750),
            )
            Spacer(Modifier.height(14.sdp()))
            Text(
                if (success) "正在为您制作饮品" else "支付未成功，请重试",
                color = FujiColor.TextSecondary,
                fontSize = 17.ssp(),
                textAlign = TextAlign.Center,
            )
            if (success) {
                Spacer(Modifier.height(30.sdp()))
                Row(horizontalArrangement = Arrangement.spacedBy(12.sdp())) {
                    dots.forEach { alpha ->
                        Canvas(Modifier.size(14.sdp())) {
                            drawCircle(green, radius = size.minDimension / 2f * alpha.value, center = center)
                        }
                    }
                }
            } else {
                Spacer(Modifier.height(32.sdp()))
                Row(horizontalArrangement = Arrangement.spacedBy(16.sdp())) {
                    PillButton(
                        "重新支付",
                        { AppState.paymentState = "waiting" },
                        modifier = Modifier.width(220.sdp()),
                        height = 56,
                    )
                    PillButton(
                        "返回首页",
                        { AppState.clearSession(); AppState.navigate(AppScreen.WELCOME) },
                        modifier = Modifier.width(220.sdp()),
                        height = 56,
                        filled = false,
                    )
                }
            }
    }
}
