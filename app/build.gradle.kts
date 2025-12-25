plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "group.eleven.snippet_sharing_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "group.eleven.snippet_sharing_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API Base URL - Change this for production
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2/api/v1/\"")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // OkHttp for networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Gson for JSON parsing
    implementation(libs.gson)

    // Lifecycle components (ViewModel, LiveData)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Security for encrypted SharedPreferences
    implementation(libs.security.crypto)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}