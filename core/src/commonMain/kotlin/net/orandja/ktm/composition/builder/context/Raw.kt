package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.NodeContext
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

/**
 * Represents a contextual map used in mustache rendering.
 *
 * @property value The underlying map containing the context values.
 */
@JvmInline
value class ContextMap(val value: MutableMap<String, MContext>) : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? = value[tag]
    override fun toString(): String = "Map(${value.entries.joinToString { "${it.key}=${it.value}" }})"
}

/**
 * Represents a context value used in the Mustache template engine.
 *
 * @property value The underlying value of the context.
 */
@JvmInline
value class ContextValue(val value: CharSequence) : MContext.Value {
    override fun get(node: NodeContext): CharSequence = value
    override fun toString(): String = "Value($value)"
}

