package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline


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
