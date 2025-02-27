package net.orandja.ktm.plugin.log

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class MessageCollectorLogger(
    private val messageCollector: MessageCollector,
) : KtmLogger {

    override fun output(message: String) {
        messageCollector.report(CompilerMessageSeverity.OUTPUT, message)
    }

    override fun log(message: String) {
        messageCollector.report(CompilerMessageSeverity.LOGGING, message)
    }

    override fun info(message: String) {
        messageCollector.report(CompilerMessageSeverity.INFO, message)
    }

    override fun warning(message: String) {
        messageCollector.report(CompilerMessageSeverity.WARNING, message)
    }

    override fun strongWarning(message: String) {
        messageCollector.report(CompilerMessageSeverity.STRONG_WARNING, message)
    }

    override fun error(message: String) {
        messageCollector.report(CompilerMessageSeverity.ERROR, message)
    }
}