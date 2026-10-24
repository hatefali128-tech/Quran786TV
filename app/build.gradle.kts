plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ir.fena.qurantv786_tv"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "ir.fena.qurantv786_tv"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.leanback)
    implementation(libs.glide)
}
// ===== Custom APK output name: Quran786-<variant>-<version>.apk =====
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val vn = runCatching { output.versionName?.orNull }.getOrNull() ?: "1.0"
            (output as? com.android.build.api.variant.impl.VariantOutputImpl)
                ?.outputFileName
                ?.set("Quran786-${variant.name}-$vn.apk")
        }
    }
}
