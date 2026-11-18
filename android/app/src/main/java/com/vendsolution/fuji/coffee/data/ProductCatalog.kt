package com.vendsolution.fuji.coffee.data

/**
 * 咖啡商品目录：中文名 / 价格 / 标签与 index.html 的 zh 字典、`products` 数组一致。
 * 本期页面只渲染第一页 [FIRST_PAGE]（3×2 = 6 件），其余数据留给后续分页迁移。
 *
 * 调试入口：
 * - 修改 [DEMO_SOLD_OUT_IDS] 可切换售罄商品；
 * - 修改 [FIRST_PAGE] 的取数即可调整本页展示件数；
 * - 拿到真实商品图后，给对应 Product 填 `imageRes = R.drawable.xxx`。
 */
object ProductCatalog {

    /** 与 Web demo 一致的售罄演示数据。 */
    val DEMO_SOLD_OUT_IDS: Set<Int> = setOf(2, 6)

    private val CUP_CYCLE = listOf(
        CupStyle.LATTE,
        CupStyle.AMERICANO,
        CupStyle.CARAMEL,
        CupStyle.MOCHA,
        CupStyle.ESPRESSO,
        CupStyle.MATCHA,
    )

    private val RAW = listOf(
        Spec(1, "冰美式", 320, Temperature.ICED, PromoTag.TOP),
        Spec(2, "焦糖玛奇朵", 380, Temperature.ICED),
        Spec(3, "冰拿铁", 350, Temperature.ICED, PromoTag.BOGO),
        Spec(4, "冰摩卡", 400, Temperature.ICED),
        Spec(5, "意式浓缩", 280, Temperature.HOT, PromoTag.BEST),
        Spec(6, "冰澳白", 360, Temperature.ICED),
        Spec(7, "摩卡星冰乐", 420, Temperature.ICED, PromoTag.LIMITED),
        Spec(8, "经典拿铁", 340, Temperature.HOT),
        Spec(9, "馥芮白", 360, Temperature.HOT),
        Spec(10, "冷萃美式", 300, Temperature.ICED),
        Spec(11, "雪顶摩卡", 400, Temperature.ICED),
        Spec(12, "热拿铁", 340, Temperature.HOT, PromoTag.BEST),
        Spec(13, "焦糖拿铁", 380, Temperature.HOT, PromoTag.TOP),
        Spec(14, "双份冰美式", 320, Temperature.ICED),
        Spec(15, "海盐焦糖拿铁", 390, Temperature.ICED),
        Spec(16, "香草冰拿铁", 370, Temperature.ICED, PromoTag.NEW),
        Spec(17, "柠檬冷萃", 330, Temperature.ICED),
        Spec(18, "节日雪顶摩卡", 420, Temperature.HOT, PromoTag.BOGO),
        Spec(19, "焦糖布蕾拿铁", 380, Temperature.HOT),
        Spec(20, "抹茶椰香冰沙", 430, Temperature.ICED, PromoTag.LIMITED),
        Spec(21, "椰香冰咖啡", 390, Temperature.ICED),
        Spec(22, "香草拿铁", 350, Temperature.HOT, PromoTag.BEST),
        Spec(23, "洛神花冷萃", 360, Temperature.ICED),
        Spec(24, "香橙抹茶", 420, Temperature.ICED, PromoTag.TOP),
        Spec(25, "双层焦糖玛奇朵", 390, Temperature.ICED),
        Spec(26, "芒果冰沙", 410, Temperature.ICED),
        Spec(27, "奶盖冰摩卡", 400, Temperature.ICED, PromoTag.LIMITED),
    )

    val ALL: List<Product> = RAW.map { spec ->
        Product(
            id = spec.id,
            name = spec.name,
            priceYen = spec.price,
            temperature = spec.temperature,
            tag = spec.tag,
            soldOut = spec.id in DEMO_SOLD_OUT_IDS,
            cup = CUP_CYCLE[(spec.id - 1) % CUP_CYCLE.size],
        )
    }

    /** 本期唯一渲染的一页：3 列 × 2 行。 */
    val FIRST_PAGE: List<Product> = ALL.take(com.vendsolution.fuji.coffee.design.FujiDimens.PAGE_SIZE)

    val TOTAL_PAGES: Int = (ALL.size + com.vendsolution.fuji.coffee.design.FujiDimens.PAGE_SIZE - 1) /
            com.vendsolution.fuji.coffee.design.FujiDimens.PAGE_SIZE

    private data class Spec(
        val id: Int,
        val name: String,
        val price: Int,
        val temperature: Temperature,
        val tag: PromoTag? = null,
    )
}
