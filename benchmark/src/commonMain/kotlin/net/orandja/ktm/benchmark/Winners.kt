package net.orandja.ktm.benchmark

import net.orandja.ktm.ksp.KtmContext
import kotlin.random.Random

@KtmContext
data class Winners(
    val winners: List<Winner>,
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
            val user = User(nextString(nextInt(4, 12)), nextString(nextInt(4, 12)))
            Winner(user, nextInt(5000, 100000), nextBoolean(), nextInt(10, 20))
        }

        val CONTEXT_X01 = Winners(listOf(makeWinner(-1)))
        val CONTEXT_X10 = Winners((0..<10).map { makeWinner(it) })
        val CONTEXT_X50 = Winners((0..<50).map { makeWinner(it + 10) })
    }
}

