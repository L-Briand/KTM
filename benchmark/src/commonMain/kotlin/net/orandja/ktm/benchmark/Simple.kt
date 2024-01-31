package net.orandja.ktm.benchmark

import net.orandja.ktm.ksp.KtmContext

@KtmContext
data class Simple(
    val firstName: String,
    val lastName: String,
) {
    companion object {
        val TEMPLATE = """
            <b>User:</b> {{ firstName }} {{ lastName }}
        """.trimIndent()

        val CONTEXT = Simple("John", "doe")
    }
}


