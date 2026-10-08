import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
configurations.all {
    exclude(group = "commons-logging", module = "commons-logging")
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room3)
    kotlin("plugin.serialization") version "2.3.10"

    alias(libs.plugins.koin.compiler)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    android {
       namespace = "ru.kulishov.sokratum.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

        packaging{
            resources {
                excludes += "META-INF/INDEX.LIST"
            }
        }

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
//            implementation(libs.compose.uiToolingPreview)
//            implementation(libs.compose.uiTooling)
            implementation(libs.androidx.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.langchain4j)
            implementation(libs.langchain4j.open.ai)
            implementation(libs.langchain4j.local.ai)
            implementation(libs.langchain4j.easy.rag)
            implementation(libs.langchain4j.agentic)

        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.koin.annotations)

            implementation(libs.androidx.room3.runtime)

            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.navigation.compose.v292)

            implementation(libs.kotlinx.datetime)
            implementation(libs.material3.window.size.class1)
            //implementation(libs.logback)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        nativeMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.ktor.client.darwin)
        }
        webMain.dependencies {
            implementation(libs.androidx.sqlite.bundled.web)
            implementation(libs.ktor.client.js)
            implementation(libs.kotlinx.browser)

        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
    add("kspJvm", libs.androidx.room3.compiler)
    add("kspIosArm64", libs.androidx.room3.compiler)
    add("kspJs", libs.androidx.room3.compiler)
    add("kspWasmJs", libs.androidx.room3.compiler)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}