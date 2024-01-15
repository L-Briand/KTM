package net.orandja.ktm.ksp.sample

import net.orandja.ktm.contextOf
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals

class ValueCaseTest {
    @Test
    fun transformedValueClass() {
        val context = AutoKtmAdaptersModule.createAdapters().contextOf(ValueClass("value"))
        assertEquals("value", "{{ foo }}".render(context))
    }
}