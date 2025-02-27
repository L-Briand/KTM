package net.orandja.ktm.plugin.sample


class Hello {
    fun world() = "Hello World!"
}

data class User(val name: String)


fun main() {
    val user = User("FUCKITI").copy( name = "F")
    println(user)
}