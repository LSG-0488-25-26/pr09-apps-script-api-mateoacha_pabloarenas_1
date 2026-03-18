plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

import java.util.Properties

// ==================== CARGA DE SECRETS ====================
// Carga variables de secrets.properties desde carpeta app/ 
// (Teoría: el archivo debe estar EN la carpeta app/)
val secrets = Properties().apply {
    
    val rootSecretsFile = project.rootProject.file("secrets.properties")
    val appSecretsFile = project.rootProject.file("app/secrets.properties")

    val fileToLoad = when {
        rootSecretsFile.exists() -> rootSecretsFile
        appSecretsFile.exists() -> appSecretsFile
        else -> null
    }

    if (fileToLoad != null) {
        fileToLoad.inputStream().use { load(it) }
    }
}

fun secret(name: String): String = (secrets.getProperty(name) ?: "").trim()

fun normalizeBaseUrl(raw: String): String {
    val v = raw.trim()
    require(v.isNotBlank()) { "BASE_URL está vacío en secrets.properties" }

    val withScheme = if (v.startsWith("http://") || v.startsWith("https://")) {
        v
    } else if (v.startsWith("AKfy")) {
        "https://script.google.com/macros/s/$v/"
    } else if (v.contains("/macros/s/")) {
        // Caso típico si se perdió el esquema pero quedó el path.
        "https://$v"
    } else {
        // Fallback: tratamos el valor como ID.
        "https://script.google.com/macros/s/${v.trim('/')}/"
    }

    return if (withScheme.endsWith("/")) withScheme else "$withScheme/"
}

android {
    namespace = "com.example.pr09_app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.pr09_app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // ==================== BUILD CONFIG FIELDS ====================
        buildConfigField("String", "BASE_URL", "\"${normalizeBaseUrl(secret("BASE_URL"))}\"")
        buildConfigField("String", "API_KEY", "\"${secret("API_KEY")}\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}