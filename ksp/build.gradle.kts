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
    implementation("com.google.devtools.ksp:symbol-processing-api:$kotlin-$ksp")
}