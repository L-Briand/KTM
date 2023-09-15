package net.orandja.ktm.composition.parser

/**
 * Basics delimiters found inside a mustache document.
 */
enum class Delimiter {
    /** Sequence opening a tag, mostly '{{' */
    START,

    /** Sequence closing a tag, mostly '}}' */
    STOP,

    /** New line in the document, many exceptions resolve around it. */
    NEW_LINE,

    /** END on the document*/
    END
}