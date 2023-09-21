rootProject.name = "KTM"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}


include(":core")
// plugins {
//     id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
// }
// include(":core-mp")