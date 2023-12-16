package net.orandja.ktm.ksp.generation

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import net.orandja.ktm.*
import net.orandja.ktm.base.MContext
import net.orandja.ktm.ksp.visitor.VisitorResult

internal object GenAdapter {

    private val pool = Ktm.pool.make {
        "_class" by AdapterToken.NormalClass.Template
        "_class_field" by AdapterToken.Field.Template
        "_enum_class" by AdapterToken.EnumClass.Template
    }

    private val adapters = Ktm.adapters.make {
        +EnumKtmAdapter<AdapterToken.Kind>()
        +AdapterToken.Kind.Adapter
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
                tk.toMustacheContext(adapters)
            )

            is AdapterToken.EnumClass -> GenerationInfo(
                tk.packageName,
                tk.simpleName,
                tk.sanitizedSimpleName,
                tk.toMustacheContext(adapters)
            )

            else -> error("Invalid token to generate")
        }

        val fileName = "${gen.sanitizedSimpleName}KtmAdapter"
        logger.info("Compiling File ${gen.packageName}.$fileName.kt")
        val dependencies = Dependencies(true, element.classDeclaration.containingFile!!)
        generator.createNewFile(dependencies, gen.packageName, fileName).bufferedWriter().use { output ->
            AdapterToken.Kind.Template.streamRender(gen.context, pool) { part ->
                output.write(part.toString())
            }
        }
        return GeneratedFile(gen.packageName, fileName)
    }
}