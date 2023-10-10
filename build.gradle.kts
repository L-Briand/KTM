import java.util.Properties

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}


plugins {
    val kotlin = "1.9.0" // TODO: Find a way to read from gradle.properties here
    val ksp = "1.0.13" // TODO: Find a way to read from gradle.properties here

    kotlin("jvm") version kotlin apply false
    kotlin("plugin.serialization") version kotlin apply false
    kotlin("multiplatform") version kotlin apply false
    id("com.google.devtools.ksp") version "$kotlin-$ksp" apply false
}