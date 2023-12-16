plugins {
    kotlin("jvm")
}

group = property("group") as String
version = property("module.ksp") as String

val kotlin = property("version.kotlin") as String
val ksp = property("version.ksp") as String

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("net.orandja.kt:either:1.2.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kotlin-$ksp")
}