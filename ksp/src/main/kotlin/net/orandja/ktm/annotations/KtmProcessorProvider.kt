package net.orandja.ktm.annotations

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Factory class defined in the ksp plugin META-INF service, providing processor to kotlin's KSP plugin.
 */
class KtmProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = KtmSymbolProcessor(environment)
}
