package net.orandja.ktm.composition.context

import net.orandja.ktm.base.CtxNode
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.context.DefaultGroup
import net.orandja.ktm.base.context.DefaultMulti
import net.orandja.ktm.base.context.DefaultValue

class ContextBuilder constructor(
    parent: CtxNode?,
) : CtxNode(
    current = MutableMContextGroup(),
    parent = parent,
) {

    private class MutableMContextGroup : MContext.Group {
        val backing = mutableMapOf<String, MContext>()
        override fun get(node: CtxNode, tag: String): MContext? = backing[tag]
    }

    companion object Default {
        inline fun make(
            current: ContextBuilder? = null,
            configuration: ContextBuilder.() -> Unit,
        ): MContext = ContextBuilder(current)
            .apply(configuration)
            .build()

        val no = MContext.No
        val yes = MContext.Yes
        fun value(value: CharSequence) = DefaultValue(value)
        fun valueDelegate(delegate: CtxNode.() -> CharSequence) = MContext.Value { it.delegate() }
        fun list(vararg ctx: MContext) = DefaultMulti(ctx.toList())
        fun listDelegate(delegate: CtxNode.() -> Iterator<MContext>) = MContext.Multi { it.delegate() }
        inline fun group(conf: ContextBuilder.() -> Unit) = make(null, conf)
        fun groupDelegate(delegate: CtxNode.(tag: String) -> MContext?) =
            MContext.Group { node, tag -> node.delegate(tag) }
    }

    private val backing = (current as MutableMContextGroup).backing

    infix fun String.by(value: CharSequence?) {
        backing[this] = value?.let(::value) ?: no
    }

    infix fun String.by(value: Boolean) {
        backing[this] = if (value) yes else no
    }

    infix fun String.by(value: MContext?) {
        backing[this] = value ?: no
    }

    val no = MContext.No
    val yes = MContext.Yes

    fun value(value: CharSequence) = Default.value(value)
    fun valueDelegate(delegate: CtxNode.() -> CharSequence) = Default.valueDelegate(delegate)

    fun group(conf: ContextBuilder.() -> Unit) = make(this, conf)
    fun groupDelegate(delegate: CtxNode.(tag: String) -> MContext?) = Default.groupDelegate(delegate)

    fun list(vararg ctx: MContext) = Default.list(*ctx)
    fun listDelegate(delegate: CtxNode.() -> Iterator<MContext>) = Default.listDelegate(delegate)

    fun build(): MContext = if (backing.isEmpty()) MContext.Yes else DefaultGroup(backing)
}
