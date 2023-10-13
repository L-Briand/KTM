package net.orandja.ktm.composition.builder

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument

fun main() {
    val document = """
        {{# greeting }}
        Hello {{ name }},
        {{/ greeting }}
        
        tasks:
        {{# tasks }}
        - {{ . }}
        {{/ tasks }}
    """.trimIndent().toMustacheDocument()

    val user = mapOf("name" to "Jon")
    val tasks = listOf("Sleep", "Eat")

    fun tasks() = Ktm.ctx.makeList {
        + "Call for lunch."
        + stringDelegate {
            "Welcome ${getValue("mister")} to the office."
        }
    }

    val context: MContext = Ktm.ctx.make {
        "name" by "Jon"
        "mister" by "M. Smith"
        "greeting" by true
        "tasks" by tasks()
    }

    println(document.render(context))
}