package net.orandja.ktm.adapters

import net.orandja.ktm.NoKtmAdapterException
import net.orandja.ktm.adapters.defaults.*
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import kotlin.jvm.JvmStatic

/**
 * A map containing default adapters for various types, mapping each [TypeKey] to a corresponding [KtmAdapter].
 *
 * This map is used internally to provide type-specific conversion logic when transforming values
 * into a [MContext]. It includes adapters for primitive types, arrays, iterators,
 * and specific types like `MContext` and `MDocument`.
 *
 * Adapters defined in this map handle conversions for common data structures and ensure compatibility
 * with the rendering process in the Ktm library.
 *
 * The keys are instances of [TypeKey], which provide a more efficient way to compare Kotlin types.
 * The values are instances of [KtmAdapter], which define the conversion logic for their respective types.
 *
 * The map includes:
 * - Adapters for primitive types (e.g., String, Boolean, Int, Double, etc.).
 * - Adapters for array types (e.g., ByteArray, IntArray, etc.).
 * - Adapters for iterator types (e.g., ByteIterator, IntIterator, etc.).
 * - Adapters for custom types like `MContext` and its variants.
 * - Adapters for document-related types like `MDocument` and its subtypes.
 */
val DEFAULT_KTM_ADAPTERS = mapOf<TypeKey, KtmAdapter<*>>(
    // Primitives
    typeKey<String>() to StringKtmAdapter,
    typeKey<Boolean>() to BooleanKtmAdapter,
    typeKey<Int>() to IntKtmAdapter,
    typeKey<Long>() to LongKtmAdapter,
    typeKey<Short>() to ShortKtmAdapter,
    typeKey<Float>() to FloatKtmAdapter,
    typeKey<Double>() to DoubleKtmAdapter,

    // Arrays
    typeKey<ByteArray>() to ByteArrayKtmAdapter,
    typeKey<CharArray>() to CharArrayKtmAdapter,
    typeKey<ShortArray>() to ShortArrayKtmAdapter,
    typeKey<IntArray>() to IntArrayKtmAdapter,
    typeKey<LongArray>() to LongArrayKtmAdapter,
    typeKey<FloatArray>() to FloatArrayKtmAdapter,
    typeKey<DoubleArray>() to DoubleArrayKtmAdapter,
    typeKey<BooleanArray>() to BooleanArrayKtmAdapter,

    // Iterators
    typeKey<ByteIterator>() to ByteIteratorKtmAdapter,
    typeKey<ShortIterator>() to ShortIteratorKtmAdapter,
    typeKey<IntIterator>() to IntIteratorKtmAdapter,
    typeKey<LongIterator>() to LongIteratorKtmAdapter,
    typeKey<BooleanIterator>() to BooleanIteratorKtmAdapter,
    typeKey<FloatIterator>() to FloatIteratorKtmAdapter,
    typeKey<DoubleIterator>() to DoubleIteratorKtmAdapter,
    typeKey<CharIterator>() to CharIteratorKtmAdapter,


    // MContext
    typeKey<MContext>() to MContextAdapter,
    typeKey<MContext.No>() to MContextAdapter,
    typeKey<MContext.Yes>() to MContextAdapter,
    typeKey<MContext.Value>() to MContextAdapter,
    typeKey<MContext.Map>() to MContextAdapter,
    typeKey<MContext.List>() to MContextAdapter,
    typeKey<MContext.Document>() to MContextAdapter,
    typeKey<MContext.Delegate>() to MContextAdapter,

    // MDocument
    typeKey<MDocument>() to MDocumentAdapter,
    typeKey<MDocument.Static>() to MDocumentAdapter,
    typeKey<MDocument.Partial>() to MDocumentAdapter,
    typeKey<MDocument.Tag>() to MDocumentAdapter,
    typeKey<MDocument.Section>() to MDocumentAdapter,
)

/**
 * A map containing default factories for creating [KtmAdapter] instances based on specific types.
 *
 * The keys in the map are [TypeKey] instances representing specific types or generic type structures,
 * and the values are factories of type [KtmAdapter.Factory], which can generate [KtmAdapter]s for those types.
 *
 * This map includes default factories for various Kotlin collections such as arrays, lists, sets, iterators,
 * sequences, maps, and more. Each entry links a type key to a corresponding factory that can handle the creation
 * of an adapter for it.
 *
 * The predefined factories cover:
 * - `Array`
 * - Collectibles: `List`, `Set`, `Collection`, `ArrayList`, `LinkedHashSet`, etc.
 * - Iterables: `Iterable`, `MutableIterable`, `Iterator`, `ListIterator`, `MutableIterator`, etc.
 * - Sequences: `Sequence`
 * - Maps: `Map`, `MutableMap`, and `Map.Entry`, including specific map types like `LinkedHashMap`.
 */
val DEFAULT_KTM_FACTORIES = mapOf<TypeKey, KtmAdapter.Factory>(
    // Array
    typeKey<Array<*>>().noArgs() to ArrayKtmAdapter.Factory,

    // Iterable
    typeKey<List<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<MutableList<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<Set<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<MutableSet<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<Collection<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<MutableCollection<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<Iterable<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<MutableIterable<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<ArrayList<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<LinkedHashSet<*>>().noArgs() to IterableKtmAdapter.Factory,
    typeKey<ArrayDeque<*>>().noArgs() to IterableKtmAdapter.Factory,

    // Iterator
    typeKey<ListIterator<*>>().noArgs() to IteratorKtmAdapter.Factory,
    typeKey<Iterator<*>>().noArgs() to IteratorKtmAdapter.Factory,
    typeKey<MutableIterator<*>>().noArgs() to IteratorKtmAdapter.Factory,
    typeKey<MutableListIterator<*>>().noArgs() to IteratorKtmAdapter.Factory,

    // Sequence
    typeKey<Sequence<*>>().noArgs() to SequenceKtmAdapter.Factory,

    // Map
    typeKey<Map<*, *>>().noArgs() to MapKtmAdapter.Factory,
    typeKey<MutableMap<*, *>>().noArgs() to MapKtmAdapter.Factory,
    typeKey<Map.Entry<*, *>>().noArgs() to MapEntryKtmAdapter.Factory,
    typeKey<MutableMap.MutableEntry<*, *>>().noArgs() to MapEntryKtmAdapter.Factory,
    typeKey<LinkedHashMap<*, *>>().noArgs() to MapKtmAdapter.Factory,
)

/**
 * A Simple implementation of [KtmAdapter.Provider].
 *
 * @param backing An optional backup provider that can be used if the adapter is not found in the current provider.
 * @param factories A map of factories capable of creating adapters for specific types.
 * @param adapters A map of pre-defined adapters for specific types.
 *
 * @see KtmAdapter
 * @see KtmAdapter.Factory
 * @see KtmAdapter.Provider
 * @see TypeKey
 */
open class DefaultKtmAdapterProvider(
    private val backing: KtmAdapter.Provider?,
    private val factories: Map<TypeKey, KtmAdapter.Factory> = DEFAULT_KTM_FACTORIES,
    private val adapters: Map<TypeKey, KtmAdapter<*>> = DEFAULT_KTM_ADAPTERS,
) : KtmAdapter.Provider {

    /**
     * Create a new set of adapters on top of the current one [backing] with the given [configuration]
     *
     * Usage:
     * ```kotlin
     * @KtmContext
     * class Foo(val foo: String)
     *
     * @KtmContext
     * enum class Status { LOADING, VISIBLE, HIDDEN }
     *
     * val adapters = Ktm.adapter.make {
     *    + FooKtmAdapter // Auto generated by KSP
     *    + StatusKtmAdapter // Auto generated by KSP
     * }
     *
     * val context = Foo("value").toMustacheContext(adapters)
     * val context = adapters.contextOf(Status.LOADING)
     * ```
     *
     * @see DefaultKtmAdapterProvider.Builder
     */
    fun create(
        backing: KtmAdapter.Provider? = this,
        configuration: Builder.() -> Unit
    ): DefaultKtmAdapterProvider = Builder().apply(configuration).build(backing)

    protected val knownAdapters = mutableMapOf<TypeKey, KtmAdapter<*>?>()

    override fun get(kType: TypeKey): KtmAdapter<*>? = knownAdapters.getOrPut(kType) {
        adapters[kType]
            ?: factories[kType.noArgs()]?.create(kType.arguments)
            ?: getArrayFactory(kType)?.create(kType.arguments) // Special case for java arrays
            ?: backing?.get(kType)
    }

    /**
     * Construct [factories] and [adapters] in order to [build] a [DefaultKtmAdapterProvider].
     */
    class Builder {

        // region Factories

        private val factories = mutableMapOf<TypeKey, KtmAdapter.Factory>()

        /**
         * Add factories with a '+' symbol `+ MyFactory`. Type is deducted from [T]
         */
        inline operator fun <reified T> KtmAdapter.Factory.unaryPlus() = add<T>(this)

        /**
         * Add an [factory] for type [T]
         */
        inline fun <reified T> add(factory: KtmAdapter.Factory) {
            associate(typeKey<T>(), factory)
        }

        /**
         * associate [factory] with given [type] without type checks.
         */
        fun associate(type: TypeKey, factory: KtmAdapter.Factory) {
            factories[type] = factory
        }

        // endregion

        // region Adapters

        private val adapters = mutableMapOf<TypeKey, KtmAdapter<*>>()

        /**
         * Add adapters with a '+' symbol `+ MyAdapter`. Type is deducted from [T]
         */
        inline operator fun <reified T> KtmAdapter<T>.unaryPlus() = add<T>(this)

        /**
         * Add an [adapter] for type [T]
         */
        inline fun <reified T> add(adapter: KtmAdapter<T>) {
            associate(typeKey<T>(), adapter)
        }

        /**
         * associate [adapter] with given [type] without type checks.
         */
        fun associate(type: TypeKey, adapter: KtmAdapter<*>) {
            adapters[type] = adapter
        }

        // endregion

        /**
         * Builds a new instance of [DefaultKtmAdapterProvider] using the available factories and adapters.
         *
         * @param backing An optional provider of [KtmAdapter] instances used as a fallback if an adapter is not found in the current configuration.
         * @return A [DefaultKtmAdapterProvider] created with the given backing provider, along with the configured factories and adapters.
         */
        fun build(backing: KtmAdapter.Provider?) = DefaultKtmAdapterProvider(backing, factories, adapters)
    }
}