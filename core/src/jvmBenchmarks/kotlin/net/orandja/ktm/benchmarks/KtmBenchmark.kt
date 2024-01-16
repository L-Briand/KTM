package net.orandja.ktm.benchmarks

import kotlinx.benchmark.*
import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.contextOf
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class KtmBenchmark {

    private val classLoader get() = KtmBenchmark::class.java.classLoader

    // This is what is generally build with @KtmContext annotation
    private val adapters = Ktm.adapters.make {
        +KtmMapAdapter<_1_Context> {
            "name" by it.name
        }
        +KtmMapAdapter<_2_Context> {
            "name" by it.name
            "value" by it.value
            "taxed_value" by it.taxed_value
            "in_ca" by it.in_ca
        }
        +KtmMapAdapter<_3_Context> {
            "items" by it.items
        }
        +KtmMapAdapter<_3_Context.Item> {
            "name" by it.name
            "price" by it.price
            "features" by it.countries
        }
    }

    private val _1_raw_template = classLoader.readTemplateResource("_1_template.mustache")
    private val _1_template = _1_raw_template.toMustacheDocument()
    private val _1_context = adapters.contextOf(classLoader.readJsonResource<_1_Context>("_1_context.json"))

    @Benchmark
    fun _1_parse() = _1_raw_template.toMustacheDocument()

    @Benchmark
    fun _1_render() = _1_template.render(_1_context)

    private val _2_raw_template = classLoader.readTemplateResource("_2_template.mustache")
    private val _2_template = _2_raw_template.toMustacheDocument()
    private val _2_context = adapters.contextOf(classLoader.readJsonResource<_2_Context>("_2_context.json"))


    @Benchmark
    fun _2_parse() = _2_raw_template.toMustacheDocument()

    @Benchmark
    fun _2_render() = _2_template.render(_2_context)

    private val _3_raw_template = classLoader.readTemplateResource("_3_template.mustache")
    private val _3_template = _3_raw_template.toMustacheDocument()
    private val _3_context = adapters.contextOf(classLoader.readJsonResource<_3_Context>("_3_context.json"))


    @Benchmark
    fun _3_parse() = _3_raw_template.toMustacheDocument()

    @Benchmark
    fun _3_render() = _3_template.render(_3_context)
}