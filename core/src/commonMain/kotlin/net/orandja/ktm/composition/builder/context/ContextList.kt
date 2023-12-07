package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline


/**
 * Represents a list of [MContext] objects within a Mustache document context.
 *
 * @param value The underlying iterable collection of [MContext] objects.
 */
@JvmInline
value class ContextList(val value: Iterable<MContext>) : MContext.List {
    override fun iterator(node: NodeContext): Iterator<MContext> = value.iterator()
    override fun toString(): String = "List(${Iterable { iterator(NodeContext(this, null)) }.joinToString { "$it" }})"
}
