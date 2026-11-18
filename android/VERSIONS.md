# Android 独立版本线

本目录（`android/`）是一个**自包含、可直接交付研发**的独立 Gradle 工程，
与仓库根目录的 Web 原型版本线（`v3.x.x`）互不混用。

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
