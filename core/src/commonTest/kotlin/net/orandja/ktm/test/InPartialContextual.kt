package net.orandja.ktm.test

import net.orandja.ktm.Ktm
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class InPartialContextual {
    @Test
    fun test() {
        val context = Ktm.ctx.make {
            "foo" by make {
                "baz" by "TEST"
            }
            "bar" by document("{{ baz }}")
        }
        val template = "{{#foo}}{{>bar}}{{/foo}}"
        assertEquals("TEST", template.render(context))
    }
}