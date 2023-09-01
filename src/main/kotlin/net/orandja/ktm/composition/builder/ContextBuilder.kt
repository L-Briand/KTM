package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MContext.*
import net.orandja.ktm.base.context.*

open class ContextBuilder(
    parent: NodeContext?,
) : NodeContext(
    current = MutableGroup(),
    parent = parent,
) {

    private data class MutableGroup(
        val backing: MutableMap<String, MContext> = mutableMapOf(),
    ) : Group {
        override fun get(node: NodeContext, tag: String): MContext? = backing[tag]
    }

    private val backing = (current as MutableGroup).backing

    infix fun String.by(value: CharSequence?) {
        backing[this] = value?.let(::value) ?: no
    }

    infix fun String.by(value: Boolean) {
        backing[this] = if (value) yes else no
    }

    infix fun String.by(value: MContext?) {
        backing[this] = value ?: no
    }

    fun build(): MContext = if (backing.isEmpty()) yes else group(backing)

    // Creation functions

    inline fun make(configuration: ContextBuilder.() -> Unit) = make(null, configuration)
    inline fun make(
        current: ContextBuilder? = null,
        configuration: ContextBuilder.() -> Unit,
    ): MContext = ContextBuilder(current).apply(configuration).build()

    val no = No
    val yes = Yes

    fun value(value: CharSequence) = ValueContext(value)
    fun valueDelegate(delegate: NodeContext.() -> CharSequence) = Value { it.delegate() }

    fun group(context: Map<String, MContext>) = GroupContext(context)
    fun groupDelegate(delegate: NodeContext.(tag: String) -> MContext?) = Group { node, tag -> node.delegate(tag) }

    fun list(vararg context: MContext) = MultiContext(context.toList())
    fun list(contexts: List<MContext>) = MultiContext(contexts)
    fun listDelegate(delegate: NodeContext.() -> Iterator<MContext>) = Multi { it.delegate() }

    fun merge(vararg contexts: Group) = MultiGroupContext(contexts.toList())
    fun merge(contexts: List<Group>) = MultiGroupContext(contexts.toList())
}
