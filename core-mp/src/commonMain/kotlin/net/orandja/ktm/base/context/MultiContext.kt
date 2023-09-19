package net.orandja.ktm.base.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext

data class MultiContext(val value: Collection<MContext>) : MContext.Multi {
    override fun iterator(node: NodeContext): Iterator<MContext> = value.iterator()
    override fun toString(): String = "Multi(${Iterable { iterator(NodeContext(this, null)) }.joinToString { "$it" }})"
}
