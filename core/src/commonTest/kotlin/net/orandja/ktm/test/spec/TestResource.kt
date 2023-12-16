package net.orandja.ktm.test.spec

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TestResource(
    val overview: String,
    val tests: List<Test>,
) {
    @Serializable
    data class Test(
        val name: String,
        val desc: String,
        val template: String,
        val expected: String,
        val data: JsonElement? = null,
        val partials: Map<String, String>? = null,
    )
}
