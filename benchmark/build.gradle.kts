import kotlinx.benchmark.gradle.JvmBenchmarkTarget
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
        compilations {
            all {
                kotlinOptions { jvmTarget = "1.8" }
                jvmToolchain(8)
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
                implementation("org.openjdk.jmh:jmh-core:${property("version.jmh")}")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:${property("version.benchmark")}")
                implementation("com.github.spullara.mustache.java:compiler:0.9.10")
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
            (this as JvmBenchmarkTarget).jmhVersion = property("version.jmh") as String
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
