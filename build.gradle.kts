// Gradle needs this to find "org.jetbrains.kotlin:kotlin-klib-commonizer-embeddable:1.9.10" in particular.
// Even if the same repository is placed inside plugin management in settings.gradle...
repositories { mavenCentral() }
plugins {
    kotlin("jvm") version "1.9.10" apply false
    kotlin("plugin.serialization") version "1.9.10" apply false
    kotlin("multiplatform") version "1.9.10" apply false
}