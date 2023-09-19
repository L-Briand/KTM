package net.orandja.ktm.composition.parser

fun interface CharStreamReader {
    /** @return -1 for the end of the stream */
    fun read(): Int
}