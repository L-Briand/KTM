import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    id("maven-publish")
    id("signing")
}

fun findProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }

group = findProperty("group")!!
version = findProperty("module.ksp")!!

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.ksp)
    implementation(libs.orandja.either)
}

java {
    withSourcesJar()
    withJavadocJar()
    java.toolchain.languageVersion = JavaLanguageVersion.of(8)
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

val ossrhUsername = findFilledProperty("osshr.username")
val ossrhPassword = findFilledProperty("osshr.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null

publishing {
    publications.create<MavenPublication>("library") {
        from(components["java"])

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