buildscript {
    repositories {
        mavenLocal()
    }

    dependencies {
        classpath("net.orandja.ktm:ktm-gradle-plugin:2.0.0")
    }
}

apply { plugin("net.orandja.ktm") }

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
}

group = findProperty("group")!!
version = findProperty("module.plugin")!!

ktm {
    loggingLevel = LEVEL_OUTPUT
}
kotlin {
    compilerOptions {
        this.freeCompilerArgs.add("-verbose")
    }
}
