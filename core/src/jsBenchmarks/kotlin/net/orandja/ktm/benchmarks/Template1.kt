package net.orandja.ktm.benchmarks

import kotlinx.serialization.Serializable


@Serializable
data class _1_Context(
    val name: String
)

val __1_template = """
Hello {{ name }}!
""".trimIndent()

val __1_context = """
{
  "name": "world"
}
""".trimIndent()