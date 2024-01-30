package net.orandja.ktm.benchmark

import com.github.mustachejava.Mustache
import kotlinx.benchmark.*
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class WinnerBenchmark {

    lateinit var LBriandTemplate: MDocument
    lateinit var SpullaraTemplate: Mustache

    @Setup
    fun setup() {
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
        LBriandTemplate = LBriandKtmParse()
        SpullaraTemplate = SpullaraMustacheParse()
    }

    @Benchmark
    fun LBriandKtmParse() = LBriandKtm.compile(Winner.TEMPLATE)

    @Benchmark
    fun SpullaraMustacheParse() = SpullaraMustache.compile(Winner.TEMPLATE)

    @Benchmark
    fun LBriandKtmRender() = LBriandKtm.render(LBriandTemplate, Winner.CONTEXT)

    @Benchmark
    fun SpullaraMustacheRender() = SpullaraMustache.render(SpullaraTemplate, Winner.CONTEXT)

}