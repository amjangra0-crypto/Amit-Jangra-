package com.example.data.model

import android.content.Context
import java.util.Locale

data class CountryCode(
    val isoCode: String,
    val name: String,
    val dialCode: String,
    val flagEmoji: String,
    val exampleNumber: String = "9876543210",
    val minDigits: Int = 7,
    val maxDigits: Int = 14,
    val primaryLanguage: String = "English",
    val languageCode: String = "en",
    val nativeLanguageName: String = "English",
    val defaultVoiceAccent: String = "Standard Anime (English)"
)

object CountryCodeProvider {
    val defaultCountry = CountryCode(
        isoCode = "IN",
        name = "India",
        dialCode = "+91",
        flagEmoji = "🇮🇳",
        exampleNumber = "9876543210",
        minDigits = 10,
        maxDigits = 10,
        primaryLanguage = "Hindi",
        languageCode = "hi",
        nativeLanguageName = "हिन्दी",
        defaultVoiceAccent = "Hindi Dub (Heroic Bollywood Anime)"
    )

    val countries: List<CountryCode> = listOf(
        // Popular / Top
        CountryCode("IN", "India", "+91", "🇮🇳", "9876543210", 10, 10, "Hindi", "hi", "हिन्दी", "Hindi Dub (Heroic Bollywood Anime)"),
        CountryCode("US", "United States", "+1", "🇺🇸", "2025550143", 10, 10, "English", "en", "English (US)", "American Heroic Action"),
        CountryCode("GB", "United Kingdom", "+44", "🇬🇧", "7911123456", 10, 11, "English", "en", "English (UK)", "British Aristocrat / Royal"),
        CountryCode("JP", "Japan", "+81", "🇯🇵", "9012345678", 10, 11, "Japanese", "ja", "日本語", "Tokyo Standard Anime (Japanese Cadence)"),
        CountryCode("KR", "South Korea", "+82", "🇰🇷", "1012345678", 9, 11, "Korean", "ko", "한국어", "Korean Dramatic (Manhwa Style)"),
        CountryCode("DE", "Germany", "+49", "🇩🇪", "15123456789", 10, 11, "German", "de", "Deutsch", "German Epic Anime Dub"),
        CountryCode("FR", "France", "+33", "🇫🇷", "612345678", 9, 10, "French", "fr", "Français", "French Elegant Anime"),
        CountryCode("ES", "Spain", "+34", "🇪🇸", "612345678", 9, 9, "Spanish", "es", "Español", "Spanish Passionate Hero"),
        CountryCode("MX", "Mexico", "+52", "🇲🇽", "5512345678", 10, 10, "Spanish", "es", "Español Latino", "Spanish Passionate Hero"),
        CountryCode("BR", "Brazil", "+55", "🇧🇷", "11987654321", 10, 11, "Portuguese", "pt", "Português", "Portuguese Energetic"),
        CountryCode("PT", "Portugal", "+351", "🇵🇹", "912345678", 9, 9, "Portuguese", "pt", "Português", "Portuguese Energetic"),
        CountryCode("RU", "Russia", "+7", "🇷🇺", "9123456789", 10, 10, "Russian", "ru", "Русский", "Russian Deep Cinematic"),
        CountryCode("CN", "China", "+86", "🇨🇳", "13812345678", 11, 11, "Chinese", "zh", "中文 (Mandarin)", "Chinese Cultivation Cadence"),
        CountryCode("AE", "United Arab Emirates", "+971", "🇦🇪", "501234567", 9, 9, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("SA", "Saudi Arabia", "+966", "🇸🇦", "512345678", 9, 9, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("EG", "Egypt", "+20", "🇪🇬", "1012345678", 10, 10, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("ID", "Indonesia", "+62", "🇮🇩", "81234567890", 9, 12, "Indonesian", "id", "Bahasa Indonesia", "Standard Anime"),
        CountryCode("IT", "Italy", "+39", "🇮🇹", "3123456789", 9, 11, "Italian", "it", "Italiano", "Italian Dramatic"),
        CountryCode("TR", "Turkey", "+90", "🇹🇷", "5321234567", 10, 10, "Turkish", "tr", "Türkçe", "Turkish Energetic"),
        CountryCode("VN", "Vietnam", "+84", "🇻🇳", "912345678", 9, 10, "Vietnamese", "vi", "Tiếng Việt", "Standard Anime"),
        CountryCode("TH", "Thailand", "+66", "🇹🇭", "812345678", 9, 10, "Thai", "th", "ไทย", "Standard Anime"),
        CountryCode("PH", "Philippines", "+63", "🇵🇭", "9171234567", 10, 10, "English", "en", "Filipino / English", "American Heroic Action"),
        CountryCode("CA", "Canada", "+1", "🇨🇦", "4165550198", 10, 10, "English", "en", "English (Canada)", "American Heroic Action"),
        CountryCode("AU", "Australia", "+61", "🇦🇺", "412345678", 9, 10, "English", "en", "English (Australia)", "American Heroic Action"),
        CountryCode("SG", "Singapore", "+65", "🇸🇬", "81234567", 8, 8, "English", "en", "English (Singapore)", "Standard Anime"),
        CountryCode("MY", "Malaysia", "+60", "🇲🇾", "123456789", 9, 10, "Indonesian", "ms", "Bahasa Melayu", "Standard Anime"),
        CountryCode("PK", "Pakistan", "+92", "🇵🇰", "3001234567", 10, 10, "Hindi", "ur", "اردو / Urdu", "Hindi Dub (Heroic Bollywood Anime)"),
        CountryCode("BD", "Bangladesh", "+880", "🇧🇩", "1712345678", 10, 10, "Hindi", "bn", "বাংলা / Bengali", "Hindi Dub (Heroic Bollywood Anime)"),
        CountryCode("NP", "Nepal", "+977", "🇳🇵", "9812345678", 10, 10, "Hindi", "ne", "नेपाली / Nepali", "Hindi Dub (Heroic Bollywood Anime)"),
        CountryCode("LK", "Sri Lanka", "+94", "🇱🇰", "712345678", 9, 9, "English", "si", "Sinhala / English", "Standard Anime"),
        CountryCode("NL", "Netherlands", "+31", "🇳🇱", "612345678", 9, 9, "German", "nl", "Nederlands", "German Epic Anime Dub"),
        CountryCode("CH", "Switzerland", "+41", "🇨🇭", "781234567", 9, 9, "German", "de", "Deutsch (Schweiz)", "German Epic Anime Dub"),
        CountryCode("SE", "Sweden", "+46", "🇸🇪", "701234567", 9, 9, "English", "sv", "Svenska", "British Aristocrat / Royal"),
        CountryCode("NO", "Norway", "+47", "🇳🇴", "41234567", 8, 8, "English", "no", "Norsk", "British Aristocrat / Royal"),
        CountryCode("DK", "Denmark", "+45", "🇩🇰", "20123456", 8, 8, "English", "da", "Dansk", "British Aristocrat / Royal"),
        CountryCode("FI", "Finland", "+358", "🇫🇮", "401234567", 9, 10, "English", "fi", "Suomi", "British Aristocrat / Royal"),
        CountryCode("PL", "Poland", "+48", "🇵🇱", "512345678", 9, 9, "English", "pl", "Polski", "Standard Anime"),
        CountryCode("BE", "Belgium", "+32", "🇧🇪", "470123456", 9, 9, "French", "fr", "Français / Nederlands", "French Elegant Anime"),
        CountryCode("AT", "Austria", "+43", "🇦🇹", "6641234567", 10, 11, "German", "de", "Deutsch (Österreich)", "German Epic Anime Dub"),
        CountryCode("IE", "Ireland", "+353", "🇮🇪", "851234567", 9, 9, "English", "en", "English (Ireland)", "British Aristocrat / Royal"),
        CountryCode("GR", "Greece", "+30", "🇬🇷", "6912345678", 10, 10, "English", "el", "Ελληνικά", "Standard Anime"),
        CountryCode("IL", "Israel", "+972", "🇮🇱", "501234567", 9, 9, "English", "he", "עברית", "Standard Anime"),
        CountryCode("QA", "Qatar", "+974", "🇶🇦", "33123456", 8, 8, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("KW", "Kuwait", "+965", "🇰🇼", "99123456", 8, 8, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("OM", "Oman", "+968", "🇴🇲", "91234567", 8, 8, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("BH", "Bahrain", "+973", "🇧🇭", "39123456", 8, 8, "Arabic", "ar", "العربية", "Arabic Classical Epic"),
        CountryCode("ZA", "South Africa", "+27", "🇿🇦", "821234567", 9, 9, "English", "en", "English (South Africa)", "Standard Anime"),
        CountryCode("NG", "Nigeria", "+234", "🇳🇬", "8031234567", 10, 10, "English", "en", "English (Nigeria)", "Standard Anime"),
        CountryCode("KE", "Kenya", "+254", "🇰🇪", "712345678", 9, 9, "English", "sw", "Swahili / English", "Standard Anime"),
        CountryCode("GH", "Ghana", "+233", "🇬🇭", "241234567", 9, 9, "English", "en", "English (Ghana)", "Standard Anime"),
        CountryCode("NZ", "New Zealand", "+64", "🇳🇿", "211234567", 8, 10, "English", "en", "English (NZ)", "Standard Anime"),
        CountryCode("AR", "Argentina", "+54", "🇦🇷", "91123456789", 10, 11, "Spanish", "es", "Español (Argentina)", "Spanish Passionate Hero"),
        CountryCode("CL", "Chile", "+56", "🇨🇱", "912345678", 9, 9, "Spanish", "es", "Español (Chile)", "Spanish Passionate Hero"),
        CountryCode("CO", "Colombia", "+57", "🇨🇴", "3001234567", 10, 10, "Spanish", "es", "Español (Colombia)", "Spanish Passionate Hero"),
        CountryCode("PE", "Peru", "+51", "🇵🇪", "912345678", 9, 9, "Spanish", "es", "Español (Perú)", "Spanish Passionate Hero")
    )

    fun search(query: String): List<CountryCode> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return countries
        return countries.filter {
            it.name.lowercase().contains(q) ||
            it.dialCode.contains(q) ||
            it.isoCode.lowercase().contains(q) ||
            it.primaryLanguage.lowercase().contains(q) ||
            it.nativeLanguageName.lowercase().contains(q)
        }
    }

    fun findByDialCode(dialCode: String): CountryCode? {
        val cleaned = if (!dialCode.startsWith("+")) "+$dialCode" else dialCode
        return countries.firstOrNull { it.dialCode == cleaned }
    }

    fun findByIso(iso: String): CountryCode? {
        return countries.firstOrNull { it.isoCode.equals(iso, ignoreCase = true) }
    }

    fun findByLanguage(languageName: String): CountryCode? {
        return countries.firstOrNull {
            it.primaryLanguage.equals(languageName, ignoreCase = true) ||
            it.languageCode.equals(languageName, ignoreCase = true)
        }
    }

    /**
     * Automatically detects user's device/country when downloaded from Play Store
     * or any app store platform based on Locale.getDefault().
     */
    fun detectDeviceCountry(context: Context? = null): CountryCode {
        val defaultLocale = Locale.getDefault()
        val countryIso = defaultLocale.country
        val languageCode = defaultLocale.language

        // 1. Match by country ISO (e.g. "IN", "JP", "US", "DE")
        if (!countryIso.isNullOrBlank()) {
            val matched = findByIso(countryIso)
            if (matched != null) return matched
        }

        // 2. Match by language code (e.g. "hi", "ja", "ko", "de", "fr", "es", "ru", "zh", "ar")
        if (!languageCode.isNullOrBlank()) {
            val matchedLang = countries.firstOrNull { it.languageCode.equals(languageCode, ignoreCase = true) }
            if (matchedLang != null) return matchedLang
        }

        // 3. Fallback to default
        return defaultCountry
    }
}
