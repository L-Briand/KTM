package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class ContextMap(val value: Map<String, MContext>) : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? = value[tag]
    override fun toString(): String = "Map(${value.entries.joinToString { "${it.key}=${it.value}" }})"
}
