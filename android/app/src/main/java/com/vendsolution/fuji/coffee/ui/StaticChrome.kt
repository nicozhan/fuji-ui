package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.vendsolution.fuji.coffee.data.ProductCatalog
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.FujiDimens
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp

/**
 * 页面外围框架（状态栏 / 品牌导航 / 分类 / 广告位 / 分页 / 购物车胶囊）。
 *
 * 说明：按本期范围，这些元素**仅为静态视觉占位，不实现任何交互**，
 * 唯一有数据驱动与三态逻辑的组件是 [ProductCard]。后续迁移其它页面时
 * 再逐个替换为可交互组件。
 */

@Composable
fun StatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FujiDimens.STATUS_BAR_H.sdp())
            .background(FujiColor.StatusBarBg)
            .padding(horizontal = 28.sdp()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.sdp()),
    ) {
        DeviceGlyph(Modifier.size(16.sdp()))
        Text("Machine ID:", color = FujiColor.MachineId, fontSize = 13.ssp())
        Text(
            "Fuji12345678",
            color = FujiColor.MachineIdStrong,
            fontSize = 13.ssp(),
            fontWeight = FontWeight.W500,
        )
        Spacer(Modifier.width(12.sdp()))
        Text("联系电话 800-1234-5678", color = FujiColor.StatusText, fontSize = 13.ssp())
        Spacer(Modifier.weight(1f))

        Row(
            Modifier
                .height(30.sdp())
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 12.sdp()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.sdp()),
        ) {
            HomeGlyph(Modifier.size(13.sdp()))
            Text("首页", color = FujiColor.AccentDeep, fontSize = 12.ssp(), fontWeight = FontWeight.W600)
        }

        Row(
            Modifier
                .height(30.sdp())
                .clip(CircleShape)
                .background(Color(0x1F000000))
                .border(1.sdp(), Color(0x42FFFFFF), CircleShape)
                .padding(horizontal = 8.sdp()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.sdp()),
        ) {
            Box(
                Modifier
                    .size(width = 28.sdp(), height = 18.sdp())
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Text("DEMO", color = FujiColor.AccentDeep, fontSize = 10.ssp(), fontWeight = FontWeight.W800)
            }
            Box(
                Modifier
                    .size(width = 34.sdp(), height = 19.sdp())
                    .clip(CircleShape)
                    .background(FujiColor.Accent),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Box(
                    Modifier
                        .padding(end = 2.sdp())
                        .size(15.sdp())
                        .clip(CircleShape)
                        .background(Color.White),
                )
            }
            Text("ON", color = Color.White, fontSize = 12.ssp())
        }

        Text("11月4日 23:40", color = FujiColor.StatusText, fontSize = 13.ssp())
        SignalGlyph(Modifier.size(width = 22.sdp(), height = 14.sdp()))
    }
}

@Composable
fun MainHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FujiDimens.HEADER_H.sdp())
            .background(FujiColor.Surface)
            .padding(horizontal = 34.sdp()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "FE",
            color = FujiColor.Accent,
            fontSize = 31.ssp(),
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.W900,
            letterSpacing = (-3.7f).ssp(),
        )
        Spacer(Modifier.width(10.sdp()))
        Text(
            "Fuji Electric",
            color = Color(0xFF101C26),
            fontSize = 20.ssp(),
            fontWeight = FontWeight(750),
        )
        Spacer(Modifier.width(24.sdp()))
        HeaderChip("帮助", showIcon = true)
        Spacer(Modifier.weight(1f))
        CategoryNav()
        Spacer(Modifier.weight(1f))
        HeaderChip("简体中文", showIcon = false)
    }
}

@Composable
private fun HeaderChip(label: String, showIcon: Boolean) {
    Row(
        Modifier
            .height(52.sdp())
            .clip(CircleShape)
            .background(FujiColor.Surface)
            .border(1.sdp(), FujiColor.Border, CircleShape)
            .padding(horizontal = 23.sdp()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.sdp()),
    ) {
        if (showIcon) HelpGlyph(Modifier.size(20.sdp()))
        Text(label, color = FujiColor.Text, fontSize = 17.ssp(), fontWeight = FontWeight(650))
        if (!showIcon) CaretGlyph(Modifier.size(13.sdp()))
    }
}

@Composable
private fun CategoryNav() {
    val labels = listOf("全部", "咖啡", "奶茶", "其他饮品")
    Row(horizontalArrangement = Arrangement.spacedBy(12.sdp())) {
        labels.forEach { label ->
            val active = label == "咖啡"
            Row(
                Modifier
                    .height(52.sdp())
                    .clip(CircleShape)
                    .background(if (active) FujiColor.Accent else FujiColor.AccentWeak)
                    .border(
                        1.sdp(),
                        if (active) FujiColor.Accent else Color(0xFFB9D9ED),
                        CircleShape,
                    )
                    .padding(horizontal = 24.sdp()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.sdp()),
            ) {
                CategoryGlyph(Modifier.size(18.sdp()), if (active) Color.White else FujiColor.Text)
                Text(
                    label,
                    color = if (active) Color.White else FujiColor.Text,
                    fontSize = 17.ssp(),
                    fontWeight = FontWeight(650),
                    maxLines = 1,
                )
            }
        }
    }
}

/** 广告位占位：不引入图片素材，待后续提供海报后替换本组件内容。 */
@Composable
fun AdSidebar(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    listOf(FujiColor.AdPlaceholderTop, FujiColor.AdPlaceholderBottom),
                ),
            )
            .padding(start = 26.sdp(), top = 34.sdp(), end = 26.sdp(), bottom = 26.sdp()),
    ) {
        // 装饰圆，纯代码绘制
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x22FFFFFF),
                radius = size.width * 0.72f,
                center = Offset(size.width * 0.95f, size.height * 0.72f),
            )
        }
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(horizontal = 14.sdp(), vertical = 7.sdp()),
            ) {
                Text(
                    "FUJI SELECTION · AD",
                    color = FujiColor.AccentDark,
                    fontSize = 13.ssp(),
                    fontWeight = FontWeight.W800,
                )
            }
            Spacer(Modifier.height(38.sdp()))
            Text(
                "精选咖啡\n体验",
                color = FujiColor.AccentDeep,
                fontSize = 44.ssp(),
                fontWeight = FontWeight.W800,
                lineHeight = 52.ssp(),
            )
            Spacer(Modifier.height(16.sdp()))
            Text(
                "香醇一杯，特别时刻。",
                color = FujiColor.TextSecondary,
                fontSize = 20.ssp(),
                fontWeight = FontWeight.W500,
            )
            Spacer(Modifier.weight(1f))
            CoffeeCupSilhouette(
                Modifier
                    .fillMaxWidth(0.72f)
                    .height(300.sdp())
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.weight(1f))
            Row(
                Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.sdp()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(width = 22.sdp(), height = 8.sdp())
                        .clip(CircleShape)
                        .background(Color.White),
                )
                Box(
                    Modifier
                        .size(8.sdp())
                        .clip(CircleShape)
                        .background(Color(0x80FFFFFF)),
                )
            }
        }
    }
}

@Composable
private fun CoffeeCupSilhouette(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cupTop = h * 0.30f
        val cupBottom = h * 0.88f
        val cupHalf = w * 0.34f
        val cup = Path().apply {
            moveTo(cx - cupHalf, cupTop)
            lineTo(cx + cupHalf, cupTop)
            lineTo(cx + cupHalf * 0.78f, cupBottom)
            lineTo(cx - cupHalf * 0.78f, cupBottom)
            close()
        }
        drawPath(cup, color = Color(0xFFFDFEFF))
        drawOval(
            color = Color(0xFFC58A54),
            topLeft = Offset(cx - cupHalf * 0.92f, cupTop - h * 0.015f),
            size = Size(cupHalf * 1.84f, h * 0.10f),
        )
        drawOval(
            color = Color(0x66FFFFFF),
            topLeft = Offset(cx - cupHalf * 0.34f, cupTop + h * 0.01f),
            size = Size(cupHalf * 0.68f, h * 0.055f),
        )
        // 杯托
        drawOval(
            color = Color(0xFFFDFEFF).copy(alpha = 0.90f),
            topLeft = Offset(cx - w * 0.46f, cupBottom - h * 0.02f),
            size = Size(w * 0.92f, h * 0.11f),
        )
        // 把手
        drawCircle(
            color = Color.White,
            radius = w * 0.12f,
            center = Offset(cx + cupHalf * 1.05f, cupTop + (cupBottom - cupTop) * 0.42f),
            style = Stroke(width = w * 0.055f),
        )
    }
}

@Composable
fun PaginationBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FujiDimens.PAGER_H.sdp()),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PageArrowButton(isPrev = true, disabled = true)
        repeat(ProductCatalog.TOTAL_PAGES) { index ->
            PageButton((index + 1).toString(), active = index == 0)
        }
        PageArrowButton(isPrev = false)
        Spacer(Modifier.width(14.sdp()))
        Text(
            "1 / ${ProductCatalog.TOTAL_PAGES}　${ProductCatalog.FIRST_PAGE.size} 件商品",
            color = FujiColor.TextSecondary,
            fontSize = 14.ssp(),
        )
    }
}

/** 前后翻页箭头：用 Canvas 绘制 chevron，避免字体缺字 / 字号过小。 */
@Composable
private fun PageArrowButton(isPrev: Boolean, disabled: Boolean = false) {
    val tint = if (disabled) Color(0x9A075B9E) else FujiColor.AccentDark
    val stroke = 2.2f.sdp()
    val background = if (disabled) {
        Brush.verticalGradient(listOf(Color(0xFFF5F9FC), Color(0xFFF5F9FC)))
    } else {
        Brush.verticalGradient(listOf(Color.White, Color(0xFFF2F8FC)))
    }
    Box(
        Modifier
            .padding(horizontal = 5.sdp())
            .size(width = 44.sdp(), height = 42.sdp())
            .clip(RoundedCornerShape(12.sdp()))
            .background(background)
            .border(1.sdp(), FujiColor.PageBorder, RoundedCornerShape(12.sdp())),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(18.sdp())) {
            val c = center
            val d = size.width * 0.24f
            val sw = stroke.toPx()
            if (isPrev) {
                drawLine(tint, Offset(c.x + d * 0.6f, c.y - d), Offset(c.x - d * 0.6f, c.y), sw, StrokeCap.Round)
                drawLine(tint, Offset(c.x - d * 0.6f, c.y), Offset(c.x + d * 0.6f, c.y + d), sw, StrokeCap.Round)
            } else {
                drawLine(tint, Offset(c.x - d * 0.6f, c.y - d), Offset(c.x + d * 0.6f, c.y), sw, StrokeCap.Round)
                drawLine(tint, Offset(c.x + d * 0.6f, c.y), Offset(c.x - d * 0.6f, c.y + d), sw, StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun PageButton(
    label: String,
    active: Boolean = false,
    disabled: Boolean = false,
) {
    val background = when {
        disabled -> Brush.verticalGradient(listOf(Color(0xFFF5F9FC), Color(0xFFF5F9FC)))
        active -> Brush.verticalGradient(
            listOf(Color(0xFF2F95DA), Color(0xFF0E6BAD)),
        )
        else -> Brush.verticalGradient(listOf(Color.White, Color(0xFFF2F8FC)))
    }
    Box(
        Modifier
            .padding(horizontal = 5.sdp())
            .size(width = 44.sdp(), height = 42.sdp())
            .clip(RoundedCornerShape(12.sdp()))
            .background(background)
            .border(
                1.sdp(),
                if (active) Color.Transparent else FujiColor.PageBorder,
                RoundedCornerShape(12.sdp()),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = when {
                disabled -> Color(0x9A075B9E)
                active -> Color.White
                else -> FujiColor.AccentDark
            },
            fontSize = 15.ssp(),
            fontWeight = FontWeight(750),
        )
    }
}

/** 右下角购物车胶囊：静态占位，本期不接购物车逻辑。 */
@Composable
fun CartPill(modifier: Modifier = Modifier) {
    Row(
        modifier
            .height(52.sdp())
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF2F95DA), Color(0xFF0E6BAD)),
                ),
            )
            .padding(horizontal = 20.sdp()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.sdp()),
    ) {
        Text(
            "购物车",
            color = Color.White,
            fontSize = 20.ssp(),
            fontWeight = FontWeight.W700,
        )
        Box(
            Modifier
                .size(28.sdp())
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Text("3", color = FujiColor.AccentDark, fontSize = 15.ssp(), fontWeight = FontWeight.W800)
        }
    }
}

/* ---------- 纯代码绘制的小图标（不引入任何图片 / 字体素材） ---------- */

@Composable
private fun DeviceGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRoundRect(
            color = FujiColor.MachineId,
            size = Size(size.width, size.height * 0.82f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
        )
        drawLine(
            FujiColor.MachineId,
            Offset(0f, size.height * 0.93f),
            Offset(size.width * 0.55f, size.height * 0.93f),
            strokeWidth = 2f,
        )
    }
}

@Composable
private fun HomeGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val p = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.12f)
            lineTo(size.width * 0.92f, size.height * 0.48f)
            lineTo(size.width * 0.78f, size.height * 0.48f)
            lineTo(size.width * 0.78f, size.height * 0.90f)
            lineTo(size.width * 0.22f, size.height * 0.90f)
            lineTo(size.width * 0.22f, size.height * 0.48f)
            lineTo(size.width * 0.08f, size.height * 0.48f)
            close()
        }
        drawPath(p, color = FujiColor.AccentDeep, style = Stroke(width = 1.6f))
    }
}

@Composable
private fun HelpGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawCircle(
            FujiColor.Accent,
            radius = size.minDimension * 0.42f,
            center = center,
            style = Stroke(width = 1.8f),
        )
        drawCircle(FujiColor.Accent, radius = 1.8f, center = Offset(center.x, center.y + size.height * 0.24f))
    }
}

@Composable
private fun CaretGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawLine(
            FujiColor.Text,
            Offset(size.width * 0.22f, size.height * 0.40f),
            Offset(size.width * 0.50f, size.height * 0.66f),
            strokeWidth = 2f,
        )
        drawLine(
            FujiColor.Text,
            Offset(size.width * 0.50f, size.height * 0.66f),
            Offset(size.width * 0.78f, size.height * 0.40f),
            strokeWidth = 2f,
        )
    }
}

@Composable
private fun CategoryGlyph(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier) {
        val p = Path().apply {
            moveTo(size.width * 0.20f, size.height * 0.22f)
            lineTo(size.width * 0.80f, size.height * 0.22f)
            lineTo(size.width * 0.70f, size.height * 0.86f)
            lineTo(size.width * 0.30f, size.height * 0.86f)
            close()
        }
        drawPath(p, color = tint, style = Stroke(width = 1.8f))
        drawLine(
            tint,
            Offset(size.width * 0.24f, size.height * 0.42f),
            Offset(size.width * 0.76f, size.height * 0.42f),
            strokeWidth = 1.8f,
        )
    }
}

@Composable
private fun SignalGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val p = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width * 0.62f, size.height * 0.18f)
            lineTo(size.width * 0.62f, size.height)
            close()
        }
        drawPath(p, color = Color(0xFF8EA6B6))
        drawCircle(
            Color(0xFF8EA6B6),
            radius = size.height * 0.16f,
            center = Offset(size.width * 0.86f, size.height * 0.84f),
        )
    }
}
