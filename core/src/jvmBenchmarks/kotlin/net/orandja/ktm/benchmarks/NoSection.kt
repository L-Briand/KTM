package net.orandja.ktm.benchmarks

import kotlinx.benchmark.*
import net.orandja.ktm.benchmarks.base.KtmInfo
import net.orandja.ktm.benchmarks.base.SpullaraInfo
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class NoSection {
//    @Benchmark
//    fun parseSpullara() = SpullaraInfo.compileTemplate(SpullaraInfo._1_raw_template)
//
//    @Benchmark
//    fun parseKtm() = KtmInfo._1_raw_template.toMustacheDocument()
//
//    @Benchmark
//    fun renderSpullara() = SpullaraInfo.render(SpullaraInfo._1_template, SpullaraInfo._1_context)
//
//    @Benchmark
//    fun renderKtm() = KtmInfo._1_template.render(KtmInfo._1_context)
}