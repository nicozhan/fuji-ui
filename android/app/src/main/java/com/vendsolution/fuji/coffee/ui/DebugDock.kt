package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp

/** 调试浮窗：点击任意页面的 FE 品牌标即可打开，便于交付后逐页调试。 */
@Composable
fun DebugDock() {
    Row(
        Modifier
            .fillMaxHeight()
            .width(310.sdp())
            .shadow(
                elevation = 18.sdp(),
                shape = RoundedCornerShape(topStart = 18.sdp(), bottomStart = 18.sdp()),
            )
            .clip(RoundedCornerShape(topStart = 18.sdp(), bottomStart = 18.sdp()))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF0B4F80), Color(0xFF08395C))),
            )
            .padding(horizontal = 18.sdp(), vertical = 24.sdp()),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "调试面板",
                color = Color.White,
                fontSize = 18.ssp(),
                fontWeight = FontWeight(750),
            )
            Text(
                "点击页面左上角 FE 可关闭",
                color = Color(0xB3FFFFFF),
                fontSize = 11.ssp(),
            )
            Spacer(Modifier.height(16.sdp()))
            Text(
                "支付方式（点击快速切换）",
                color = Color.White,
                fontSize = 13.ssp(),
                fontWeight = FontWeight(750),
            )
            Spacer(Modifier.height(8.sdp()))
            Row(horizontalArrangement = Arrangement.spacedBy(8.sdp())) {
                listOf(
                    Triple(1, "一种支付", "仅扫码"),
                    Triple(2, "两种支付", "扫码+POS"),
                    Triple(3, "三种支付", "扫码+POS+现金"),
                ).forEach { (count, label, hint) ->
                    val active = AppState.paymentMethodCount == count
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.sdp()))
                            .background(if (active) FujiColor.Accent else Color(0x1AFFFFFF))
                            .clickable {
                                AppState.paymentMethodCount = count
                                AppState.debugOpen = false
                            }
                            .padding(vertical = 10.sdp()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(label, color = Color.White, fontSize = 12.ssp(), fontWeight = FontWeight(750))
                        Spacer(Modifier.height(3.sdp()))
                        Text(hint, color = Color(0xB3FFFFFF), fontSize = 9.ssp())
                    }
                }
            }
            Spacer(Modifier.height(16.sdp()))
            val items = listOf(
                "欢迎页" to AppScreen.WELCOME,
                "商品页（咖啡）" to AppScreen.PRODUCTS,
                "支付方式" to AppScreen.PAYMENT,
                "扫码支付" to AppScreen.QR_PAYMENT,
                "制作中" to AppScreen.PREPARATION,
                "暂停服务" to AppScreen.OUT_OF_SERVICE,
                "设备设置" to AppScreen.SETUP,
            )
            items.forEach { (label, screen) ->
                val active = AppState.screen == screen
                Text(
                    label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.sdp()))
                        .background(if (active) Color(0x33FFFFFF) else Color(0x14FFFFFF))
                        .clickable { AppState.navigate(screen) }
                        .padding(horizontal = 12.sdp(), vertical = 9.sdp()),
                    color = Color.White,
                    fontSize = 13.ssp(),
                    fontWeight = if (active) FontWeight(750) else FontWeight(500),
                )
                Spacer(Modifier.height(6.sdp()))
            }
            Spacer(Modifier.height(12.sdp()))
            Text(
                "设备档位：${AppState.devicePreset}",
                color = Color(0xB3FFFFFF),
                fontSize = 11.ssp(),
            )
            Spacer(Modifier.height(12.sdp()))
            Text(
                "制作流程",
                color = Color(0xB3FFFFFF),
                fontSize = 11.ssp(),
            )
            Spacer(Modifier.height(6.sdp()))
            Row(horizontalArrangement = Arrangement.spacedBy(6.sdp())) {
                listOf(
                    "开始制作" to "start",
                    "模拟取走" to "taken",
                    "跳到末杯" to "last",
                ).forEach { (label, action) ->
                    Text(
                        label,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.sdp()))
                            .background(Color(0x14FFFFFF))
                            .clickable {
                                AppState.prepDemoAction = action to AppState.prepDemoAction.second + 1
                                AppState.navigate(AppScreen.PREPARATION)
                            }
                            .padding(horizontal = 9.sdp(), vertical = 7.sdp()),
                        color = Color.White,
                        fontSize = 10.ssp(),
                        fontWeight = FontWeight(500),
                    )
                }
            }
            Spacer(Modifier.height(12.sdp()))
            Text(
                "扫码支付状态",
                color = Color(0xB3FFFFFF),
                fontSize = 11.ssp(),
            )
            Spacer(Modifier.height(6.sdp()))
            Row(horizontalArrangement = Arrangement.spacedBy(6.sdp())) {
                listOf(
                    "请扫码" to "waiting",
                    "多码" to "channels",
                    "成功" to "paid",
                    "失败" to "failed",
                ).forEach { (label, value) ->
                    val active = AppState.paymentState == value
                    Text(
                        label,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.sdp()))
                            .background(if (active) Color(0x55FFFFFF) else Color(0x14FFFFFF))
                            .clickable { AppState.paymentState = value }
                            .padding(horizontal = 10.sdp(), vertical = 7.sdp()),
                        color = Color.White,
                        fontSize = 11.ssp(),
                        fontWeight = if (active) FontWeight(750) else FontWeight(500),
                    )
                }
            }
        }
        Spacer(Modifier.width(8.sdp()))
        Text(
            "✕",
            modifier = Modifier
                .clip(RoundedCornerShape(9.sdp()))
                .clickable { AppState.debugOpen = false }
                .padding(6.sdp()),
            color = Color.White,
            fontSize = 14.ssp(),
        )
    }
}
