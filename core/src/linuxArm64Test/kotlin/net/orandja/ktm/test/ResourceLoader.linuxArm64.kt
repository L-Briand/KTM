package net.orandja.ktm.test

import kotlinx.cinterop.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

actual object ResourceLoader {

    private val codec = Json {
        ignoreUnknownKeys = true
    }

    actual fun load(name: String): TestResource {
        val text = readAllText("src/commonTest/resources/$name")
        return codec.decodeFromString(serializer(), text)
    }

    // Shamefully stolen from https://www.nequalsonelifestyle.com/2020/11/16/kotlin-native-file-io/
    @OptIn(ExperimentalForeignApi::class)
    fun readAllText(filePath: String): String {
        val returnBuffer = StringBuilder()
        val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open input file $filePath")

        try {
            memScoped {
                val readBufferLength = 8 * 1024
                val buffer = allocArray<ByteVar>(readBufferLength)
                var line = fgets(buffer, readBufferLength, file)?.toKString()
                while (line != null) {
                    returnBuffer.append(line)
                    line = fgets(buffer, readBufferLength, file)?.toKString()
                }
            }
        } finally {
            fclose(file)
        }

        return returnBuffer.toString()
    }
}