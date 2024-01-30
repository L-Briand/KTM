package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class ContextList(val value: Iterable<MContext>) : MContext.List {
    override fun iterator(node: NodeContext): Iterator<MContext> = value.iterator()
    override fun toString(): String = "List(${Iterable { iterator(NodeContext(this, null)) }.joinToString { "$it" }})"
}

@JvmInline
value class ContextMap(val value: MutableMap<String, MContext>) : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? = value[tag]
    override fun toString(): String = "Map(${value.entries.joinToString { "${it.key}=${it.value}" }})"
}

@JvmInline
value class ContextValue(val value: CharSequence) : MContext.Value {
    override fun get(node: NodeContext): CharSequence = value
    override fun toString(): String = "'$value'"
}

@JvmInline
value class ContextDocument(val value: MDocument) : MContext.Document {
    override fun get(node: NodeContext): MDocument = value
    override fun toString(): String = "Document($value)"
}

