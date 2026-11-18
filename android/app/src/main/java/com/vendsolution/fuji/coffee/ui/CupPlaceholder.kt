package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import com.vendsolution.fuji.coffee.data.CupStyle
import com.vendsolution.fuji.coffee.data.Product
import kotlin.math.cos
import kotlin.math.sin

/**
 * 商品图占位：纯 Canvas 绘制的透明底饮品杯，**不引入任何图片素材**。
 *
 * 拿到真实商品图后，在 ProductCard 中改为按 `product.imageRes` 渲染 Image 即可，
 * 本组件保留作为无图时的兜底。售罄时按 Web 稿做灰度处理（filter: grayscale(1)）。
 */
@Composable
fun CupPlaceholder(
    product: Product,
    soldOut: Boolean,
    modifier: Modifier = Modifier,
) {
    val style = product.cup
    Canvas(modifier = modifier) {
        drawCup(style, soldOut, size.width, size.height)
    }
}

private fun Color.desaturated(): Color {
    val luma = 0.299f * red + 0.587f * green + 0.114f * blue
    return Color(luma, luma, luma, alpha)
}

private fun DrawScope.drawCup(style: CupStyle, soldOut: Boolean, w: Float, h: Float) {
    val tone: (Color) -> Color = { if (soldOut) it.desaturated() else it }
    val cx = w / 2f
    val topY = h * 0.18f
    val bottomY = h * 0.92f
    val topHalf = w * 0.27f
    val bottomHalf = w * 0.215f
    val cupH = bottomY - topY

    // 杯底投影，对应 CSS drop-shadow(0 12px 18px rgba(24,58,84,.13))
    drawOval(
        color = Color(0x21183A54),
        topLeft = Offset(cx - w * 0.25f, bottomY - h * 0.005f),
        size = Size(w * 0.50f, h * 0.045f),
    )

    val cupPath = Path().apply {
        moveTo(cx - topHalf, topY)
        lineTo(cx + topHalf, topY)
        lineTo(cx + bottomHalf, bottomY)
        lineTo(cx - bottomHalf, bottomY)
        close()
    }

    // 分层液体
    clipPath(cupPath) {
        drawRect(
            color = tone(style.middle),
            topLeft = Offset(0f, topY),
            size = Size(w, cupH),
        )
        drawRect(
            color = tone(style.bottom),
            topLeft = Offset(0f, topY + cupH * 0.58f),
            size = Size(w, cupH * 0.42f),
        )
        drawRect(
            brush = Brush.verticalGradient(
                listOf(tone(style.top), tone(style.middle)),
            ),
            topLeft = Offset(0f, topY),
            size = Size(w, cupH * 0.26f),
        )
        if (style.foam) {
            drawOval(
                color = Color(0x55FFFFFF),
                topLeft = Offset(cx - topHalf, topY - h * 0.012f),
                size = Size(topHalf * 2f, h * 0.055f),
            )
        }
        // 左侧高光，模拟透明杯壁
        drawRect(
            brush = Brush.horizontalGradient(listOf(Color(0x33FFFFFF), Color.Transparent)),
            topLeft = Offset(cx - topHalf, topY),
            size = Size(w * 0.10f, cupH),
        )
    }

    // 杯壁描边
    drawPath(cupPath, color = Color(0x24102D43), style = Stroke(width = 1.6f))

    // 杯身品牌环（纯图形，不含文字素材）
    val markCenter = Offset(cx, topY + cupH * 0.62f)
    drawCircle(
        color = Color(0x332F4A5C),
        radius = w * 0.052f,
        center = markCenter,
        style = Stroke(width = 2f),
    )
    drawCircle(color = Color(0x332F4A5C), radius = w * 0.012f, center = markCenter)

    // 杯盖
    val lidHalf = topHalf + w * 0.025f
    drawRoundRect(
        color = tone(style.lid),
        topLeft = Offset(cx - lidHalf, topY - h * 0.035f),
        size = Size(lidHalf * 2f, h * 0.052f),
        cornerRadius = CornerRadius(h * 0.016f, h * 0.016f),
    )
    drawRoundRect(
        color = Color(0x1A102D43),
        topLeft = Offset(cx - lidHalf, topY + h * 0.010f),
        size = Size(lidHalf * 2f, h * 0.006f),
        cornerRadius = CornerRadius(h * 0.003f, h * 0.003f),
    )

    // 吸管
    withTransform({
        rotate(-9f, pivot = Offset(cx + w * 0.09f, topY))
    }) {
        drawRoundRect(
            color = tone(Color(0xFF0877C9)),
            topLeft = Offset(cx + w * 0.078f, topY - h * 0.185f),
            size = Size(w * 0.024f, h * 0.19f),
            cornerRadius = CornerRadius(w * 0.012f, w * 0.012f),
        )
    }
}

/** 五角星路径，用于「人气 No.1 / 冬日畅销」标签图标。 */
internal fun DrawScope.drawStar(color: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = size.minDimension / 2f
    val star = Path().apply {
        for (i in 0..9) {
            val angle = Math.toRadians((-90.0 + i * 36.0))
            val r = if (i % 2 == 0) radius else radius * 0.44f
            val x = cx + (cos(angle) * r).toFloat()
            val y = cy + (sin(angle) * r).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(star, color)
}
