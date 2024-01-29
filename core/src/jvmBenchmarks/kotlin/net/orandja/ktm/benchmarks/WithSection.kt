package net.orandja.ktm.benchmarks

import kotlinx.benchmark.*

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class WithSection {
//    @Benchmark
//    fun parseSpullara() = SpullaraInfo.compileTemplate(SpullaraInfo._2_raw_template)
//
//    @Benchmark
//    fun parseKtm() = KtmInfo._2_raw_template.toMustacheDocument()
//
//    @Benchmark
//    fun renderSpullara() = SpullaraInfo.render(SpullaraInfo._2_template, SpullaraInfo._2_context)
//
//    @Benchmark
//    fun renderKtm() = KtmInfo._2_template.render(KtmInfo._2_context)
}