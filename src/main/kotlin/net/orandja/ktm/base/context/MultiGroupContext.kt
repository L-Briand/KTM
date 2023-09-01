package net.orandja.ktm.base.context

import net.orandja.ktm.base.MContext


class MultiGroupContext(val contexts: List<MContext.Group>) : MContext.Group {
    override fun get(node: NodeContext, tag: String): MContext? {
        for (ctx in contexts) {
            return ctx.get(node, tag) ?: continue
        }
        return null
    }
}