package com.vendsolution.fuji.coffee.ui

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp
import kotlinx.coroutines.delay

/** 暂停服务页：先自检、后停机；自检完成显示暂停卡片与客服电话。 */
@Composable
fun OutOfServiceScreen() {
    var checking by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3000)
        checking = false
    }

    Column(Modifier.fillMaxSize()) {
        StatusBar()
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.sdp(), vertical = 32.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (checking) {
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(180.sdp())
                        .clip(CircleShape)
                        .border(10.sdp(), Color(0x33FFFFFF), CircleShape)
                        .background(FujiColor.Accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("…", color = Color.White, fontSize = 64.ssp(), fontWeight = FontWeight(800))
                }
                Spacer(Modifier.height(28.sdp()))
                Text("设备自检中", color = FujiColor.Text, fontSize = 40.ssp(), fontWeight = FontWeight(750))
                Text(
                    "正在检测水温与出杯模块，请稍候。",
                    color = FujiColor.TextSecondary,
                    fontSize = 19.ssp(),
                )
                Spacer(Modifier.weight(1f))
            } else {
                Spacer(Modifier.height(30.sdp()))
                Box(
                    Modifier
                        .size(280.sdp())
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFE4F2FB), Color(0x00000000)),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(22.sdp())) {
                        repeat(2) {
                            Box(
                                Modifier
                                    .size(width = 34.sdp(), height = 120.sdp())
                                    .clip(RoundedCornerShape(10.sdp()))
                                    .background(
                                        Brush.verticalGradient(listOf(Color(0xFF3BA3E4), FujiColor.Accent)),
                                    ),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(26.sdp()))
                Text("暂停服务", color = FujiColor.Text, fontSize = 54.ssp(), fontWeight = FontWeight(750))
                Spacer(Modifier.height(14.sdp()))
                Text(
                    "设备暂时无法提供饮品，可能几分钟后恢复。\n如需帮助，请联系客服。",
                    color = FujiColor.TextSecondary,
                    fontSize = 21.ssp(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(30.sdp()))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(18.sdp()))
                        .background(Color.White)
                        .border(1.sdp(), FujiColor.Border, RoundedCornerShape(18.sdp()))
                        .padding(horizontal = 36.sdp(), vertical = 18.sdp()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("客服电话", color = FujiColor.TextSecondary, fontSize = 17.ssp())
                    Spacer(Modifier.width(28.sdp()))
                    Text("800-1234-5678", color = FujiColor.AccentDark, fontSize = 40.ssp(), fontWeight = FontWeight(750))
                }
                Spacer(Modifier.height(30.sdp()))
                PillButton("设备已恢复 · 返回商品页", {
                    AppState.navigate(AppScreen.PRODUCTS)
                }, modifier = Modifier.width(420.sdp()), height = 64, fontSize = 18)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

private val PRESETS = listOf(
    Triple("s10", "10 寸", "1024 × 768"),
    Triple("m21", "21 寸", "1920 × 1080"),
    Triple("l27", "27 寸", "2560 × 1440"),
)

/** 设备设置页：三个预设档位 + 自定义宽高，写入后返回来源页。 */
@Composable
fun SetupScreen() {
    var customW by remember { mutableStateOf("1920") }
    var customH by remember { mutableStateOf("1080") }
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 64.sdp(), vertical = 48.sdp()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("设备设置", color = FujiColor.Text, fontSize = 38.ssp(), fontWeight = FontWeight(750))
        Spacer(Modifier.height(10.sdp()))
        Text("选择这台机器的屏幕尺寸", color = FujiColor.TextSecondary, fontSize = 20.ssp())
        Spacer(Modifier.height(32.sdp()))

        Row(horizontalArrangement = Arrangement.spacedBy(20.sdp())) {
            PRESETS.forEach { (id, label, size) ->
                val active = AppState.devicePreset == id
                Column(
                    Modifier
                        .width(360.sdp())
                        .clip(RoundedCornerShape(16.sdp()))
                        .background(if (active) FujiColor.AccentWeak else Color.White)
                        .border(
                            if (active) 3.sdp() else 1.sdp(),
                            if (active) FujiColor.Accent else FujiColor.Border,
                            RoundedCornerShape(16.sdp()),
                        )
                        .clickable { AppState.devicePreset = id }
                        .padding(22.sdp()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(label, color = FujiColor.Text, fontSize = 26.ssp(), fontWeight = FontWeight(800))
                    Spacer(Modifier.height(8.sdp()))
                    Text(size, color = FujiColor.TextSecondary, fontSize = 20.ssp(), fontWeight = FontWeight(700))
                    if (active) {
                        Spacer(Modifier.height(8.sdp()))
                        Text("已选择", color = FujiColor.Accent, fontSize = 14.ssp())
                    }
                }
            }
        }
        Spacer(Modifier.height(28.sdp()))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("自定义宽度", color = FujiColor.TextSecondary, fontSize = 17.ssp())
            Spacer(Modifier.width(12.sdp()))
            Box(
                Modifier
                    .width(140.sdp())
                    .height(52.sdp())
                    .clip(RoundedCornerShape(10.sdp()))
                    .background(Color.White)
                    .border(1.sdp(), FujiColor.Border, RoundedCornerShape(10.sdp()))
                    .padding(horizontal = 12.sdp()),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.TextField(
                    value = customW,
                    onValueChange = { customW = it },
                    singleLine = true,
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = FujiColor.Accent,
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 17.ssp(),
                        color = FujiColor.Text,
                    ),
                )
            }
            Spacer(Modifier.width(24.sdp()))
            Text("高度", color = FujiColor.TextSecondary, fontSize = 17.ssp())
            Spacer(Modifier.width(12.sdp()))
            Box(
                Modifier
                    .width(140.sdp())
                    .height(52.sdp())
                    .clip(RoundedCornerShape(10.sdp()))
                    .background(Color.White)
                    .border(1.sdp(), FujiColor.Border, RoundedCornerShape(10.sdp()))
                    .padding(horizontal = 12.sdp()),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.TextField(
                    value = customH,
                    onValueChange = { customH = it },
                    singleLine = true,
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = FujiColor.Accent,
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 17.ssp(),
                        color = FujiColor.Text,
                    ),
                )
            }
        }
        Spacer(Modifier.height(32.sdp()))
        Row(horizontalArrangement = Arrangement.spacedBy(20.sdp())) {
            PillButton("保存并返回", {
                val w = customW.toIntOrNull() ?: 1920
                val h = customH.toIntOrNull() ?: 1080
                AppState.devicePreset = when {
                    w >= 2200 -> "l27"
                    w >= 1280 -> "m21"
                    else -> "s10"
                }
                AppState.navigate(AppScreen.PRODUCTS)
            }, modifier = Modifier.width(260.sdp()), height = 60, fontSize = 18)
            PillButton("自动检测", {
                AppState.devicePreset = "m21"
                AppState.navigate(AppScreen.PRODUCTS)
            }, modifier = Modifier.width(260.sdp()), height = 60, fontSize = 18, filled = false)
            PillButton("返回", {
                AppState.navigate(AppScreen.WELCOME)
            }, modifier = Modifier.width(180.sdp()), height = 60, fontSize = 18, filled = false)
        }
    }
}
