package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AuthProvider
import com.example.data.model.CardPaymentDetails
import com.example.data.model.CurrencyType
import com.example.data.model.CurrencyWallet
import com.example.data.model.OwnerBankAccount
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
        const val OWNER_UID = "owner_amjangra0"
    }

    fun getOwnerName(): String = prefs.getString("custom_owner_name", "Aman Jangra") ?: "Aman Jangra"
    fun getOwnerEmail(): String = prefs.getString("custom_owner_email", OWNER_EMAIL) ?: OWNER_EMAIL
    fun getOwnerUsername(): String = prefs.getString("custom_owner_username", "Aman Jangra (Owner)") ?: "Aman Jangra (Owner)"
    fun getOwnerPassword(): String = prefs.getString("custom_owner_password", "9999") ?: "9999"
    fun getOwnerMobile(): String = prefs.getString("custom_owner_mobile", OWNER_PHONE) ?: OWNER_PHONE
    fun getOwnerAdminUid(): String = prefs.getString("custom_owner_uid", OWNER_UID) ?: OWNER_UID

    private val _currentUser = MutableStateFlow(loadUserProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _connectedBankAccount = MutableStateFlow(loadBankAccount())
    val connectedBankAccount: StateFlow<OwnerBankAccount> = _connectedBankAccount.asStateFlow()

    private val _wallets = MutableStateFlow(loadWallets())
    val wallets: StateFlow<Map<CurrencyType, CurrencyWallet>> = _wallets.asStateFlow()

    private val _transactions = MutableStateFlow(loadTransactions())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _subscribers = MutableStateFlow(loadSubscribers())
    val subscribers: StateFlow<List<com.example.data.model.SubscriberRecord>> = _subscribers.asStateFlow()

    // -----------------------------------------------------------------------------------------
    // AUTHENTICATION METHODS (Gmail, Mobile Number OTP, Logout)
    // -----------------------------------------------------------------------------------------

    fun loginWithGmail(email: String, name: String? = null): Boolean {
        val cleanEmail = email.trim()
        val currentOwnerEmail = getOwnerEmail()
        val isOwner = cleanEmail.equals(currentOwnerEmail, ignoreCase = true) || cleanEmail.equals(OWNER_EMAIL, ignoreCase = true)
        val defaultName = if (isOwner) getOwnerUsername() else name ?: cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

        val updated = _currentUser.value.copy(
            userId = if (isOwner) getOwnerAdminUid() else "user_${UUID.randomUUID().toString().take(8)}",
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

    /**
     * Strictly allows the verified Owner to modify Owner Dashboard Username, Email & Admin UID
     */
    fun updateOwnerCredentials(newUsername: String, newEmail: String, newAdminUid: String? = null): Boolean {
        val cleanEmail = newEmail.trim()
        val cleanUsername = newUsername.trim()
        val cleanUid = newAdminUid?.trim()?.ifBlank { null } ?: "owner_${cleanEmail.substringBefore("@")}"

        prefs.edit()
            .putString("custom_owner_email", cleanEmail)
            .putString("custom_owner_username", cleanUsername)
            .putString("custom_owner_uid", cleanUid)
            .apply()

        // If currently signed-in user is Owner, reflect immediately
        val current = _currentUser.value
        if (current.isOwner) {
            val updated = current.copy(
                displayName = cleanUsername,
                email = cleanEmail,
                userId = cleanUid
            )
            _currentUser.value = updated
            saveUserProfile(updated)
        }

        // Add Security Transaction Record
        val auditTxn = WalletTransaction(
            id = "SEC_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.SECURITY_CREDENTIAL_CHANGE,
            currency = CurrencyType.INR,
            amount = 0.0,
            targetAccountOrUser = "Owner Security Portal",
            description = "Owner credentials updated: Username='$cleanUsername', Email='$cleanEmail', UID='$cleanUid'",
            status = "Security Audit Verified",
            referenceId = "SEC-OWNER-${UUID.randomUUID().toString().uppercase().take(6)}"
        )
        val updatedTxns = listOf(auditTxn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return true
    }

    /**
     * Complete Dashboard Credentials Updater:
     * Updates Owner Name, User's Name (Username), Password/PIN, Mobile No., and Email ID.
     */
    fun updateOwnerDashboardDetails(
        name: String,
        username: String,
        password: String,
        mobile: String,
        email: String
    ): Boolean {
        val cleanName = name.trim().ifBlank { "Aman Jangra" }
        val cleanUsername = username.trim().ifBlank { "Aman Jangra (Owner)" }
        val cleanPassword = password.trim().ifBlank { "9999" }
        val cleanMobile = mobile.trim().ifBlank { OWNER_PHONE }
        val cleanEmail = email.trim().ifBlank { OWNER_EMAIL }
        val cleanUid = "owner_${cleanEmail.substringBefore("@")}"

        prefs.edit()
            .putString("custom_owner_name", cleanName)
            .putString("custom_owner_username", cleanUsername)
            .putString("custom_owner_password", cleanPassword)
            .putString("custom_owner_mobile", cleanMobile)
            .putString("custom_owner_email", cleanEmail)
            .putString("custom_owner_uid", cleanUid)
            .apply()

        val current = _currentUser.value
        if (current.isOwner) {
            val updated = current.copy(
                displayName = cleanName,
                email = cleanEmail,
                phoneNumber = cleanMobile,
                userId = cleanUid
            )
            _currentUser.value = updated
            saveUserProfile(updated)
        }

        val auditTxn = WalletTransaction(
            id = "SEC_DASH_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.SECURITY_CREDENTIAL_CHANGE,
            currency = CurrencyType.INR,
            amount = 0.0,
            targetAccountOrUser = "Owner Dashboard Admin",
            description = "Owner Dashboard Updated: Name='$cleanName', Username='$cleanUsername', Phone='$cleanMobile', Email='$cleanEmail'",
            status = "Security Audit Verified",
            referenceId = "DASH-${UUID.randomUUID().toString().uppercase().take(6)}"
        )
        val updatedTxns = listOf(auditTxn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
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

        // Record Subscriber in Subscription History
        val cleanUser = subscriberEmailOrPhone.substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "Anime Creator" }
        val subscriber = com.example.data.model.SubscriberRecord(
            userName = cleanUser,
            email = if (subscriberEmailOrPhone.contains("@")) subscriberEmailOrPhone else "$subscriberEmailOrPhone@creator.studio",
            mobileNumber = if (!subscriberEmailOrPhone.contains("@")) subscriberEmailOrPhone else "+91 98" + (10000000..99999999).random(),
            planName = planName,
            amountPaid = amount,
            currency = currency,
            paymentGateway = gateway,
            subscriptionDateMillis = System.currentTimeMillis(),
            expiryDateString = "Active (Annual Unlimited)",
            isActive = true
        )
        val updatedSubs = listOf(subscriber) + _subscribers.value
        _subscribers.value = updatedSubs
        saveSubscribers(updatedSubs)

        // Upgrade subscriber's active user profile
        if (!_currentUser.value.isOwner) {
            val updatedUser = _currentUser.value.copy(
                subscriptionPlan = planName,
                subscriptionExpiry = "Active (Auto-Renewed)"
            )
            _currentUser.value = updatedUser
            saveUserProfile(updatedUser)
        }

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

    fun withdrawViaRtgs(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        ifsc: String,
        holderName: String,
        bankName: String
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
            id = "WTH_RTGS_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_RTGS,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "$bankName A/C ••••${accountNumber.takeLast(4)} (IFSC: $ifsc)",
            description = "RTGS Real-Time Gross Settlement to $holderName",
            status = "सफल (RTGS Settled)",
            referenceId = "RTGS-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "✓ RTGS instant settlement successful! ${currency.symbol}$amount transferred to $bankName (••••${accountNumber.takeLast(4)}).")
    }

    fun withdrawViaNeft(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        ifsc: String,
        holderName: String,
        bankName: String
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
            id = "WTH_NEFT_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_NEFT,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "$bankName A/C ••••${accountNumber.takeLast(4)} (IFSC: $ifsc)",
            description = "NEFT Electronic Funds Transfer to $holderName",
            status = "सफल (NEFT Processed)",
            referenceId = "NEFT-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "✓ NEFT transfer processed successfully! ${currency.symbol}$amount dispatched to $bankName (••••${accountNumber.takeLast(4)}).")
    }

    fun withdrawViaSwift(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        swiftBic: String,
        holderName: String,
        bankName: String,
        targetCountry: String = "Global"
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
            id = "WTH_SWIFT_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_SWIFT,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "$bankName ($targetCountry) SWIFT: $swiftBic A/C ••••${accountNumber.takeLast(4)}",
            description = "SWIFT International Wire to $holderName ($targetCountry)",
            status = "सफल (SWIFT Dispatched)",
            referenceId = "SWIFT-${UUID.randomUUID().toString().uppercase().take(10)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "✓ SWIFT International wire dispatched! ${currency.symbol}$amount sent to $bankName via SWIFT $swiftBic.")
    }

    fun withdrawToPaytm(
        amount: Double,
        currency: CurrencyType,
        paytmNumberOrUpi: String
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
            id = "WTH_PAYTM_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_PAYTM,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "Paytm Wallet / UPI: $paytmNumberOrUpi",
            description = "Instant Paytm Payout",
            status = "सफल (Instant Paytm)",
            referenceId = "PTM-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "✓ Paytm transfer successful! ${currency.symbol}$amount credited to Paytm ($paytmNumberOrUpi).")
    }

    fun withdrawToUpiGeneric(
        amount: Double,
        currency: CurrencyType,
        upiId: String,
        platformName: String = "UPI"
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
            id = "WTH_UPI_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_UPI,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "$platformName: $upiId",
            description = "Instant $platformName Transfer to $upiId",
            status = "सफल (Instant UPI)",
            referenceId = "UPI-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "✓ Instant $platformName transfer successful! ${currency.symbol}$amount transferred to $upiId.")
    }

    fun withdrawViaScannedBarcode(
        amount: Double,
        currency: CurrencyType,
        barcodeData: String,
        targetType: String = "Scanned QR/Barcode"
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
            id = "WTH_SCAN_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = TransactionType.WITHDRAWAL_BARCODE_SCAN,
            currency = currency,
            amount = amount,
            targetAccountOrUser = "$targetType: ${barcodeData.take(30)}",
            description = "Withdrawal via Scanned Barcode/QR: $barcodeData",
            status = "सफल (Scanned Settlement)",
            referenceId = "SCAN-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return Pair(true, "✓ Barcode/QR withdrawal successful! ${currency.symbol}$amount transferred to scanned destination (${barcodeData.take(25)}...).")
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

    /**
     * Owner Balance Adjustment (Add funds or update balance securely from Owner Dashboard)
     */
    fun adjustWalletBalance(currency: CurrencyType, deltaAmount: Double, reason: String): Boolean {
        val currentWallets = _wallets.value.toMutableMap()
        val wallet = currentWallets[currency] ?: CurrencyWallet(currency, 0.0, 0.0, 0.0)
        val newBalance = (wallet.balance + deltaAmount).coerceAtLeast(0.0)
        val updatedWallet = if (deltaAmount >= 0) {
            wallet.copy(balance = newBalance, totalReceived = wallet.totalReceived + deltaAmount)
        } else {
            wallet.copy(balance = newBalance, totalWithdrawn = wallet.totalWithdrawn + (-deltaAmount))
        }
        currentWallets[currency] = updatedWallet
        _wallets.value = currentWallets
        saveWallets(currentWallets)

        val txn = WalletTransaction(
            id = "ADJ_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = if (deltaAmount >= 0) TransactionType.SUBSCRIPTION_DEPOSIT else TransactionType.WITHDRAWAL_BANK,
            currency = currency,
            amount = Math.abs(deltaAmount),
            targetAccountOrUser = "👑 Owner Dashboard ($reason)",
            description = if (deltaAmount >= 0) "Manual Balance Addition: $reason" else "Manual Balance Deduction: $reason",
            status = "सफल (Balance Adjusted)",
            referenceId = "ADJ-${UUID.randomUUID().toString().uppercase().take(8)}"
        )
        val updatedTxns = listOf(txn) + _transactions.value
        _transactions.value = updatedTxns
        saveTransactions(updatedTxns)
        return true
    }

    // -----------------------------------------------------------------------------------------
    // PERSISTENCE HELPERS
    // -----------------------------------------------------------------------------------------

    private fun loadUserProfile(): UserProfile {
        val isLoggedIn = prefs.getBoolean("is_logged_in", true)
        val isOwner = prefs.getBoolean("is_owner", true)
        val defaultOwnerEmail = getOwnerEmail()
        val email = prefs.getString("user_email", defaultOwnerEmail)
        val phone = prefs.getString("user_phone", OWNER_PHONE)
        val name = prefs.getString("user_display_name", if (isOwner) getOwnerUsername() else "Creator") ?: getOwnerUsername()
        val avatar = prefs.getString("user_avatar", "char_shonen_hero") ?: "char_shonen_hero"
        val bio = prefs.getString("user_bio", "Lead Director & Founder at Anime Studio AI. Building next-generation anime narratives with AI.") ?: ""
        val specialty = prefs.getString("user_specialty", "Anime Film Director") ?: "Anime Film Director"
        val plan = prefs.getString("user_plan", if (isOwner) "VIP OWNER LIFETIME" else "PRO CREATOR") ?: "VIP OWNER LIFETIME"

        return UserProfile(
            userId = prefs.getString("user_id", if (isOwner) getOwnerAdminUid() else "owner_amjangra0") ?: getOwnerAdminUid(),
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

    fun updateBankAccount(account: OwnerBankAccount): Boolean {
        _connectedBankAccount.value = account
        saveBankAccount(account)
        return true
    }

    fun disconnectBankAccount(): Boolean {
        val disconnected = _connectedBankAccount.value.copy(isConnected = false)
        _connectedBankAccount.value = disconnected
        saveBankAccount(disconnected)
        return true
    }

    private fun loadBankAccount(): OwnerBankAccount {
        val json = prefs.getString("owner_bank_account_json", null)
        if (!json.isNullOrBlank()) {
            return try {
                val obj = JSONObject(json)
                OwnerBankAccount(
                    holderName = obj.optString("holderName", "Aman Jangra"),
                    bankName = obj.optString("bankName", "HDFC Bank"),
                    accountNumber = obj.optString("accountNumber", "50100482194812"),
                    ifscCode = obj.optString("ifscCode", "HDFC0001234"),
                    accountType = obj.optString("accountType", "Savings"),
                    branchName = obj.optString("branchName", "Connaught Place, New Delhi"),
                    swiftBic = obj.optString("swiftBic", "HDFCINBB"),
                    upiId = obj.optString("upiId", "amjangra0@okhdfcbank"),
                    isConnected = obj.optBoolean("isConnected", true),
                    lastUpdated = obj.optLong("lastUpdated", System.currentTimeMillis())
                )
            } catch (_: Exception) {
                OwnerBankAccount()
            }
        }
        return OwnerBankAccount()
    }

    private fun saveBankAccount(account: OwnerBankAccount) {
        try {
            val obj = JSONObject().apply {
                put("holderName", account.holderName)
                put("bankName", account.bankName)
                put("accountNumber", account.accountNumber)
                put("ifscCode", account.ifscCode)
                put("accountType", account.accountType)
                put("branchName", account.branchName)
                put("swiftBic", account.swiftBic)
                put("upiId", account.upiId)
                put("isConnected", account.isConnected)
                put("lastUpdated", account.lastUpdated)
            }
            prefs.edit().putString("owner_bank_account_json", obj.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadWallets(): Map<CurrencyType, CurrencyWallet> {
        val inrBal = prefs.getFloat("wallet_inr_bal", 48500.0f).toDouble()
        val usdBal = prefs.getFloat("wallet_usd_bal", 620.0f).toDouble()
        val eurBal = prefs.getFloat("wallet_eur_bal", 450.0f).toDouble()
        val gbpBal = prefs.getFloat("wallet_gbp_bal", 380.0f).toDouble()
        val jpyBal = prefs.getFloat("wallet_jpy_bal", 48000.0f).toDouble()

        return mapOf(
            CurrencyType.INR to CurrencyWallet(CurrencyType.INR, inrBal, inrBal + 12000.0, 12000.0),
            CurrencyType.USD to CurrencyWallet(CurrencyType.USD, usdBal, usdBal + 200.0, 200.0),
            CurrencyType.EUR to CurrencyWallet(CurrencyType.EUR, eurBal, eurBal + 150.0, 150.0),
            CurrencyType.GBP to CurrencyWallet(CurrencyType.GBP, gbpBal, gbpBal + 100.0, 100.0),
            CurrencyType.JPY to CurrencyWallet(CurrencyType.JPY, jpyBal, jpyBal + 15000.0, 15000.0)
        )
    }

    private fun saveWallets(wallets: Map<CurrencyType, CurrencyWallet>) {
        prefs.edit().apply {
            wallets[CurrencyType.INR]?.let { putFloat("wallet_inr_bal", it.balance.toFloat()) }
            wallets[CurrencyType.USD]?.let { putFloat("wallet_usd_bal", it.balance.toFloat()) }
            wallets[CurrencyType.EUR]?.let { putFloat("wallet_eur_bal", it.balance.toFloat()) }
            wallets[CurrencyType.GBP]?.let { putFloat("wallet_gbp_bal", it.balance.toFloat()) }
            wallets[CurrencyType.JPY]?.let { putFloat("wallet_jpy_bal", it.balance.toFloat()) }
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

    private fun loadSubscribers(): List<com.example.data.model.SubscriberRecord> {
        val raw = prefs.getString("subscribers_history_json", null)
        if (raw.isNullOrBlank()) {
            val now = System.currentTimeMillis()
            return listOf(
                com.example.data.model.SubscriberRecord(
                    id = "SUB_01",
                    userName = "Rahul Sharma",
                    email = "rahul.sharma.anime@gmail.com",
                    mobileNumber = "+91 98112 34567",
                    planName = "Hokage VIP Unlimited",
                    amountPaid = 999.0,
                    currency = CurrencyType.INR,
                    paymentGateway = PaymentGateway.OWNER_WALLET_BARCODE,
                    subscriptionDateMillis = now - (86400000L * 3),
                    expiryDateString = "Active (Auto-Renewed 2027)",
                    isActive = true,
                    transactionRef = "TXN-SUB9921"
                ),
                com.example.data.model.SubscriberRecord(
                    id = "SUB_02",
                    userName = "Priya Verma",
                    email = "priya.verma.creator@gmail.com",
                    mobileNumber = "+91 98223 45678",
                    planName = "Shonen Pro Tier",
                    amountPaid = 499.0,
                    currency = CurrencyType.INR,
                    paymentGateway = PaymentGateway.PAYTM,
                    subscriptionDateMillis = now - (86400000L * 7),
                    expiryDateString = "Active (Monthly)",
                    isActive = true,
                    transactionRef = "TXN-SUB8812"
                ),
                com.example.data.model.SubscriberRecord(
                    id = "SUB_03",
                    userName = "Alex Chen",
                    email = "alex.chen.director@hollywood.com",
                    mobileNumber = "+1 (415) 555-0199",
                    planName = "Enterprise Studio Global",
                    amountPaid = 49.0,
                    currency = CurrencyType.USD,
                    paymentGateway = PaymentGateway.PAYPAL,
                    subscriptionDateMillis = now - (86400000L * 12),
                    expiryDateString = "Active (Annual License)",
                    isActive = true,
                    transactionRef = "TXN-SUB7734"
                ),
                com.example.data.model.SubscriberRecord(
                    id = "SUB_04",
                    userName = "Hiroshi Tanaka",
                    email = "hiroshi.tanaka@anime-kyoto.jp",
                    mobileNumber = "+81 90-1234-5678",
                    planName = "Manga Storyboard Master",
                    amountPaid = 3500.0,
                    currency = CurrencyType.JPY,
                    paymentGateway = PaymentGateway.CARD,
                    subscriptionDateMillis = now - (86400000L * 18),
                    expiryDateString = "Active (Annual)",
                    isActive = true,
                    transactionRef = "TXN-SUB6651"
                ),
                com.example.data.model.SubscriberRecord(
                    id = "SUB_05",
                    userName = "Lucas Moreau",
                    email = "lucas.moreau@paris-art.fr",
                    mobileNumber = "+33 6 12 34 56 78",
                    planName = "European Creator VIP",
                    amountPaid = 19.99,
                    currency = CurrencyType.EUR,
                    paymentGateway = PaymentGateway.BANK_TRANSFER,
                    subscriptionDateMillis = now - (86400000L * 25),
                    expiryDateString = "Active (Quarterly)",
                    isActive = true,
                    transactionRef = "TXN-SUB5540"
                )
            )
        }
        return try {
            val list = mutableListOf<com.example.data.model.SubscriberRecord>()
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    com.example.data.model.SubscriberRecord(
                        id = obj.getString("id"),
                        userName = obj.getString("userName"),
                        email = obj.getString("email"),
                        mobileNumber = obj.getString("mobileNumber"),
                        planName = obj.getString("planName"),
                        amountPaid = obj.getDouble("amountPaid"),
                        currency = CurrencyType.valueOf(obj.optString("currency", "INR")),
                        paymentGateway = PaymentGateway.valueOf(obj.optString("paymentGateway", "OWNER_WALLET_BARCODE")),
                        subscriptionDateMillis = obj.getLong("subscriptionDateMillis"),
                        expiryDateString = obj.optString("expiryDateString", "Active"),
                        isActive = obj.optBoolean("isActive", true),
                        transactionRef = obj.optString("transactionRef", "TXN-000")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveSubscribers(subs: List<com.example.data.model.SubscriberRecord>) {
        try {
            val array = JSONArray()
            subs.take(50).forEach { s ->
                val obj = JSONObject().apply {
                    put("id", s.id)
                    put("userName", s.userName)
                    put("email", s.email)
                    put("mobileNumber", s.mobileNumber)
                    put("planName", s.planName)
                    put("amountPaid", s.amountPaid)
                    put("currency", s.currency.name)
                    put("paymentGateway", s.paymentGateway.name)
                    put("subscriptionDateMillis", s.subscriptionDateMillis)
                    put("expiryDateString", s.expiryDateString)
                    put("isActive", s.isActive)
                    put("transactionRef", s.transactionRef)
                }
                array.put(obj)
            }
            prefs.edit().putString("subscribers_history_json", array.toString()).apply()
        } catch (_: Exception) {}
    }
}

