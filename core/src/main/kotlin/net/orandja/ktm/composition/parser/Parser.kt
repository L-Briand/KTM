package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument.Section
import java.io.Reader
import java.io.StringReader

class Parser {

    private val level0 = ParserLevel0()
    private val level1 = ParserLevel1()

    fun parse(document: String) = parse(StringReader(document))
    fun parse(reader: Reader): Section {
        // Parse the document and return every part of it.
        val root = level0.parse(ParserLevel0Context(reader))

        // Filter out parts of the document that match rules in the specification.
        level1.applyRules(root)

        return root
    }
}