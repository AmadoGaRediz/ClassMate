plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.classmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.classmate"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.material3)
    implementation(libs.material3.theme)
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation ("androidx.wear.compose:compose-material:1.4.1")
    implementation ("com.google.android.gms:play-services-wearable:18.1.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.activity:activity-compose:1.8.0") // Si usa Jetpack Compose
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // BOM, solo una vez
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx") // Solo si usarás autenticación
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("androidx.activity:activity-compose:1.8.2")

}