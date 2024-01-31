package net.orandja.ktm.test

import net.orandja.ktm.*
import net.orandja.ktm.adapters.KtmAdapter
import kotlin.test.Test
import kotlin.test.assertEquals

class EnumTransformationTest {
    enum class EnumVariants {
        FOO, BAR, BAZ;
    }

    @Test
    fun testContextTransformation() {
        val context = EnumVariants.FOO.toMustacheContext()
        assertEquals("FOO", "{{ name }}".render(context))
        assertEquals("0", "{{ ordinal }}".render(context))
        assertEquals("foo", "{{# FOO }}foo{{/ FOO }}".render(context))
        assertEquals("!bar", "{{^ BAR }}!bar{{/ BAR }}".render(context))
        assertEquals("!baz", "{{^ BAZ }}!baz{{/ BAZ }}".render(context))
        assertEquals("<FOO><BAR><BAZ>", "{{# values }}<{{.}}>{{/ values }}".render(context))
    }

    val CustomAdapter = KtmAdapter<EnumVariants> { adapters, value ->
        Ktm.ctx.make {
            when (value) {
                EnumVariants.FOO -> "FOO" by "A"
                EnumVariants.BAR -> "BAR" by "B"
                EnumVariants.BAZ -> "BAZ" by "C"
            }
        }
    }

    @Test
    fun testContextAdapter() {
        val adapters = Ktm.adapters.make { +CustomAdapter }
        val context = EnumVariants.FOO.toMustacheContext(adapters)
        assertEquals("A", "{{ FOO }}".render(context))
        assertEquals("", "{{ BAR }}".render(context))
        assertEquals("", "{{ BAZ }}".render(context))
    }

    @Test
    fun delegateEnum() {
        val adapters = Ktm.adapters.make {
            +EnumKtmAdapter<EnumVariants>()
        }
        val enum = Ktm.ctx.make(adapters) {
            "enum" by delegate { adapters.contextOf(EnumVariants.FOO) }
        }
        assertEquals("FOO", "{{ enum.name }}".render(enum))
    }
}