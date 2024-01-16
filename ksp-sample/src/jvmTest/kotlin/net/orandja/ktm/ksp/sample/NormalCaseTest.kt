package net.orandja.ktm.ksp.sample

import net.orandja.ktm.Ktm
import net.orandja.ktm.contextOf
import net.orandja.ktm.render
import kotlin.test.Test
import kotlin.test.assertEquals


class NormalCaseTest {
    @Test
    fun assertAdapters() {
        ClassWithInnerKtmAdapter
        ClassWithInner_ClassKtmAdapter
        ClassWithPropertyKtmAdapter
        ClassCallableKtmAdapter
    }

    @Test
    fun propertyClass() {
        val context = AutoKtmAdaptersModule.createAdapters().contextOf(ClassWithProperty())
        assertEquals("foo", "{{ foo }}".render(context))
        assertEquals("bar", "{{ bar }}".render(context))
        assertEquals("0", "{{ count }}".render(context))
        assertEquals("1", "{{ count }}".render(context))
        assertEquals("", "{{ _count }}".render(context))
    }

    @Test
    fun callableClass() {
        val data = ClassCallable("id")
        val adapters = AutoKtmAdaptersModule.createAdapters()
        val context = adapters.contextOf(data)
        val richContext = Ktm.ctx.make(adapters) {
            configureLike(data)
            "id" by "secret"
        }
        assertEquals("", "{{ id }}".render(context))
        assertEquals("id", "{{ function }}".render(context))
        assertEquals("id", "{{ lambda }}".render(context))
        assertEquals("id", "{{ getIdFunction }}".render(context))
        assertEquals("id", "{{ getIdLambda }}".render(context))
        assertEquals("id", "{{ lambdaNotTyped }}".render(context))
        assertEquals("secret", "{{ paramContextFunction }}".render(richContext))
        assertEquals("secret", "{{ paramContextLambda }}".render(richContext))
        assertEquals("secret", "{{ receiverContextFunction }}".render(richContext))
        assertEquals("secret", "{{ receiverContextLambda }}".render(richContext))
    }
}