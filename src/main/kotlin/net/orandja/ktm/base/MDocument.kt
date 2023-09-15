package net.orandja.ktm.base

/**
 * Precompiled parts of a document to render it.
 * To render a document you need both the tokens and source.
 */
sealed interface MDocument {
    /**
     * Comment in the document.
     *
     * Rendering can change depending on where a comment occurs.
     * Standalone comment block tags will remove the entire line.
     */
    data object Comment : MDocument {
        override fun toString(): String = "'_!'"
    }

    /**
     * New line in the document.
     *
     * Rendering can change depending on where a new line occurs.
     * Standalone tags which do not render can remove new lines.
     */
    data object NewLine : MDocument {
        override fun toString(): String = "'\\n'"
    }

    /**
     * Static part of the document to render as is.
     * @param content part of the mustache document to render as is.
     */
    @JvmInline
    value class Static(val content: String) : MDocument {
        override fun toString(): String = "'$content'"
    }

    /**
     * Partial tag in a mustache document: `'{{> partial }}'`
     *
     * @param name Name of the partial tag.
     */
    data class Partial(
        val name: String,
    ) : MDocument {
        override fun toString(): String = "'_>$name'"
    }


    /**
     * A Tag inside the document.
     * Tags inside the document. Can be represented as :
     *
     * Normal tags can be:
     * - `'{{ field }}'`: simple field
     * - `'{{ parent.field }}'`: compounded, to match inside objects
     * - `'{{ . }}'`: Special case when a list possesses only values.
     *
     * Unescaped tags are like `'{{{ field }}}'` or `'{{& field }}'`
     *
     * @param name parts of the tag name. Single dot tag should be an empty array.
     * @param escapeHtml if the rendered content of this tag should be html escaped. Normal tags are true.
     */
    data class Tag(val name: Array<String>, val escapeHtml: Boolean) : MDocument {

        val realName get() = name.joinToString(".") { it }.ifEmpty { "." }

        override fun toString(): String = "'${if (escapeHtml) "" else "_&"}$realName'"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Tag

            if (!name.contentEquals(other.name)) return false
            if (escapeHtml != other.escapeHtml) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.contentHashCode()
            result = 31 * result + escapeHtml.hashCode()
            return result
        }
    }


    /**
     * A section of a mustache document: `'{{# section }} content {{/ section }}'`
     *
     * Can also be an inverted section: `'{{^ section }} content {{/ section }}'`
     *
     * @param name parts of the section name tag. Empty array is illegal, section cannot be unnamed.
     * @param inverted true if the section is an inverted one
     * @param parts tokens to renders in order inside this section.
     */
    data class Section(
        val name: Array<String>,
        val inverted: Boolean,
        val parts: Array<MDocument>,
    ) : MDocument {

        val realName get() = name.joinToString(".") { it }.ifEmpty { "root" }

        override fun toString(): String {
            val invertedStr = if (inverted) "^" else ""
            val parts = parts.joinToString(", ", ", [", "]") { it.toString() }
            return "_[$invertedStr$realName$parts]"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Section

            if (!name.contentEquals(other.name)) return false
            if (inverted != other.inverted) return false
            if (!parts.contentEquals(other.parts)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.contentHashCode()
            result = 31 * result + inverted.hashCode()
            result = 31 * result + parts.contentHashCode()
            return result
        }
    }
}