@file:Suppress("UNCHECKED_CAST")

package net.orandja.ktm.adapters.defaults

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.typeKey
import net.orandja.ktm.base.MContext

object AnyKtmAdapter : KtmAdapter<Any?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Any?): MContext =
        Ktm.ctx.value(value?.toString())

    override fun toString(): String = "KtmAdapter(Any)"
}

// Primitives

internal object StringKtmAdapter : KtmAdapter<String?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: String?): MContext = Ktm.ctx.value(value)
    override fun toString(): String = "KtmAdapter(String)"
}

internal object BooleanKtmAdapter : KtmAdapter<Boolean?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Boolean?): MContext = Ktm.ctx.value(value)
    override fun toString(): String = "KtmAdapter(Boolean)"
}

internal object ShortKtmAdapter : KtmAdapter<Short?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Short?): MContext =
        Ktm.ctx.value(value.toString())

    override fun toString(): String = "KtmAdapter(Short)"
}

internal object IntKtmAdapter : KtmAdapter<Int?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Int?): MContext =
        Ktm.ctx.value(value.toString())

    override fun toString(): String = "KtmAdapter(Int)"
}

internal object LongKtmAdapter : KtmAdapter<Long?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Long?): MContext =
        Ktm.ctx.value(value.toString())

    override fun toString(): String = "KtmAdapter(Long)"
}

internal object FloatKtmAdapter : KtmAdapter<Float?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Float?): MContext =
        Ktm.ctx.value(value.toString())

    override fun toString(): String = "KtmAdapter(Float)"
}

internal object DoubleKtmAdapter : KtmAdapter<Double?> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: Double?): MContext =
        Ktm.ctx.value(value.toString())

    override fun toString(): String = "KtmAdapter(Double)"
}

// Arrays

internal object ByteArrayKtmAdapter : KtmAdapter<ByteArray> {
    private val KEY = typeKey<Byte>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: ByteArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(ByteArray)"
}

internal object CharArrayKtmAdapter : KtmAdapter<CharArray> {
    private val KEY = typeKey<Char>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: CharArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(CharArray)"
}

internal object ShortArrayKtmAdapter : KtmAdapter<ShortArray> {
    private val KEY = typeKey<Short>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: ShortArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(ShortArray)"
}

internal object IntArrayKtmAdapter : KtmAdapter<IntArray> {
    private val KEY = typeKey<Int>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: IntArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(IntArray)"
}

internal object LongArrayKtmAdapter : KtmAdapter<LongArray> {
    private val KEY = typeKey<Long>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: LongArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(LongArray)"
}

internal object FloatArrayKtmAdapter : KtmAdapter<FloatArray> {
    private val KEY = typeKey<Float>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: FloatArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(FloatArray)"
}

internal object DoubleArrayKtmAdapter : KtmAdapter<DoubleArray> {
    private val KEY = typeKey<Double>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: DoubleArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(DoubleArray)"
}

internal object BooleanArrayKtmAdapter : KtmAdapter<BooleanArray> {
    private val KEY = typeKey<Boolean>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: BooleanArray): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                private val iterator = value.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, iterator.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(BooleanArray)"
}

// Iterators

internal object ByteIteratorKtmAdapter : KtmAdapter<ByteIterator> {
    private val KEY = typeKey<Byte>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: ByteIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(ByteIterator)"
}

internal object ShortIteratorKtmAdapter : KtmAdapter<ShortIterator> {
    private val KEY = typeKey<Short>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: ShortIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(ShortIterator)"
}


internal object IntIteratorKtmAdapter : KtmAdapter<IntIterator> {
    private val KEY = typeKey<Int>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: IntIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(IntIterator)"
}

internal object LongIteratorKtmAdapter : KtmAdapter<LongIterator> {
    private val KEY = typeKey<Long>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: LongIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(LongIterator)"
}


internal object BooleanIteratorKtmAdapter : KtmAdapter<BooleanIterator> {
    private val KEY = typeKey<Boolean>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: BooleanIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(BooleanIterator)"
}

internal object FloatIteratorKtmAdapter : KtmAdapter<FloatIterator> {
    private val KEY = typeKey<Float>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: FloatIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(FloatIterator)"
}

internal object DoubleIteratorKtmAdapter : KtmAdapter<DoubleIterator> {
    private val KEY = typeKey<Double>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: DoubleIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(DoubleIterator)"
}

internal object CharIteratorKtmAdapter : KtmAdapter<CharIterator> {
    private val KEY = typeKey<Char>()
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: CharIterator): MContext {
        val adapter = adapters[KEY] as? KtmAdapter<Any?> ?: return Ktm.ctx.no
        return MContext.List {
            object : Iterator<MContext> {
                override fun hasNext(): Boolean = value.hasNext()
                override fun next(): MContext = adapter.toMustacheContext(adapters, value.next())
            }
        }
    }

    override fun toString(): String = "KtmAdapter(CharIterator)"
}
