import java.util.Properties

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}


plugins {
    val kotlin = "1.9.21" // also in gradle.properties
    val ksp = "1.0.15" // also in gradle.properties
    val benchmark = "0.4.9"

    kotlin("jvm") version kotlin apply false
    kotlin("plugin.serialization") version kotlin apply false
    kotlin("multiplatform") version kotlin apply false
    id("com.google.devtools.ksp") version "$kotlin-$ksp" apply false
    id("org.jetbrains.kotlinx.benchmark") version benchmark apply false
    id("org.jetbrains.kotlin.plugin.allopen") version kotlin apply false
}