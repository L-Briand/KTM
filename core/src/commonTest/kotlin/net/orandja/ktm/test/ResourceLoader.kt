package net.orandja.ktm.test

expect object ResourceLoader {
    fun load(name: String): TestResource
}