repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.22"))
    }
}

plugins {
    val kotlin = "1.9.22" // also in gradle.properties
    val ksp = "1.0.17" // also in gradle.properties
    val benchmark = "0.4.9"
    val dokka = "1.9.10"

    kotlin("jvm") version kotlin apply false
    kotlin("plugin.serialization") version kotlin apply false
    kotlin("multiplatform") version kotlin apply false
    id("com.google.devtools.ksp") version "$kotlin-$ksp" apply false
    id("org.jetbrains.dokka") version dokka apply false
    id("org.jetbrains.kotlinx.benchmark") version benchmark apply false
    id("org.jetbrains.kotlin.plugin.allopen") version kotlin apply false
}