package net.orandja.ktm.base.context

import net.orandja.ktm.base.CtxNode
import net.orandja.ktm.base.MContext

data class DefaultMulti(val value: Collection<MContext>) : MContext.Multi {
    override fun iterator(node: CtxNode): Iterator<MContext> = value.iterator()
    override fun toString(): String = "Multi(${Iterable { iterator(CtxNode(this, null)) }.joinToString { "$it" }})"
}
