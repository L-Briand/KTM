package net.orandja.ktm.test

import net.orandja.ktm.Ktm
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class DelegatedContext {
    val context = Ktm.ctx.make {
        "secret" by "secret"
        "delegate" by delegate {
            value(findValue("secret"))
        }
        "value" by delegateValue { findValue("secret") ?: "not found" }
        "map" by delegateMap {
            when (it) {
                "~secret" -> find("secret")
                else -> null
            }
        }
        "list" by delegateList {
            listOf(find("secret")!!).iterator()
        }
    }

    @Test
    fun testDelegatedContext() {
        assertEquals("secret", "{{ secret }}".render(context))
        assertEquals("secret", "{{ delegate }}".render(context))
        assertEquals("secret", "{{ value }}".render(context))
        assertEquals("secret", "{{ map.~secret }}".render(context))
        assertEquals("secret", "{{# list }}{{.}}{{/ list }}".render(context))
    }
}