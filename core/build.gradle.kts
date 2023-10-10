plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `java-library`
    id("maven-publish")
}

group = property("group") as String
version = property("core.version") as String

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm("jvm") {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    js("js") {
        // browser()
        nodejs()
    }

    macosArm64("macosArm64")
    macosX64("macosX64")
    linuxArm64("linuxArm64")
    linuxX64("linuxX64")
    mingwX64("mingwX64")

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

    publishing {
        repositories {
            mavenLocal()
        }
    }
}