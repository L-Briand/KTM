package net.orandja.ktm.test

import net.orandja.ktm.*
import net.orandja.ktm.adapters.DelegatedKtmAdapter
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.typeKey
import net.orandja.ktm.base.MContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class AdapterExtensions {
    enum class EnumVariants {
        FOO, BAR, BAZ;
    }

    open class Foo(val value: String) {
        object Adapter : KtmAdapter<Foo?> {
            override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Foo?): MContext {
                return Ktm.ctx.make {
                    "value" by value?.value
                }
            }
        }
    }

    class ExtendedFoo(foo: String) : Foo(foo)
    class Merged(val value: String)


    private val MergedKtmAdapter = KtmAdapter<Merged> { adapters, value ->
        Ktm.ctx.make(adapters) {
            like(EnumVariants.FOO)
            like(ExtendedFoo(value.value))
        }
    }

    private val adapters = Ktm.adapters.create {
        +Foo.Adapter
        +DelegatedKtmAdapter<ExtendedFoo>(typeKey<Foo>())
        +EnumKtmAdapter<EnumVariants>()
        +MergedKtmAdapter
    }

    @Test
    fun defaultFactories() {
        assertEquals("(foo)(bar)", "({{.}})".render(arrayOf("foo", "bar")))
        assertEquals("(foo)(bar)", "({{.}})".render(listOf("foo", "bar").iterator()))
        assertEquals("(foo)(bar)", "({{.}})".render(listOf("foo", "bar").asIterable()))
        assertEquals("(foo)(bar)", "({{.}})".render(listOf("foo", "bar").asSequence()))
        assertEquals("(bar)", "({{foo}})".render(mapOf("foo" to "bar")))
        assertEquals("(bar)", "({{foo}})".render(mapOf<String, String>("foo" to "bar").entries.first()))
        val list = ArrayList<String>(1).apply { add("bar") }
        assertEquals("(bar)", "({{.}})".render(list))
    }

    @Test
    fun starProjection() {
        val data = mapOf("hello" to "world")
        assertEquals("world", "{{hello}}".render(data as Map<*, String>))
        assertFails { "".render(data as Map<String, *>) }
    }

    @Test
    fun invalidTypeKeyForFactory() {
        assertFails { Ktm.adapters.get(typeKey<List<String>>().noArgs()) }
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun getAdapterFromType() {
        assertEquals(Foo.Adapter, adapters.get<Foo?>())
        assertNotNull((adapters.get<ExtendedFoo>() as? DelegatedKtmAdapter<Foo>) != null)
        val name = EnumKtmAdapter<EnumVariants>().toString()
        assertEquals(name, adapters.get<EnumVariants>().toString())
        assertEquals(MergedKtmAdapter, adapters.get<Merged>())
    }

    @Test
    fun SimpleAdapterInProvider() {
        val context = Foo("foo").toMustacheContext(adapters)
        assertEquals("foo", "{{ value }}".render(context))
    }

    @Test
    fun testDelegate() {
        val context = ExtendedFoo("foo").toMustacheContext(adapters)
        assertEquals("foo", "{{ value }}".render(context))
    }

    @Test
    fun testMergedContext() {
        val context = Merged("string").toMustacheContext(adapters)
        // from Foo
        assertEquals("string", "{{ value }}".render(context))

        // from EnumVariants
        assertEquals("FOO", "{{ name }}".render(context))
        assertEquals("0", "{{ ordinal }}".render(context))
        assertEquals("foo", "{{# FOO }}foo{{/ FOO }}".render(context))
        assertEquals("!bar", "{{^ BAR }}!bar{{/ BAR }}".render(context))
        assertEquals("!baz", "{{^ BAZ }}!baz{{/ BAZ }}".render(context))
    }

    private fun renderNullableFoo(foo: Foo?): String {
        val adapters = Ktm.adapters.create { +Foo.Adapter }
        return "{{ value }}".render(adapters.contextOf<Foo?>(foo))
    }

    @Test
    fun nullableContextOf() {
        assertEquals("", renderNullableFoo(null))
        assertEquals("foo", renderNullableFoo(Foo("foo")))
    }
}