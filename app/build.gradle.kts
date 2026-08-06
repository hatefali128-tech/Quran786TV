plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ir.fena.arshadqurantv_arshad_quran_tv"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "ir.fena.arshadqurantv_arshad_quran_tv"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

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