package net.orandja.ktm.composition.parser

/**
 * Used by the [Parser] to read a mustache document.
 * The parser will read each char one by one until it receives a null element.
 */
fun interface CharStream {
    /** @return null if caller reaches the end. */
    fun read(): Char?
}