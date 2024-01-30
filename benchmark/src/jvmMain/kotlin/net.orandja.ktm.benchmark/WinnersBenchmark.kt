package net.orandja.ktm.benchmark

import com.github.mustachejava.Mustache
import kotlinx.benchmark.*
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class WinnersBenchmark {

    lateinit var LBriandTemplate: MDocument
    lateinit var SpullaraTemplate: Mustache

    @Setup
    fun setup() {
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
        LBriandTemplate = LBriandKtmParse()
        SpullaraTemplate = SpullaraMustacheParse()
    }

    @Benchmark
    fun LBriandKtmParse() = LBriandKtm.compile(Winners.TEMPLATE)

    @Benchmark
    fun SpullaraMustacheParse() = SpullaraMustache.compile(Winners.TEMPLATE)

    @Benchmark
    fun LBriandKtmRenderX01() = LBriandKtm.render(LBriandTemplate, Winners.CONTEXT_X01)

    @Benchmark
    fun SpullaraMustacheRenderX01() = SpullaraMustache.render(SpullaraTemplate, Winners.CONTEXT_X01)

    @Benchmark
    fun LBriandKtmRenderX10() = LBriandKtm.render(LBriandTemplate, Winners.CONTEXT_X10)

    @Benchmark
    fun SpullaraMustacheRenderX10() = SpullaraMustache.render(SpullaraTemplate, Winners.CONTEXT_X10)

    @Benchmark
    fun LBriandKtmRenderX50() = LBriandKtm.render(LBriandTemplate, Winners.CONTEXT_X50)

    @Benchmark
    fun SpullaraMustacheRenderX50() = SpullaraMustache.render(SpullaraTemplate, Winners.CONTEXT_X50)

}