<div align="center">

# 🌦️ Weather App

**An offline-first Android weather app built with Kotlin & Jetpack Compose**

Real-time forecasts • Offline caching • Firebase authentication • Cloud-synced profiles • Interactive charts

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-24-brightgreen?style=for-the-badge)](https://developer.android.com/studio)
[![License](https://img.shields.io/badge/License-Not%20Specified-lightgrey?style=for-the-badge)](#-license)

[Screenshots](#-screenshots) • [Features](#-features) • [Tech Stack](#-tech-stack) • [Installation](#-installation--setup) • [Download](#-download-apk)

</div>

---

## 📖 Overview

**Weather App** is a native Android application that delivers real-time weather conditions, hourly forecasts, and air quality data for any city in the world. It is built **offline-first** — every successful API response is cached locally with **Room**, so previously searched cities remain viewable without an internet connection.

The app follows a clean **MVVM + Repository** architecture, uses **Hilt** for dependency injection, and is written entirely in **Kotlin** with **Jetpack Compose** and **Material 3**. User accounts are backed by **Firebase Authentication** (email/password and Google Sign-In) with profile data synced through **Cloud Firestore**, while profile pictures are hosted on **Supabase Storage**.

---

## ✨ Features

- 🔍 **City search with live suggestions** — autocomplete powered by the Geoapify Geocoding API, debounced as the user types.
- 🌡️ **Real-time current weather** — temperature, min/max, humidity, pressure, condition, and icon via the OpenWeatherMap API.
- 🌫️ **Air Quality Index (AQI)** — fetched from OpenWeatherMap's Air Pollution API for the searched coordinates.
- ⏱️ **Hourly / multi-point forecast** — rendered as an interactive line chart (MPAndroidChart) with a custom marker view showing temperature and time.
- 📴 **Offline-first caching** — current weather, forecast data, and search history are persisted with Room and instantly shown while fresh data loads in the background.
- 📡 **Live network status bar** — a real-time connectivity observer (built on `ConnectivityManager`) shows a "Connected / No Internet" banner as the network changes.
- ❤️ **Favorites / liked cities** — save cities and revisit their cached weather from a dedicated tab.
- 🔐 **Authentication** — sign up / log in with email & password, or one-tap **Google Sign-In** via the Credential Manager API, backed by Firebase Auth.
- 👤 **User profile management** — update display name and profile photo (uploaded to Supabase Storage), synced live through Cloud Firestore snapshot listeners.
- 🌗 **Bottom navigation** — Favorites, Home, and Profile tabs in a single-activity Compose UI.
- 💫 **Splash screen** — implemented with the AndroidX Core SplashScreen API.

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Toolkit** | Jetpack Compose, Material 3 |
| **Architecture** | MVVM + Repository pattern, single-Activity |
| **Dependency Injection** | Hilt |
| **Navigation** | Navigation Compose (type-safe routes via `kotlinx.serialization`) |
| **Networking** | Retrofit2 + Gson |
| **Local Database** | Room |
| **Async** | Kotlin Coroutines & Flow (`StateFlow`, `debounce`, `callbackFlow`) |
| **Auth** | Firebase Authentication (Email/Password + Google Sign-In) |
| **Cloud Database** | Cloud Firestore |
| **Cloud Storage** | Supabase Storage (profile images) |
| **Image Loading** | Coil 3 |
| **Charts** | MPAndroidChart |
| **Splash Screen** | AndroidX Core SplashScreen |
| **Build System** | Gradle Kotlin DSL, Version Catalog (`libs.versions.toml`) |

---

## 🏗️ Architecture

The app follows a layered **MVVM (Model–View–ViewModel)** architecture with a Repository layer mediating between remote/local data sources and the UI:

```
┌─────────────────────────────────────────────┐
│                 Presentation                 │
│   Composable Screens  +  ViewModels          │
│   (HomeViewmodel, AuthViewModel)             │
│   exposes StateFlow<UiState> to the UI       │
└───────────────────────┬─────────────────────┘
                         │
┌───────────────────────▼─────────────────────┐
│                   Domain                     │
│   Repository interfaces + Domain models      │
│   (WeatherRepository, NetworkConnectivity    │
│    Observer)                                 │
└───────────────────────┬─────────────────────┘
                         │
┌───────────────────────▼─────────────────────┐
│                    Data                      │
│  ┌───────────────┐   ┌──────────────────┐    │
│  │  Remote (API)  │   │  Local (Room DB)  │    │
│  │ Retrofit +     │   │ MainDao /         │    │
│  │ Gson services  │   │ MainDatabase      │    │
│  └───────────────┘   └──────────────────┘    │
│           Mappers → Domain models             │
└───────────────────────────────────────────────┘
```

**Key architectural points confirmed in the codebase:**
- **Dependency Injection:** a single Hilt module (`di/HiltModule.kt`) provides two named `Retrofit` instances (OpenWeatherMap base URL and Geoapify base URL), all API services, the `Room` database/DAO, a singleton `CoroutineScope`, and the network connectivity observer.
- **Offline-first flow:** `HomeViewmodel` first emits any cached `WeatherDataEntity` from Room for instant UI feedback, then concurrently fetches fresh weather, AQI, and forecast data via `async`/`awaitAll`, and finally upserts the fresh result back into Room.
- **Data mapping:** dedicated mapper files (`data/mapper/`) convert network DTOs → domain models → Room entities, keeping each layer decoupled.
- **Navigation:** a single `NavHost` with type-safe `@Serializable` routes (`Routes.AuthScreen`, `Routes.HomeScreen`, `Routes.ForecastScreen(city)`, etc.), with the start destination decided by the current `FirebaseAuth` session state.

---

## 📁 Project Structure

```
WeatherApp/
├── app/
│   ├── google-services.json
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/abhishek/gupta/weatherapp/
│       │       ├── MainActivity.kt
│       │       ├── MyApplication.kt              # @HiltAndroidApp entry point
│       │       ├── data/
│       │       │   ├── converter/                # Uri ↔ ByteArray & misc extensions
│       │       │   ├── local/
│       │       │   │   ├── MainDao.kt             # Room DAO (weather / forecast / search history)
│       │       │   │   ├── MainDatabase.kt        # Room database
│       │       │   │   ├── entity/                # WeatherDataEntity, ForecastEntity, PreviousSearchCityDataEntity
│       │       │   │   └── typeConverter/         # Room type converters
│       │       │   ├── mapper/                    # DTO ⇄ Domain ⇄ Entity mappers
│       │       │   ├── remote/
│       │       │   │   ├── api/                   # WeatherApiService, HourlyApiService, AirPollutionApiService, GeoapifyApiService
│       │       │   │   ├── dto/                    # Retrofit response models
│       │       │   │   └── supabase/               # SupabaseClientProvider (Storage)
│       │       │   └── repositoryImpl/             # WeatherRepositoryImpl, NetworkConnectivityObserverImpl
│       │       ├── di/
│       │       │   └── HiltModule.kt               # Retrofit, Room, Repository, Scope providers
│       │       ├── domain/
│       │       │   ├── domainModel/                # DomainWeatherData, DomainForecastData, DomainAqiData, DomainSuggestedCity
│       │       │   └── repository/                 # WeatherRepository & NetworkConnectivityObserver interfaces
│       │       ├── presentation/
│       │       │   ├── authScreens/                # AuthScreen, LogInScreen, SignUpScreen, AuthViewModel
│       │       │   ├── homeScreen/                 # HomeScreen, HomeViewmodel, ForecastChartUI
│       │       │   ├── navigation/                 # Navigation.kt, Routes.kt
│       │       │   ├── theme/                      # Color.kt, Theme.kt, Type.kt
│       │       │   └── utilsScreens/                # HomePage, FavPage, ProfilePage, SuggestedCard, HourlyForecastItem, WeatherDetailItem
│       │       └── utils/                          # AppUtils (API keys/base URL), NetworkStatusBar
│       └── res/                                    # drawables, mipmaps, values, xml
├── build.gradle.kts                                 # Root Gradle config (Hilt plugin, GMS plugin, etc.)
├── settings.gradle.kts                               # Single module: `:app` — repos: google, mavenCentral, JitPack
├── gradle/libs.versions.toml                         # Version catalog
└── gradle.properties
```

> The project is a **single-module** Android app (`settings.gradle.kts` only includes `:app`), internally organized by Clean-Architecture-style layers (`data` / `domain` / `presentation`).

---

## 📦 Dependencies

<details>
<summary><b>Click to expand full dependency list & rationale</b></summary>

| Library | Version | Purpose |
|---|---|---|
| `androidx.compose.bom` | 2026.02.01 | Compose Bill-of-Materials for consistent Compose library versions |
| `androidx.compose.material3` | 1.4.0 | Material 3 components and theming |
| `androidx.navigation:navigation-compose` | 2.9.3 | In-app navigation graph for Compose screens |
| `androidx.hilt:hilt-navigation-compose` | 1.2.0 | Injects Hilt ViewModels into Composables |
| `com.google.dagger:hilt-android` / `hilt-compiler` | 2.57.1 | Compile-time dependency injection across ViewModels, repositories, and DAOs |
| `com.squareup.retrofit2:retrofit` | 2.9.0 | Type-safe HTTP client for the OpenWeatherMap and Geoapify REST APIs |
| `com.squareup.retrofit2:converter-gson` + `gson` | 2.9.0 / 2.8.8 | JSON ↔ Kotlin object (de)serialization for API responses |
| `androidx.room:room-runtime` / `room-ktx` / `room-compiler` | 2.8.4 | Local SQLite persistence for offline weather, forecast, and search-history caching |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.8.0 | `ViewModel` + `StateFlow` integration with Compose |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.7.3 | Type-safe navigation arguments (`@Serializable` `Routes`) |
| `androidx.core:core-splashscreen` | 1.0.1 | Native Android 12+ splash screen API |
| `io.coil-kt.coil3:coil-compose` / `coil-network-okhttp` | 3.1.0 | Asynchronous image loading (weather icons, profile pictures) over OkHttp |
| `com.github.PhilJay:MPAndroidChart` | v3.1.0 | Renders the interactive hourly/forecast **line chart** with custom marker view |
| `com.google.firebase:firebase-auth` | 24.1.0 | Email/password and Google account authentication |
| `com.google.firebase:firebase-firestore` | 26.4.0 | Cloud sync of user profile documents (`user` collection) with live snapshot listeners |
| `androidx.credentials` + `credentials-play-services-auth` + `googleid` | 1.6.0 / 1.2.0 | Modern **Credential Manager** flow for one-tap Google Sign-In |
| `com.google.gms:google-services` plugin | 4.5.0 | Reads `google-services.json` and wires Firebase into the build |
| `io.github.jan-tennert.supabase:storage-kt` | 1.3.2 | Uploads and serves user profile images from a Supabase Storage bucket |
| `androidx.compose.material:material-icons-extended` | 1.7.0 | Extended Material icon set used throughout the UI |

**Also declared in `app/build.gradle.kts`** (present in the dependency graph, but not currently wired into a visible feature): `io.github.jan-tennert.supabase:gotrue-kt` / `compose-auth` / `compose-auth-ui`, `io.ktor:ktor-client-okhttp` / `ktor-client-cio`, `com.google.firebase:firebase-messaging`, and `com.patrykandpatrick.vico:compose` / `compose-m3` / `core`.

</details>

---

## 🌐 APIs Used

| API | Used For | Base URL |
|---|---|---|
| **OpenWeatherMap — Current Weather API** | Current temperature, condition, humidity, pressure, coordinates | `https://api.openweathermap.org/data/2.5/weather` |
| **OpenWeatherMap — Forecast API** | Hourly forecast data plotted on the chart screen | `https://api.openweathermap.org/data/2.5/forecast` |
| **OpenWeatherMap — Air Pollution API** | Air Quality Index (AQI) for the searched location | `https://api.openweathermap.org/data/2.5/air_pollution` |
| **Geoapify — Geocoding Autocomplete API** | City-name search suggestions as the user types | `https://api.geoapify.com/v1/geocode/autocomplete` |
| **Firebase Authentication / Firestore** | User accounts and cloud-synced profile data | Firebase SDK |
| **Supabase Storage** | Hosting user-uploaded profile pictures | Supabase project SDK |

> ⚠️ API keys currently live as constants inside `utils/AppUtils.kt`. For your own build, replace them with your own OpenWeatherMap and Geoapify keys (see [Installation](#-installation--setup)).

---

## ⚙️ Installation & Setup

### Prerequisites
- **Android Studio** (recent stable version, AGP `8.13.2`)
- **JDK 17**
- An Android device/emulator running **API 24 (Android 7.0)** or higher
- A **Firebase project** with Authentication (Email/Password + Google) and Firestore enabled
- API keys from **[OpenWeatherMap](https://openweathermap.org/api)** and **[Geoapify](https://www.geoapify.com/)**
- A **Supabase** project with a Storage bucket (if you want profile-picture upload to work)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/androidwithabhishek/Weather-App.git
   cd Weather-App
   ```

2. **Add your Firebase config**
   Download your own `google-services.json` from the Firebase console and place it in:
   ```
   app/google-services.json
   ```

3. **Add your API keys**
   Open `app/src/main/java/abhishek/gupta/weatherapp/utils/AppUtils.kt` and replace the placeholders:
   ```kotlin
   object AppUtils {
       const val APIKEY = "YOUR_OPENWEATHERMAP_API_KEY"
       const val GEOPIFYAPIKEY = "YOUR_GEOAPIFY_API_KEY"
   }
   ```

4. **Configure Supabase** (optional — required only for profile-image upload)
   Update `SupabaseClientProvider.kt` in `data/remote/supabase/` with your own Supabase project URL and anon key.

5. **Sync Gradle**
   Open the project in Android Studio and let Gradle sync (repositories: Google, Maven Central, JitPack).

### ▶️ Run the Project

- Open the project in **Android Studio**.
- Select a device/emulator (min SDK 24).
- Click **Run ▶** or:
  ```bash
  ./gradlew installDebug
  ```

---

## 📸 Screenshots

<div align="center">
<table>
<tr>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/1.png" width="200"/></td>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/2.png" width="200"/></td>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/3.png" width="200"/></td>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/4.png" width="200"/></td>
</tr>
<tr>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/5.png" width="200"/></td>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/6.png" width="200"/></td>
<td><img src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/screen_shots/7.png" width="200"/></td>
<td></td>
</tr>
</table>
</div>

---

## 🎥 Demo Video

<div align="center">

https://github.com/androidwithabhishek/my-res/raw/main/weather_app/video/WeatherApp%20Video%20.mp4

<video src="https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/video/WeatherApp%20Video%20.mp4" controls width="320"></video>

*(GitHub renders an inline player for the raw link above on the repo page. If it doesn't play in your viewer, click the link to download/watch it directly.)*

</div>

---

## 📥 Download APK

<div align="center">

[![Download APK](https://img.shields.io/badge/Download-APK-2ea44f?style=for-the-badge&logo=android&logoColor=white)](https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/apk/app-debug.apk)

**[⬇ app-debug.apk](https://raw.githubusercontent.com/androidwithabhishek/my-res/main/weather_app/apk/app-debug.apk)**

> This is a **debug build** — install it via `adb install app-debug.apk` or by downloading directly to your Android device and allowing installs from unknown sources.

</div>

---

## 🚧 Future Improvements

> These are natural next steps based on dependencies already present in the project but not yet wired into a feature:
- Wire up **Firebase Cloud Messaging** (`firebase-messaging` is already a dependency and `POST_NOTIFICATIONS` is declared) for severe-weather push notifications.
- Adopt **Supabase GoTrue** (already added) for an alternative/parallel auth provider, or remove it if Firebase Auth remains the single source of truth.
- Evaluate **Vico** (already added as a dependency) as a modern Compose-native replacement for the current MPAndroidChart-based forecast chart.
- Add a Wi-Fi/location-based **automatic current-location weather** flow.
- Add unit/UI test coverage (currently only default template tests exist).
- Add light/dark theme toggling and localization (multi-language support).

---

## 👨‍💻 Author

**Abhishek Gupta**
GitHub: [@androidwithabhishek](https://github.com/androidwithabhishek)

---

## 📄 License

No license file is currently present in this repository. All rights reserved by the author unless a license is added.

<div align="center">

⭐ If you found this project useful, consider giving it a star on [GitHub](https://github.com/androidwithabhishek/Weather-App)!

</div>