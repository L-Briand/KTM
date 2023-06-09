package net.orandja.ktm.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class JsonResource(
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
