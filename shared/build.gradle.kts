plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")

    // KOTLIN SERIALIZATION
    kotlin("plugin.serialization") version "1.6.10"
}

version = "1.0"

kotlin {
    android()
    iosX64()
    iosArm64()
    //iosSimulatorArm64() sure all ios dependencies support this target

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // COROUTINES
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")

                // MVVM
                implementation("dev.icerock.moko:mvvm:0.11.0")

                // KODEIN
                implementation("org.kodein.di:kodein-di:7.8.0")

                // KTOR
                implementation("io.ktor:ktor-client-core:1.6.3")
                implementation("io.ktor:ktor-client-json:1.6.3")
                implementation("io.ktor:ktor-client-logging:1.6.3")
                implementation("io.ktor:ktor-client-serialization:1.6.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        //val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                // KTOR
                implementation("io.ktor:ktor-client-ios:1.6.3")
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            //iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        //val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            //iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 26
        targetSdk = 31
    }
}