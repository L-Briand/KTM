package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class MultiMapContext(private val contexts: List<MContext.Map>) : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? {
        for (ctx in contexts) {
            return ctx.get(node, tag) ?: continue
        }
        return null
    }
}