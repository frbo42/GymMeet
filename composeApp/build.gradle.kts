@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.sql)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqlAndroidDriver)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.composeNavigation)
            implementation(libs.materialIcons)
            implementation(libs.kotlinxDateTime)
            implementation(libs.kotlinxSerializationJson)
            implementation(libs.sqlRuntime)
            implementation(libs.sqlCoroutines)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.sqlJvmDriver)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(compose.uiTest)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
        }

        wasmJsMain.dependencies {
            implementation(libs.sqlWasmDriver)
        }
        jsMain.dependencies {
            implementation(libs.sqlJsDriver)
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("org.fb.gym.meet.db")
            // The .sq files will be placed under src/commonMain/sqldelight/org/fb/gym/meet/db
            dialect("app.cash.sqldelight:sqlite-3-24-dialect:2.1.0")
        }
    }
}
val localProps = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(FileInputStream(localFile))
    }
}

android {
    namespace = "org.fb.gym.meet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.fb.gym.meet"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("../etc/keystore.jks")
            keyAlias = "gym-meet"
            storePassword = localProps.getProperty("KEYSTORE_PASS")
                ?: System.getenv("KEYSTORE_PASS")
                        ?: error("Keystore password missing")
            keyPassword = localProps.getProperty("KEY_ALIAS_PASS")
                ?: System.getenv("KEY_ALIAS_PASS")
                        ?: error("Key alias password missing")
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.fb.gym.meet.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.fb.gym.meet"
            packageVersion = "1.0.0"
        }
    }
}
