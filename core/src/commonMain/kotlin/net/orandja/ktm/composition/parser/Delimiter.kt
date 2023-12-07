package net.orandja.ktm.composition.parser

/**
 * Basics delimiters found inside a mustache document.
 */
enum class Delimiter {
    /** Sequence opening a tag, mostly `{{` */
    START,

    /** Sequence closing a tag, mostly `}}` */
    STOP,

    /** END on the document */
    END,

    /** New line found in the document during parsing */
    NEW_LINE_R,
    NEW_LINE_RN,
    NEW_LINE_N,
    ;
}