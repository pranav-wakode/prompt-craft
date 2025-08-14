import java.util.Properties

// Top-level plugins block is correct
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    alias(libs.plugins.google.services)
}

// <-- START: ADD THIS LOGIC TO LOAD local.properties
// Create a new Properties object
val localProperties = Properties()
// Find the root project's local.properties file
val localPropertiesFile = rootProject.file("local.properties")
// If the file exists, load its contents into the Properties object
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
// <-- END: ADD THIS LOGIC

android {
    namespace = "com.pranav.promptcraft"
    buildFeatures {
        buildConfig = true
    }
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pranav.promptcraft"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // <-- CHANGE THIS LINE: Use the loaded properties
        // Get the API key from the loaded localProperties object
        val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
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
    // composeOptions should be inside the buildFeatures block
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Example version, use your actual version from libs
    }
}

dependencies {
    // Your dependencies block is fine, no changes needed here.
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)

    // Gemini
    implementation(libs.generativeai)
}