package net.orandja.ktm.composition.parser

/**
 * Used by the [Parser] to read a mustache document.
 * The parser will read each char one by one until it receives `-1`, `\uFFFF`, [Char.MAX_VALUE].
 */
fun interface CharStream {
    /** @return `-1`, `\uFFFF` [Char.MAX_VALUE] if caller reaches the end. */
    fun read(): Char
}