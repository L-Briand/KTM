@file:Suppress("UNCHECKED_CAST")

package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.get
import kotlin.reflect.KType

// Default implementation of adapters

class DelegatedKtmAdapter<T : R, R>(
    private val type: KType,
    private val delegate: KtmAdapter<R>,
) : KtmAdapter<T> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: T): MContext =
        delegate.toMustacheContext(adapters, value)

    override fun toString(): String = "DelegatedKtmAdapter($type using '$delegate')"
}

object AnyKtmAdapter : KtmAdapter<Any?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Any?): MContext =
        Ktm.ctx.value(value?.toString())

    override fun toString(): String = "KtmAdapter(Any)"
}

internal object StringKtmAdapter : KtmAdapter<String?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: String?): MContext = Ktm.ctx.value(value)
    override fun toString(): String = "KtmAdapter(String)"
}

internal object BooleanKtmAdapter : KtmAdapter<Boolean?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Boolean?): MContext = Ktm.ctx.value(value)
    override fun toString(): String = "KtmAdapter(Boolean)"
}

internal class IteratorKtmAdapter(
    private val type: KType
) : KtmAdapter<Iterator<*>> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Iterator<*>): MContext {
        val adapter = adapters.get(type) as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(Iterable { value }.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(Iterator<$type>)"
}

internal class IterableKtmAdapter(
    private val type: KType,
) : KtmAdapter<Iterable<*>> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Iterable<*>): MContext {
        val adapter = adapters.get(type) as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(Iterable<$type>)"
}

internal class SequenceKtmAdapter(
    private val type: KType,
) : KtmAdapter<Sequence<*>> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Sequence<*>): MContext {
        val adapter = adapters.get(type) as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) }.toList())
    }

    override fun toString(): String = "KtmAdapter(Sequence<$type>)"
}

internal class MapKtmAdapter(
    private val valueType: KType,
) : KtmAdapter<Map<*, *>> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Map<*, *>): MContext {
        val valueAdapter = adapters.get(valueType) as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxMap(
            value.map { (k, v) -> k.toString() to valueAdapter.toMustacheContext(adapters, v) }.toMap()
        )
    }

    override fun toString(): String = "KtmAdapter(Map<*, $valueType>)"
}

internal class MapEntryKtmAdapter(
    private val valueType: KType,
) : KtmAdapter<Map.Entry<*, *>> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Map.Entry<*, *>): MContext {
        val valueAdapter = adapters.get(valueType) as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxMap(value.key.toString() to valueAdapter.toMustacheContext(adapters, value))
    }

    override fun toString(): String = "KtmAdapter(Map.Entry<*, $valueType>)"
}

internal class ArrayKtmAdapter(
    private val type: KType,
) : KtmAdapter<Array<*>> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Array<*>): MContext {
        val adapter = adapters.get(type) as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(Array<$type>)"
}

internal object ByteArrayKtmAdapter : KtmAdapter<ByteArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: ByteArray): MContext {
        val adapter = adapters.get<Byte>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(ByteArray)"
}

internal object CharArrayKtmAdapter : KtmAdapter<CharArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: CharArray): MContext {
        val adapter = adapters.get<Char>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(CharArray)"
}

internal object ShortArrayKtmAdapter : KtmAdapter<ShortArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: ShortArray): MContext {
        val adapter = adapters.get<Short>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(ShortArray)"
}

internal object IntArrayKtmAdapter : KtmAdapter<IntArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: IntArray): MContext {
        val adapter = adapters.get<Int>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(IntArray)"
}

internal object LongArrayKtmAdapter : KtmAdapter<LongArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: LongArray): MContext {
        val adapter = adapters.get<Long>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(LongArray)"
}

internal object FloatArrayKtmAdapter : KtmAdapter<FloatArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: FloatArray): MContext {
        val adapter = adapters.get<Float>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(FloatArray)"
}

internal object DoubleArrayKtmAdapter : KtmAdapter<DoubleArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: DoubleArray): MContext {
        val adapter = adapters.get<Double>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(DoubleArray)"
}

internal object BooleanArrayKtmAdapter : KtmAdapter<BooleanArray> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: BooleanArray): MContext {
        val adapter = adapters.get<Boolean>() as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return Ktm.ctx.ctxList(value.map { adapter.toMustacheContext(adapters, it) })
    }

    override fun toString(): String = "KtmAdapter(BooleanArray)"
}





