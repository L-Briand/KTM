import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.benchmark")
    id("org.jetbrains.kotlin.plugin.allopen")
}

fun findProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }

group = "${findProperty("group")!!}.benchmark"
version = "no_version"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {

    // Default targets

    jvm {
        java.toolchain.languageVersion = JavaLanguageVersion.of(8)
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget = JvmTarget.JVM_1_8
                }
            }
        }

        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    // web

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { d8() }
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmWasi { nodejs() }

    // https://kotlinlang.org/docs/native-target-support.html

    // Tier1

    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // Tier2

    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // Tier3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(project(":core"))
            }
        }
        getByName("jvmMain") {
            dependencies {
                implementation(libs.kotlinx.benchmark)
                implementation(libs.jmh)
                implementation(libs.spullara)
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvmMain") {
            (this as JvmBenchmarkTarget).jmhVersion = libs.versions.jmh.get()
        }
    }

    configurations.getByName("main") {
        warmups = 20
        iterations = 10
        iterationTime = 3
    }

    configurations.register("smoke") {
        warmups = 5
        iterations = 3
        iterationTime = 1
    }
}

dependencies {
    add("kspJvm", project(":ksp"))
}

ksp {
    arg("ktm.auto_adapters_package", "$group")
}
