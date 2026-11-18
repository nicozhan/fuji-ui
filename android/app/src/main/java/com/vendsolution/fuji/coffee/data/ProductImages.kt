package com.vendsolution.fuji.coffee.data

import com.vendsolution.fuji.coffee.R

/**
 * 商品图资源映射。素材全部来自用户提供的 /Downloads/coffee/assets 目录，
 * 按商品 id 顺序文件名改为 p01…p27（前缀 p 满足 Android 资源命名规则）。
 *
 * 这是「真实图接入点」：替换某件商品的图片时，只需替换 res/drawable-nodpi/pXX.ext，
 * 或在此处修改对应 id 的资源引用，无需改动卡片与页面代码。
 */
object ProductImages {

    private val BY_ID = mapOf(
        1 to R.drawable.p01,
        2 to R.drawable.p02,
        3 to R.drawable.p03,
        4 to R.drawable.p04,
        5 to R.drawable.p05,
        6 to R.drawable.p06,
        7 to R.drawable.p07,
        8 to R.drawable.p08,
        9 to R.drawable.p09,
        10 to R.drawable.p10,
        11 to R.drawable.p11,
        12 to R.drawable.p12,
        13 to R.drawable.p13,
        14 to R.drawable.p14,
        15 to R.drawable.p15,
        16 to R.drawable.p16,
        17 to R.drawable.p17,
        18 to R.drawable.p18,
        19 to R.drawable.p19,
        20 to R.drawable.p20,
        21 to R.drawable.p21,
        22 to R.drawable.p22,
        23 to R.drawable.p23,
        24 to R.drawable.p24,
        25 to R.drawable.p25,
        26 to R.drawable.p26,
        27 to R.drawable.p27,
    )

    fun resFor(productId: Int): Int? = BY_ID[productId]
}
