package net.orandja.ktm.base.context

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import kotlin.jvm.JvmInline

@JvmInline
value class MultiGroupContext(val contexts: List<MContext.Group>) : MContext.Group {
    override fun get(node: NodeContext, tag: String): MContext? {
        for (ctx in contexts) {
            return ctx.get(node, tag) ?: continue
        }
        return null
    }
}