 
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "app.getnuri.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}
// Explicitly disable the connectedAndroidTest task for this module
androidComponents {
    beforeVariants(selector().all()) { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:util"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.okhttp)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.ai.edge) {
        exclude(group = "com.google.guava")
    }
    ksp(libs.hilt.compiler)

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Encrypted Room (SQLCipher)
    implementation("net.zetetic:android-database-sqlcipher:4.5.2")
    implementation("androidx.sqlite:sqlite-ktx:2.3.0")

    // Secure key storage for the SQLCipher pass-phrase
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Gson for the new converters (if not already present)
    implementation("com.google.code.gson:gson:2.10")
}
