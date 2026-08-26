plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.carpimadasi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aistudio.matematikadasi.mqlkvr"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            file("${rootDir}/debug.keystore").takeIf { it.exists() }?.let {
                storeFile = it
            }
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val keystoreFile = System.getenv("KEYSTORE_FILE") 
                ?: System.getenv("KEYSTORE_PATH") 
                ?: System.getenv("SIGNING_STORE_FILE")
            
            if (!keystoreFile.isNullOrEmpty() && file(keystoreFile).exists()) {
                storeFile = file(keystoreFile)
            } else {
                file("${rootDir}/debug.keystore").takeIf { it.exists() }?.let {
                    storeFile = it
                }
            }

            val pass = System.getenv("KEYSTORE_PASSWORD")
                ?: System.getenv("STORE_PASSWORD")
                ?: System.getenv("SIGNING_STORE_PASSWORD")
                ?: System.getenv("ANDROID_KEYSTORE_PASSWORD")
                ?: System.getenv("KEYSTORE_PWD")
                ?: "android"
            storePassword = pass

            keyAlias = System.getenv("KEY_ALIAS")
                ?: System.getenv("SIGNING_KEY_ALIAS")
                ?: System.getenv("ALIAS")
                ?: System.getenv("KEYALIAS")
                ?: "androiddebugkey"

            keyPassword = System.getenv("KEY_PASSWORD")
                ?: System.getenv("SIGNING_KEY_PASSWORD")
                ?: System.getenv("SIGNING_KEY_PWD")
                ?: System.getenv("KEY_PWD")
                ?: pass
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
