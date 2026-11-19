# Android 独立版本线

本目录（`android/`）是一个**自包含、可直接交付研发**的独立 Gradle 工程，
与仓库根目录的 Web 原型版本线（`v3.x.x`）互不混用。

## android-v0.10.0

本版三项改动，按用户确认执行：

- **变更一｜logo 浮层支付方式快速切换**：任何页面点左上角 FE 品牌标弹出的浮窗，顶部新增醒目的「一种支付 / 两种支付 / 三种支付」大按钮（各带副标题「仅扫码 / 扫码+POS / 扫码+POS+现金」、当前高亮）。点选立即改 `AppState.paymentMethodCount` 并自动关闭浮窗；移除了原来埋在浮窗底部的三个小数字按钮。
- **变更二｜手机支付改为现金支付**：`PaymentScreen.kt` 第三种支付方式由「手机支付 / 扫码唤起手机付款 / 开始支付」改为「现金支付 / 请到收银台付款 / 使用现金支付」；枚举 `PaymentGlyph.MOBILE`→`CASH`，图标改为 `CashPaymentGlyph`（Canvas 绘制钞票外框 + 中心圆 + ¥ + 两端印章圆）；`onClick` 为空实现（示意，不接真实流程）。
- **变更三｜支付成功在二维码页原地切换**：去掉原全屏白底 `ResultOverlay`；`QrPaymentScreen.kt` 右侧白框内用 `Crossfade` + `when(panel)` 切换二维码面板 / 成功面板 / 失败面板，对应 Web `qr-payment.html` 的 `payment-state-panel` 同容器切换。页面框架（状态栏、Fuji Electric、四步进度条、应付金额、左框支付方式图）全程保留；paid 时进度条「开始制作」高亮打勾（对应 Web `updateProgress`）；1.4s 后自动跳制作页。新增调试入口：`adb shell am start ... --es screen QR_PAYMENT --es payState paid --es freezePaid 1` 可直达并冻结成功态便于截图（`freezePaid` 仅调试用，`clearSession` 会重置）。

验证截图：`preview/v0.10.0/`（logo 支付切换、现金支付三卡、支付成功原地、支付失败原地）。

## android-v0.9.0

本版对照 Web 原型（index / payment / qr-payment / preparation.html）集中修订 6 项：

- **变更一｜帮助按钮问号图标**：`StaticChrome.kt` 的 `HelpGlyph` 由「圆圈+点」改为 Web 的问号（圆圈 + 问号曲线，Canvas Path 绘制）；`PaymentScreen.kt` 帮助按钮也补上同一问号图标（新增 `HelpButton`）。
- **变更二｜分页与页码对齐**：`PaginationBar` 由 `align-items: bottom` 改为 `CenterVertically`（对应 Web `.pagination` 的 `align-items:center`），右侧页数文字与页码按钮中线对齐。
- **变更三｜左右滑动翻页**：`ProductGridScreen.kt` 重写商品区手势——因商品卡 clickable 会消费 down，`detectHorizontalDragGestures` 失效；改用 `awaitPointerEventScope` 在 `PointerEventPass.Final` 读取原始位移，`pointerInput(Unit)` 配合 `rememberUpdatedState` 避免翻页时协程重启；左滑下一页 / 右滑上一页，一次手势只翻一页，纯点击不受影响。
- **变更四｜支付方式 1/2/3 选项**：`AppState.paymentMethodCount`（1/2/3）驱动；`PaymentScreen` 按数量渲染 1/2/3 张卡（扫码 / POS / 手机），卡片区最大宽度 680/1400/1728 设计像素（对应 Web `.payment-grid.count-N`），手机支付示意图用 Canvas 绘制；调试浮窗加「支付方式数量 1/2/3」切换；支付状态控件由三态扩为四态（请扫码/多码/成功/失败）。注：Web 的 count=1 为内联二维码，Android 仍显示单张扫码卡，属简化差异。
- **变更五｜支付成功/失败页**：`QrPaymentScreen.kt` 的 `ResultOverlay` 按 Web `.result-panel` 重写——成功为淡绿圆底+绿对勾图标+「支付成功」+「正在为您制作饮品」+三个跳动圆点；失败为淡红圆底+红叉+「支付失败」+「支付未成功，请重试」+「重新支付/返回首页」按钮。
- **变更六｜制作/取走杯**：`PreparationScreen.kt` 文案对齐 Web（待取走/已取走/等待制作、取走提示「请拿走您的饮品」、末杯「取走后本次订单结束，返回首页」）；页面上的 4 个调试按钮移入调试浮窗（开始制作/模拟取走/跳到末杯，经 `AppState.prepDemoAction` 触发）。
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器逐项实跑——帮助问号、分页对齐、左右滑动（1↔2↔3 一次一页）、1/2/3 支付卡切换、成功页、失败页、制作页末杯取走，均截图确认，无 ANR；截图见 `preview/v0.9.0/`。
- **版本**：`versionCode=9`，`versionName=0.9.0`；回滚 `git checkout android-v0.8.0`。

## android-v0.8.0

- **变更｜购物车抽屉顶部与商品区齐平**：修正 `ProductDrawers.kt` 的 `CartDrawer` 高度定位。Web 中 `.cart-panel` 的定位祖先是 `.workspace`（商品区，`position:relative`），顶部与语言/帮助栏下沿齐平；此前 Android 用 `fillMaxSize()` 覆盖全屏，导致购物车标题栏从屏幕最顶端开始、压住状态栏与语言帮助栏。现遮罩（`Scrim`）与面板均加 `padding(top = STATUS_BAR_H + HEADER_H = 160 设计像素)`，仅覆盖商品区，顶部 chrome 完整露出。
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器加购 3 件后截图确认——状态栏与语言帮助栏完整露出、购物车标题栏顶部正好起于商品区顶部，条目/千分位合计/去结算箭头正常，无 ANR；截图见 `preview/v0.8.0/`。
- **版本**：`versionCode=8`，`versionName=0.8.0`；回滚 `git checkout android-v0.7.0`。

## android-v0.7.0

- **变更｜购物车抽屉按线上参考页重写**：严格对照 `https://fuji-ui.up.railway.app/` 与 `index.html` 的 `.cart-*` CSS 与用户截图，重写 `ProductDrawers.kt` 的 `CartDrawer` / `CartItemRow`。
  - **标题栏**：通栏蓝底高 100、padding 38；新增购物车线性图标 + 文案「购物车」+ 白底数量徽章（蓝字）；右侧关闭按钮由文字 ✕ 改为 Canvas 绘制的白色叉号（新增 `CloseGlyph` 带 tint 重载）。
  - **条目**：thumb 图 150、商品名 22、配置小字（如「冷饮 / 标准杯」，由 CartItem.options 映射）、价格 26；最右侧新增浅灰圆角垃圾桶删除按钮（Canvas 图标），整条圆角白底。
  - **底部**：合计行文案改为「合计（N 件）」、价格带千分位格式化（¥1,070）、去结算按钮加「→」箭头。
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器加购 3 件后打开抽屉截图确认——标题栏图标/徽章/关闭图标、3 条商品的配置小字与垃圾桶按钮、底部「合计（3 件）」与千分位「¥1,070」、去结算箭头均正确显示，无重叠裁切、无 ANR；截图见 `preview/v0.7.0/`。
- **稳定性**：模拟器 AVD `Jinuo_API36` 内存由 2048 提升至 3072MB，并禁用导致反复 ANR 的 `com.google.android.apps.wellbeing`，验证期间运行稳定。
- **版本**：`versionCode=7`，`versionName=0.7.0`；回滚 `git checkout android-v0.6.0`。

## android-v0.6.0

- **变更｜定制饮品弹窗按线上参考页重构**：严格对照 `https://fuji-ui.up.railway.app/` 与 `index.html` 的 `.customization-*` CSS 重写 `ProductDrawers.kt` 定制弹窗内部布局。弹窗外框仍为 1240×820、底部上滑、左右留白（v0.5.0 成果保留）；内部改为三段式：
  - **头**：高 92 + 下分隔线；左侧圆形向下收起按钮（blue chevron，left 44）、居中标题「定制饮品」、右侧圆形关闭按钮（right 44），按钮均 64 圆、白底 1px 描边。
  - **身**：padding 26/64/28，左列 380 商品图 + 名称 + 现价与划线原价（现价 +20，仅展示不结算）；右列四组选项改为「标签在上、胶囊按钮横排在下一行」，组间 1px 分隔线，按钮按 Web 文案：热饮/冷饮、小杯/中杯/大杯、无糖/微糖/半糖/少糖/标准糖、牛奶/低脂奶/燕麦奶/豆奶。
  - **尾**：高 116 + 上分隔线；数量（圆形 −/+ 按钮 + 数值）、合计标签 + 现价 + 划线原价、宽 350 的「加入订单 →」胶囊按钮。
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器打开定制弹窗截图确认，三段式布局、分隔线、文案与划线价均与参考截图一致，无重叠裁切、无 ANR；截图见 `preview/v0.6.0/`。
- **版本**：`versionCode=6`，`versionName=0.6.0`；回滚 `git checkout android-v0.5.0`。

## android-v0.5.0

- **变更一｜欢迎页左对齐 + 国旗**：`WelcomeScreen.kt` 的语言按钮行由底部居中改为与「开始选购」同为 left 10.5%（start=202 设计像素）左对齐；7 个语言按钮改为纵向布局，国旗 emoji 在上、语言名在下（对应 `welcome.html` `.welcome-tools` / `.language-button`）。
- **变更二｜定制抽屉底部上滑**：`ProductDrawers.kt` 的定制面板改为从屏幕下端向上滑入（offset 900→0，0.34s 缓动）、遮罩淡入至 rgba(9,28,42,.44)；内部按 Web 三段式（头 / 身 / 尾）重新梳理，左右留白 64、选项区 SpaceEvenly，背景商品页仍可见不铺满。
- **变更三｜购物车右侧滑入 + 布局优化**：购物车面板从右侧滑入（offset 720→0，0.28s）；宽度 620→668 设计像素；修正标题栏因外层 padding 导致的左右白边问题——标题栏改为通栏（蓝底铺满面板宽），数量徽章与关闭按钮就位，条目改为图 / 名称+价格+数量 / 移除三段式。
- **变更四｜扫码支付左右等大**：`QrPaymentScreen.kt` 左右两框由 1.06:1 改为 1:1 等宽等高，整组最大宽度 1500 设计像素并水平居中，与支付方式页风格一致。
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器实跑：欢迎页左对齐与国旗、定制抽屉底部上滑留白、购物车右滑通栏标题栏、扫码页左右等大对称，均截图确认；截图见 `preview/v0.5-*.png`。
- **版本**：`versionCode=5`，`versionName=0.5.0`；回滚 `git checkout android-v0.4.0`。

## android-v0.4.0

- **变更一｜移除临时按钮**：状态栏中的「首页」与「DEMO ON」临时控件已从所有页面移除，保留机器 ID、联系电话、日期与信号，视觉与 HTML 稿一致。
- **变更二｜商品页翻页**：按 `index.html` `.slide-arrow` 补上左右圆形翻页条；触屏向右滑切下一页、向左滑切上一页；分页数字与箭头改为可点击并可禁用，页内商品按当前页从 `ProductCatalog.ALL` 取 6 件；分页行下沿内边距调整到 6 设计像素，消除多余空白。
- **变更三｜支付页等宽**：`payment.html` 两张支付方式卡改为 `1fr : 1fr` 等宽等高，整组最大宽度 1400 设计像素并水平居中（对应 HTML `.payment-grid.count-2`）；卡片内边距改为 28/36/32，与 HTML 一致。
- **变更四｜严格对照 HTML**：扫码支付主面板移除「模拟支付成功 / 模拟失败」按钮，相关状态切换移入隐藏调试浮窗（点左上角 FE 打开），主界面不再出现 demo 专用控件。
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器实测右箭头点击 1→2 页、右滑 2→3 页，支付页两卡等宽居中、扫码页主面板干净；截图见 `preview/v0.4.0/`。
- **版本**：`versionCode=4`，`versionName=0.4.0`；回滚 `git checkout android-v0.3.0`。

## android-v0.3.0

- **范围**：完成全部 7 个横屏咖啡页面（1920×1080 设计画布，10 寸横屏设备），单 Activity + Compose 页面路由
- **页面**：`WELCOME` 欢迎页（3 张海报 5 秒轮播 + 7 语言）、`PRODUCTS` 商品页（真实图 + 加购/定制抽屉/购物车抽屉/售罄）、`PAYMENT` 支付方式（订单摘要 + 帮助弹窗 + 返回确认）、`QR_PAYMENT` 扫码支付（三渠道 + 真实二维码 + 90 秒倒计时 + 结果层）、`PREPARATION` 制作中（每杯 5 秒、做好即停、取走下一杯、末杯回首页）、`OUT_OF_SERVICE` 暂停服务（3 秒自检后停机 + 恢复入口）、`SETUP` 设备设置（s10/m21/l27 档位 + 自定义宽高）
- **架构**：`data/AppState.kt` 集中管理订单、购物车、语言、设备档位、支付状态；`ui/AppRoot.kt` 统一路由；`ui/DesignFrame.kt` 统一 1920×1080 缩放。后续可平滑替换为 Repository + ViewModel
- **调试**：任意页面点左上角 `FE` 打开调试浮窗，可直接跳转 7 页；也支持 `adb shell am start -n com.vendsolution.fuji.coffee/.MainActivity -e screen PAYMENT`
- **新增素材**：`welcome_slide_1..3.jpg`、`payment_methods.jpg`、`payment_methods_square.jpg`，全部来自用户指定目录；二维码用 ZXing 3.5.3 实时生成，非图片素材
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器逐页截图 `preview/v0.3.0/`、总览 `preview/android-v0.3.0-all-pages.png`；加购 → 购物车抽屉交互实测通过
- **版本**：`versionCode=3`，`versionName=0.3.0`；回滚 `git checkout android-v0.2.0`

## android-v0.2.0

- **变更**：接入用户提供的真实素材，移除首版占位图
- **素材来源**：`/Users/qian2/Downloads/coffee/assets`（用户指定）；同时把该目录文件恢复进 Web 原型的 `assets/`，工作区已无未提交删除
- **商品图**：27 件商品按 id 映射到 `res/drawable-nodpi/p01…p27`，新增 `data/ProductImages.kt` 作为换图入口；首屏 6 件分别为美式 / 焦糖玛奇朵 / 冰拿铁 / 冰摩卡 / 意式浓缩 / 冰澳白
- **广告位**：`AdSidebar` 改用 `ad-poster-1.jpg`（848×1855，与 Web 同一张海报），删除原 Canvas 咖啡杯剪影与文案占位
- **售罄态**：真实图同时做 `ColorMatrix.setToSaturation(0)` 灰度处理，白雾与禁止圆牌规格不变
- **兜底**：`Product.imageRes == null` 时仍渲染 `CupPlaceholder`，便于单件商品缺图时调试
- **验证**：`./gradlew assembleDebug` 通过；1920×1080 模拟器实跑截图见 `preview/android-v0.2.0.png`，无系统栏、无裁切、六卡三态正常
- **回滚**：`git checkout android-v0.2.0` 回到本版；`git checkout android-v0.1.0` 回到占位图首版

## android-v0.1.0

- **范围**：横屏 1920 × 1080 咖啡商品展示页，仅商品网格卡片（图 / 名 / 价 / 促销 / 售罄）
- **方向**：锁定横屏（`screenOrientation="landscape"`），全屏沉浸式隐藏状态栏与导航栏
- **语言**：仅中文，不做多语言切换
- **素材**：不新增任何图片 / 字体文件；商品图与广告位均为 Compose Canvas 绘制的占位，
  拿到真实素材后替换 `Product.imageRes` 与 `AdSidebar` 内容
- **交互**：本期无交互。状态栏 / 品牌栏 / 分类 / 分页 / 购物车胶囊均为静态视觉占位；
  唯一有数据驱动与三态逻辑的组件是 `ProductCard`
- **数据**：`ProductCatalog.ALL` 为 27 件中文商品（与 Web 版一致），页面只渲染第一页 6 件；
  售罄演示 id 为 2、6
- **技术栈**：AGP 8.7.3 / Kotlin 2.0.21 / Jetpack Compose (BOM 2024.12.01) /
  Gradle 8.9 / minSdk 26 / targetSdk 36 / compileSdk 36

### 运行方式

1. Android Studio 打开本 `android/` 目录（不是仓库根目录），等待 Gradle Sync。
2. 连接设备或启动横屏模拟器，点击 **Run**；也可命令行执行 `./gradlew assembleDebug`。
3. 首次 Sync 需联网拉取 AGP / Compose 依赖；`local.properties` 为本机 SDK 路径，
   已加入 `.gitignore`，研发侧由 Android Studio 自动生成，无需手工填写。

### 源码结构

| 路径 | 职责 |
| --- | --- |
| `design/DesignScale.kt` | 1920×1080 设计画布缩放（sdp / ssp），对应 Web 的 rem 机制 |
| `design/FujiTheme.kt` | 颜色与尺寸常量，逐项对应 `index.html :root` 与 CSS |
| `data/Product.kt` | 商品模型、促销标签、占位杯型配色 |
| `data/ProductCatalog.kt` | 27 件中文商品数据（调试入口） |
| `ui/CupPlaceholder.kt` | 纯 Canvas 商品图占位，售罄灰度 |
| `ui/ProductCard.kt` | 商品卡：正常 / 促销 / 售罄三态 |
| `ui/StaticChrome.kt` | 页面外围静态框架（待后续迁移交互） |
| `ui/ProductGridScreen.kt` | 页面组装与 3×2 网格 |
| `MainActivity.kt` | 入口、横屏锁定配合、沉浸式系统栏 |

### 回滚

`git checkout android-v0.1.0` 可回到本版本；Web 原型请使用根目录下的 `./rollback.sh`。
