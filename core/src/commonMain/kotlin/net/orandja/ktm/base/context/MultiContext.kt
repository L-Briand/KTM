package net.orandja.ktm.base.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class MultiContext(val value: Collection<MContext>) : MContext.Multi {
    override fun iterator(node: NodeContext): Iterator<MContext> = value.iterator()
    override fun toString(): String = "Multi(${Iterable { iterator(NodeContext(this, null)) }.joinToString { "$it" }})"
}
