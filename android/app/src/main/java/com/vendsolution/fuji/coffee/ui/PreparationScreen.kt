package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.vendsolution.fuji.coffee.R
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.data.ProductCatalog
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp
import kotlinx.coroutines.delay

private const val CUP_PRODUCTION_MS = 5000L

/** 制作过程页：每杯 5 秒，做好即停，取走下一杯，末杯取走回首页。 */
@Composable
fun PreparationScreen() {
    val product = AppState.cart.firstOrNull()?.product ?: ProductCatalog.ALL[1]
    val states = remember { mutableStateListOf("waiting", "waiting", "waiting") }
    val progress = remember { mutableStateListOf(0f, 0f, 0f) }
    var active by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running, active) {
        while (running && active in 0..2) {
            delay(100)
            progress[active] = minOf(100f, progress[active] + 2f)
            if (progress[active] >= 100f) {
                states[active] = "ready"
                running = false
            }
        }
    }

    val pickup = active in 0..2 && states[active] == "ready"

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(92.sdp())
                .background(Color.White)
                .padding(horizontal = 48.sdp()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandMark(size = 31)
            Spacer(Modifier.width(10.sdp()))
            Text("Fuji Electric", color = Color(0xFF101C26), fontSize = 20.ssp(), fontWeight = FontWeight(750))
            Spacer(Modifier.width(24.sdp()))
            Text("DRINK PREPARATION", color = FujiColor.TextSecondary, fontSize = 14.ssp())
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(FujiColor.AccentWeak)
                    .padding(horizontal = 18.sdp(), vertical = 8.sdp()),
            ) {
                Text("订单 ${AppState.orderId}", color = FujiColor.AccentDark, fontSize = 15.ssp(), fontWeight = FontWeight(760))
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.sdp(), vertical = 28.sdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                if (pickup) "请取走第 ${active + 1} 杯" else "饮品正在制作中",
                color = if (pickup) Color(0xFFB8791A) else FujiColor.Text,
                fontSize = 38.ssp(),
                fontWeight = FontWeight(730),
            )
            Text(
                if (pickup) "制作已完成，请从取货口取走" else "每杯约需 5 秒，请稍候",
                color = FujiColor.TextSecondary,
                fontSize = 18.ssp(),
            )

            Spacer(Modifier.height(24.sdp()))
            Box(
                Modifier
                    .size(300.sdp())
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(if (pickup) Color(0xFFFFF3D8) else Color(0xFFDCEEF9), Color(0x00000000)),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                product.imageRes?.let { res ->
                    Image(
                        painter = painterResource(res),
                        contentDescription = product.name,
                        modifier = Modifier.size(230.sdp()),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            if (pickup) {
                Spacer(Modifier.height(10.sdp()))
                Box(
                    Modifier
                        .clip(CircleShape)
                        .border(2.sdp(), Color(0xFFE7C086), CircleShape)
                        .background(Color(0xFFFFFAF0))
                        .padding(horizontal = 28.sdp(), vertical = 12.sdp()),
                ) {
                    Text("请取走", color = Color(0xFFB8791A), fontSize = 22.ssp(), fontWeight = FontWeight(780))
                }
            }

            Spacer(Modifier.height(26.sdp()))
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(150.sdp())
                    .clip(RoundedCornerShape(24.sdp()))
                    .background(Color.White)
                    .border(1.sdp(), FujiColor.Border, RoundedCornerShape(24.sdp()))
                    .padding(20.sdp()),
                horizontalArrangement = Arrangement.spacedBy(20.sdp()),
            ) {
                repeat(3) { index ->
                    CupCard(
                        index = index,
                        state = states[index],
                        progress = progress[index],
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(20.sdp()))
            Row(horizontalArrangement = Arrangement.spacedBy(16.sdp())) {
                PillButton(
                    "开始制作",
                    {
                        states.fill("waiting"); progress.fill(0f); active = 0
                        states[0] = "making"; running = true
                    },
                    height = 52,
                    fontSize = 15,
                )
                PillButton(
                    "取走一杯",
                    {
                        if (states[active] == "ready") {
                            states[active] = "done"
                            active++
                            if (active < 3) {
                                states[active] = "making"
                                progress[active] = 0f
                                running = true
                            } else {
                                AppState.clearSession()
                                AppState.navigate(AppScreen.WELCOME)
                            }
                        }
                    },
                    height = 52,
                    fontSize = 15,
                )
                PillButton(
                    "末杯 READY",
                    {
                        states[0] = "done"; states[1] = "done"; states[2] = "ready"
                        progress[0] = 100f; progress[1] = 100f; progress[2] = 100f
                        active = 2; running = false
                    },
                    height = 52,
                    fontSize = 15,
                )
                PillButton(
                    "返回主页",
                    { AppState.clearSession(); AppState.navigate(AppScreen.WELCOME) },
                    height = 52,
                    fontSize = 15,
                    filled = false,
                )
            }
        }
    }
}

@Composable
private fun CupCard(index: Int, state: String, progress: Float, modifier: Modifier = Modifier) {
    val product = ProductCatalog.ALL.getOrNull(index + 1) ?: ProductCatalog.ALL[0]
    Column(
        modifier
            .clip(RoundedCornerShape(16.sdp()))
            .background(if (state == "ready") Color(0xFFFFFAF0) else FujiColor.Surface2)
            .border(
                1.sdp(),
                if (state == "ready") Color(0xFFE7C086) else FujiColor.Border,
                RoundedCornerShape(16.sdp()),
            )
            .padding(14.sdp()),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            product.imageRes?.let { res ->
                Image(
                    painter = painterResource(res),
                    contentDescription = product.name,
                    modifier = Modifier.size(70.sdp()),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(Modifier.width(12.sdp()))
            Column {
                Text("第 ${index + 1} 杯", color = FujiColor.TextSecondary, fontSize = 13.ssp())
                Text(
                    when (state) {
                        "making" -> "制作中 ${progress.toInt()}%"
                        "ready" -> "请取走"
                        "done" -> "已完成"
                        else -> "等待中"
                    },
                    color = if (state == "ready") Color(0xFFB8791A) else FujiColor.Text,
                    fontSize = 17.ssp(),
                    fontWeight = FontWeight(750),
                )
            }
        }
        Spacer(Modifier.height(12.sdp()))
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.sdp())
                .clip(CircleShape)
                .background(Color(0xFFDCE7EE)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progress / 100f)
                    .height(8.sdp())
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF2A9BD7), FujiColor.Accent)),
                    ),
            )
        }
    }
}
