package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument.Section

class Parser {

    private val level0 = ParserLevel0()
    private val level1 = ParserLevel1()

    fun parse(charReader: CharStream): Section {
        // Parse the document and return every part of it.
        val root = level0.parse(ParserLevel0Context(charReader))

        // Filter out parts of the document that match rules in the mustache specification.
        level1.applyRules(root)

        return root
    }
}