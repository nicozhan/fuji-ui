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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
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
    // 手势协程用 Unit 作 key，不随翻页重启；通过 rememberUpdatedState 读取最新 page/callback
    val currentPage by rememberUpdatedState(page)
    val currentOnPageChange by rememberUpdatedState(onPageChange)
    val pageCountState by rememberUpdatedState(pageCount)

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
                .pointerInput(Unit) {
                    // 用最终阶段监听原始触摸位移：即使子卡片 clickable 消费了 down，
                    // Final 阶段仍可读取到坐标；只在水平位移超过阈值时翻页，纯点击不受影响。
                    awaitPointerEventScope {
                        var startX = -1f
                        var tracking = false
                        var consumed = false // 一次手势只翻一页
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Final)
                            val change = event.changes.firstOrNull() ?: continue
                            if (change.pressed && !tracking) {
                                startX = change.position.x
                                tracking = true
                                consumed = false
                            }
                            if (tracking && change.pressed && !consumed) {
                                val dx = change.position.x - startX
                                if (kotlin.math.abs(dx) >= swipeThreshold) {
                                    // 向左拖 → 下一页；向右拖 → 上一页
                                    if (dx < 0 && currentPage < pageCountState - 1) currentOnPageChange(currentPage + 1)
                                    else if (dx > 0 && currentPage > 0) currentOnPageChange(currentPage - 1)
                                    consumed = true
                                }
                            }
                            if (change.changedToUp()) {
                                tracking = false
                                consumed = false
                            }
                        }
                    }
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
