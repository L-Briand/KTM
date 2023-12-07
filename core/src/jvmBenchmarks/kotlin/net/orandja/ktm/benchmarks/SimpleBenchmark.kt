package net.orandja.ktm.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.render

@State(Scope.Benchmark)
class SimpleBenchmark {
    private lateinit var document: MDocument
    private lateinit var context: MContext


    @Setup
    fun setUp() {
        document = Ktm.doc.string("Hello {{ world }}!")
        context = Ktm.ctx.make {
            "world" by "world"
        }
    }

    @Benchmark
    fun render() {
        document.render(context)
    }
}