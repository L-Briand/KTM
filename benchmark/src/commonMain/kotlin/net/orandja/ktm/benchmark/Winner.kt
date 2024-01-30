package net.orandja.ktm.benchmark

import net.orandja.ktm.ksp.KtmContext


@KtmContext
data class Winner(
    val user: User,
    val amount: Int,
    val isTaxed: Boolean,
    val rate: Int,
) {
    companion object {
        val TEMPLATE = """
            <b>Hello {{user.firstName}} {{user.lastName}}</b>
            You have just won {{value}} dollars!</br>
            {{#isTaxed}}
            Today's win is taxed at {{ rate }}%
            {{/isTaxed}}
        """.trimIndent()

        val CONTEXT = Winner(
            user = User("John", "doe"),
            amount = 125_000,
            isTaxed = true,
            rate = 20,
        )
    }
}

