package net.orandja.ktm.composition

import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.context.ContextBuilder
import net.orandja.ktm.composition.context.ContextMerger

object MustacheContext {
    val builder = ContextBuilder.Default
    fun merge(vararg contexts: MContext.Group) = ContextMerger.Group(*contexts)
}
