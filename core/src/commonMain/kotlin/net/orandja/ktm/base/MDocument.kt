package net.orandja.ktm.base

/**
 * Precompiled parts of a mustache document.
 * Combined with [MContext], you can render a document.
 */
sealed interface MDocument {

    /**
     * Static part of the document to render as is.
     *
     * @param content part of the mustache document to render as is.
     * @param render Mustache documents can have blank static content which is not rendered due to
     *               their placement and applied rules.
     */
    data class Static(
        val content: StringBuilder = StringBuilder(),
        var sectionLast: Boolean = false,
    ) : MDocument {
        override fun toString(): String = buildString {
            append('\'').append(content).append('\'')
            if (sectionLast) append("~")
        }
    }

    /**
     * Partial tag in a mustache document: `'{{> partial }}'`
     *
     * @param name Name of the partial tag.
     */
    data class Partial(
        val name: CharSequence,
        var padding: CharSequence,
    ) : MDocument {
        override fun toString(): String = ">$name[${padding.length}]"
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

        override fun toString(): String = "{{${if (escapeHtml) "" else "&"}$realName}}"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

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
     * @param name parts of the section name tag. Empty array is illegal, a section cannot be unnamed.
     * @param inverted true if the section is inverted
     * @param parts tokens to renders in order inside this section.
     */
    data class Section(
        val name: Array<String>,
        val inverted: Boolean,
        val parts: ArrayList<MDocument> = ArrayList(10),
    ) : MDocument {

        private val realName get() = name.joinToString(".") { it }.ifEmpty { "root" }

        override fun toString(): String {
            val invertedStr = if (inverted) "^" else ""
            val parts = parts.joinToString(", ", "(", ")") {
                it.toString().replace("\n", "\\n").replace("\r", "\\r")
            }
            return "$invertedStr$realName$parts"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Section) return false

            if (!name.contentEquals(other.name)) return false
            if (inverted != other.inverted) return false
            if (parts != other.parts) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.contentHashCode()
            result = 31 * result + inverted.hashCode()
            result = 31 * result + parts.hashCode()
            return result
        }
    }
}