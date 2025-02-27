plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    id("com.gradle.plugin-publish") version "1.2.1"
    signing
}

fun getProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun getFilledProperty(name: String): String? = getProperty(name)?.ifBlank { null }

group = getProperty("group")!!
version = getProperty("module.plugin")!!

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    val kotlinVersion = libs.versions.kotlin.version.get()
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVersion")
}

publishing {
    repositories {
        mavenLocal()
    }
}

gradlePlugin {
    website = "https://github.com/L-Briand/KTM"
    vcsUrl = "https://github.com/L-Briand/KTM"
    plugins.create("ktm") {
        id = "net.orandja.ktm"
        displayName = "KTM Plugin"
        description =
            "A Kotlin Plugin for KTM library. It auto generates Contextual Adapters from your kotlin classes for your Mustache templates."

        tags.set(buildList {
            add("kotlin")
            add("kotlin-multiplatform")
            add("multiplatform-kotlin-library")
            add("mustache")
            add("template-engine")
            add("template-processor")
            add("template-engine-html")
            add("template-engine-kotlin")
        })
        implementationClass = "net.orandja.ktm.gradle.plugin.KtmGradlePlugin"
    }
}

publishing.publications.withType<MavenPublication> {
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

val signingKeyId = getFilledProperty("signing.keyId")
val signingPassword = getFilledProperty("signing.password")
val signingSecretKeyRingFile = getFilledProperty("signing.secretKeyRingFile")
val isSigningEnabled = signingKeyId != null && signingPassword != null && signingSecretKeyRingFile != null

if (isSigningEnabled) {
    signing {
        sign(publishing.publications)
    }
}