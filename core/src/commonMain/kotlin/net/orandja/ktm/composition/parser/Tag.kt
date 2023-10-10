package net.orandja.ktm.composition.parser

/**
 * Tag types inside the mustache document.
 * @param open Character used to match the tag type during parsing
 */
enum class Tag(val open: Char, val close: Char) {

    /** Normal tag `'{{ tag }}'` */
    NORMAL(Char.MIN_VALUE, Char.MIN_VALUE),

    /** First way to print unescaped html `'{{{ unescaped }}}'` */
    UNESCAPED_1('{', '}'),

    /** Second way to print unescaped html `'{{& unescaped }}'` */
    UNESCAPED_2('&', Char.MIN_VALUE),

    /** Partial tag `'{{> partial }}'` */
    PARTIAL('>', Char.MIN_VALUE),

    /** Comment tag `'{{! partial }}'` */
    COMMENT('!', Char.MIN_VALUE),

    /** Change delimiter tag `'{{= <[ ]> =}}'` */
    DELIMITER('=', '='),

    /** Start of a section tag `'{{# section }}'` */
    START_SECTION('#', Char.MIN_VALUE),

    /** Start of an inverted section tag `'{{^ section }}'` */
    INVERTED_SECTION('^', Char.MIN_VALUE),

    /** End of a section or inverted section tag `'{{/ section }}'` */
    END_SECTION('/', Char.MIN_VALUE),
    ;

    companion object {
        fun getTagFromChar(char: Char) = when (char) {
            UNESCAPED_1.open -> UNESCAPED_1
            UNESCAPED_2.open -> UNESCAPED_2
            PARTIAL.open -> PARTIAL
            COMMENT.open -> COMMENT
            DELIMITER.open -> DELIMITER
            START_SECTION.open -> START_SECTION
            INVERTED_SECTION.open -> INVERTED_SECTION
            END_SECTION.open -> END_SECTION
            else -> NORMAL
        }
    }
}