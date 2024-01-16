package net.orandja.ktm.test

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheContext
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

    val CustomAdapter = KtmMapAdapter<EnumVariants> { value ->
        when (value) {
            EnumVariants.FOO -> "FOO" by "A"
            EnumVariants.BAR -> "BAR" by "B"
            EnumVariants.BAZ -> "BAZ" by "C"
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
}