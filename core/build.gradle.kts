plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `java-library`
    id("maven-publish")
    id("signing")
}

fun findProperty(name: String): String? =
    if (hasProperty(name)) property(name) as String else System.getenv(name)

fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }

group = findProperty("group") !!
version = findProperty("module.core") !!

val ossrhUsername = findFilledProperty("osshr.username")
val ossrhPassword = findFilledProperty("osshr.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null
val isSigningEnabled = findFilledProperty("signing.keyId") != null &&
        findFilledProperty("signing.password") != null &&
        findFilledProperty("signing.secretKeyRingFile") != null

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {

    jvm("jvm") {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    js("js") {
        browser()
        nodejs()
    }

    macosArm64("macosArm64")
    macosX64("macosX64")
    linuxArm64("linuxArm64")
    linuxX64("linuxX64")
    mingwX64("mingwX64")

    sourceSets {
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
    }

}
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
