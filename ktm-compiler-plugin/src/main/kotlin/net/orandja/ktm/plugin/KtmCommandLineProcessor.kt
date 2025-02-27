package net.orandja.ktm.plugin

import net.orandja.ktm.plugin.log.KtmLogger
import net.orandja.ktm.plugin.log.MessageCollectorLogger
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.messageCollector

class KtmCommandLineProcessor : CommandLineProcessor {

    companion object {
        val LOGGING_LEVEL_KEY = CompilerConfigurationKey<KtmLogger.Level>("loggingLevel")
    }

    override val pluginId: String = "net.orandja.ktm.ktm-compiler-plugin"

    override val pluginOptions: Collection<AbstractCliOption> = setOf(
        CliOption(
            optionName = "loggingLevel",
            valueDescription = KtmLogger.Level.entries.joinToString(",", "[", "]") { it.name },
            description = "Logging level for Ktm Compiler Plugin.",
            required = false,
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            "loggingLevel" -> {
                val level = KtmLogger.Level.entries.find { it.name.equals(value, true) }
                level ?: return
                configuration.put(LOGGING_LEVEL_KEY, level)
            }
        }
    }
}