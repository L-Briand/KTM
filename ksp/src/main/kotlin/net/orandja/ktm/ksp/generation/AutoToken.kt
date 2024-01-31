package net.orandja.ktm.ksp.generation

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.contextOf
import org.intellij.lang.annotations.Language

data class AutoToken(
    val packageName: String,
    val adapters: List<GeneratedAdapter>,
) {

    companion object {
        const val FILE_NAME = "AutoKtmAdaptersModule"
        val Adapter = KtmAdapter<AutoToken> { adapters, value ->
            Ktm.ctx.make(adapters) {
                "package_name" by value.packageName
                "adapters" by contextOf(value.adapters)
            }
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
            val Adapter = KtmAdapter<GeneratedAdapter> { adapters, value ->
                Ktm.ctx.make(adapters) {
                    "package_name" by value.packageName
                    "file_name" by value.fileName
                }
            }
        }
    }

}