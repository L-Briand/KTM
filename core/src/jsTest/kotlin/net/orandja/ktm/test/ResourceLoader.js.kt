package net.orandja.ktm.test

import kotlinx.serialization.json.Json


actual object ResourceLoader {

    private val codec = Json {
        ignoreUnknownKeys = true
    }

    actual fun load(name: String): TestResource {
        val fs = js("require('fs')")
        val file = js("process.cwd()") + "/kotlin/$name"
        val content = fs.readFileSync(file, "utf8") as String
        return codec.decodeFromString<TestResource>(TestResource.serializer(), content)
    }
}