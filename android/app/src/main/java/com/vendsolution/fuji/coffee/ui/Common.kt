package com.vendsolution.fuji.coffee.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.vendsolution.fuji.coffee.data.AppState
import com.vendsolution.fuji.coffee.design.DesignSpec
import com.vendsolution.fuji.coffee.design.FujiColor
import com.vendsolution.fuji.coffee.design.LocalDesignScale
import com.vendsolution.fuji.coffee.design.sdp
import com.vendsolution.fuji.coffee.design.ssp
import java.util.Hashtable

/** 统一设计画布：所有页面共用 1920×1080 缩放，复刻 Web 的 rem 机制。 */
@Composable
fun DesignFrame(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().background(FujiColor.Bg)) {
        val scale = minOf(
            maxWidth.value / DesignSpec.CANVAS_WIDTH,
            maxHeight.value / DesignSpec.CANVAS_HEIGHT,
        )
        CompositionLocalProvider(LocalDesignScale provides scale) {
            Box(Modifier.fillMaxSize()) { content() }
        }
    }
}

/** 通用胶囊按钮，对应 Web 的 border-radius:62.4375rem。 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    height: Int = 56,
    fontSize: Int = 17,
    background: Color = if (filled) FujiColor.Accent else Color.White,
    foreground: Color = if (filled) Color.White else FujiColor.Text,
    borderColor: Color? = if (filled) Color.Transparent else FujiColor.Border,
    enabled: Boolean = true,
) {
    val borderModifier = if (borderColor == null) modifier else modifier.border(1.sdp(), borderColor, CircleShape)
    Box(
        borderModifier
            .height(height.sdp())
            .clip(CircleShape)
            .background(if (enabled) background else background.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (enabled) foreground else foreground.copy(alpha = 0.6f),
            fontSize = fontSize.ssp(),
            fontWeight = FontWeight(700),
            textAlign = TextAlign.Center,
        )
    }
}

/** "FE" 品牌标，点击打开调试浮窗（与 Web 的 debug-trigger 一致）。 */
@Composable
fun BrandMark(modifier: Modifier = Modifier, size: Int = 31) {
    Text(
        "FE",
        modifier = modifier.clickable { AppState.debugOpen = !AppState.debugOpen },
        color = FujiColor.Accent,
        fontSize = size.ssp(),
        fontWeight = FontWeight.W900,
        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
    )
}

/** 用 ZXing 生成真实二维码；512px 位图保证缩放后仍清晰。 */
@Composable
fun QrCode(text: String, modifier: Modifier = Modifier) {
    val bitmap = remember(text) {
        val size = 512
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.MARGIN] = 1
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    setPixel(
                        x, y,
                        if (matrix[x, y]) android.graphics.Color.rgb(16, 45, 67)
                        else android.graphics.Color.WHITE,
                    )
                }
            }
        }.asImageBitmap()
    }
    Image(
        bitmap = bitmap,
        contentDescription = "支付二维码",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/** 半透明遮罩，点击外围关闭。 */
@Composable
fun Scrim(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0x700C2030))
            .clickable(onClick = onDismiss),
    )
}

/** 帮助信息里的键值行。 */
@Composable
fun InfoRow(label: String, value: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(vertical = 2.sdp()),
    ) {
        Text(label, color = FujiColor.TextSecondary, fontSize = 13.ssp(), fontWeight = FontWeight(650))
        Text(
            value,
            modifier = Modifier.padding(start = 92.sdp()),
            color = FujiColor.Text,
            fontSize = 17.ssp(),
            fontWeight = FontWeight(650),
        )
    }
}
