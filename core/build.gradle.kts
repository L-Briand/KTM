@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.dokka)
    id("maven-publish")
    id("signing")
}

fun getProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = getProperty(name)?.ifBlank { null }

group = getProperty("group")!!
version = getProperty("module.core")!!

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {

    // Default targets

    jvm {
        java.toolchain.languageVersion = JavaLanguageVersion.of(11)
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget = JvmTarget.JVM_11
                }
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
        binaries.library()
        browser()
        nodejs()
    }

    wasmJs { d8() }
    wasmWasi { nodejs() }

    // https://kotlinlang.org/docs/native-target-support.html

    // Tier1

    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

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

    // Tier3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization)
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
    }
}

// region Publish

val ossrhUsername = findFilledProperty("ossrh.username")
val ossrhPassword = findFilledProperty("ossrh.password")
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
            name = getProperty("POM_NAME")!!
            description = getProperty("POM_DESCRIPTION")!!
            url = getProperty("POM_URL")!!
            licenses {
                license {
                    name = getProperty("POM_LICENSE_NAME")!!
                    url = getProperty("POM_LICENSE_URL")!!
                }
            }
            developers {
                developer {
                    id = getProperty("POM_DEVELOPER_LBRIAND_ID")!!
                    name = getProperty("POM_DEVELOPER_LBRIAND_NAME")!!
                    email = getProperty("POM_DEVELOPER_LBRIAND_EMAIL")!!
                }
            }
            scm {
                connection = getProperty("POM_SCM_URL")!!
                developerConnection = getProperty("POM_SCM_CONNECTION")!!
                url = getProperty("POM_SCM_DEV_CONNECTION")!!
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