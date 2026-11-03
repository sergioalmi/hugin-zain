import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// La App Key de DJI va en local.properties (ignorado por git): dji.appKey=...
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    namespace = "com.huginzain.pruebadespegue"
    compileSdk = 35

    defaultConfig {
        // La App Key de DJI está ligada a este nombre de paquete.
        applicationId = "com.huginzain.pruebadespegue"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
        ndk { abiFilters += "arm64-v8a" } // el MSDK solo trae arm64-v8a
        manifestPlaceholders["djiAppKey"] = localProps.getProperty("dji.appKey", "FALTA_APP_KEY")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    lint {
        warningsAsErrors = true
        // Herramienta de prueba: un solo idioma (castellano), sin icono y solo arm64 (lo exige el MSDK).
        disable += setOf("SetTextI18n", "MissingApplicationIcon", "ChromeOsAbiSupport")
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true // genera android:extractNativeLibs="true", que exige el MSDK desde 5.17.0
            pickFirsts += "lib/arm64-v8a/libc++_shared.so"
            keepDebugSymbols += "*/*/*.so" // el MSDK no admite librerías nativas recortadas
        }
    }
}

dependencies {
    implementation("com.dji:dji-sdk-v5-aircraft:5.18.0")
    compileOnly("com.dji:dji-sdk-v5-aircraft-provided:5.18.0")
    runtimeOnly("com.dji:dji-sdk-v5-networkImp:5.18.0") // el registro de la App Key necesita red
    testImplementation("junit:junit:4.13.2")
}
