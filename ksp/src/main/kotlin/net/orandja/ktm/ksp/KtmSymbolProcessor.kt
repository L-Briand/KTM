package net.orandja.ktm.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import net.orandja.ktm.ksp.generation.AdapterGenerator
import net.orandja.ktm.ksp.generation.AutoGenerator
import net.orandja.ktm.ksp.generation.AutoToken
import net.orandja.ktm.ksp.generation.GeneratedFile
import net.orandja.ktm.ksp.visitor.TokenBuilderVisitor
import net.orandja.ktm.ksp.visitor.VisitorResult

class KtmSymbolProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {

    companion object {
        private const val CONTEXT_ANNOTATION = "net.orandja.ktm.ksp.KtmContext"
    }

    private val configuration = SymbolProcessorConfiguration.fromEnvironment(env)
    private val logger = KtmLogger(env.logger)
    private val ktmVisitor = TokenBuilderVisitor(logger)

    private val generatedFile = mutableListOf<GeneratedFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotated = resolver.getSymbolsWithAnnotation(CONTEXT_ANNOTATION).filter { it.validate() }

        val options = TokenBuilderVisitor.Options(configuration.allowInternalClass)

        annotated.forEach {
            when (val element = it.accept(ktmVisitor, options)) {
                VisitorResult.Empty -> return@forEach
                is VisitorResult.Failure -> printFailure(element)
                is VisitorResult.ClassAdapterToken -> {
                    generatedFile += AdapterGenerator.generateKtmAdapterFile(logger, env.codeGenerator, element)
                }

                else -> logger.error("@KtmContext is applied on an invalid element", element.baseNode)
            }
        }

        return emptyList()
    }

    override fun finish() {
        if (generatedFile.isEmpty()) return
        configuration.packageName ?: return
        val generated = generatedFile.map { AutoToken.GeneratedAdapter(it.packageName, it.fileName) }
        val autoToken = AutoToken(configuration.packageName, generated)
        AutoGenerator.generateAutoKtmAdapters(logger, env.codeGenerator, autoToken)
    }

    private fun printFailure(element: VisitorResult.Failure, padding: String = "") {
        logger.error("$padding${element.message}", element.node)
        element.attached.onEach {
            printFailure(it, "$padding>")
        }
    }
}