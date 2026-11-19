package com.vendsolution.fuji.coffee.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.data.ProductCatalog
import com.vendsolution.fuji.coffee.design.DesignSpec
import com.vendsolution.fuji.coffee.design.FujiCoffeeTheme
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.FujiDimens
import com.vendsolution.fuji.coffee.design.LocalDesignScale
import com.vendsolution.fuji.coffee.design.sdp

/**
 * 横屏 1920 × 1080 咖啡商品展示页。
 *
 * 布局与 index.html 一致：状态栏 → 品牌导航 →（广告栏 + 商品区）→ 分页，
 * 右下角悬浮购物车胶囊。缩放系数按 min(宽/1920, 高/1080) 计算并注入
 * [LocalDesignScale]，与 Web 的 rem 缩放机制等价。
 */
@Composable
fun ProductGridScreen() {
    var currentPage by remember { mutableIntStateOf(0) }
    BoxWithConstraints(Modifier.fillMaxSize().background(FujiColor.Bg)) {
        val scale = minOf(
            maxWidth.value / DesignSpec.CANVAS_WIDTH,
            maxHeight.value / DesignSpec.CANVAS_HEIGHT,
        )

        CompositionLocalProvider(LocalDesignScale provides scale) {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize()) {
                    StatusBar()
                    MainHeader()

                    BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
                        // 广告栏宽度 = 工作区高度 × 海报比例 848/1855
                        val adWidth = maxHeight *
                                (FujiDimens.AD_POSTER_W.toFloat() / FujiDimens.AD_POSTER_H.toFloat())
                        Row(Modifier.fillMaxSize()) {
                            AdSidebar(Modifier.width(adWidth))
                            ProductStage(
                                Modifier.weight(1f).fillMaxHeight(),
                                page = currentPage,
                                onPageChange = {
                                    currentPage = it.coerceIn(0, ProductCatalog.TOTAL_PAGES - 1)
                                },
                            )
                        }
                    }
                }

                CartPill(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.sdp(), bottom = 20.sdp())
                        .clickable { AppState.cartOpen = true },
                )
            }

            if (AppState.cartOpen) CartDrawer()
            if (AppState.customizeProductId != null) CustomizationDrawer()
        }
    }
}

@Composable
private fun ProductStage(
    modifier: Modifier = Modifier,
    page: Int = 0,
    onPageChange: (Int) -> Unit = {},
) {
    val pageCount = ProductCatalog.TOTAL_PAGES
    val items = ProductCatalog.ALL
        .drop(page * FujiDimens.PAGE_SIZE)
        .take(FujiDimens.PAGE_SIZE)
    val swipeThreshold = 96f * LocalDesignScale.current * LocalDensity.current.density

    Column(
        modifier.padding(
            top = FujiDimens.STAGE_PADDING_TOP.sdp(),
            start = FujiDimens.STAGE_PADDING_H.sdp(),
            end = FujiDimens.STAGE_PADDING_H.sdp(),
        ),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(page, pageCount) {
                    var dragOffset = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = { dragOffset = 0f },
                        onDragCancel = { dragOffset = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffset += dragAmount
                            // 触屏向右滑 → 下一页；向左滑 → 上一页
                            if (dragOffset >= swipeThreshold && page < pageCount - 1) {
                                onPageChange(page + 1)
                                dragOffset = 0f
                            } else if (dragOffset <= -swipeThreshold && page > 0) {
                                onPageChange(page - 1)
                                dragOffset = 0f
                            }
                        },
                    )
                },
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(FujiDimens.GRID_GAP.sdp()),
            ) {
                for (row in 0 until 2) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(FujiDimens.GRID_GAP.sdp()),
                    ) {
                        for (col in 0 until 3) {
                            val item = items.getOrNull(row * 3 + col)
                            if (item != null) {
                                ProductCard(
                                    item,
                                    Modifier.weight(1f).fillMaxHeight(),
                                    onAdd = { AppState.addToCart(item) },
                                    onCustomize = { AppState.customizeProductId = item.id },
                                )
                            } else {
                                Spacer(Modifier.weight(1f).fillMaxHeight())
                            }
                        }
                    }
                }
            }
            SlideArrow(
                isNext = false,
                enabled = page > 0,
                onClick = { onPageChange(page - 1) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.sdp()),
            )
            SlideArrow(
                isNext = true,
                enabled = page < pageCount - 1,
                onClick = { onPageChange(page + 1) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.sdp()),
            )
        }
        PaginationBar(
            page = page,
            shownCount = items.size,
            onPageChange = onPageChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(widthDp = 1920, heightDp = 1080, showBackground = true)
@Composable
private fun ProductGridScreenPreview() {
    FujiCoffeeTheme {
        ProductGridScreen()
    }
}
