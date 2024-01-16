package net.orandja.ktm.benchmarks

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import kotlinx.benchmark.*
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class SpullaraBenchmark {
    private val classLoader get() = SpullaraBenchmark::class.java.classLoader

    /** Same render condition as Ktm Renderer renderToString method. */
    private fun <T : Any> render(template: Mustache, context: T): String {
        val writer: Writer = StringWriter(128)
        template.execute(writer, context)
        writer.flush()
        return writer.toString()
    }

    private fun String.compileTemplate(): Mustache {
        return DefaultMustacheFactory().compile(StringReader(this), "template")
    }

    private val _1_raw_template = classLoader.readTemplateResource("_1_template.mustache")
    private val _1_template = _1_raw_template.compileTemplate()
    private val _1_context = classLoader.readJsonResource<_1_Context>("_1_context.json")

    @Benchmark
    fun _1_parse() = _1_raw_template.compileTemplate()

    @Benchmark
    fun _1_render() = render(_1_template, _1_context)

    private val _2_raw_template = classLoader.readTemplateResource("_2_template.mustache")
    private val _2_template = _2_raw_template.compileTemplate()
    private val _2_context = classLoader.readJsonResource<_2_Context>("_2_context.json")

    @Benchmark
    fun _2_parse() = _2_raw_template.compileTemplate()

    @Benchmark
    fun _2_render() = render(_2_template, _2_context)

    private val _3_raw_template = classLoader.readTemplateResource("_3_template.mustache")
    private val _3_template = _3_raw_template.compileTemplate()
    private val _3_context = classLoader.readJsonResource<_3_Context>("_3_context.json")

    @Benchmark
    fun _3_parse() = _3_raw_template.compileTemplate()

    @Benchmark
    fun _3_render() = render(_3_template, _3_context)
}