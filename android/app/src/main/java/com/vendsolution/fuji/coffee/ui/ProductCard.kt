package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.vendsolution.fuji.coffee.data.Product
import com.vendsolution.fuji.coffee.data.PromoTag
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.FujiDimens
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp

/**
 * 商品卡：图 / 名 / 价 / 促销 / 售罄三态，规格逐项复刻 index.html `.product-card`。
 * 本期为纯展示组件，不接任何点击逻辑（售罄态同样不响应点击）。
 */
@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
) {
    val soldOut = product.soldOut
    val shape = RoundedCornerShape(FujiDimens.CARD_RADIUS.sdp())

    BoxWithConstraints(
        modifier = modifier
            .shadow(
                elevation = 9f.sdp(),
                shape = shape,
                clip = false,
                ambientColor = Color(0x0F214965),
                spotColor = Color(0x0F214965),
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    0f to FujiColor.CardTop,
                    0.68f to Color.White,
                    1f to Color.White,
                ),
            )
            .border(FujiDimens.CARD_BORDER.sdp(), FujiColor.CardBorder, shape),
    ) {
        // 1) 商品图区，占卡片高度 68%
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(FujiDimens.CARD_MEDIA_RATIO)
                .align(Alignment.TopCenter),
        ) {
            CupPlaceholder(
                product = product,
                soldOut = soldOut,
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .fillMaxHeight(0.96f)
                    .align(Alignment.BottomCenter),
            )
            product.tag?.let { tag ->
                PromoBadge(
                    tag = tag,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = FujiDimens.TAG_OFFSET.sdp(),
                            y = FujiDimens.TAG_OFFSET.sdp(),
                        ),
                )
            }
        }

        // 2) 售罄白雾（z-index 3），覆盖图片与卡片底色
        if (soldOut) {
            Box(Modifier.fillMaxSize().background(FujiColor.SoldOutVeil))
        }

        // 3) 中央禁止圆牌（z-index 4），圆心位于整卡高度 36% 处
        if (soldOut) {
            SoldOutMark(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = (maxHeight * FujiDimens.SOLD_OUT_MARK_CENTER_Y) -
                                (FujiDimens.SOLD_OUT_MARK_SIZE / 2f).sdp(),
                    ),
            )
        }

        // 4) 信息区（z-index 5），抬到白雾之上
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(FujiDimens.CARD_INFO_RATIO)
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = FujiDimens.INFO_PADDING_H.sdp(),
                    vertical = FujiDimens.INFO_PADDING_V.sdp(),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    color = if (soldOut) FujiColor.SoldOutName else FujiColor.Text,
                    fontSize = FujiDimens.CARD_NAME_SIZE.ssp(),
                    fontWeight = FontWeight(800),
                    letterSpacing = (-0.288f).ssp(),
                    lineHeight = (FujiDimens.CARD_NAME_SIZE * 1.14f).ssp(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(7.sdp()))
                PriceRow(product, soldOut)
            }
            AddOrBanButton(soldOut, Modifier.size(FujiDimens.ADD_BUTTON_SIZE.sdp()))
        }
    }
}

@Composable
private fun PriceRow(product: Product, soldOut: Boolean) {
    val priceColor = if (soldOut) FujiColor.SoldOutPrice else FujiColor.Accent
    val decoration = if (soldOut) TextDecoration.LineThrough else TextDecoration.None
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.sdp()),
    ) {
        Text(
            text = "¥",
            color = priceColor,
            fontSize = FujiDimens.PRICE_CURRENCY_SIZE.ssp(),
            fontWeight = FontWeight(750),
            textDecoration = decoration,
            style = TextStyle(lineHeight = 21.ssp()),
        )
        Text(
            text = product.priceYen.toString(),
            color = priceColor,
            fontSize = FujiDimens.PRICE_AMOUNT_SIZE.ssp(),
            fontWeight = FontWeight(800),
            textDecoration = decoration,
            style = TextStyle(lineHeight = 21.ssp()),
        )
        if (product.showStrikePrice && !soldOut) {
            Text(
                text = "¥${product.priceYen + FujiDimens.STRIKE_DELTA}",
                color = FujiColor.PriceWas,
                fontSize = FujiDimens.PRICE_WAS_SIZE.ssp(),
                fontWeight = FontWeight(650),
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.padding(start = 5.sdp()),
            )
        }
    }
}

@Composable
private fun PromoBadge(tag: PromoTag, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(FujiDimens.TAG_H.sdp())
            .clip(CircleShape)
            .background(Brush.horizontalGradient(listOf(tag.start, tag.end)))
            .padding(horizontal = 11.sdp()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.sdp()),
    ) {
        TagGlyph(tag, Modifier.size(14.sdp()))
        Text(
            text = tag.label,
            color = Color.White,
            fontSize = FujiDimens.TAG_TEXT_SIZE.ssp(),
            fontWeight = FontWeight.W700,
            maxLines = 1,
            softWrap = false,
            style = TextStyle(lineHeight = FujiDimens.TAG_TEXT_SIZE.ssp()),
        )
    }
}

@Composable
private fun TagGlyph(tag: PromoTag, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = center
        val r = size.minDimension / 2f
        val white = Color.White
        when (tag) {
            PromoTag.TOP, PromoTag.BEST -> drawStar(white)
            PromoTag.BOGO -> {
                drawCircle(white, radius = r * 0.82f, center = c, style = Stroke(width = r * 0.30f))
                drawLine(
                    white,
                    Offset(c.x - r * 0.38f, c.y),
                    Offset(c.x + r * 0.38f, c.y),
                    strokeWidth = r * 0.30f,
                    cap = StrokeCap.Round,
                )
            }
            PromoTag.LIMITED -> {
                drawCircle(white, radius = r * 0.84f, center = c, style = Stroke(width = r * 0.26f))
                drawLine(white, Offset(c.x, c.y), Offset(c.x, c.y - r * 0.48f), strokeWidth = r * 0.24f)
                drawLine(white, Offset(c.x, c.y), Offset(c.x + r * 0.40f, c.y), strokeWidth = r * 0.24f)
            }
            PromoTag.NEW -> {
                drawCircle(white, radius = r * 0.84f, center = c, style = Stroke(width = r * 0.24f))
                drawCircle(white, radius = r * 0.34f, center = c)
            }
        }
    }
}

@Composable
private fun SoldOutMark(modifier: Modifier = Modifier) {
    val stroke = 6f.sdp()
    Box(
        modifier
            .size(FujiDimens.SOLD_OUT_MARK_SIZE.sdp())
            .shadow(
                elevation = 13.sdp(),
                shape = CircleShape,
                ambientColor = Color(0x331C3E54),
                spotColor = Color(0x331C3E54),
            )
            .clip(CircleShape)
            .background(Color.White),
    ) {
        Canvas(Modifier.fillMaxSize().padding(20.sdp())) {
            val r = size.minDimension / 2f * 0.88f
            drawCircle(
                color = FujiColor.SoldOutMark,
                radius = r,
                center = center,
                style = Stroke(width = stroke.toPx()),
            )
            val d = r * 0.707f
            drawLine(
                color = FujiColor.SoldOutMark,
                start = Offset(center.x - d, center.y - d),
                end = Offset(center.x + d, center.y + d),
                strokeWidth = stroke.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun AddOrBanButton(soldOut: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (soldOut) FujiColor.SoldOutButtonBorder else FujiColor.Accent
    val backgroundColor = if (soldOut) FujiColor.SoldOutButtonBg else Color.White
    val iconColor = if (soldOut) FujiColor.SoldOutButtonIcon else FujiColor.Accent
    val stroke = 2.4f.sdp()
    Box(
        modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(FujiDimens.ADD_BUTTON_BORDER.sdp(), borderColor, CircleShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val s = size.minDimension
            val c = center
            if (soldOut) {
                drawCircle(
                    color = iconColor,
                    radius = s * 0.27f,
                    center = c,
                    style = Stroke(width = stroke.toPx()),
                )
                val d = s * 0.27f * 0.707f
                drawLine(
                    iconColor,
                    Offset(c.x - d, c.y - d),
                    Offset(c.x + d, c.y + d),
                    strokeWidth = stroke.toPx(),
                    cap = StrokeCap.Round,
                )
            } else {
                val half = s * 0.25f
                drawLine(
                    iconColor,
                    Offset(c.x - half, c.y),
                    Offset(c.x + half, c.y),
                    strokeWidth = stroke.toPx(),
                    cap = StrokeCap.Round,
                )
                drawLine(
                    iconColor,
                    Offset(c.x, c.y - half),
                    Offset(c.x, c.y + half),
                    strokeWidth = stroke.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
