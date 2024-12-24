package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import kotlin.jvm.JvmInline

@JvmInline
value class ContextList(val value: Iterable<MContext>) : MContext.List {
    override fun iterator(node: MContext.Node): Iterator<MContext> = value.iterator()
    override fun toString(): String = "List(${Iterable { iterator(MContext.Node(this, null)) }.joinToString { "$it" }})"
}

@JvmInline
value class ContextMap(val value: MutableMap<String, MContext>) : MContext.Map {
    override fun get(node: MContext.Node, tag: CharSequence): MContext? = value[tag]
    override fun toString(): String = "Map(${value.entries.joinToString { "${it.key}=${it.value}" }})"
}

@JvmInline
value class ContextValue(val value: CharSequence) : MContext.Value {
    override fun get(node: MContext.Node): CharSequence = value
    override fun toString(): String = "'$value'"
}

@JvmInline
value class ContextDocument(val value: MDocument) : MContext.Document {
    override fun get(node: MContext.Node): MDocument = value
    override fun toString(): String = "Document($value)"
}

