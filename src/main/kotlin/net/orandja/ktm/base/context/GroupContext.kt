package net.orandja.ktm.base.context

import net.orandja.ktm.base.MContext

data class GroupContext(val value: Map<String, MContext>) : MContext.Group {
    override fun get(node: NodeContext, tag: String): MContext? = value[tag]
    override fun toString(): String = "Group(${value.entries.joinToString { "${it.key}=${it.value}" }})"
}
