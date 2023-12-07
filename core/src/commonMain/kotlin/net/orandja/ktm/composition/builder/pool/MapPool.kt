package net.orandja.ktm.composition.builder.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import kotlin.jvm.JvmInline

/**
 * A class that represents a pool of mapped documents.
 *
 * This class implements the MPool interface, which allows it to hold mustache documents
 * and perform partial search during rendering.
 *
 * @property delegate The delegate map that holds the map documents.
 */
@JvmInline
value class MapPool(private val delegate: Map<String, MDocument>) : MPool {
    override fun get(name: String): MDocument? = delegate[name]
}