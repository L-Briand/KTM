package net.orandja.ktm.ksp.sample

import net.orandja.ktm.contextOf
import net.orandja.ktm.render
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class EnumCaseTest {
    @Test
    fun assertAdapters() {
        EnumWithInnerKtmAdapter
        EnumWithInner_EnumKtmAdapter
        EnumWithPropertyKtmAdapter
    }

    @Test
    fun defaultEnum() {
        val context = AutoKtmAdaptersModule.createAdapters().contextOf(EnumWithProperty.FOO)
        assertEquals("FOO", "{{ name }}".render(context))
        assertEquals("0", "{{ ordinal }}".render(context))
        assertEquals("foo", "{{# FOO }}foo{{/ FOO }}".render(context))
        assertEquals("!bar", "{{^ BAR }}!bar{{/ BAR }}".render(context))
        assertEquals("<FOO><BAR><BAZ>", "{{# values }}<{{.}}>{{/ values }}".render(context))
    }
}