plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
}

group = "net.orandja.ktm"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.compileJava {
    // use the project's version or define one directly
    options.javaModuleVersion.set(provider { version as String })
}

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.49.1") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

val ktlintCheck by tasks.registering(JavaExec::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information
    args("**/src/**/*.kt", "**.kts", "!**/build/**")
}

tasks.check { dependsOn(ktlintCheck) }

tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information
    args("-F", "**/src/**/*.kt", "**.kts", "!**/build/**")
}

publishing {
    publications {
        create<MavenPublication>("ktm") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
