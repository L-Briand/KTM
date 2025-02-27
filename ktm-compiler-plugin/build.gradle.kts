plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
}

fun getProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun getFilledProperty(name: String): String? = getProperty(name)?.ifBlank { null }

group = getProperty("group")!!
version = getProperty("module.plugin")!!

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    }
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("compilerPlugin") {
            from(components["java"])

            pom {
                name = getFilledProperty("POM_NAME")
                description = getFilledProperty("POM_DESCRIPTION")
                url = getFilledProperty("POM_URL")
                licenses {
                    license {
                        name = getFilledProperty("POM_LICENSE_NAME")
                        url = getFilledProperty("POM_LICENSE_URL")
                    }
                }
                developers {
                    developer {
                        id = getFilledProperty("POM_DEVELOPER_LBRIAND_ID")
                        name = getFilledProperty("POM_DEVELOPER_LBRIAND_NAME")
                        email = getFilledProperty("POM_DEVELOPER_LBRIAND_EMAIL")
                    }
                }
                scm {
                    connection = getFilledProperty("POM_SCM_URL")
                    developerConnection = getFilledProperty("POM_SCM_CONNECTION")
                    url = getFilledProperty("POM_SCM_DEV_CONNECTION")
                }
            }
        }
    }
}

val signingKeyId = getFilledProperty("signing.keyId")
val signingPassword = getFilledProperty("signing.password")
val signingSecretKeyRingFile = getFilledProperty("signing.secretKeyRingFile")
val isSigningEnabled = signingKeyId != null && signingPassword != null && signingSecretKeyRingFile != null

if (isSigningEnabled) {
    signing {
        sign(publishing.publications)
    }
}