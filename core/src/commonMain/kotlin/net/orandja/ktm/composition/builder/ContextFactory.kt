package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MContext.Value
import net.orandja.ktm.base.MContext.Group
import net.orandja.ktm.base.MContext.Multi
import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.base.context.GroupContext
import net.orandja.ktm.base.context.MultiContext
import net.orandja.ktm.base.context.MultiGroupContext
import net.orandja.ktm.base.context.ValueContext

open class ContextFactory {

    inline fun make(configuration: ContextBuilder.() -> Unit): MContext =
        ContextBuilder(mutableMapOf()).apply(configuration).build()

    val no = MContext.No
    val yes = MContext.Yes

    fun value(value: CharSequence) = ValueContext(value)
    fun valueDelegate(delegate: NodeContext.() -> CharSequence) = Value { it.delegate() }

    fun group(context: Map<String, MContext>) = GroupContext(context)
    fun group(vararg context: Pair<String, MContext>) = GroupContext(mapOf(*context))
    fun groupDelegate(delegate: NodeContext.(tag: String) -> MContext?) = Group { node, tag -> node.delegate(tag) }

    fun list(vararg context: MContext) = MultiContext(context.toList())
    fun list(contexts: List<MContext>) = MultiContext(contexts)
    fun listDelegate(delegate: NodeContext.() -> Iterator<MContext>) = Multi { it.delegate() }

    fun merge(vararg contexts: MContext.Group) = MultiGroupContext(contexts.toList())
    fun merge(contexts: List<MContext.Group>) = MultiGroupContext(contexts.toList())
}