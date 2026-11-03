# One-Time Google Maps Permission & Global Architecture Localization Engine with Maps Grounding

Deliver a complete, high-fidelity implementation of one-time Google Maps/GPS permission on installation, a strict Global Architecture Localization Engine obeying the 4 Unbreakable Laws (English baseline, hardware GPS auto-detection, absolute manual override freedom, zero-bleed purity), and Google Maps Grounding via `models/gemini-3.8-flash` with the `googleMaps` tool.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed **"मैं ये सभी app में चाहता हूं"** (I want all of these across the app). Every component, onboarding flow, localization layer, and Maps Grounding capability will be fully integrated.

- **Confirmed Decision 1: Installation Permission Flow**: A dedicated, welcoming one-time onboarding card on first install explaining GPS location & Maps features with direct system permission request launcher and persistence in DataStore so it is never re-prompted automatically.
- **Confirmed Decision 2: Google Maps Data Grounding Scope**: Deep integration using `models/gemini-3.8-flash` with the `googleMaps` tool in TrendPulse regional scouting, real-world anime scene location blueprints, and Studio Creation Hub suggestions.
- **Confirmed Decision 3: Localization Hierarchy & Zero-Bleed Purity**:
  - `Base Baseline`: English out-of-the-box.
  - `GPS Auto-Detection`: Runtime GPS hardware coordinates/country mapped to official regional language (e.g., India -> Hindi `हिंदी`, Japan -> Japanese `日本語`, etc.).
  - `Manual Override Freedom`: User's manual selection in settings or quick-switch bar instantly overrides GPS, flushes cache, and forces 100% translation purity.
  - `Purity`: Zero mixing/bleeding of English or foreign scripts in translated components, creative scripts, and financial escrow payout cards with native currency formatting (`₹`, `$`, `¥`, `€`).

---

## 1. Overview & Core Concept

- **What It Does**:
  1. Requests Google Maps / GPS location permission strictly once on installation via a transparent, beautiful M3 onboarding experience.
  2. Runs the **Absolute Core Global Architecture Engine**: automatically detects runtime hardware GPS coordinates, maps to the regional country (`[ACTIVE_GPS_COUNTRY]`), and instantly switches the application's entire UI, dialogs, blueprints, and payout cards into the regional language.
  3. Guarantees **Absolute Manual Override Freedom**: user selection (`[MANUAL_LANGUAGE_SELECTION]`) flushes translation cache and forces 100% pure script without cross-contamination.
  4. Integrates **Google Maps Grounding** via `models/gemini-3.8-flash` and the `googleMaps` tool for live place intelligence, anime landmark scene generation, and regional trend discovery.
- **Target Audience**: Global anime creators, directors, animators, and studio owners worldwide who want seamless native localization in their language and real-world Maps-grounded creative blueprints.
- **Key Value**: Frictionless onboarding, privacy-respecting one-time permission, culturally native zero-bleed localization, and intelligent location-aware generative anime workflows.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **One-Time Installation Permission Onboarding**:
   - On fresh install, the app displays a stylish, glassmorphic M3 dialog explaining Google Maps GPS Studio Localization and Voice Dubbing permissions.
   - User grants permission -> system dialog opens -> DataStore permanently records `already_requested_location_on_install = true`.
   - App automatically receives hardware GPS coordinates, resolves country (e.g. India), logs `[ACTIVE_GPS_COUNTRY: India]`, and auto-sets the pure native language (Hindi).
2. **Instant Manual Override & Quick Switcher**:
   - An accessible Floating Quick-Language Switcher badge and dedicated Settings panel allow switching to any of the 12+ supported global languages.
   - Selecting a language immediately invalidates cached strings, switches the framework `LocaleManager`, and renders 100% pure script.
3. **Google Maps Grounded Studio Scouting & Scene Blueprints**:
   - In TrendPulse AI and Studio Creation Hub, users can trigger "Maps-Grounded Location Scouting".
   - Gemini 3.8 Flash queries real-world landmarks and places (e.g. Akihabara Tokyo, Connaught Place New Delhi, Shibuya Sky, Gothic Quarter Barcelona) to generate rich, geographically accurate anime background scenes, lighting cues, and architectural references.
4. **Financial Escrow & Owner Payout Vaults**:
   - Payout cards dynamically format financial balances with native regional currency symbols (e.g., `₹` for India/Hindi, `$` for US/English, `¥` for Japan, `€` for Europe).

```
┌────────────────────────────────────────────────────────┐
│               App Launch & Initialization              │
└───────────────────────────┬────────────────────────────┘
                            │
               Has Onboarding Prompted?
             ┌──────────────┴──────────────┐
            No                            Yes
             │                             │
┌────────────▼──────────────┐  ┌───────────▼────────────┐
│ One-Time M3 Location &    │  │ Load Saved Preferences │
│ Maps Onboarding Dialog    │  └───────────┬────────────┘
└────────────┬──────────────┘              │
             │                             │
┌────────────▼─────────────────────────────▼────────────┐
│      Core Global Architecture Localization Engine      │
│  [BASE_DEFAULT_LANGUAGE: English]                     │
│  [ACTIVE_GPS_COUNTRY: Auto-detected via GPS]          │
│  [MANUAL_LANGUAGE_SELECTION: Ultimate Override]       │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│           Zero-Bleed Dynamic UI Matrix                 │
│  ├─ [ACTIVATED_COMPONENTS]: Native feature routing    │
│  ├─ [GLOBAL_UI_MATRIX]: 100% pure script across all   │
│  ├─ [CREATIVE_PRODUCTION_BLUEPRINT]: Scene scripts     │
│  └─ [FINANCIAL_PAYOUT_ROUTING]: Regional currency sync│
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ Gemini 3.8 Flash + Google Maps Grounding (`googleMaps`)│
│ Real-world anime scene scout & regional trend pulses   │
└────────────────────────────────────────────────────────┘
```

---

## 3. Key Product Decisions & Architecture

### Decision 1: One-Time Permission Enforcement
- **Chosen Approach**: Store `already_requested_location_on_install` in DataStore preferences + fallback SharedPreferences. The onboarding dialog requests `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` once. Subsequent app launches will never show an automatic pop-up.
- **Why**: Strictly conforms to user requirement and Google Play guidelines for least-intrusive permissions.

### Decision 2: GPS Hardware Auto-Detection with Geocoding Fallback
- **Chosen Approach**: A dedicated `GpsLocationManager` checks GPS provider / network provider for the latest coordinates, then resolves country via Android `Geocoder` or country code tables.
- **Why**: Works reliably on real devices and emulators, and safely defaults to English if location is unavailable or denied.

### Decision 3: Ultimate Manual Override & Zero-Bleed Translation Engine
- **Chosen Approach**: If `MANUAL_LANGUAGE_SELECTION` is set, GPS auto-switch is bypassed. `AppLocaleStrings.kt` provides 100% verified complete translations for all studio keys, preventing any mixed-language strings ("zero-bleed").
- **Why**: Gives the user total control while fulfilling the unbreakable localization laws.

### Decision 4: Gemini 3.8 Flash with Google Maps Grounding
- **Chosen Approach**: Update `GeminiApiService.kt` to target `gemini-3.8-flash` and include the `"googleMaps"` tool definition in the generateContent payload.
- **Why**: Meets the exact feature specification for Maps data grounding.

---

## 4. Technical Implementation Steps

1. **Manifest Permissions**: Add `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />` and `<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />` to `AndroidManifest.xml`.
2. **Permission Persistence**: Extend `PermissionManager.kt` and `PermissionPreferenceManager.kt` with location keys (`KEY_INSTALL_LOCATION_PROMPTED`, `KEY_USER_ALLOW_LOCATION`).
3. **GPS Location Engine (`GpsLocationManager.kt`)**: Implement GPS coordinate acquisition, geocoding to country, and mapping country to language code.
4. **Onboarding Dialog (`AppPermissionsOnboardingDialog.kt`)**: Add Google Maps & Location permission explanation card, one-time permission trigger, and language detection preview.
5. **Global Architecture Localization Integration**: Wire `AnimeViewModel.kt`, `LocaleManager.kt`, and `AppLocaleStrings.kt` to enforce:
   - Baseline English.
   - GPS country auto-switch.
   - Manual language override with memory flush.
   - Pure script zero-bleed string lookup and regional currency formatting (`₹`, `$`, `¥`, `€`).
6. **Gemini 3.8 Flash + Maps Grounding**: Update `GeminiApiService.kt` to support `gemini-3.8-flash` with the `googleMaps` tool for TrendPulse and Scene Location scouting.
7. **Verification**: Run `compile_applet` to ensure zero compilation errors and full build pass.
