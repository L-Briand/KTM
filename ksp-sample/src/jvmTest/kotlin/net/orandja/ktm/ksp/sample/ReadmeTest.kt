package net.orandja.ktm.ksp.sample

import net.orandja.ktm.Ktm
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheDocument
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReadmeTest {
    @Test
    fun _1() {
        Ktm.setDefaultAdapters { +UserKtmAdapter }
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule)

        val document = "Hello {{ name }}".toMustacheDocument()
        val data = User("John", "Doe")

        assertEquals("Hello John Doe", document.render(data))
    }

    @Test
    fun _2() {
        val document = "Hello {{ name }}".toMustacheDocument()
        val context = Ktm.ctx.make {
            "firstName" by "John"
            "lastName" by "Doe"
            "name" by delegateValue { "${findValue("firstName")} ${findValue("lastName")}" }
        }
        assertEquals("Hello John Doe", document.render(context))
    }

    @Test
    fun _3() {
        Ktm.setDefaultAdapters { +UserKtmAdapter }
        val john = User("John", "Doe")

        val documents = Ktm.ctx.make {
            "content" by "Hello {{ firstName }}".toMustacheDocument()
            "header" by "Header for {{ firstName }}"
        }

        val template = "{{> header }}\n\n{{> content }}".toMustacheDocument()

        val context = Ktm.ctx.make {
            like(documents)
            like(john)
        }

        assertEquals("Header for John\nHello John", template.render(context))
    }
}