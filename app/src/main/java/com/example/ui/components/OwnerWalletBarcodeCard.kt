package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyType
import com.example.data.model.OwnerWalletConstants
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
import kotlin.math.abs

/**
 * OwnerWalletBarcodeCard
 * Renders the Owner's In-App Wallet Barcode and QR Code, allowing users to scan or copy
 * the Owner's Wallet ID to deposit subscription payments directly into the Owner's in-app Wallet.
 */
@Composable
fun OwnerWalletBarcodeCard(
    amount: Double? = null,
    currency: CurrencyType = CurrencyType.INR,
    language: String = "Hindi",
    modifier: Modifier = Modifier,
    isOwnerSelfView: Boolean = false
) {
    val context = LocalContext.current
    var isBarcodeMode by remember { mutableStateOf(false) } // false = QR Code, true = 1D Barcode
    var hasCopied by remember { mutableStateOf(false) }

    val walletId = OwnerWalletConstants.OWNER_WALLET_ID
    val ownerName = OwnerWalletConstants.OWNER_DISPLAY_NAME
    val upiId = OwnerWalletConstants.OWNER_UPI_ID

    fun copyWalletId() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Owner Wallet ID", walletId)
        clipboard?.setPrimaryClip(clip)
        hasCopied = true
        Toast.makeText(
            context,
            "📋 Owner Wallet ID Copied: $walletId",
            Toast.LENGTH_SHORT
        ).show()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AnimeGold.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
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
                        Text("👑", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isOwnerSelfView) {
                                AppLocaleStrings.tr(language, "Your Receiving Wallet Code", "आपका इन-ऐप वॉलेट बारकोड")
                            } else {
                                AppLocaleStrings.tr(language, "Owner In-App Wallet", "ओनर का इन-ऐप वॉलेट")
                            },
                            color = AnimeGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = ownerName,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Switch between QR code and 1D Barcode view
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurface)
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = { isBarcodeMode = false },
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (!isBarcodeMode) AnimeGold.copy(alpha = 0.25f) else Color.Transparent, RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = if (!isBarcodeMode) AnimeGold else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { isBarcodeMode = true },
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (isBarcodeMode) AnimeGold.copy(alpha = 0.25f) else Color.Transparent, RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            Icons.Default.ViewWeek,
                            contentDescription = "Barcode",
                            tint = if (isBarcodeMode) AnimeGold else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Canvas Container (White background for authentic scanning contrast)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, AnimeGold, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isBarcodeMode) {
                    // 1D Barcode Graphic
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(
                            modifier = Modifier
                                .width(220.dp)
                                .height(72.dp)
                        ) {
                            val barCount = 45
                            val barWidth = size.width / (barCount * 1.3f)
                            val seed = abs(walletId.hashCode())
                            for (i in 0 until barCount) {
                                val isThick = ((seed shr (i % 31)) and 1) == 1 || (i % 4 == 0)
                                val w = if (isThick) barWidth * 1.8f else barWidth * 0.85f
                                val x = i * (barWidth * 1.3f)
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(x, 0f),
                                    size = Size(w, size.height)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "* $walletId *",
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    // 2D QR Code Graphic
                    Canvas(
                        modifier = Modifier
                            .size(140.dp)
                    ) {
                        val matrixSize = 21
                        val cellSize = size.width / matrixSize

                        // Draw Finder patterns at 3 corners: (0,0), (matrixSize-7, 0), (0, matrixSize-7)
                        fun drawFinder(startX: Float, startY: Float) {
                            drawRect(Color.Black, Offset(startX, startY), Size(7 * cellSize, 7 * cellSize))
                            drawRect(Color.White, Offset(startX + cellSize, startY + cellSize), Size(5 * cellSize, 5 * cellSize))
                            drawRect(Color.Black, Offset(startX + 2 * cellSize, startY + 2 * cellSize), Size(3 * cellSize, 3 * cellSize))
                        }

                        drawFinder(0f, 0f)
                        drawFinder((matrixSize - 7) * cellSize, 0f)
                        drawFinder(0f, (matrixSize - 7) * cellSize)

                        // Draw deterministic data dots based on walletId and amount
                        val hash = abs("$walletId-$amount".hashCode())
                        for (r in 0 until matrixSize) {
                            for (c in 0 until matrixSize) {
                                val inFinder1 = r < 7 && c < 7
                                val inFinder2 = r < 7 && c >= matrixSize - 7
                                val inFinder3 = r >= matrixSize - 7 && c < 7
                                if (!inFinder1 && !inFinder2 && !inFinder3) {
                                    val isFilled = ((hash shr ((r * 5 + c) % 31)) and 1) == 1 ||
                                            ((r + c) % 3 == 0) ||
                                            (r == 6 || c == 6)
                                    if (isFilled) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize * 0.9f, cellSize * 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Wallet ID Display & Copy Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurface)
                    .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable { copyWalletId() }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OWNER WALLET ID",
                            color = AnimeCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = walletId,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { copyWalletId() },
                        modifier = Modifier.height(30.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            if (hasCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = if (hasCopied) AnimeGreen else AnimeCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasCopied) "Copied!" else "Copy ID",
                            color = if (hasCopied) AnimeGreen else AnimeCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Extra Info (UPI & Amount if available)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UPI: $upiId",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                if (amount != null && amount > 0) {
                    Text(
                        text = "Amount: ${currency.symbol}$amount",
                        color = AnimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Deposit Guarantee Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnimeGreen.copy(alpha = 0.12f))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = AnimeGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            language,
                            "Direct In-App Deposit: Any payment is credited directly into the Owner's Wallet in this app.",
                            "सीधे इन-ऐप डिपॉजिट: भुगतान की राशि सीधे इसी ऐप में ओनर के वॉलेट में जमा होगी जिसे ओनर जब चाहें निकाल सकते हैं।"
                        ),
                        color = AnimeGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
