package net.orandja.ktm.benchmarks

import kotlinx.benchmark.*
import kotlinx.serialization.json.Json
import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.contextOf
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class KtmBenchmark {

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
            "features" by it.features
        }
    }

    private val _1_template = __1_template.toMustacheDocument()
    private val _1_context = adapters.contextOf(Json.decodeFromString<_1_Context>(__1_context))

    @Benchmark
    fun _1_render() = _1_template.render(_1_context)

    // Simple template

    private val _2_template = __2_template.toMustacheDocument()
    private val _2_context = adapters.contextOf(Json.decodeFromString<_2_Context>(__2_context))

    @Benchmark
    fun _2_render() = _2_template.render(_2_context)

    // Multi items


    private val _3_template = __3_template.toMustacheDocument()
    private val _3_context = adapters.contextOf(Json.decodeFromString<_3_Context>(__3_context))

    @Benchmark
    fun _3_render() = _3_template.render(_3_context)
}