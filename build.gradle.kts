buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.1.1")

        // NAVIGATION
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")

        // GOOGLE MAPS KEY SECRET
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        // IMAGE SLIDER
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}