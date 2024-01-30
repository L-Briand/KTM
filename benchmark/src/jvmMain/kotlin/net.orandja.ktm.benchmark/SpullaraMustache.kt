package net.orandja.ktm.benchmark

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer

object SpullaraMustache {
    fun compile(template: String): Mustache = DefaultMustacheFactory().compile(StringReader(template), "template")
    fun <T : Any> render(template: Mustache, context: T): String {
        val writer: Writer = StringWriter(128)
        template.execute(writer, context)
        writer.flush()
        return writer.toString()
    }
}