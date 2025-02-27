package net.orandja.ktm.plugin

import net.orandja.ktm.plugin.log.MessageCollectorLogger
import org.jetbrains.kotlin.cli.common.messages.FilteringMessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageCollectorUtil
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

class KtmCompilerComponentRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration
    ) {
        val logger = MessageCollectorLogger(configuration.messageCollector)
        logger.output("hello !")
        logger.log("hello !")
        logger.info("hello !")
        logger.warning("hello !")
        logger.strongWarning("hello !")

        val ktmCompiler = KtmCompilerRegistrar(logger)
        FirExtensionRegistrarAdapter.registerExtension(ktmCompiler)
    }
}