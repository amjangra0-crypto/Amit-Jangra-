package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyType
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.AuthModalBottomSheet
import com.example.ui.components.LogoutConfirmationDialog
import com.example.ui.components.OwnerWalletBottomSheet
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

@Composable
fun ProfileScreen(
    viewModel: AnimeViewModel,
    onNavigateToProfileSettings: () -> Unit,
    onNavigateToAppSettings: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val context = LocalContext.current

    var showAuthModal by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showOwnerWalletModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        // Main Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, if (currentUser.isOwner) AnimeGold else AnimePurple)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                (if (currentUser.isOwner) AnimeGold else AnimePurple).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Container
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    if (currentUser.isOwner) AnimeGold else AnimeCyan,
                                    CircleShape
                                )
                                .background(AnimeSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            val resId = context.resources.getIdentifier(
                                currentUser.avatarDrawableName,
                                "drawable",
                                context.packageName
                            )
                            if (resId != 0) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = "User Avatar",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = AnimeGold,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.displayName,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (currentUser.isLoggedIn) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = if (currentUser.isOwner) AnimeGold else AnimeGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Role Badge
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp, bottom = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (currentUser.isOwner) AnimeGold.copy(alpha = 0.2f) else AnimePurple.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentUser.isOwner) "👑 VIP APP OWNER" else currentUser.subscriptionPlan,
                                    color = if (currentUser.isOwner) AnimeGold else AnimeCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = currentUser.creatorSpecialty,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Edit Profile Quick Button
                        IconButton(
                            onClick = onNavigateToProfileSettings,
                            modifier = Modifier.testTag("profile_quick_edit_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = AnimeCyan)
                        }
                    }

                    // Bio
                    if (currentUser.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentUser.bio,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact Badges (Gmail & Mobile No.)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Gmail Badge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AnimeSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mail, contentDescription = null, tint = AnimePink, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Gmail", color = TextMuted, fontSize = 9.sp)
                                    Text(
                                        text = currentUser.email ?: "लिंक नहीं किया गया",
                                        color = if (currentUser.email != null) TextPrimary else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Mobile Badge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AnimeSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("मोबाइल नंबर", color = TextMuted, fontSize = 9.sp)
                                    Text(
                                        text = currentUser.phoneNumber ?: "लिंक नहीं किया गया",
                                        color = if (currentUser.phoneNumber != null) TextPrimary else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Creator Studio Stats Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "प्रोजेक्ट्स",
                value = "${currentUser.projectsCreated}",
                icon = Icons.Default.Movie,
                color = AnimeCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "वीडियो रेंडर",
                value = "${currentUser.videosRendered}",
                icon = Icons.Default.VideoLibrary,
                color = AnimePink,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "वॉइस मिनट्स",
                value = "${currentUser.voiceMinutesUsed}m",
                icon = Icons.Default.Mic,
                color = AnimeGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // -------------------------------------------------------------------------------------
        // STRICT OWNER WALLET SYSTEM (ONLY VISIBLE IF USER IS THE OWNER!)
        // -------------------------------------------------------------------------------------
        if (currentUser.isOwner) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, AnimeGold)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(AnimeGold.copy(alpha = 0.18f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(AnimeGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Wallet, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "👑 ओनर डिपॉजिट व विथड्रॉल वॉलेट",
                                        color = AnimeGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "सब्सक्रिप्शन की जमा राशि केवल आपकी ID में",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("ONLY OWNER", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Multi-currency Wallets Balance Grid: INR, USD, EUR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val inrWallet = wallets[CurrencyType.INR]
                            val usdWallet = wallets[CurrencyType.USD]
                            val eurWallet = wallets[CurrencyType.EUR]

                            CurrencyMiniCard(
                                title = "INR वॉलेट",
                                symbol = "₹",
                                balance = inrWallet?.balance ?: 0.0,
                                color = AnimeGreen,
                                modifier = Modifier.weight(1f)
                            )
                            CurrencyMiniCard(
                                title = "Dollar वॉलेट",
                                symbol = "$",
                                balance = usdWallet?.balance ?: 0.0,
                                color = AnimeCyan,
                                modifier = Modifier.weight(1f)
                            )
                            CurrencyMiniCard(
                                title = "Euro वॉलेट",
                                symbol = "€",
                                balance = eurWallet?.balance ?: 0.0,
                                color = AnimePink,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showOwnerWalletModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("open_owner_wallet_btn")
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "वॉलेट खोलें व बैंक / UPI / PayPal में ट्रांसफर करें",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Action Options Section
        Text(
            text = "खाता व सेटिंग्स विकल्प (Account & Settings)",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 1. Profile Settings Option
        ProfileOptionItem(
            icon = Icons.Default.Edit,
            iconTint = AnimeCyan,
            title = "प्रोफ़ाइल सेटिंग्स (Profile Settings)",
            subtitle = "नाम, बायो, अवतार, मोबाइल नंबर व Gmail अपडेट करें",
            onClick = onNavigateToProfileSettings,
            testTag = "menu_profile_settings"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. App Settings Option
        ProfileOptionItem(
            icon = Icons.Default.Settings,
            iconTint = AnimePurple,
            title = "ऐप सेटिंग्स (App Settings)",
            subtitle = "डार्क मोड, वॉइस डबिंग स्पीड, वीडियो रिज़ॉल्यूशन व स्टोरेज",
            onClick = onNavigateToAppSettings,
            testTag = "menu_app_settings"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Subscription Status & VIP Hub Option
        ProfileOptionItem(
            icon = Icons.Default.WorkspacePremium,
            iconTint = AnimeGold,
            title = "सब्सक्रिप्शन व VIP हब (Subscription Plans)",
            subtitle = if (currentUser.isOwner) "ओनर लाइसेंस: आजीवन फ्री एक्सेस सक्रिय" else "UPI / कार्ड / PayPal से प्लान अपग्रेड करें",
            onClick = { viewModel.setTab(AppTab.SUBSCRIPTION) },
            testTag = "menu_subscription"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Login / Switch Account
        ProfileOptionItem(
            icon = Icons.Default.Login,
            iconTint = AnimeGreen,
            title = if (currentUser.isLoggedIn) "खाता बदलें (Switch Account)" else "लॉगिन करें (Sign In)",
            subtitle = "Gmail या मोबाइल नंबर OTP से किसी भी खाते में लॉगिन करें",
            onClick = { showAuthModal = true },
            testTag = "menu_auth_login"
        )

        if (currentUser.isLoggedIn) {
            Spacer(modifier = Modifier.height(8.dp))

            // 5. Logout Option
            ProfileOptionItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                iconTint = AnimePink,
                title = "लॉगआउट (Logout)",
                subtitle = "वर्तमान सत्र समाप्त करें और गेस्ट मोड में जाएँ",
                onClick = { showLogoutConfirm = true },
                testTag = "menu_logout"
            )
        }
    }

    // Modal Sheet: Gmail / Mobile OTP Login
    if (showAuthModal) {
        AuthModalBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAuthModal = false }
        )
    }

    // Modal Sheet: Owner Multi-Currency Wallet
    if (showOwnerWalletModal) {
        OwnerWalletBottomSheet(
            viewModel = viewModel,
            onDismiss = { showOwnerWalletModal = false }
        )
    }

    // Dialog: Logout Confirmation
    if (showLogoutConfirm) {
        LogoutConfirmationDialog(
            onConfirmLogout = {
                viewModel.logout()
                showLogoutConfirm = false
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun CurrencyMiniCard(
    title: String,
    symbol: String,
    balance: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, color = TextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$symbol${String.format("%,.0f", balance)}",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
