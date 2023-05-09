package net.orandja.ktm.base.context

import net.orandja.ktm.base.CtxNode
import net.orandja.ktm.base.MContext

data class DefaultGroup(val value: Map<String, MContext>) : MContext.Group {
    override fun get(node: CtxNode, tag: String): MContext? = value[tag]
    override fun toString(): String = "Group(${value.entries.joinToString { "${it.key}=${it.value}" }})"
}