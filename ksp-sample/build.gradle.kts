plugins {
    kotlin("jvm")
}

group = property("group") as String
version = "unused"

val kotlin = property("version.kotlin") as String

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.orandja.ktm:core:0.1.0")
}
