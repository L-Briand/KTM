package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

/**
 * Represents a multi-level map context for mustache templating.
 * Inherits from [MContext.Map] interface.
 *
 * @property contexts The list of [MContext.Map] contexts to be wrapped.
 */
@JvmInline
value class MultiMapContext(val contexts: List<MContext.Map>) : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? {
        for (ctx in contexts) {
            return ctx.get(node, tag) ?: continue
        }
        return null
    }
}

// TODO : Multi list