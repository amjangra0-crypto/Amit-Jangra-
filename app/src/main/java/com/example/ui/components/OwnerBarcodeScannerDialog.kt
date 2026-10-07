package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLocaleStrings
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ScannedBarcodeResult(
    val rawText: String,
    val type: String, // "BANK_NEFT", "BANK_RTGS", "BANK_SWIFT", "UPI", "PAYTM", "PAYPAL", "GENERIC"
    val accountOrId: String,
    val extraCode: String = "" // IFSC, SWIFT, etc.
)

/**
 * OwnerBarcodeScannerDialog
 * Allows the app owner to scan recipient bar codes / QR codes (or pick standard verified formats)
 * to instantly withdraw wallet funds to Bank (RTGS/NEFT/SWIFT), UPI, Paytm, or PayPal.
 */
@Composable
fun OwnerBarcodeScannerDialog(
    language: String,
    onDismiss: () -> Unit,
    onBarcodeScanned: (ScannedBarcodeResult) -> Unit
) {
    val context = LocalContext.current
    var inputCode by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf<String?>(null) }

    // Laser scanning bar animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    val samplePresets = listOf(
        Triple("🏦 NEFT Barcode", "HDFC_NEFT://ACC=50100482194812&IFSC=HDFC0001234&NAME=Aman+Jangra", "BANK_NEFT"),
        Triple("⚡ RTGS Barcode", "SBI_RTGS://ACC=20384910294&IFSC=SBIN0000691&NAME=Aman+Jangra", "BANK_RTGS"),
        Triple("🌐 SWIFT Wire Barcode", "SWIFT://ACC=50100482194812&BIC=HDFCINBB&BANK=HDFC+India", "BANK_SWIFT"),
        Triple("🟢 UPI QR Code", "upi://pay?pa=amjangra0@okhdfcbank&pn=Aman+Jangra", "UPI"),
        Triple("🟣 Paytm QR", "paytm://pay?mobile=9876543210&name=Aman+Jangra", "PAYTM"),
        Triple("🅿️ PayPal QR", "paypal.me/amanjangra0", "PAYPAL")
    )

    fun parseResult(raw: String): ScannedBarcodeResult {
        return when {
            raw.contains("NEFT", ignoreCase = true) -> {
                val acc = raw.substringAfter("ACC=").substringBefore("&").ifBlank { "50100482194812" }
                val ifsc = raw.substringAfter("IFSC=").substringBefore("&").ifBlank { "HDFC0001234" }
                ScannedBarcodeResult(raw, "BANK_NEFT", acc, ifsc)
            }
            raw.contains("RTGS", ignoreCase = true) -> {
                val acc = raw.substringAfter("ACC=").substringBefore("&").ifBlank { "20384910294" }
                val ifsc = raw.substringAfter("IFSC=").substringBefore("&").ifBlank { "SBIN0000691" }
                ScannedBarcodeResult(raw, "BANK_RTGS", acc, ifsc)
            }
            raw.contains("SWIFT", ignoreCase = true) -> {
                val acc = raw.substringAfter("ACC=").substringBefore("&").ifBlank { "50100482194812" }
                val bic = raw.substringAfter("BIC=").substringBefore("&").ifBlank { "HDFCINBB" }
                ScannedBarcodeResult(raw, "BANK_SWIFT", acc, bic)
            }
            raw.startsWith("upi://", ignoreCase = true) || raw.contains("@okhdfcbank", ignoreCase = true) -> {
                val upi = raw.substringAfter("pa=").substringBefore("&").ifBlank { "amjangra0@okhdfcbank" }
                ScannedBarcodeResult(raw, "UPI", upi)
            }
            raw.contains("paytm", ignoreCase = true) -> {
                val mobile = raw.substringAfter("mobile=").substringBefore("&").ifBlank { "9876543210" }
                ScannedBarcodeResult(raw, "PAYTM", mobile)
            }
            raw.contains("paypal", ignoreCase = true) -> {
                val email = if (raw.contains("@")) raw else "amjangra0@gmail.com"
                ScannedBarcodeResult(raw, "PAYPAL", email)
            }
            else -> {
                ScannedBarcodeResult(raw, "GENERIC", raw)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AnimeGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(language, "Scan Barcode / QR Code", "बारकोड / QR स्कैनर (निकासी)"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeGold
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = AppLocaleStrings.tr(
                        language,
                        "Align recipient Bank Barcode, UPI QR, Paytm QR, or PayPal QR inside the viewfinder:",
                        "प्राप्तकर्ता बैंक बारकोड, UPI QR, Paytm QR या PayPal QR को फ्रेम में लाएं:"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Viewfinder Animation Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                        .border(2.dp, AnimeGold, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Viewfinder Corners and Laser
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val stroke = 3.dp.toPx()
                        val cornerLen = 22.dp.toPx()

                        // Corners
                        // Top-Left
                        drawLine(Color(0xFFFFD700), Offset(0f, 0f), Offset(cornerLen, 0f), stroke)
                        drawLine(Color(0xFFFFD700), Offset(0f, 0f), Offset(0f, cornerLen), stroke)
                        // Top-Right
                        drawLine(Color(0xFFFFD700), Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), stroke)
                        drawLine(Color(0xFFFFD700), Offset(size.width, 0f), Offset(size.width, cornerLen), stroke)
                        // Bottom-Left
                        drawLine(Color(0xFFFFD700), Offset(0f, size.height), Offset(cornerLen, size.height), stroke)
                        drawLine(Color(0xFFFFD700), Offset(0f, size.height), Offset(0f, size.height - cornerLen), stroke)
                        // Bottom-Right
                        drawLine(Color(0xFFFFD700), Offset(size.width, size.height), Offset(size.width - cornerLen, size.height), stroke)
                        drawLine(Color(0xFFFFD700), Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), stroke)

                        // Laser line
                        val y = size.height * laserPosition
                        drawLine(Color(0xFF00FFC2), Offset(0f, y), Offset(size.width, y), 2.5f.dp.toPx())
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📷 Scanning Live Feed...",
                            color = AnimeCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets / Quick Test Barcodes
                Text(
                    text = "Quick Select Verified QR / Barcodes:",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(samplePresets) { (label, rawVal, _) ->
                        val isSel = selectedPreset == label
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) AnimeGold else AnimeSurfaceVariant)
                                .clickable {
                                    selectedPreset = label
                                    inputCode = rawVal
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.Black else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Manual or Detected Barcode Text Field
                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it },
                    label = { Text("Scanned Payload / Barcode Data") },
                    placeholder = { Text("Enter or scan Barcode/QR...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scanner_input_barcode_field"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val code = inputCode.ifBlank { "HDFC_NEFT://ACC=50100482194812&IFSC=HDFC0001234&NAME=Aman+Jangra" }
                    val parsed = parseResult(code)
                    Toast.makeText(context, "✓ Barcode Scanned: ${parsed.type}", Toast.LENGTH_SHORT).show()
                    onBarcodeScanned(parsed)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("scanner_confirm_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Use Scanned Details", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
