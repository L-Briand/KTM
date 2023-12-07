import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.kotlinx.benchmark")
    id("org.jetbrains.kotlin.plugin.allopen")
}

fun findProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }

group = findProperty("group") !!
version = findProperty("module.core") !!

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {

    // Default targets

    jvm {
        compilations {
            all {
                kotlinOptions { jvmTarget = "1.8" }
                jvmToolchain(8)
            }
            create("benchmarks") {
                associateWith(getByName("main"))
            }
        }
        withJava()
        withSourcesJar(true)
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    // web

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { d8() }
    // wasmWasi { nodejs() }

    // https://kotlinlang.org/docs/native-target-support.html

    // Tier1

    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // Tier2

    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // Tier3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    sourceSets {
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                val serialization = property("version.serialization")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
            }
        }
        getByName("jvmBenchmarks") {
            dependencies {
                val benchmark = property("version.benchmark")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:$benchmark")
                implementation("org.openjdk.jmh:jmh-core:1.21")
            }
        }
    }
}

// This is required for benchmark to work
allOpen {
    this.annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvmBenchmarks")
    }

    configurations.getByName("main") {
        warmups = 20
        iterations = 10
        iterationTime = 3
    }

    configurations.register("smoke") {
        warmups = 5
        iterations = 3
        iterationTime = 500
        iterationTimeUnit = "ms"
    }
}

// region Publish

val ossrhUsername = findFilledProperty("osshr.username")
val ossrhPassword = findFilledProperty("osshr.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null
val isSigningEnabled = findFilledProperty("signing.keyId") != null &&
        findFilledProperty("signing.password") != null &&
        findFilledProperty("signing.secretKeyRingFile") != null

publishing {
    publications.withType<MavenPublication> {
        val publicationName = this@withType.name
        val javadocJar = tasks.register("${publicationName}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveBaseName.set("${archiveBaseName.get()}-${publicationName}")
        }
        artifact(javadocJar)
        pom {
            name = findProperty("POM_NAME") !!
            description = findProperty("POM_DESCRIPTION") !!
            url = findProperty("POM_URL") !!
            licenses {
                license {
                    name = findProperty("POM_LICENSE_NAME") !!
                    url = findProperty("POM_LICENSE_URL") !!
                }
            }
            developers {
                developer {
                    id = findProperty("POM_DEVELOPER_LBRIAND_ID") !!
                    name = findProperty("POM_DEVELOPER_LBRIAND_NAME") !!
                    email = findProperty("POM_DEVELOPER_LBRIAND_EMAIL") !!
                }
            }
            scm {
                connection = findProperty("POM_SCM_URL") !!
                developerConnection = findProperty("POM_SCM_CONNECTION") !!
                url = findProperty("POM_SCM_DEV_CONNECTION") !!
            }
        }
    }

    repositories {
        mavenLocal()
        if (ossrhMavenEnabled) {
            maven {
                name = "sonatype"
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }
}

if (isSigningEnabled) {
    signing {
        sign(publishing.publications)
    }
}

// endregion