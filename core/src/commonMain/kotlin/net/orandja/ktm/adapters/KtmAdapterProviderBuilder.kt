package net.orandja.ktm.adapters

import kotlin.reflect.KType
import kotlin.reflect.typeOf


/**
 * Construct a new set of adapter based on [backing].
 *
 * Usage:
 * ```kotlin
 * @KtmContext class Foo(val foo: String)
 * @KtmContext class Bar(val bar: String)
 * @KtmContext class Baz(val baz: String)
 *
 * val adapters = Ktm.adapter.make {
 *    + FooKtmAdapter
 *    add(BarKtmAdapter)
 *    associate(typeOf<Baz>(), BazKtmAdapter)
 * }
 *
 * val fooContext = Foo(foo).toMustacheContext(adapters)
 * ```
 *
 * @param backing The underlying [KtmAdapter.Provider] instance for unknown type queries.
 */
class KtmAdapterProviderBuilder(private val backing: KtmAdapter.Provider?) : BaseKtmAdapterProvider() {
    private val adapters = mutableMapOf<TypeKey, KtmAdapter<*>>()

    /**
     * Add adapters with a '+' symbol `+ MyAdapter`. Type is deducted from [T]
     */
    inline operator fun <reified T> KtmAdapter<T>.unaryPlus() = add<T>(this)

    /**
     * Add an [adapter] for type [T]
     */
    inline fun <reified T> add(adapter: KtmAdapter<T>) {
        associate(typeOf<T>(), adapter)
    }

    /**
     * associate [adapter] with given [type] without type checks.
     */
    fun associate(type: KType, adapter: KtmAdapter<*>) {
        adapters[TypeKey(type)] = adapter
    }

    /**
     * Implementation of [KtmAdapter.Provider] to build custom adapters from current builder
     */
    override fun get(kType: TypeKey): KtmAdapter<*>? {
        return adapters[kType] ?: backing?.get(kType) ?: super.get(kType)
    }

    fun build(): BaseKtmAdapterProvider = make(backing, adapters)
}