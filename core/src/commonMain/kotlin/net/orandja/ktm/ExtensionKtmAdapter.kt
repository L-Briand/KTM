package net.orandja.ktm

import net.orandja.ktm.adapters.DelegatedKtmAdapter
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.KtmAdapterModule
import net.orandja.ktm.adapters.KtmAdapterProviderBuilder
import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.NodeContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf


/** Thrown by extension function [get] when no provider is found for a given type */
class NoProviderException(type: KType) : IllegalArgumentException(
    "Failed to get adapter for type $type"
)

/**
 * Get the corresponding [KtmAdapter] of the given type [T] from the adapter provider.
 *
 * @receiver The [KtmAdapter.Provider] to search the [KtmAdapter] from.
 * @return a [KtmAdapter] for the specified type [T].
 */
@Throws(NoProviderException::class)
@Suppress("UNCHECKED_CAST")
inline fun <reified T> KtmAdapter.Provider.get(): KtmAdapter<T>? {
    val kType = typeOf<T>()
    return get(kType) as? KtmAdapter<T>
}


/**
 * Returns a [KtmAdapter] of type [T] from the adapter provider.
 * If the adapter is not found, it throws a [NoProviderException].
 *
 * @receiver The [KtmAdapter.Provider] to search the [KtmAdapter] from.
 * @return The [KtmAdapter] of type [T].
 * @throws NoProviderException if no provider is found for the given type [T].
 */
inline fun <reified T> KtmAdapter.Provider.getOrThrow(): KtmAdapter<T> {
    return get<T>() ?: throw NoProviderException(typeOf<T>())
}

/**
 * Returns a delegated [KtmAdapter] of type [T].
 * Given [T] extending [R] create a [KtmAdapter] of type [T] using [R]
 *
 * ```kotlin
 * @KtmContext
 * open class A(val foo: String)
 * class B(foo: String) : A(foo)
 *
 * val adapters = Ktm.adapters.make {
 *     + AKtmAdapter
 *     + delegate<B, A>()
 * }
 * val context = adapters.contextOf(B("bar"))
 * "{{ foo }}".render(context) // bar
 * ```
 *
 * @param adapter The adapter to be used as the delegate. By default, it is the [KtmAdapter] of type [R]
 * @return The [KtmAdapter] of type [T].
 * @throws NoProviderException if no provider is found for the given type [T].
 */
inline fun <reified T : R, reified R> KtmAdapter.Provider.delegate(
    adapter: KtmAdapter<R> = getOrThrow<R>()
): KtmAdapter<T> = DelegatedKtmAdapter(typeOf<T>(), adapter)

/**
 * Returns a [MContext] given the value type [T].

 *
 * @param value the value for which to get the [MContext].
 * @return the [MContext] of the given value.
 * @throws NoProviderException if no provider is found for the given type [T].
 */
@Throws(NoProviderException::class)
inline fun <reified T> KtmAdapter.Provider.contextOf(value: T?): MContext = when (value) {
    is MContext -> value
    null -> MContext.No
    else -> getOrThrow<T>().toMustacheContext(this, value)
}

/**
 * Returns a [MContext] of the given callable value (`() -> T`).
 *
 * @param value the callable value for which to get the [MContext].
 * @return the [MContext] of the given callable value.
 */
inline fun <reified T : () -> R, reified R> KtmAdapter.Provider.contextOfCallable(value: T?): MContext =
    if (value is MContext) value else Ktm.ctx.delegate { contextOf<R>(value?.invoke()) }

/**
 * Returns a [MContext] of the given callable value (`(NodeContext) -> T`).
 *
 * @param value the callable value for which to get the [MContext].
 * @return the [MContext] of the given callable value.
 */
inline fun <reified T : (NodeContext) -> R, reified R> KtmAdapter.Provider.contextOfNodeCallable(value: T?): MContext =
    if (value is MContext) value else Ktm.ctx.delegate { contextOf<R>(value?.invoke(this)) }

/**
 * Constructs a [KtmAdapterModule] instance with the given [configure] lambda.
 *
 * @param configure A lambda that is used to configure the [KtmAdapterProviderBuilder].
 * @return The constructed [KtmAdapterModule] instance.
 */
fun makeKtmAdapterModule(
    configure: KtmAdapterProviderBuilder.() -> Unit
): KtmAdapterModule = object : KtmAdapterModule() {
    override fun KtmAdapterProviderBuilder.configure() = configure()
}