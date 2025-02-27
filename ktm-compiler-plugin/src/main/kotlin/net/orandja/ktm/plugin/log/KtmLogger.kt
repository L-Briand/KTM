package net.orandja.ktm.plugin.log

import org.jetbrains.kotlin.util.Logger

interface KtmLogger : Logger {

    enum class Level(val severity: Int) {
        ERROR(0),
        WARNING(1),
        INFO(2),
        LOGGING(3),
        OUTPUT(4),
    }

    fun output(message: String)
    fun info(message: String)

    @Deprecated(Logger.Companion.FATAL_DEPRECATION_MESSAGE, ReplaceWith("error(message)"))
    override fun fatal(message: String): Nothing {
        throw IllegalStateException("Fatal error: $message")
    }

    companion object EmptyLogger : KtmLogger {
        override fun output(message: String) = Unit
        override fun info(message: String) = Unit
        override fun error(message: String) = Unit
        override fun log(message: String) = Unit
        override fun warning(message: String) = Unit
    }
}