package com.example.localization

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Absolute Core Global Architecture Engine for Location & Dynamic Localization.
 * Implements the 4 Unbreakable Localization Laws:
 * 1. Out-of-the-box Default: English.
 * 2. GPS Regional Auto-Detection: Maps runtime hardware GPS coordinates or regional country identifier into [ACTIVE_GPS_COUNTRY]
 *    and maps to primary official regional language.
 * 3. Absolute Manual Override Freedom: [MANUAL_LANGUAGE_SELECTION] possesses ultimate execution priority,
 *    flushes translation cache, and force-translates the entire app.
 * 4. Zero-Bleed Translation Purity: Pure target language script without cross-contamination.
 */
class GpsLocationManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val prefs = context.getSharedPreferences("global_architecture_prefs", Context.MODE_PRIVATE)

    companion object {
        const val BASE_DEFAULT_LANGUAGE = "English"

        private const val KEY_MANUAL_LANG = "key_manual_language_override"
        private const val KEY_LAST_GPS_COUNTRY = "key_last_gps_country"
        private const val KEY_LAST_GPS_LAT = "key_last_gps_lat"
        private const val KEY_LAST_GPS_LNG = "key_last_gps_lng"

        @Volatile
        private var INSTANCE: GpsLocationManager? = null

        fun getInstance(context: Context): GpsLocationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GpsLocationManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Dynamic App Data Plugins
    private val _activeGpsCountry = MutableStateFlow(
        prefs.getString(KEY_LAST_GPS_COUNTRY, null) ?: "Global (Detecting...)"
    )
    val activeGpsCountry: StateFlow<String> = _activeGpsCountry.asStateFlow()

    private val _activeCoordinates = MutableStateFlow<Pair<Double, Double>?>(
        if (prefs.contains(KEY_LAST_GPS_LAT) && prefs.contains(KEY_LAST_GPS_LNG)) {
            Pair(
                prefs.getFloat(KEY_LAST_GPS_LAT, 0f).toDouble(),
                prefs.getFloat(KEY_LAST_GPS_LNG, 0f).toDouble()
            )
        } else null
    )
    val activeCoordinates: StateFlow<Pair<Double, Double>?> = _activeCoordinates.asStateFlow()

    private val _manualLanguageSelection = MutableStateFlow<String?>(
        prefs.getString(KEY_MANUAL_LANG, null)
    )
    val manualLanguageSelection: StateFlow<String?> = _manualLanguageSelection.asStateFlow()

    // Calculated effective language implementing Law 1, Law 2, Law 3
    private val _effectiveLanguage = MutableStateFlow(computeEffectiveLanguage())
    val effectiveLanguage: StateFlow<String> = _effectiveLanguage.asStateFlow()

    private val _currencySymbol = MutableStateFlow(computeCurrencySymbol(_effectiveLanguage.value))
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _currencyCode = MutableStateFlow(computeCurrencyCode(_effectiveLanguage.value))
    val currencyCode: StateFlow<String> = _currencyCode.asStateFlow()

    init {
        // Trigger initial background GPS lookup on startup
        refreshGpsLocation()
    }

    /**
     * Resolves the effective language following unbreakable precedence:
     * 1. [MANUAL_LANGUAGE_SELECTION] (Ultimate override priority)
     * 2. [ACTIVE_GPS_COUNTRY] (Mapped to regional official language)
     * 3. [BASE_DEFAULT_LANGUAGE] ("English")
     */
    private fun computeEffectiveLanguage(): String {
        val manual = _manualLanguageSelection.value
        if (!manual.isNullOrBlank()) {
            return manual
        }

        val country = _activeGpsCountry.value
        if (country.isNotBlank() && !country.contains("Detecting") && !country.contains("Global")) {
            return mapCountryToLanguage(country)
        }

        // Try device system locale as intermediate detection if GPS not yet resolved
        val sysLang = Locale.getDefault().language.lowercase()
        return when (sysLang) {
            "hi" -> "Hindi"
            "es" -> "Spanish"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "fr" -> "French"
            "de" -> "German"
            "zh" -> "Chinese"
            "ar" -> "Arabic"
            "ru" -> "Russian"
            "pt" -> "Portuguese"
            "id" -> "Indonesian"
            else -> BASE_DEFAULT_LANGUAGE
        }
    }

    /**
     * Maps country name or ISO code to official primary regional language
     */
    fun mapCountryToLanguage(countryNameOrCode: String): String {
        val normalized = countryNameOrCode.trim().lowercase()
        return when {
            normalized.contains("india") || normalized == "in" || normalized.contains("bharat") -> "Hindi"
            normalized.contains("japan") || normalized == "jp" || normalized.contains("nippon") -> "Japanese"
            normalized.contains("korea") || normalized == "kr" -> "Korean"
            normalized.contains("spain") || normalized == "es" ||
                    normalized.contains("mexico") || normalized == "mx" ||
                    normalized.contains("argentina") || normalized == "ar" ||
                    normalized.contains("colombia") || normalized == "co" ||
                    normalized.contains("chile") || normalized == "cl" ||
                    normalized.contains("peru") || normalized == "pe" -> "Spanish"
            normalized.contains("germany") || normalized == "de" ||
                    normalized.contains("austria") || normalized == "at" ||
                    normalized.contains("switzerland") || normalized == "ch" -> "German"
            normalized.contains("france") || normalized == "fr" ||
                    normalized.contains("belgium") || normalized == "be" -> "French"
            normalized.contains("china") || normalized == "cn" ||
                    normalized.contains("taiwan") || normalized == "tw" ||
                    normalized.contains("hong kong") || normalized == "hk" -> "Chinese"
            normalized.contains("saudi") || normalized == "sa" ||
                    normalized.contains("emirates") || normalized == "ae" ||
                    normalized.contains("egypt") || normalized == "eg" ||
                    normalized.contains("qatar") || normalized == "qa" -> "Arabic"
            normalized.contains("russia") || normalized == "ru" ||
                    normalized.contains("belarus") || normalized == "by" ||
                    normalized.contains("kazakhstan") || normalized == "kz" -> "Russian"
            normalized.contains("brazil") || normalized == "br" ||
                    normalized.contains("portugal") || normalized == "pt" -> "Portuguese"
            normalized.contains("indonesia") || normalized == "id" -> "Indonesian"
            else -> BASE_DEFAULT_LANGUAGE
        }
    }

    /**
     * Resolves regional currency symbol for Financial Escrow Payout Routing
     */
    fun computeCurrencySymbol(language: String): String {
        return when (language.trim().lowercase()) {
            "hindi" -> "₹"
            "japanese" -> "¥"
            "korean" -> "₩"
            "spanish", "german", "french", "portuguese" -> "€"
            "chinese" -> "¥"
            "arabic" -> "د.إ"
            "russian" -> "₽"
            "indonesian" -> "Rp"
            else -> "$"
        }
    }

    /**
     * Resolves ISO currency code
     */
    fun computeCurrencyCode(language: String): String {
        return when (language.trim().lowercase()) {
            "hindi" -> "INR"
            "japanese" -> "JPY"
            "korean" -> "KRW"
            "spanish", "german", "french", "portuguese" -> "EUR"
            "chinese" -> "CNY"
            "arabic" -> "AED"
            "russian" -> "RUB"
            "indonesian" -> "IDR"
            else -> "USD"
        }
    }

    /**
     * Absolute Manual Override: Sets the manual language, flushes cache, and forces UI translation.
     */
    fun setManualLanguageOverride(language: String?) {
        val cleanLang = language?.trim()
        if (cleanLang.isNullOrBlank() || cleanLang.equals("auto", ignoreCase = true) || cleanLang.equals("gps", ignoreCase = true)) {
            // Reset manual override -> return to GPS detection
            _manualLanguageSelection.value = null
            prefs.edit().remove(KEY_MANUAL_LANG).apply()
        } else {
            _manualLanguageSelection.value = cleanLang
            prefs.edit().putString(KEY_MANUAL_LANG, cleanLang).apply()
        }

        // Flush and recompute effective language
        val updatedLang = computeEffectiveLanguage()
        _effectiveLanguage.value = updatedLang
        _currencySymbol.value = computeCurrencySymbol(updatedLang)
        _currencyCode.value = computeCurrencyCode(updatedLang)

        // Apply to Android System LocaleManager & Resources
        try {
            AppLocaleManager.applyLocale(context, updatedLang)
        } catch (_: Throwable) {}

        Log.d("GpsLocationManager", "Language updated: effective=$updatedLang, manual=$cleanLang, gps=${_activeGpsCountry.value}")
    }

    /**
     * Queries hardware GPS Location and Network location, then geocodes country.
     */
    @SuppressLint("MissingPermission")
    fun refreshGpsLocation() {
        scope.launch {
            try {
                val hasFine = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasFine && !hasCoarse) {
                    Log.d("GpsLocationManager", "Location permission not granted, checking telephony / locale fallback")
                    detectFallbackCountry()
                    return@launch
                }

                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                if (locationManager == null) {
                    detectFallbackCountry()
                    return@launch
                }

                var bestLocation: Location? = null
                val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)

                for (provider in providers) {
                    try {
                        if (locationManager.isProviderEnabled(provider)) {
                            val loc = locationManager.getLastKnownLocation(provider)
                            if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                                bestLocation = loc
                            }
                        }
                    } catch (_: SecurityException) {}
                }

                if (bestLocation != null) {
                    val lat = bestLocation.latitude
                    val lng = bestLocation.longitude
                    _activeCoordinates.value = Pair(lat, lng)
                    prefs.edit()
                        .putFloat(KEY_LAST_GPS_LAT, lat.toFloat())
                        .putFloat(KEY_LAST_GPS_LNG, lng.toFloat())
                        .apply()

                    // Geocode coordinates to country
                    resolveCountryFromCoordinates(lat, lng)
                } else {
                    detectFallbackCountry()
                }
            } catch (e: Exception) {
                Log.w("GpsLocationManager", "Error querying GPS location: ${e.message}")
                detectFallbackCountry()
            }
        }
    }

    private suspend fun resolveCountryFromCoordinates(lat: Double, lng: Double) {
        withContext(Dispatchers.IO) {
            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(lat, lng, 1) { addresses ->
                            handleAddressResult(addresses.firstOrNull())
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        handleAddressResult(addresses?.firstOrNull())
                    }
                } else {
                    detectFallbackCountry()
                }
            } catch (e: Exception) {
                Log.w("GpsLocationManager", "Geocoder failed: ${e.message}")
                detectFallbackCountry()
            }
        }
    }

    private fun handleAddressResult(address: Address?) {
        val detectedCountry = address?.countryName ?: address?.countryCode ?: run {
            detectFallbackCountry()
            return
        }

        applyGpsCountry(detectedCountry)
    }

    private fun detectFallbackCountry() {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val simCountry = tm?.simCountryIso?.uppercase()
            val netCountry = tm?.networkCountryIso?.uppercase()
            val fallbackCode = when {
                !simCountry.isNullOrBlank() -> simCountry
                !netCountry.isNullOrBlank() -> netCountry
                else -> Locale.getDefault().country.uppercase()
            }

            val countryName = Locale("", fallbackCode).displayCountry.ifBlank {
                when (fallbackCode) {
                    "IN" -> "India"
                    "US" -> "United States"
                    "JP" -> "Japan"
                    "ES" -> "Spain"
                    else -> "Global / Default"
                }
            }

            applyGpsCountry(countryName)
        } catch (_: Throwable) {
            applyGpsCountry("Global / Default")
        }
    }

    private fun applyGpsCountry(country: String) {
        _activeGpsCountry.value = country
        prefs.edit().putString(KEY_LAST_GPS_COUNTRY, country).apply()

        // Recompute effective language if user has not manually set an override (Law 3)
        if (_manualLanguageSelection.value.isNullOrBlank()) {
            val autoLang = mapCountryToLanguage(country)
            _effectiveLanguage.value = autoLang
            _currencySymbol.value = computeCurrencySymbol(autoLang)
            _currencyCode.value = computeCurrencyCode(autoLang)

            try {
                AppLocaleManager.applyLocale(context, autoLang)
            } catch (_: Throwable) {}
        }
    }
}
