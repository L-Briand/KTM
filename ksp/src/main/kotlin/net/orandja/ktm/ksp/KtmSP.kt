package net.orandja.ktm.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate

class KtmSP(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger get() = environment.logger
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotated = resolver.getSymbolsWithAnnotation("").filter {
            logger.warn("$it")
            it.validate()
        }
        annotated.forEach { logger.warn("$it") }
        return annotated.toList()
    }
}