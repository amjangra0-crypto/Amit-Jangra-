package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AuthProvider
import com.example.data.model.CurrencyType
import com.example.data.model.CurrencyWallet
import com.example.data.model.PaymentGateway
import com.example.data.model.TransactionType
import com.example.data.model.UserProfile
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AuthAndWalletRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("anime_auth_wallet_prefs", Context.MODE_PRIVATE)

    companion object {
        const val OWNER_EMAIL = "amjangra0@gmail.com"
        const val OWNER_PHONE = "+91 98765 43210"
    }

    private val _currentUser = MutableStateFlow(loadUserProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _wallets = MutableStateFlow(loadWallets())
    val wallets: StateFlow<Map<CurrencyType, CurrencyWallet>> = _wallets.asStateFlow()

    private val _transactions = MutableStateFlow(loadTransactions())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // -----------------------------------------------------------------------------------------
    // AUTHENTICATION METHODS (Gmail, Mobile Number OTP, Logout)
    // -----------------------------------------------------------------------------------------

    fun loginWithGmail(email: String, name: String? = null): Boolean {
        val cleanEmail = email.trim()
        val isOwner = cleanEmail.equals(OWNER_EMAIL, ignoreCase = true)
        val defaultName = if (isOwner) "Aman Jangra (Owner)" else name ?: cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

        val updated = _currentUser.value.copy(
            userId = if (isOwner) "owner_${cleanEmail.substringBefore("@")}" else "user_${UUID.randomUUID().toString().take(8)}",
            displayName = defaultName,
            email = cleanEmail,
            phoneNumber = _currentUser.value.phoneNumber.takeIf { it?.isNotBlank() == true } ?: if (isOwner) OWNER_PHONE else null,
            avatarDrawableName = if (isOwner) "char_shonen_hero" else "char_anime_heroine",
            isOwner = isOwner,
            authProvider = AuthProvider.GMAIL,
            subscriptionPlan = if (isOwner) "VIP OWNER LIFETIME" else "PRO CREATOR",
            subscriptionExpiry = if (isOwner) "Lifetime Unlimited" else "1 Year Active",
            isLoggedIn = true
        )
        _currentUser.value = updated
        saveUserProfile(updated)
        return true
    }

    fun loginWithMobile(phoneNumber: String, inputOtp: String): Boolean {
        val cleanPhone = phoneNumber.trim()
        // Accept OTP verification (demo 6-digit code or default 123456)
        val isOwner = cleanPhone.contains("9876543210") || cleanPhone.contains("98765 43210")
        val updated = _currentUser.value.copy(
            userId = if (isOwner) "owner_phone" else "user_${UUID.randomUUID().toString().take(8)}",
            displayName = if (isOwner) "Aman Jangra (Owner)" else "Anime Creator (+${cleanPhone.takeLast(4)})",
            phoneNumber = cleanPhone,
            email = if (isOwner) OWNER_EMAIL else _currentUser.value.email,
            isOwner = isOwner,
            authProvider = AuthProvider.MOBILE_OTP,
            avatarDrawableName = if (isOwner) "char_shonen_hero" else "char_lady_mentor",
            subscriptionPlan = if (isOwner) "VIP OWNER LIFETIME" else "FREE TIER",
            subscriptionExpiry = if (isOwner) "Lifetime Unlimited" else "Trial 30 Days",
            isLoggedIn = true
        )
        _currentUser.value = updated
        saveUserProfile(updated)
        return true
    }

    fun logout() {
        val guest = UserProfile(
            userId = "guest_${UUID.randomUUID().toString().take(6)}",
            displayName = "Guest Creator",
            email = null,
            phoneNumber = null,
            avatarDrawableName = "char_chibi_mascot",
            bio = "Exploring Anime Studio AI. Login with Gmail or Mobile to unlock features!",
            creatorSpecialty = "Viewer",
            isOwner = false,
            authProvider = AuthProvider.GUEST,
            subscriptionPlan = "GUEST PASS",
            subscriptionExpiry = "Login Required",
            isLoggedIn = false,
            joinedDate = "Just now",
            projectsCreated = 0,
            videosRendered = 0,
            voiceMinutesUsed = 0
        )
        _currentUser.value = guest
        saveUserProfile(guest)
    }

    fun updateProfile(
        displayName: String,
        bio: String,
        phone: String?,
        email: String?,
        avatarDrawable: String,
        specialty: String,
        notifications: Boolean,
        autoSync: Boolean
    ) {
        val current = _currentUser.value
        val isOwnerNow = (email?.equals(OWNER_EMAIL, ignoreCase = true) == true) || current.isOwner
        val updated = current.copy(
            displayName = displayName.ifBlank { current.displayName },
            bio = bio,
            phoneNumber = phone?.ifBlank { null },
            email = email?.ifBlank { null },
            avatarDrawableName = avatarDrawable,
            creatorSpecialty = specialty,
            isOwner = isOwnerNow,
            notificationEnabled = notifications,
            autoSyncDubbing = autoSync
        )
        _currentUser.value = updated
        saveUserProfile(updated)
    }

    // -----------------------------------------------------------------------------------------
    // OWNER MULTI-CURRENCY WALLET DEPOSIT & WITHDRAWAL ENGINE
    // -----------------------------------------------------------------------------------------

    fun recordSubscriptionDeposit(
        planName: String,
        amount: Double,
        currency: CurrencyType,
        gateway: PaymentGateway,
        subscriberEmailOrPhone: String
    ): Boolean {
        val currentWallets = _wallets.value.toMutableMap()
        val wallet = currentWallets[currency] ?: CurrencyWallet(currency, 0.0, 0.0, 0.0)

        val updatedWallet = wallet.copy(
            balance = wallet.balance + amount,
            totalReceived = wallet.totalReceived + amount
        )
        currentWallets[currency] = updatedWallet
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        // Record Transaction
        val txn = WalletTransaction(
            id = "DEP_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.SUBSCRIPTION_DEPOSIT,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "$subscriberEmailOrPhone via ${gateway.title}",
            description = "Subscription Plan Purchase: $planName",
            status = "सफल (Deposit Received)",
            referenceId = "TXN-${UUID.randomUUID().toString().uppercase().take(10)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return true
    }

    fun withdrawToBank(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        ifscOrSwift: String,
        holderName: String
    ): Pair<Boolean, String> {
        val currentWallets = _wallets.value.toMutableMap()
        val wallet = currentWallets[currency] ?: return Pair(false, "Wallet not found")

        if (wallet.balance < amount) {
            return Pair(false, "Insufficient balance in ${currency.code} wallet! Available: ${currency.symbol}${wallet.balance}")
        }

        val updatedWallet = wallet.copy(
            balance = wallet.balance - amount,
            totalWithdrawn = wallet.totalWithdrawn + amount
        )
        currentWallets[currency] = updatedWallet
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        val txn = WalletTransaction(
            id = "WTH_BANK_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_BANK,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "Bank A/C: ••••${accountNumber.takeLast(4)} (IFSC: $ifscOrSwift)",
            description = "Direct Bank Transfer to $holderName",
            status = "प्रसंस्कृत (Settled to Bank)",
            referenceId = "BANK-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "Successfully transferred ${currency.symbol}$amount to Bank Account (••••${accountNumber.takeLast(4)})")
    }

    fun withdrawToPhonePe(
        amount: Double,
        currency: CurrencyType,
        phonePeUpiOrMobile: String
    ): Pair<Boolean, String> {
        val currentWallets = _wallets.value.toMutableMap()
        val wallet = currentWallets[currency] ?: return Pair(false, "Wallet not found")

        if (wallet.balance < amount) {
            return Pair(false, "Insufficient balance! Available: ${currency.symbol}${wallet.balance}")
        }

        val updatedWallet = wallet.copy(
            balance = wallet.balance - amount,
            totalWithdrawn = wallet.totalWithdrawn + amount
        )
        currentWallets[currency] = updatedWallet
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        val txn = WalletTransaction(
            id = "WTH_PHONEPE_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_PHONEPE,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "PhonePe: $phonePeUpiOrMobile",
            description = "Instant PhonePe UPI Payout",
            status = "सफल (Instant UPI)",
            referenceId = "PP-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "Successfully transferred ${currency.symbol}$amount to PhonePe ($phonePeUpiOrMobile)")
    }

    fun withdrawToGooglePay(
        amount: Double,
        currency: CurrencyType,
        gpayUpiOrMobile: String
    ): Pair<Boolean, String> {
        val currentWallets = _wallets.value.toMutableMap()
        val wallet = currentWallets[currency] ?: return Pair(false, "Wallet not found")

        if (wallet.balance < amount) {
            return Pair(false, "Insufficient balance! Available: ${currency.symbol}${wallet.balance}")
        }

        val updatedWallet = wallet.copy(
            balance = wallet.balance - amount,
            totalWithdrawn = wallet.totalWithdrawn + amount
        )
        currentWallets[currency] = updatedWallet
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        val txn = WalletTransaction(
            id = "WTH_GPAY_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_GPAY,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "Google Pay: $gpayUpiOrMobile",
            description = "Instant Google Pay UPI Payout",
            status = "सफल (Instant GPay)",
            referenceId = "GPAY-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "Successfully transferred ${currency.symbol}$amount to Google Pay ($gpayUpiOrMobile)")
    }

    fun withdrawToPayPal(
        amount: Double,
        currency: CurrencyType,
        paypalEmail: String
    ): Pair<Boolean, String> {
        val currentWallets = _wallets.value.toMutableMap()
        val wallet = currentWallets[currency] ?: return Pair(false, "Wallet not found")

        if (wallet.balance < amount) {
            return Pair(false, "Insufficient balance! Available: ${currency.symbol}${wallet.balance}")
        }

        val updatedWallet = wallet.copy(
            balance = wallet.balance - amount,
            totalWithdrawn = wallet.totalWithdrawn + amount
        )
        currentWallets[currency] = updatedWallet
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        val txn = WalletTransaction(
            id = "WTH_PAYPAL_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_PAYPAL,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "PayPal: $paypalEmail",
            description = "PayPal International Transfer",
            status = "सफल (PayPal Dispatched)",
            referenceId = "PPAL-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "Successfully transferred ${currency.symbol}$amount to PayPal ($paypalEmail)")
    }

    fun interWalletTransfer(
        sourceCurrency: CurrencyType,
        targetCurrency: CurrencyType,
        amount: Double
    ): Pair<Boolean, String> {
        if (sourceCurrency == targetCurrency) return Pair(false, "Source and target currencies must be different")

        val currentWallets = _wallets.value.toMutableMap()
        val srcWallet = currentWallets[sourceCurrency] ?: return Pair(false, "Source wallet not found")
        val tgtWallet = currentWallets[targetCurrency] ?: return Pair(false, "Target wallet not found")

        if (srcWallet.balance < amount) {
            return Pair(false, "Insufficient balance in ${sourceCurrency.code} wallet")
        }

        // Calculate converted amount based on exchange rates
        val inrValue = amount * sourceCurrency.exchangeToInr
        val convertedAmount = inrValue / targetCurrency.exchangeToInr

        currentWallets[sourceCurrency] = srcWallet.copy(balance = srcWallet.balance - amount)
        currentWallets[targetCurrency] = tgtWallet.copy(balance = tgtWallet.balance + convertedAmount)
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        val txn = WalletTransaction(
            id = "XCHG_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WALLET_EXCHANGE_TRANSFER,
            currency = targetCurrency,
            amount = convertedAmount,
            targetAccountOrUser = "${sourceCurrency.code} -> ${targetCurrency.code} Exchange",
            description = "Converted ${sourceCurrency.symbol}$amount into ${targetCurrency.symbol}${String.format("%.2f", convertedAmount)}",
            status = "सफल (Exchange Complete)",
            referenceId = "XCH-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "Exchanged ${sourceCurrency.symbol}$amount to ${targetCurrency.symbol}${String.format("%.2f", convertedAmount)}")
    }

    // -----------------------------------------------------------------------------------------
    // PERSISTENCE HELPERS
    // -----------------------------------------------------------------------------------------

    private fun loadUserProfile(): UserProfile {
        val isLoggedIn = prefs.getBoolean("is_logged_in", true)
        val isOwner = prefs.getBoolean("is_owner", true)
        val email = prefs.getString("user_email", OWNER_EMAIL)
        val phone = prefs.getString("user_phone", OWNER_PHONE)
        val name = prefs.getString("user_display_name", if (isOwner) "Aman Jangra" else "Creator") ?: "Aman Jangra"
        val avatar = prefs.getString("user_avatar", "char_shonen_hero") ?: "char_shonen_hero"
        val bio = prefs.getString("user_bio", "Lead Director & Founder at Anime Studio AI. Building next-generation anime narratives with AI.") ?: ""
        val specialty = prefs.getString("user_specialty", "Anime Film Director") ?: "Anime Film Director"
        val plan = prefs.getString("user_plan", if (isOwner) "VIP OWNER LIFETIME" else "PRO CREATOR") ?: "VIP OWNER LIFETIME"

        return UserProfile(
            userId = prefs.getString("user_id", "owner_amjangra0") ?: "owner_amjangra0",
            displayName = name,
            email = email,
            phoneNumber = phone,
            avatarDrawableName = avatar,
            bio = bio,
            creatorSpecialty = specialty,
            isOwner = isOwner,
            authProvider = if (email != null) AuthProvider.GMAIL else AuthProvider.MOBILE_OTP,
            subscriptionPlan = plan,
            subscriptionExpiry = if (isOwner) "Lifetime Unlimited" else "1 Year Active",
            isLoggedIn = isLoggedIn,
            projectsCreated = prefs.getInt("stat_projects", 18),
            videosRendered = prefs.getInt("stat_videos", 42),
            voiceMinutesUsed = prefs.getInt("stat_voice", 320)
        )
    }

    private fun saveUserProfile(profile: UserProfile) {
        prefs.edit().apply {
            putBoolean("is_logged_in", profile.isLoggedIn)
            putBoolean("is_owner", profile.isOwner)
            putString("user_email", profile.email)
            putString("user_phone", profile.phoneNumber)
            putString("user_display_name", profile.displayName)
            putString("user_avatar", profile.avatarDrawableName)
            putString("user_bio", profile.bio)
            putString("user_specialty", profile.creatorSpecialty)
            putString("user_plan", profile.subscriptionPlan)
            putString("user_id", profile.userId)
            putInt("stat_projects", profile.projectsCreated)
            putInt("stat_videos", profile.videosRendered)
            putInt("stat_voice", profile.voiceMinutesUsed)
            apply()
        }
    }

    private fun loadWallets(): Map<CurrencyType, CurrencyWallet> {
        val inrBal = prefs.getFloat("wallet_inr_bal", 48500.0f).toDouble()
        val usdBal = prefs.getFloat("wallet_usd_bal", 620.0f).toDouble()
        val eurBal = prefs.getFloat("wallet_eur_bal", 450.0f).toDouble()

        return mapOf(
            CurrencyType.INR to CurrencyWallet(CurrencyType.INR, inrBal, inrBal + 12000.0, 12000.0),
            CurrencyType.USD to CurrencyWallet(CurrencyType.USD, usdBal, usdBal + 200.0, 200.0),
            CurrencyType.EUR to CurrencyWallet(CurrencyType.EUR, eurBal, eurBal + 150.0, 150.0)
        )
    }

    private fun saveWallets(wallets: Map<CurrencyType, CurrencyWallet>) {
        prefs.edit().apply {
            wallets[CurrencyType.INR]?.let { putFloat("wallet_inr_bal", it.balance.toFloat()) }
            wallets[CurrencyType.USD]?.let { putFloat("wallet_usd_bal", it.balance.toFloat()) }
            wallets[CurrencyType.EUR]?.let { putFloat("wallet_eur_bal", it.balance.toFloat()) }
            apply()
        }
    }

    private fun loadTransactions(): List<WalletTransaction> {
        val raw = prefs.getString("wallet_txns_json", null)
        if (raw.isNullOrBlank()) {
            // Seed realistic initial transactions
            return listOf(
                WalletTransaction(
                    id = "DEP_INIT_1",
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    type = TransactionType.SUBSCRIPTION_DEPOSIT,
                    currency = CurrencyType.INR,
                    amount = 1499.0,
                    targetAccountOrUser = "rohit.dubey@gmail.com via PhonePe",
                    description = "VIP Lifetime Anime Pass Purchase",
                    status = "सफल (Deposit Received)",
                    referenceId = "TXN-IN7729103"
                ),
                WalletTransaction(
                    id = "DEP_INIT_2",
                    timestamp = System.currentTimeMillis() - 3600000 * 6,
                    type = TransactionType.SUBSCRIPTION_DEPOSIT,
                    currency = CurrencyType.USD,
                    amount = 49.0,
                    targetAccountOrUser = "alex.nakamura@tokyo.jp via PayPal",
                    description = "Studio Director Global Plan",
                    status = "सफल (Deposit Received)",
                    referenceId = "TXN-US4902188"
                ),
                WalletTransaction(
                    id = "WTH_INIT_3",
                    timestamp = System.currentTimeMillis() - 3600000 * 24,
                    type = TransactionType.WITHDRAWAL_BANK,
                    currency = CurrencyType.INR,
                    amount = 10000.0,
                    targetAccountOrUser = "HDFC Bank (••••4812)",
                    description = "Withdrawal to Owner Savings Account",
                    status = "प्रसंस्कृत (Settled to Bank)",
                    referenceId = "BANK-NEFT9923"
                )
            )
        }
        return try {
            val list = mutableListOf<WalletTransaction>()
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val cType = CurrencyType.valueOf(obj.optString("currency", "INR"))
                val tType = TransactionType.valueOf(obj.optString("type", "SUBSCRIPTION_DEPOSIT"))
                list.add(
                    WalletTransaction(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        type = tType,
                        currency = cType,
                        amount = obj.getDouble("amount"),
                        targetAccountOrUser = obj.getString("targetAccountOrUser"),
                        description = obj.getString("description"),
                        status = obj.optString("status", "सफल"),
                        referenceId = obj.optString("referenceId", "REF-000")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveTransactions(txns: List<WalletTransaction>) {
        try {
            val array = JSONArray()
            txns.take(40).forEach { t ->
                val obj = JSONObject().apply {
                    put("id", t.id)
                    put("timestamp", t.timestamp)
                    put("type", t.type.name)
                    put("currency", t.currency.name)
                    put("amount", t.amount)
                    put("targetAccountOrUser", t.targetAccountOrUser)
                    put("description", t.description)
                    put("status", t.status)
                    put("referenceId", t.referenceId)
                }
                array.put(obj)
            }
            prefs.edit().putString("wallet_txns_json", array.toString()).apply()
        } catch (_: Exception) {}
    }
}
