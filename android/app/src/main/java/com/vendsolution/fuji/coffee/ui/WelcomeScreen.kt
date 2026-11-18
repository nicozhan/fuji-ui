package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.vendsolution.fuji.coffee.R
import com.vendsolution.fuji.coffee.data.AppScreen
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp
import kotlinx.coroutines.delay

private val SLIDES = listOf(
    R.drawable.welcome_slide_1,
    R.drawable.welcome_slide_2,
    R.drawable.welcome_slide_3,
)

private val LANGUAGES = listOf(
    "ja" to "日本語",
    "zh" to "中文",
    "en" to "English",
    "ko" to "한국어",
    "fr" to "Français",
    "vi" to "Tiếng Việt",
    "th" to "ไทย",
)

/** 欢迎页：3 张海报 5 秒轮播，7 语言平铺，点任意区域进入商品页。 */
@Composable
fun WelcomeScreen() {
    var slide by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            slide = (slide + 1) % SLIDES.size
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .clickable { AppState.navigate(AppScreen.PRODUCTS) },
    ) {
        Image(
            painter = painterResource(SLIDES[slide]),
            contentDescription = "欢迎海报",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // 开始按钮：left 10.5%，bottom 20%，与 Web 一致
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = 202.sdp(), bottom = 216.sdp())
                .width(320.sdp())
                .height(78.sdp())
                .clip(RoundedCornerShape(16.sdp()))
                .border(1.sdp(), Color(0x61FFFFFF), RoundedCornerShape(16.sdp()))
                .background(Color(0xE0075B9E))
                .clickable { AppState.navigate(AppScreen.PRODUCTS) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "开始选购",
                color = Color.White,
                fontSize = 22.ssp(),
                fontWeight = FontWeight(650),
            )
        }

        // 7 语言按钮：bottom 7%，gap 20，点击即保存语言并跳页
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.sdp()),
            horizontalArrangement = Arrangement.spacedBy(20.sdp()),
        ) {
            LANGUAGES.forEach { (code, label) ->
                val active = AppState.language == code
                Box(
                    Modifier
                        .width(120.sdp())
                        .height(78.sdp())
                        .clip(RoundedCornerShape(13.sdp()))
                        .border(
                            1.sdp(),
                            if (active) Color(0xFF075B9E) else Color(0xD1FFFFFF),
                            RoundedCornerShape(13.sdp()),
                        )
                        .background(if (active) Color(0xE0075B9E) else Color(0xB8FFFFFF))
                        .clickable {
                            AppState.language = code
                            AppState.navigate(AppScreen.PRODUCTS)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        color = if (active) Color.White else Color(0xFF17384E),
                        fontSize = 14.ssp(),
                        fontWeight = FontWeight(600),
                    )
                }
            }
        }
    }
}
