plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "abhishek.gupta.weatherapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "abhishek.gupta.weatherapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}



dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

//  Hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


//  Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.8")

//    ktor
    implementation("io.ktor:ktor-client-okhttp:2.3.3")

//    viewmodel compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

//    navigation
    implementation("androidx.navigation:navigation-compose:2.9.3")

//    room

    implementation("androidx.room:room-runtime:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")

//    coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)


    implementation("androidx.compose.material:material-icons-extended:1.7.0")
//  serialization
    implementation(libs.kotlinx.serialization)

//   splash screen
    implementation(libs.androidx.core.splashscreen)



    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.3.2")
    implementation("io.github.jan-tennert.supabase:compose-auth:1.3.2")
    implementation("io.github.jan-tennert.supabase:compose-auth-ui:1.3.2")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.3.2")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("com.google.firebase:firebase-messaging:24.0.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.patrykandpatrick.vico:compose:2.0.0")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0")
    implementation("com.patrykandpatrick.vico:core:2.0.0")
}