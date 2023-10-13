import java.util.Properties

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}


plugins {
    val kotlin = "1.9.0" // also in gradle.properties
    val ksp = "1.0.13" // also in gradle.properties

    kotlin("jvm") version kotlin apply false
    kotlin("plugin.serialization") version kotlin apply false
    kotlin("multiplatform") version kotlin apply false
    id("com.google.devtools.ksp") version "$kotlin-$ksp" apply false
    
}