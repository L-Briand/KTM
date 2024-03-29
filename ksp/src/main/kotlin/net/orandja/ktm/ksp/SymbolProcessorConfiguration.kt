package net.orandja.ktm.ksp

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

/** Configuration to use thorough symbol processing */
data class SymbolProcessorConfiguration(
    val allowInternalClass: Boolean,
    val packageName: String?,
) {
    companion object {
        const val OPTION_ALLOW_INTERNAL_CLASS = "ktm.allow_internal_class"
        const val OPTION_PACKAGE_NAME = "ktm.auto_adapters_package"

        fun fromEnvironment(env: SymbolProcessorEnvironment) = SymbolProcessorConfiguration(
            allowInternalClass = env.options[OPTION_ALLOW_INTERNAL_CLASS]?.toBooleanStrictOrNull() ?: false,
            packageName = env.options[OPTION_PACKAGE_NAME].let { if (it.isNullOrBlank()) null else it },
        )
    }
}