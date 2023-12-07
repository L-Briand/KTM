package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument.Section

/**
 * This class is the main Mustache document parser.
 *
 * @property level0 An instance of ParserLevel0, which represents the first level of parsing.
 * @property level1 An instance of ParserLevel1, which represents the second level of parsing.
 *
 * @constructor Creates a Parser object.
 */
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