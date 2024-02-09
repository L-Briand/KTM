package net.orandja.ktm.test

import net.orandja.ktm.*
import net.orandja.ktm.adapters.DelegatedKtmAdapter
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdapterExtensions {
    enum class EnumVariants {
        FOO, BAR, BAZ;
    }

    open class Foo(val value: String) {
        object Adapter : KtmAdapter<Foo> {
            override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Foo): MContext {
                return Ktm.ctx.make {
                    "value" by value.value
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

    private val adapters = Ktm.adapters.make {
        +Foo.Adapter
        +delegate<ExtendedFoo, Foo>()
        +EnumKtmAdapter<EnumVariants>()
        +MergedKtmAdapter
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun getAdapterFromType() {
        assertEquals(Foo.Adapter, adapters.get<Foo>())
        assertTrue { (adapters.get<ExtendedFoo>() as? DelegatedKtmAdapter<ExtendedFoo, Foo>) != null }
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
        assertEquals("<FOO><BAR><BAZ>", "{{# values }}<{{.}}>{{/ values }}".render(context))
    }

    private fun renderNullableFoo(foo: Foo?): String {
        val adapters = Ktm.adapters.make { +Foo.Adapter }
        return "{{ value }}".render(adapters.contextOf<Foo>(foo))
    }

    @Test
    fun nullableContextOf() {
        assertEquals("", renderNullableFoo(null))
        assertEquals("foo", renderNullableFoo(Foo("foo")))
    }
}