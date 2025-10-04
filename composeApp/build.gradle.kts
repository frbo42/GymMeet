@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
//            www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html#qwvm1x_56
//            implementation(libs.junit)
//            implementation(compose.desktop.uiTestJUnit4)
//            implementation(libs.kotlin.testJunit)
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

android {
    namespace = "org.fb.gym.meet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.fb.gym.meet"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
