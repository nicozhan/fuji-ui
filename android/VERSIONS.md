# Android 独立版本线

本目录（`android/`）是一个**自包含、可直接交付研发**的独立 Gradle 工程，
与仓库根目录的 Web 原型版本线（`v3.x.x`）互不混用。

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
