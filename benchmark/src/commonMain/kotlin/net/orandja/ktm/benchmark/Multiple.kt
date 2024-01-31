package net.orandja.ktm.benchmark

import net.orandja.ktm.ksp.KtmContext
import kotlin.random.Random

@KtmContext
data class Multiple(
    val winners: List<Complex>,
    val winnersCount: Int = winners.size
) {
    companion object {
        val TEMPLATE = """
            <h1>Last's winners</h1>
            Total number of winners : {{ winnersCount }}
            {{#winners}}
                <li>
                    {{user.firstName}} {{user.lastName}} <br/>
                    Amount : {{value}}{{#isTaxed}} at {{ rate }}%{{/isTaxed}}
                </li>
            {{/winners}}
        """.trimIndent()

        @OptIn(ExperimentalStdlibApi::class)
        private fun Random.nextString(length: Int) = nextBytes(length).toHexString()
        private fun makeWinner(seed: Int) = with(Random(seed)) {
            val user = Simple(nextString(nextInt(4, 12)), nextString(nextInt(4, 12)))
            Complex(user, nextInt(5000, 100000), nextBoolean(), nextInt(10, 20))
        }

        val CONTEXT_X01 = Multiple(listOf(makeWinner(-1)))
        val CONTEXT_X10 = Multiple((0..<10).map { makeWinner(it) })
        val CONTEXT_X50 = Multiple((0..<50).map { makeWinner(it + 10) })
    }
}

