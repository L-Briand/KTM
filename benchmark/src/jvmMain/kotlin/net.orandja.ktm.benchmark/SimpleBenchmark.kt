package net.orandja.ktm.benchmark

import com.github.mustachejava.Mustache
import kotlinx.benchmark.*
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class SimpleBenchmark {

    lateinit var LBriandTemplate: MDocument
    lateinit var SpullaraTemplate: Mustache

    @Setup
    fun setup() {
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
        LBriandTemplate = LBriandKtmParse()
        SpullaraTemplate = SpullaraMustacheParse()
    }

    @Benchmark
    fun LBriandKtmParse() = LBriandKtm.compile(Simple.TEMPLATE)

    @Benchmark
    fun SpullaraMustacheParse() = SpullaraMustache.compile(Simple.TEMPLATE)

    @Benchmark
    fun LBriandKtmRender() = LBriandKtm.render(LBriandTemplate, Simple.CONTEXT)

    @Benchmark
    fun SpullaraMustacheRender() = SpullaraMustache.render(SpullaraTemplate, Simple.CONTEXT)

}