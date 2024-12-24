@file:Suppress("UNCHECKED_CAST")

package net.orandja.ktm.adapters.defaults

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.TypeKey
import net.orandja.ktm.base.MContext

// ARRAY

internal class ArrayKtmAdapter(
    private val type: TypeKey,
) : KtmAdapter<Array<*>> {

    internal object Factory : KtmAdapter.Factory {
        private const val NAME = "Array<?>"
        override fun create(types: List<TypeKey?>): KtmAdapter<*>? {
            if (types.size != 1) throw ArgumentCountException(NAME, types)
            if (types[0] == null) throw StarProjectionException(NAME, 0, types)
            return ArrayKtmAdapter(types[0]!!)
        }
    }

    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Array<*>): MContext {
        val adapter = adapters[type] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(Array<$type>)"
}

// LIST

internal class IterableKtmAdapter(
    private val type: TypeKey,
) : KtmAdapter<Iterable<*>> {

    internal object Factory : KtmAdapter.Factory {
        private const val NAME = "T : Iterable<?>"
        override fun create(types: List<TypeKey?>): KtmAdapter<*>? {
            if (types.size != 1) throw ArgumentCountException(NAME, types)
            if (types[0] == null) throw StarProjectionException(NAME, 0, types)
            return IterableKtmAdapter(types[0]!!)
        }
    }

    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Iterable<*>): MContext {
        val adapter = adapters[type] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(Iterable<$type>)"
}

internal class IteratorKtmAdapter(
    private val type: TypeKey
) : KtmAdapter<Iterator<*>> {

    internal object Factory : KtmAdapter.Factory {
        private const val NAME = "T : Iterator<?>"
        override fun create(types: List<TypeKey?>): KtmAdapter<*>? {
            if (types.size != 1) throw ArgumentCountException(NAME, types)
            if (types[0] == null) throw StarProjectionException(NAME, 0, types)
            return IteratorKtmAdapter(types[0]!!)
        }
    }

    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Iterator<*>): MContext {
        val adapter = adapters[type] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(Iterator<$type>)"
}


internal class SequenceKtmAdapter(
    private val type: TypeKey,
) : KtmAdapter<Sequence<*>> {

    internal object Factory : KtmAdapter.Factory {
        private const val NAME = "T : Sequence<?>"
        override fun create(types: List<TypeKey?>): KtmAdapter<*>? {
            if (types.size != 1) throw ArgumentCountException(NAME, types)
            if (types[0] == null) throw StarProjectionException(NAME, 0, types)
            return SequenceKtmAdapter(types[0]!!)
        }
    }

    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Sequence<*>): MContext {
        val adapter = adapters[type] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(Sequence<$type>)"
}

// MAP

internal class MapKtmAdapter(
    private val valueType: TypeKey,
) : KtmAdapter<Map<*, *>> {

    internal object Factory : KtmAdapter.Factory {
        private const val NAME = "T : Map<*,?>"
        override fun create(types: List<TypeKey?>): KtmAdapter<*>? {
            if (types.size != 2) throw ArgumentCountException(NAME, types)
            if (types[1] == null) throw StarProjectionException(NAME, 1, types)
            return MapKtmAdapter(types[1]!!)
        }
    }

    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Map<*, *>): MContext {
        val valueAdapter = adapters[valueType] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.Map { node, tag ->
            val v = value[tag] ?: return@Map null
            valueAdapter.toMustacheContext(adapters, v)
        }
    }

    override fun toString(): String = "KtmAdapter(Map<*, $valueType>)"
}

internal class MapEntryKtmAdapter(
    private val valueType: TypeKey,
) : KtmAdapter<Map.Entry<*, *>> {

    internal object Factory : KtmAdapter.Factory {
        private const val NAME = "T : Map.Entry<*,?>"
        override fun create(types: List<TypeKey?>): KtmAdapter<*>? {
            if (types.size != 2) throw ArgumentCountException(NAME, types)
            if (types[1] == null) throw StarProjectionException(NAME, 1, types)
            return MapEntryKtmAdapter(types[1]!!)
        }
    }

    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Map.Entry<*, *>): MContext {
        val valueAdapter = adapters[valueType] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.Map { node, tag ->
            if(value.key != tag) return@Map null
            valueAdapter.toMustacheContext(adapters, value.value)
        }
    }

    override fun toString(): String = "KtmAdapter(Map.Entry<*, $valueType>)"
}