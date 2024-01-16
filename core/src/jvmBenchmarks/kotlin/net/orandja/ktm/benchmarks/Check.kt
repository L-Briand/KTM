package net.orandja.ktm.benchmarks

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class Check {
    val ktm = KtmBenchmark()
    val spullara = SpullaraBenchmark()
    @Setup
    fun init() {
        assert(ktm._1_render() == spullara._1_render()) {
            """
                Failed to render _1_:
                ktm:      ${ktm._1_render()}
                spullara: ${spullara._1_render()}
            """.trimIndent()
        }

        assert(ktm._2_render() == spullara._2_render()) {
            """
                Failed to render _2_:
                ktm:      ${ktm._2_render()}
                spullara: ${spullara._2_render()}
            """.trimIndent()
        }

        assert(ktm._3_render() == spullara._3_render()) {
            """
                Failed to render _3_:
                ktm:      ${ktm._3_render()}
                spullara: ${spullara._3_render()}
            """.trimIndent()
        }
    }
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    fun trigger() {

    }
}