repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.0.0"))
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.allopen) apply false
    alias(libs.plugins.kotlin.dokka) apply false
    alias(libs.plugins.kotlin.benchmark) apply false
}