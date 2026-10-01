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

enum class PaymentGateway(val title: String, val description: String) {
    CARD("Debit / Credit Card (Global)", "Visa, MasterCard, RuPay, Amex & Discover"),
    UPI_GPAY("Google Pay (GPay)", "Instant UPI via Google Pay"),
    UPI_PHONEPE("PhonePe", "Instant UPI via PhonePe"),
    UPI_GENERIC("UPI ID / QR Code", "Scan & Pay with any UPI app"),
    PAYPAL("PayPal", "Fast global checkout for USD & EUR"),
    BANK_TRANSFER("Direct Bank Transfer", "Direct NEFT / IMPS / SWIFT wire");

    val displayName: String get() = title
    val iconEmoji: String
        get() = when (this) {
            CARD -> "💳"
            UPI_GPAY -> "🟢"
            UPI_PHONEPE -> "🟣"
            UPI_GENERIC -> "📲"
            PAYPAL -> "🅿️"
            BANK_TRANSFER -> "🏦"
        }
}

enum class TransactionType(val title: String, val isCredit: Boolean) {
    SUBSCRIPTION_DEPOSIT("Subscription Received", true),
    SUBSCRIPTION_CARD("Global Card Payment", true),
    WITHDRAWAL_BANK("Bank Transfer Withdrawal", false),
    WITHDRAWAL_PHONEPE("PhonePe Withdrawal", false),
    WITHDRAWAL_GPAY("Google Pay Withdrawal", false),
    WITHDRAWAL_PAYPAL("PayPal Withdrawal", false),
    WALLET_EXCHANGE_TRANSFER("Wallet Currency Exchange", true)
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
