plugins {
    id("com.android.application")
    kotlin("android")

    // KOTLIN ANDROID
    id("kotlin-android")

    // KOTLIN KAPT
    id("kotlin-kapt")

    // GOOGLE MAPS KEY SECRET
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.servirunplatomas.android"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    // ANDROID CORE
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")

    // NAVIGATION
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.1")

    // MVVM
    implementation("dev.icerock.moko:mvvm-livedata-material:0.11.0")
    implementation("dev.icerock.moko:mvvm-livedata-glide:0.11.0")
    implementation("dev.icerock.moko:mvvm-livedata-swiperefresh:0.11.0")
    implementation("dev.icerock.moko:mvvm-databinding:0.11.0")
    implementation("dev.icerock.moko:mvvm-viewbinding:0.11.0")

    // KOTLIN STANDARD
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.10")

    // KOTLIN SERIALIZATION
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    // KTOR
    implementation("io.ktor:ktor-client-android:1.6.3")

    // GOOGLE MAPS
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:19.0.1")
    implementation("com.google.maps.android:android-maps-utils:0.5")

    // PICASSO
    implementation("com.squareup.picasso:picasso:2.71828")

    // IMAGE SLIDER
    implementation("com.github.denzcoskun:ImageSlideshow:0.0.7")
}