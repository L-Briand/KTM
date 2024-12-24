package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import kotlin.reflect.KType

/**
 * An interface that can be used to convert [T] types to a [MContext].
 *
 * ```kotlin
 * val stringAsList = KtmAdapter<String> { _, value ->
 *     Ktm.ctx.list(value.split("-"))
 * }
 *
 * val context = stringAsList.toMustacheContext("Hello-World")
 * "({{.}})".render(context) // (Hello)(World)
 * ```
 */
fun interface KtmAdapter<T> {

    /**
     * Transform the given [value] of type [T] into a [MContext].
     * It can use [adapters] to find other [KtmAdapter] inside [T]
     */
    fun toMustacheContext(adapters: Provider, value: T): MContext
    fun toMustacheContext(value: T): MContext = toMustacheContext(Ktm.adapters, value)

    /**
     * An interface that provides instances of [KtmAdapter] given its type.
     * The adapters inside are to be stored and generated.
     *
     * @see DefaultKtmAdapterProvider
     */
    fun interface Provider {
        operator fun get(kType: TypeKey): KtmAdapter<*>?
        operator fun get(kType: KType): KtmAdapter<*>? = get(TypeKey(kType))
    }

    /**
     * KtmAdapter is not sufficient for generic types.
     *
     * With something like `Pair<X, Y>`, you can either:
     * - have a special adapters for each generic type. Which is hell, and you do not want that.
     * - have a factory for Pair<?,?> and let the [Provider] create the adapter on the fly.
     */
    fun interface Factory {
        fun create(types: List<TypeKey?>): KtmAdapter<*>?
    }

    /**
     * Something that can produce a single [KtmAdapter] handling multiple types conversions.
     *
     * A module always [configure] itself.
     * A module can merge with other modules with [createAdapters]
     *
     * @see Ktm.setDefaultAdapters
     * @see Ktm.resetDefaultAdapters
     */
    abstract class Module {
        fun createAdapters(vararg modules: Module) = Ktm.adapters.create {
            for (module in modules) with(module) { configure() }
            configure()
        }

        abstract fun DefaultKtmAdapterProvider.Builder.configure()
    }

    companion object {
        fun buildModule(configuration: DefaultKtmAdapterProvider.Builder.() -> Unit): Module = object : Module() {
            override fun DefaultKtmAdapterProvider.Builder.configure() {
                configuration()
            }
        }
    }
}
