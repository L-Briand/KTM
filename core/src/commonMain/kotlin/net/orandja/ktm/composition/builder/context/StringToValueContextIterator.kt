package net.orandja.ktm.composition.builder.context

import net.orandja.ktm.base.MContext

/**
 * The `StringToValueContextIterator` class is an iterator
 * that converts a sequence of strings into a sequence of `MContext` objects.
 *
 * @param values The iterator of strings to be converted.
 */
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