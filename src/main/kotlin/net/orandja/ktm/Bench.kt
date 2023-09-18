package net.orandja.ktm

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import net.orandja.ktm.base.MPool
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.io.StringReader
import java.io.StringWriter

@State(Scope.Benchmark)
class Bench {
    data class Context(val section: Boolean, val data: String)

    val document =
        "[\n{{#section}}\n  {{data}}\n  |data|\n{{/section}}\n\n {{# section }} other {{/ section }} {{= | | =}}\n|#section|\n  {{data}}\n  |data|\n|/section|\n]\n"
    val context = KTM.ctx.make {
        "section" by true
        "data" by "I got Interpolated"
    }
    val doc1 = KTM.doc.string(document)
    val mfContext = Context(true, "I got Interpolated")
    val mf: MustacheFactory = DefaultMustacheFactory()
    val mustache = mf.compile(StringReader(document), "document")

    @Benchmark
    fun java() {
        val writer = StringWriter()
        mustache.execute(writer, mfContext)
        writer.toString()
    }

    @Benchmark
    fun ktm() {
        doc1.render(context, MPool.Empty, KTM.renderer)
    }
}