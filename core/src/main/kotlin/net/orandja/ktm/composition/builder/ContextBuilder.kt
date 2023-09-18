package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MContext.*
import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.base.context.*

class ContextBuilder constructor(
    private val backing: MutableMap<String, MContext>? = null,
) {

    infix fun String.by(value: CharSequence?) {
        backing!![this] = value?.let(::value) ?: no
    }

    infix fun String.by(value: Boolean) {
        backing!![this] = if (value) yes else no
    }

    infix fun String.by(value: MContext?) {
        backing!![this] = value ?: no
    }

    fun build(): MContext = if (backing!!.isEmpty()) yes else group(backing)

    // Creation functions

    inline fun make(configuration: ContextBuilder.() -> Unit): MContext =
        ContextBuilder(mutableMapOf()).apply(configuration).build()

    val no = No
    val yes = Yes

    fun value(value: CharSequence) = ValueContext(value)
    fun valueDelegate(delegate: NodeContext.() -> CharSequence) = Value { it.delegate() }

    fun group(context: Map<String, MContext>) = GroupContext(context)
    fun group(vararg context: Pair<String, MContext>) = GroupContext(mapOf(*context))
    fun groupDelegate(delegate: NodeContext.(tag: String) -> MContext?) = Group { node, tag -> node.delegate(tag) }

    fun list(vararg context: MContext) = MultiContext(context.toList())
    fun list(contexts: List<MContext>) = MultiContext(contexts)
    fun listDelegate(delegate: NodeContext.() -> Iterator<MContext>) = Multi { it.delegate() }

    fun merge(vararg contexts: Group) = MultiGroupContext(contexts.toList())
    fun merge(contexts: List<Group>) = MultiGroupContext(contexts.toList())
}
