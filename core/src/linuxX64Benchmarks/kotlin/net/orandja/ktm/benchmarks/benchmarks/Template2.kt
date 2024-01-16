package net.orandja.ktm.benchmarks

import kotlinx.serialization.Serializable

@Serializable
data class _2_Context(
    val name: String,
    val value: Int,
    val taxed_value: Int,
    val in_ca: Boolean,
)

val __2_template = """
Hello {{name}}
You have just won {{value}} dollars!
{{#in_ca}}
    Well, {{taxed_value}} dollars, after taxes.
{{/in_ca}}
""".trimIndent()

val __2_context = """
{
  "name": "Chris",
  "value": 10000,
  "taxed_value": 6000,
  "in_ca": true
}
""".trimIndent()