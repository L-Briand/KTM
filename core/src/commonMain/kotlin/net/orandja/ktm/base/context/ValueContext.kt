package net.orandja.ktm.base.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class ValueContext(val value: CharSequence) : MContext.Value {
    override fun get(node: NodeContext): CharSequence = value
    override fun toString(): String = "Value($value)"
}