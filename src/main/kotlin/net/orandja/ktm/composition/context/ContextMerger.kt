package net.orandja.ktm.composition.context

import net.orandja.ktm.base.CtxNode
import net.orandja.ktm.base.MContext

class ContextMerger private constructor() {
    class Group(vararg val contexts: MContext.Group) : MContext.Group {
        override fun get(node: CtxNode, tag: String): MContext? {
            for (ctx in contexts) {
                return ctx.get(node, tag) ?: continue
            }
            return null
        }
    }
}
