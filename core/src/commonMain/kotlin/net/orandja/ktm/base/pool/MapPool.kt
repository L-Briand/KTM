package net.orandja.ktm.base.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import kotlin.jvm.JvmInline

@JvmInline
value class MapPool(private val delegate: Map<String, MDocument>) : MPool {
    override fun get(name: String): MDocument? = delegate[name]
}