plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialisation)
    alias(libs.plugins.ksp.tools)
}

android {
    namespace = "com.example.matchmate"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.matchmate"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.retrofit)
    // Gson converter for JSON parsing
    implementation(libs.converter.gson)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData (often used with ViewModel)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Kotlin Coroutines for asynchronous work
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity.ktx)

    // kotlin x serialisation
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // room database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
}