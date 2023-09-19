package net.orandja.ktm.test

expect object ResourceLoader {
    fun loadResource(name: String): JsonResource
}