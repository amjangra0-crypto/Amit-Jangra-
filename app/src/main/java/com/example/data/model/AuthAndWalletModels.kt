package com.example.data.model

enum class AuthProvider(val displayName: String) {
    GMAIL("Google Gmail"),
    MOBILE_OTP("Mobile Number (SMS OTP)"),
    GUEST("Guest Account")
}

enum class UserRole(val badge: String) {
    OWNER("👑 App Owner"),
    VIP_SUBSCRIBER("💎 VIP Subscriber"),
    CREATOR("🌟 Anime Creator"),
    GUEST("👤 Guest User")
}

data class UserProfile(
    val userId: String = "owner_amjangra0",
    val displayName: String = "Aman Jangra",
    val email: String? = "amjangra0@gmail.com",
    val phoneNumber: String? = "+91 98765 43210",
    val avatarDrawableName: String = "char_shonen_hero",
    val customAvatarUri: String? = null,
    val bio: String = "Lead Director & Founder at Anime Studio AI. Building next-generation anime narratives with AI.",
    val creatorSpecialty: String = "Anime Film Director",
    val isOwner: Boolean = true,
    val authProvider: AuthProvider = AuthProvider.GMAIL,
    val subscriptionPlan: String = "VIP OWNER LIFETIME",
    val subscriptionExpiry: String = "Lifetime Unlimited",
    val isLoggedIn: Boolean = true,
    val joinedDate: String = "Jan 2026",
    val projectsCreated: Int = 18,
    val videosRendered: Int = 42,
    val voiceMinutesUsed: Int = 320,
    val notificationEnabled: Boolean = true,
    val autoSyncDubbing: Boolean = true
)

enum class CurrencyType(val code: String, val symbol: String, val title: String, val exchangeToInr: Double) {
    INR("INR", "₹", "Indian Rupee", 1.0),
    USD("USD", "$", "US Dollar", 86.5),
    EUR("EUR", "€", "Euro", 94.0),
    GBP("GBP", "£", "British Pound", 110.0),
    JPY("JPY", "¥", "Japanese Yen", 0.58)
}

data class CurrencyWallet(
    val currency: CurrencyType,
    val balance: Double,
    val totalReceived: Double,
    val totalWithdrawn: Double
)

// Owner in-app Wallet Constants
object OwnerWalletConstants {
    const val OWNER_WALLET_ID = "WALLET_OWNER_AMJANGRA0_9999"
    const val OWNER_DISPLAY_NAME = "Aman Jangra (Anime Studio Owner)"
    const val OWNER_UPI_ID = "amjangra0@okhdfcbank"
    const val OWNER_PAYTM_NUMBER = "9876543210"
    const val OWNER_PAYPAL_EMAIL = "amjangra0@gmail.com"
}

enum class BankTransferProtocol(val title: String, val description: String) {
    RTGS("RTGS", "Real Time Gross Settlement (Instant, for high value settlements)"),
    NEFT("NEFT", "National Electronic Funds Transfer (Batch settlements)"),
    SWIFT("SWIFT", "International SWIFT / BIC Wire (Multi-currency global wire)")
}

enum class PaymentGateway(val title: String, val description: String) {
    OWNER_WALLET_BARCODE("Owner Wallet Barcode / QR", "Scan Owner's in-app Wallet Barcode & QR Code"),
    OWNER_WALLET_ID("Owner Wallet ID Transfer", "Direct transfer to Owner in-app Wallet ID"),
    CARD("Debit / Credit Card (Global)", "Visa, MasterCard, RuPay, Amex & Discover"),
    UPI_GPAY("Google Pay (GPay)", "Instant UPI via Google Pay"),
    UPI_PHONEPE("PhonePe", "Instant UPI via PhonePe"),
    PAYTM("Paytm Wallet / UPI", "Instant Paytm Wallet & UPI payment"),
    UPI_GENERIC("UPI ID / QR Code", "Scan & Pay with any UPI app"),
    PAYPAL("PayPal", "Fast global checkout for USD & EUR"),
    BANK_TRANSFER("Direct Bank Transfer", "Direct NEFT / RTGS / SWIFT wire");

    val displayName: String get() = title
    val iconEmoji: String
        get() = when (this) {
            OWNER_WALLET_BARCODE -> "📷"
            OWNER_WALLET_ID -> "👑"
            CARD -> "💳"
            UPI_GPAY -> "🟢"
            UPI_PHONEPE -> "🟣"
            PAYTM -> "🔵"
            UPI_GENERIC -> "📲"
            PAYPAL -> "🅿️"
            BANK_TRANSFER -> "🏦"
        }
}

enum class TransactionType(val title: String, val isCredit: Boolean) {
    SUBSCRIPTION_DEPOSIT("Subscription Received into Owner Wallet", true),
    SUBSCRIPTION_CARD("Global Card Payment into Owner Wallet", true),
    WITHDRAWAL_BANK("Bank Transfer Withdrawal", false),
    WITHDRAWAL_RTGS("Bank RTGS Instant Settlement", false),
    WITHDRAWAL_NEFT("Bank NEFT Electronic Transfer", false),
    WITHDRAWAL_SWIFT("International SWIFT Wire", false),
    WITHDRAWAL_PAYTM("Paytm Wallet Withdrawal", false),
    WITHDRAWAL_UPI("Instant UPI Withdrawal", false),
    WITHDRAWAL_PHONEPE("PhonePe Withdrawal", false),
    WITHDRAWAL_GPAY("Google Pay Withdrawal", false),
    WITHDRAWAL_PAYPAL("PayPal Withdrawal", false),
    WITHDRAWAL_BARCODE_SCAN("QR/Barcode Scanned Withdrawal", false),
    WALLET_EXCHANGE_TRANSFER("Wallet Currency Exchange", true),
    SECURITY_CREDENTIAL_CHANGE("Owner Security Credentials Modified", false)
}

data class WalletTransaction(
    val id: String,
    val timestamp: Long,
    val type: TransactionType,
    val currency: CurrencyType,
    val amount: Double,
    val targetAccountOrUser: String,
    val description: String,
    val status: String = "Completed",
    val referenceId: String
)

data class OwnerBankAccount(
    val holderName: String = "Aman Jangra",
    val bankName: String = "HDFC Bank",
    val accountNumber: String = "50100482194812",
    val ifscCode: String = "HDFC0001234",
    val accountType: String = "Savings", // "Savings", "Current", "Business"
    val branchName: String = "Connaught Place, New Delhi",
    val swiftBic: String = "HDFCINBB",
    val upiId: String = "amjangra0@okhdfcbank",
    val isConnected: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val maskedAccountNumber: String
        get() = if (accountNumber.length > 4) {
            "•••• •••• •••• " + accountNumber.takeLast(4)
        } else {
            accountNumber
        }
}

data class CardPaymentDetails(
    val cardholderName: String = "",
    val cardNumber: String = "",
    val expiryMonthYear: String = "",
    val cvv: String = "",
    val cardBrand: String = "Visa", // "Visa", "MasterCard", "Amex", "RuPay", "Discover"
    val billingCountry: String = "United States",
    val saveCard: Boolean = true
)

data class SubscriberRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val userName: String,
    val email: String,
    val mobileNumber: String,
    val planName: String,
    val amountPaid: Double,
    val currency: CurrencyType = CurrencyType.INR,
    val paymentGateway: PaymentGateway = PaymentGateway.OWNER_WALLET_BARCODE,
    val subscriptionDateMillis: Long = System.currentTimeMillis(),
    val expiryDateString: String = "Active (Annual)",
    val isActive: Boolean = true,
    val transactionRef: String = "TXN-" + java.util.UUID.randomUUID().toString().uppercase().take(8)
)

