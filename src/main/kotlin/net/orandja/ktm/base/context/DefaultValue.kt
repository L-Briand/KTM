package net.orandja.ktm.base.context

import net.orandja.ktm.base.CtxNode
import net.orandja.ktm.base.MContext

data class DefaultValue(val value: CharSequence) : MContext.Value {
    override fun get(node: CtxNode): CharSequence = value
    override fun toString(): String = "Value($value)"
}