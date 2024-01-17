import kotlinx.benchmark.gradle.JsBenchmarkTarget
import kotlinx.benchmark.gradle.JsBenchmarksExecutor
import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.benchmark")
    id("org.jetbrains.kotlin.plugin.allopen")
}

fun findProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }

group = findProperty("group")!!
version = findProperty("module.core")!!

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {

    // Default targets

    jvm {
        compilations.all {
            kotlinOptions { jvmTarget = "1.8" }
            jvmToolchain(8)
        }
        compilations.create("benchmarks") {
            associateWith(compilations.getByName("main"))
        }

        withJava()
        withSourcesJar(true)
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    // web

    js(IR) {
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
                val serialization = property("version.serialization")
                val benchmark = property("version.benchmark")
                val jmh = property("version.jmh")
                implementation("org.openjdk.jmh:jmh-core:$jmh")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:$benchmark")
                implementation("com.github.spullara.mustache.java:compiler:0.9.10")
            }
        }
    }
}

// This is required for benchmark to work
allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvmBenchmarks") {
            (this as JvmBenchmarkTarget).jmhVersion = property("version.jmh") as String
        }
    }

    configurations.getByName("main") {
        warmups = 20
        iterations = 10
        iterationTime = 3
    }

    configurations.register("smoke") {
        warmups = 5
        iterations = 3
        iterationTime = 1
    }
}

// region Publish

val ossrhUsername = findFilledProperty("osshr.username")
val ossrhPassword = findFilledProperty("osshr.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null

publishing {
    publications.withType<MavenPublication> {
        val publicationName = this@withType.name
        val javadocJar = tasks.register("${publicationName}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveBaseName.set("${archiveBaseName.get()}-${publicationName}")
        }
        artifact(javadocJar)
        pom {
            name = findProperty("POM_NAME")!!
            description = findProperty("POM_DESCRIPTION")!!
            url = findProperty("POM_URL")!!
            licenses {
                license {
                    name = findProperty("POM_LICENSE_NAME")!!
                    url = findProperty("POM_LICENSE_URL")!!
                }
            }
            developers {
                developer {
                    id = findProperty("POM_DEVELOPER_LBRIAND_ID")!!
                    name = findProperty("POM_DEVELOPER_LBRIAND_NAME")!!
                    email = findProperty("POM_DEVELOPER_LBRIAND_EMAIL")!!
                }
            }
            scm {
                connection = findProperty("POM_SCM_URL")!!
                developerConnection = findProperty("POM_SCM_CONNECTION")!!
                url = findProperty("POM_SCM_DEV_CONNECTION")!!
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

val signingKeyId = findFilledProperty("signing.keyId")
val signingPassword = findFilledProperty("signing.password")
val signingSecretKeyRingFile = findFilledProperty("signing.secretKeyRingFile")
val isSigningEnabled = signingKeyId != null && signingPassword != null && signingSecretKeyRingFile != null

if (isSigningEnabled) {
    signing {
        sign(publishing.publications)
    }
}

// endregion

tasks.create<Delete>("cleanupGithubDocumentation") {
    delete(file("docs"))
}
tasks.create<Copy>("generateGithubDocumentation") {
    dependsOn("cleanupGithubDocumentation")
    dependsOn("dokkaHtml")
    val buildDir = layout.buildDirectory
    from(buildDir.dir("dokka/html")).into("docs")
}