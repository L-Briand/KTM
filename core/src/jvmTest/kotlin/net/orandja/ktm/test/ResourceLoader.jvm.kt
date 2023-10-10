package net.orandja.ktm.test

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

actual object ResourceLoader {

    private val codec = Json {
        ignoreUnknownKeys = true
    }

    actual fun load(name: String): TestResource {
        val raw = ResourceLoader::class.java.getResource("/$name")!!.readText()
        return codec.decodeFromString(serializer(), raw)
    }
}