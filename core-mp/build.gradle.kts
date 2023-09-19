plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `java-library`
}

group = "net.orandja"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    sourceSets {
        getByName("commonMain")
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        getByName("jvmMain")
        getByName("jvmTest")
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"

    when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

}