package net.orandja.ktm.gradle.plugin

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

open class KtmPluginConfiguration {
    enum class Level { ERROR, WARNING, INFO, LOGGING, OUTPUT }

    val LEVEL_ERROR get() = Level.ERROR
    val LEVEL_WARNING get() = Level.WARNING
    val LEVEL_INFO get() = Level.INFO
    val LEVEL_LOGGING get() = Level.LOGGING
    val LEVEL_OUTPUT get() = Level.OUTPUT

    var loggingLevel: Level = LEVEL_WARNING
    var enableFor: KotlinCompilation<*>.() -> Boolean = { true }
}