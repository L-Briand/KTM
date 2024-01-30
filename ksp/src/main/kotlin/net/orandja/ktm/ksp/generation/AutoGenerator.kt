package net.orandja.ktm.ksp.generation

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import net.orandja.ktm.Ktm
import net.orandja.ktm.streamRender
import net.orandja.ktm.toMustacheContext

object AutoGenerator {
    val adapters = Ktm.adapters.make {
        +AutoToken.Adapter
        +AutoToken.GeneratedAdapter.Adapter
    }

    fun generateAutoKtmAdapters(
        logger: KSPLogger,
        generator: CodeGenerator,
        token: AutoToken
    ): GeneratedFile {
        val fileName = AutoToken.FILE_NAME
        logger.info("Compiling File ${token.packageName}.$fileName.kt")
        val dependencies = Dependencies(true)
        val context = token.toMustacheContext(adapters)
        generator.createNewFile(dependencies, token.packageName, fileName).bufferedWriter().use { output ->
            AutoToken.Template.streamRender(context) { part ->
                output.write(part.toString())
            }
        }
        return GeneratedFile(token.packageName, fileName)
    }
}