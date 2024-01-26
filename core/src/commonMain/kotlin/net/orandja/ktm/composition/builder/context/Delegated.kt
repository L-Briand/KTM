package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class Delegated(
    private val delegate: NodeContext.() -> MContext?,
) : MContext.Delegate {
    override fun get(node: NodeContext): MContext = node.delegate() ?: MContext.No
    override fun toString(): String = "Delegated"
}

@JvmInline
value class DelegatedValue(
    private val delegate: NodeContext.() -> CharSequence,
) : MContext.Value {
    override fun get(node: NodeContext): CharSequence = node.delegate()
    override fun toString(): String = "DelegatedValue"
}

@JvmInline
value class DelegatedMap(
    private val delegate: NodeContext.(tag: String) -> MContext?,
) : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? = node.delegate(tag)
    override fun toString(): String = "DelegatedMap"
}

@JvmInline
value class DelegatedList(
    private val delegate: NodeContext.() -> Iterator<MContext>,
) : MContext.List {
    override fun iterator(node: NodeContext): Iterator<MContext> = node.delegate()
    override fun toString(): String = "DelegatedList"
}
