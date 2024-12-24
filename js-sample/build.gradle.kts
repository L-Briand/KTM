plugins {
    alias(libs.plugins.kotlin.js)
}

kotlin {
    js {
        binaries.library()
        browser()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.orandja.ktm:core:2.0.0")
    implementation("net.orandja.ktm:core-js:2.0.0")
}