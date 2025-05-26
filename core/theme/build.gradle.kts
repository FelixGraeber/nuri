 
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
val fontName = properties["fontName"] as String?

android {
    namespace = "app.getnuri.theme"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String" , "fontName" , fontName ?: "\"Roboto Flex\"")
        }
        release {
            buildConfigField("String" , "fontName" , fontName ?: "\"Roboto Flex\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

}

dependencies {
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(project(":core:util"))

    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    // api because we need to access LocalNavAnimatedScope in feature modules for animations.
    api(libs.androidx.navigation3.ui)
    debugImplementation(libs.ui.tooling)

    // Android Instrumented Tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(project(":core:testing"))

    debugImplementation(libs.androidx.ui.test.manifest)
}
