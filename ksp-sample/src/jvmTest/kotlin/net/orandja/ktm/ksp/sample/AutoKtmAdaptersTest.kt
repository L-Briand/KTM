package net.orandja.ktm.ksp.sample

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.builder.ContextMapBuilder
import net.orandja.ktm.makeKtmAdapterModule
import net.orandja.ktm.render
import net.orandja.ktm.toMustacheContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoKtmAdaptersTest {
    class Foo(val foo: String) {
        object Adapter : KtmMapAdapter<Foo> {
            override fun ContextMapBuilder.configure(value: Foo) {
                "foo" by value.foo
            }
        }
    }

    val customModule = makeKtmAdapterModule {
        +Foo.Adapter
    }

    @Test
    fun generatedAdapters() {
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule, customModule)
        var context: MContext
        context = Foo("value").toMustacheContext()
        assertEquals("value", "{{ foo }}".render(context))
        context = EnumWithProperty.FOO.toMustacheContext()
        assertEquals("FOO", "{{ name }}".render(context))
        context = ClassWithProperty("value").toMustacheContext()
        assertEquals("value", "{{ foo }}".render(context))
    }
}