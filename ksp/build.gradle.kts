import org.gradle.initialization.DefaultGradlePropertiesController
import org.gradle.initialization.GradlePropertiesController
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-library")
    id("maven-publish")
    id("signing")
}

fun getProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = getProperty(name)?.ifBlank { null }

group = getProperty("group")!!
version = getProperty("module.ksp")!!

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.ksp)
    implementation(libs.kotlin.ksp.api)
    implementation(libs.orandja.either)
}

java {
    withSourcesJar()
    withJavadocJar()
    java.toolchain.languageVersion = JavaLanguageVersion.of(11)
}


tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

val ossrhUsername = findFilledProperty("ossrh.username")
val ossrhPassword = findFilledProperty("ossrh.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null

publishing {
    publications.create<MavenPublication>("library") {
        from(components["java"])

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