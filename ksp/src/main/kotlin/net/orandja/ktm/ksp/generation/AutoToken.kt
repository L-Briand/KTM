package net.orandja.ktm.ksp.generation

import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.contextOf
import org.intellij.lang.annotations.Language

data class AutoToken(
    val packageName: String,
    val adapters: List<GeneratedAdapter>,
) {

    companion object {
        const val FILE_NAME = "AutoKtmAdaptersModule"
        val Adapter = KtmMapAdapter<AutoToken> {
            "package_name" by it.packageName
            "adapters" by contextOf(it.adapters)
        }

        @Language("mustache")
        val Template = """
            package {{ package_name }}
            
            import net.orandja.ktm.adapters.KtmAdapterProviderBuilder
            import net.orandja.ktm.adapters.KtmAdapterModule
            {{# adapters }}
            import {{ package_name }}.{{ file_name }}
            {{/adapters}}

            data object $FILE_NAME : KtmAdapterModule() {
                override fun KtmAdapterProviderBuilder.configure() {
                    {{# adapters }}
                    +{{ file_name }}
                    {{/adapters}} 
                }
            }
        """.trimIndent()
    }

    data class GeneratedAdapter(
        val packageName: String,
        val fileName: String,
    ) {
        companion object {
            val Adapter = KtmMapAdapter<GeneratedAdapter> {
                "package_name" by it.packageName
                "file_name" by it.fileName
            }
        }
    }

}