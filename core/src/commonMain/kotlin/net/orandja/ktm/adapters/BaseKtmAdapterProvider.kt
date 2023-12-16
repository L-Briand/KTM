package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * BaseKtmAdapter is an open class that implements the [KtmAdapter.Provider] interface.
 * It provides a default set of adapters for primitives types.
 *
 * You can extend it to create your own or use the [make] method to construct another provider on top of the current one.
 *
 * @see KtmAdapterProviderBuilder
 */
open class BaseKtmAdapterProvider : KtmAdapter.Provider {

    var enableAnyKtmAdapter = true

    fun make(
        base: KtmAdapter.Provider? = Ktm.adapters,
        configuration: KtmAdapterProviderBuilder.() -> Unit
    ): BaseKtmAdapterProvider = KtmAdapterProviderBuilder(base).apply(configuration).build()

    fun make(
        base: KtmAdapter.Provider? = Ktm.adapters,
        adapters: Map<KType, KtmAdapter<*>>,
    ): BaseKtmAdapterProvider = KtmAdapterProvider(base, adapters)

    override fun get(kType: KType): KtmAdapter<*>? = when (val kClass = kType.asKClass()) {

        // Adapter for primitive
        // Others falls under the AnyKtmAdapter which is used when nothing is found.
        String::class -> StringKtmAdapter
        Boolean::class -> BooleanKtmAdapter

        // Adapter for primitive arrays

        ByteArray::class -> ByteArrayKtmAdapter
        CharArray::class -> CharArrayKtmAdapter
        ShortArray::class -> ShortArrayKtmAdapter
        IntArray::class -> IntArrayKtmAdapter
        LongArray::class -> LongArrayKtmAdapter
        FloatArray::class -> FloatArrayKtmAdapter
        DoubleArray::class -> DoubleArrayKtmAdapter
        BooleanArray::class -> BooleanArrayKtmAdapter

        // Adapter for kotlin.collections package

        MutableListIterator::class,
        MutableIterator::class,
        ListIterator::class,
        Iterator::class -> IteratorKtmAdapter(kType.requireTypeProjection(0))

        Iterable::class,
        Collection::class,
        Set::class,
        List::class,
        MutableIterable::class,
        MutableCollection::class,
        MutableSet::class,
        MutableList::class -> IterableKtmAdapter(kType.requireTypeProjection(0))

        Sequence::class -> SequenceKtmAdapter(kType.requireTypeProjection(0))

        Map::class,
        MutableMap::class -> MapKtmAdapter(kType.requireTypeProjection(1))

        Map.Entry::class,
        MutableMap.MutableEntry::class -> MapEntryKtmAdapter(kType.requireTypeProjection(1))

        else -> {
            // Arrays are a pain. All `Array` class falls here even if the Array is not `kotlin.Array`
            // JS do not allow qualified name on kClass, so it is impossible to check for `kotlin.Array` string.
            when (kClass.simpleName) {
                Array::class.simpleName -> ArrayKtmAdapter(kType.requireTypeProjection(0))
                else -> if(enableAnyKtmAdapter) AnyKtmAdapter else null
            }
        }
    }

    /** Return `Type` in `Class<Type>` */
    private fun KType.requireTypeProjection(position: Int): KType {
        val projection = arguments.getOrNull(position)
            ?: error("There is no projection at positional argument '$position' on $this")
        return projection.type ?: error("Cannot create adapter for 'Nothing' type ($this)")
    }

    @Suppress("UNCHECKED_CAST")
    private fun KType.asKClass() = when (val classifier = classifier) {
        is KClass<*> -> classifier
        else -> error("Only KClass supported as type classifier, got $classifier")
    } as KClass<Any>
}