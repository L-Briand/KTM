plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"

    id("org.jetbrains.kotlinx.benchmark") version "0.4.9"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.10"
}

group = "net.orandja.ktm"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.spullara.mustache.java:compiler:0.9.10")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.4")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations.getByName("main") {
        warmups = 20 // number of warmup iterations
        iterations = 10 // number of iterations
        iterationTime = 3 // time in seconds per iteration
    }
    targets {
        register("main")
    }
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
