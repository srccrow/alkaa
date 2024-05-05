import extension.androidDependencies
import extension.commonDependencies
import extension.commonTestDependencies
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.escodro.multiplatform")
    alias(libs.plugins.compose)
    id(libs.plugins.moko.multiplatform.resources.get().pluginId) // Use version from classpath
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)

            dependencies {
                implementation(libs.test.junit4.android)
                debugImplementation(libs.test.manifest)
            }
        }
    }
    commonDependencies {
        implementation(projects.data.local)
        implementation(projects.data.datastore)
        implementation(projects.data.repository)
        implementation(projects.domain)
        implementation(projects.libraries.navigation)
        implementation(projects.libraries.coroutines)
        implementation(projects.libraries.designsystem)
        implementation(projects.libraries.di)
        implementation(projects.libraries.appstate)

        implementation(projects.features.home)
        implementation(projects.features.task)
        implementation(projects.features.alarm)
        implementation(projects.features.category)
        implementation(projects.features.search)
        implementation(projects.features.preference)

        implementation(projects.resources)

        implementation(projects.domain)
        implementation(compose.runtime)
        implementation(compose.material)
        implementation(compose.material3)
        implementation(libs.koin.compose.jb)
        implementation(libs.moko.resources.core)
        implementation(libs.moko.mvvm.compose)
    }

    androidDependencies {
        implementation(projects.features.glance)
    }

    commonTestDependencies {
        implementation(kotlin("test"))

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        implementation(compose.uiTest)
    }

    // Explicit dependency due to Moko issues with Kotlin 1.9.0
    // https://github.com/icerockdev/moko-resources/issues/531
    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    namespace = "com.escodro.shared"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.escodro.alkaa"
}

// Add compile options to link sqlite3 library allowing iOS UI testing
// https://github.com/touchlab/SQLiter/issues/77#issuecomment-1472089931
project.extensions.findByType(KotlinMultiplatformExtension::class.java)?.apply {
    targets
        .filterIsInstance<KotlinNativeTarget>()
        .flatMap { it.binaries }
        .forEach { compilationUnit -> compilationUnit.linkerOpts("-lsqlite3") }
}
