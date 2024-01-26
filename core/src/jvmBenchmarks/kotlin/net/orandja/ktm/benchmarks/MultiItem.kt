package net.orandja.ktm.benchmarks

import kotlinx.benchmark.*
import net.orandja.ktm.benchmarks.base.KtmInfo
import net.orandja.ktm.benchmarks.base.SpullaraInfo
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class MultiItem {
//    @Benchmark
//    fun parseSpullara() = SpullaraInfo.compileTemplate(SpullaraInfo._3_raw_template)
//
//    @Benchmark
//    fun parseKtm() = KtmInfo._3_raw_template.toMustacheDocument()

    @Benchmark
    fun renderSpullara_x1() = SpullaraInfo.render(SpullaraInfo._3_template, SpullaraInfo._3_context_x1)

    @Benchmark
    fun renderKtm_x1() = KtmInfo._3_template.render(KtmInfo._3_context_x1)

    @Benchmark
    fun renderSpullara_x10() = SpullaraInfo.render(SpullaraInfo._3_template, SpullaraInfo._3_context_x10)

    @Benchmark
    fun renderKtm_x10() = KtmInfo._3_template.render(KtmInfo._3_context_x10)

    @Benchmark
    fun renderSpullara_x50() = SpullaraInfo.render(SpullaraInfo._3_template, SpullaraInfo._3_context_x50)

    @Benchmark
    fun renderKtm_x50() = KtmInfo._3_template.render(KtmInfo._3_context_x50)
}