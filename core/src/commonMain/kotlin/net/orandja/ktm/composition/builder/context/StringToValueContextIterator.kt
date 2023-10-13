package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext

class StringToValueContextIterator(
    private val values: Iterator<String>,
) : Iterator<MContext> {
    constructor(iterable: Iterable<String>) : this(iterable.iterator())

    override fun hasNext(): Boolean = values.hasNext()
    override fun next(): MContext {
        val next = values.next()
        return MContext.Value { next }
    }
}