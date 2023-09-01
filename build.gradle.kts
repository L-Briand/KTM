plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

group = "net.orandja.ktm"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.compileJava {
    // use the project's version or define one directly
    options.javaModuleVersion.set(provider { version as String })
}

publishing {
    publications {
        create<MavenPublication>("ktm") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
