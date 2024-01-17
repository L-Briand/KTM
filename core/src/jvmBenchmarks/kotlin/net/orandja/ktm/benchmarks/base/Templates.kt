package net.orandja.ktm.benchmarks.base

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

fun ClassLoader.readResource(name: String) =
    getResource(name)!!.openStream().bufferedReader().readText()

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ClassLoader.readJsonResource(name: String) =
    getResource(name)!!.openStream().use {
        Json.decodeFromStream<T>(it)
    }

@Serializable
data class _1_Context(
    val first_name: String,
    val last_name: String,
)

@Serializable
data class _2_Context(
    val name: String,
    val value: Int,
    val rate: Int,
    val is_taxed: Boolean,
)

@Serializable
data class _3_Context(
    val users: List<Item>,
) {
    @Serializable
    data class Item(
        val name: String,
        val summary: String,
    )
}