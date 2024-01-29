package net.orandja.ktm.composition.parser

import kotlin.jvm.JvmStatic

/**
 * Represents a token used in parsing and rendering templates.
 *
 * @property type The type of the token.
 * @property content The content associated with the token.
 */
data class Token(val type: Int, val content: CharSequence) {

    inline val isNewLine get() = type and 0x40 > 0
    inline val isOpaque get() = type <= TAG_ESCAPE_2 || type == STATIC_CONTENT
    inline val isTag get() = type <= TAG_PARTIAL

    override fun toString(): String = when (type) {
        TAG_NORMAL -> "{{ $content }}"
        TAG_ESCAPE_1 -> "{{& $content }}"
        TAG_ESCAPE_2 -> "{{{ $content }}}"
        TAG_SECTION -> "{{# $content }}"
        TAG_INVERT -> "{{^ $content }}"
        TAG_CLOSE -> "{{/ $content }}"
        TAG_PARTIAL -> "{{> $content }}"
        TAG_COMMENT -> "{{! $content }}"
        TAG_DELIMITER -> "{{= $content =}}"
        STATIC_CONTENT -> "'$content'"
        WHITE_CONTENT -> "'$content'"
        NEW_LINE_R -> "\\r"
        NEW_LINE_RN -> "\\r\\n"
        NEW_LINE_N -> "\\n"
        else -> "?"
    }

    companion object {

        const val NO_CONTENT = ""

        const val NONE = -1

        const val TAG_NORMAL = 0
        const val TAG_ESCAPE_1 = 2
        const val TAG_ESCAPE_2 = 4
        const val TAG_SECTION = 6
        const val TAG_INVERT = 8
        const val TAG_CLOSE = 10
        const val TAG_PARTIAL = 12
        const val TAG_COMMENT = 14
        const val TAG_DELIMITER = 16

        const val WHITE_CONTENT = 0x20
        const val STATIC_CONTENT = 0x21

        const val NEW_LINE_R = 0x41
        const val NEW_LINE_RN = 0x42
        const val NEW_LINE_N = 0x43

        @Suppress("NOTHING_TO_INLINE")
        inline fun isNewLine(type: Int) = type and 0x40 > 0

        @JvmStatic
        val TAG = arrayOf<Char>(
            // @formatter:off
            Char.MAX_VALUE, Char.MAX_VALUE, // {{ tag }}
            '&', Char.MAX_VALUE, // {{& escaped_tag }}
            '{', '}',            // {{{ escaped_tag }}}
            '#', Char.MAX_VALUE, // {{# open_section }}
            '^', Char.MAX_VALUE, // {{^ invert_section }}
            '/', Char.MAX_VALUE, // {{/ close_section }}
            '>', Char.MAX_VALUE, // {{> partial }}
            '!', Char.MAX_VALUE, // {{! comment }}
            '=', '=',            // {{=%% %%=}}
            // @Formatter:on
        )


        @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        inline fun tagTokenIndexFromChar(char: Char) = when (char) {
            '&' -> TAG_ESCAPE_1
            '>' -> TAG_PARTIAL
            '!' -> TAG_COMMENT
            '#' -> TAG_SECTION
            '^' -> TAG_INVERT
            '/' -> TAG_CLOSE
            '{' -> TAG_ESCAPE_2
            '=' -> TAG_DELIMITER
            else -> TAG_NORMAL
        }
    }
}