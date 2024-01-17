package net.orandja.ktm.benchmarks.base

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer

object SpullaraInfo {

    private val classLoader get() = SpullaraInfo::class.java.classLoader

    /** Same render condition as Ktm Renderer renderToString method. */
    fun <T : Any> render(template: Mustache, context: T): String {
        val writer: Writer = StringWriter(128)
        template.execute(writer, context)
        writer.flush()
        return writer.toString()
    }

    fun compileTemplate(template: String): Mustache {
        return DefaultMustacheFactory().compile(StringReader(template), "template")
    }

    val _1_raw_template = classLoader.readResource("_1_template.mustache")
    val _1_template = compileTemplate(_1_raw_template)
    val _1_context = classLoader.readJsonResource<_1_Context>("_1_context.json")

    val _2_raw_template = classLoader.readResource("_2_template.mustache")
    val _2_template = compileTemplate(_2_raw_template)
    val _2_context = classLoader.readJsonResource<_2_Context>("_2_context.json")

    val _3_raw_template = classLoader.readResource("_3_template.mustache")
    val _3_template = compileTemplate(_3_raw_template)
    val _3_context_x1 = classLoader.readJsonResource<_3_Context>("_3_context.1.json")
    val _3_context_x10 = classLoader.readJsonResource<_3_Context>("_3_context.10.json")
    val _3_context_x50 = classLoader.readJsonResource<_3_Context>("_3_context.50.json")

}