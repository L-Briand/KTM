package net.orandja.ktm.benchmark

import com.github.mustachejava.Mustache
import kotlinx.benchmark.*
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MDocument

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class UserBenchmark {

    lateinit var LBriandTemplate: MDocument
    lateinit var SpullaraTemplate: Mustache

    @Setup
    fun setup() {
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
        LBriandTemplate = LBriandKtmParse()
        SpullaraTemplate = SpullaraMustacheParse()
    }

    @Benchmark
    fun LBriandKtmParse() = LBriandKtm.compile(User.TEMPLATE)

    @Benchmark
    fun SpullaraMustacheParse() = SpullaraMustache.compile(User.TEMPLATE)

    @Benchmark
    fun LBriandKtmRender() = LBriandKtm.render(LBriandTemplate, User.CONTEXT)

    @Benchmark
    fun SpullaraMustacheRender() = SpullaraMustache.render(SpullaraTemplate, User.CONTEXT)

}