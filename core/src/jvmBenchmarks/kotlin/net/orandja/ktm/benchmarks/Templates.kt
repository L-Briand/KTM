package net.orandja.ktm.benchmarks

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

fun ClassLoader.readTemplateResource(name: String) =
    getResource(name)!!.openStream().bufferedReader().readText()

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ClassLoader.readJsonResource(name: String) =
    getResource(name)!!.openStream().use {
        Json.decodeFromStream<T>(it)
    }

@Serializable
data class _1_Context(
    val name: String
)

@Serializable
data class _2_Context(
    val name: String,
    val value: Int,
    val taxed_value: Int,
    val in_ca: Boolean,
)

@Serializable
data class _3_Context(
    val items: List<Item>,
) {
    @Serializable
    data class Item(
        val name: String,
        val price: Int,
        val countries: List<String>
    )
}