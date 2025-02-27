rootProject.name = "KTM"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}



include(":core")
include(":ktm-compiler-plugin")
include(":ktm-gradle-plugin")
include(":ktm-plugin-sample")

// Removed until the plugin is ready for K2
//include(":benchmark")
//include(":sample-js")