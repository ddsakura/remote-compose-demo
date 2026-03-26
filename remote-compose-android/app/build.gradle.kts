plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.remotecompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.remotecompose"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.10.0")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // AppCompat (for theme)
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Remote Compose Player
    implementation("androidx.compose.remote:remote-core:1.0.0-alpha07")
    implementation("androidx.compose.remote:remote-player-core:1.0.0-alpha07")
    implementation("androidx.compose.remote:remote-player-view:1.0.0-alpha07")
}
