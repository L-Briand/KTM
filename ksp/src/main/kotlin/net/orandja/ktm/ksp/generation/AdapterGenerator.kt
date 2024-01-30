package net.orandja.ktm.ksp.generation

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import net.orandja.ktm.EnumKtmAdapter
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.ksp.visitor.VisitorResult
import net.orandja.ktm.streamRender
import net.orandja.ktm.toMustacheDocument

internal object AdapterGenerator {

    private val partials = Ktm.ctx.make {
        "_class" by AdapterToken.NormalClass.Template.toMustacheDocument()
        "_class_field" by AdapterToken.Field.Template.toMustacheDocument()
        "_enum_class" by AdapterToken.EnumClass.Template.toMustacheDocument()
    }

    private val adapters = Ktm.adapters.make {
        +EnumKtmAdapter<AdapterToken.Kind>()
        +AdapterToken
        +AdapterToken.NormalClass.Adapter
        +AdapterToken.EnumClass.Adapter
        +AdapterToken.Field.Adapter
    }

    private data class GenerationInfo(
        val packageName: String,
        val simpleName: String,
        val sanitizedSimpleName: String,
        val context: MContext,
    )

    fun generateKtmAdapterFile(
        logger: KSPLogger,
        generator: CodeGenerator,
        element: VisitorResult.ClassAdapterToken
    ): GeneratedFile {
        val gen = when (val tk = element.token) {
            is AdapterToken.NormalClass -> GenerationInfo(
                tk.packageName,
                tk.simpleName,
                tk.sanitizedSimpleName,
                Ktm.ctx.make(adapters) {
                    configureLike(tk)
                    addBackingContext(partials)
                }
            )

            is AdapterToken.EnumClass -> GenerationInfo(
                tk.packageName,
                tk.simpleName,
                tk.sanitizedSimpleName,
                Ktm.ctx.make(adapters) {
                    addBackingContext(partials)
                    configureLike(tk)
                }
            )

            else -> error("Invalid token to generate")
        }

        val fileName = "${gen.sanitizedSimpleName}KtmAdapter"
        logger.info("Compiling File ${gen.packageName}.$fileName.kt")
        val dependencies = Dependencies(true, element.classDeclaration.containingFile!!)
        generator.createNewFile(dependencies, gen.packageName, fileName).bufferedWriter().use { output ->
            AdapterToken.Kind.Template.streamRender(gen.context) { part ->
                output.write(part.toString())
            }
        }
        return GeneratedFile(gen.packageName, fileName)
    }
}