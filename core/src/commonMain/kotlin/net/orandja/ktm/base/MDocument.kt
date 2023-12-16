package net.orandja.ktm.base

/**
 * Precompiled parts of a mustache document.
 * Combined with [MContext], you can render a document.
 */
sealed interface MDocument {
    // Used for parsing purpose
    // TODO : Do not use and Remove
    data object Empty : MDocument

    /**
     * Comment in the document. This should not render but affect rendering.
     *
     * Rendering can change depending on where a comment occurs.
     * For example, a standalone comment tag in a line will remove the entire line.
     */
    data object Comment : MDocument {
        override fun toString(): String = "'!'"
    }

    /**
     * Same as [Comment]
     */
    data object Delimiter : MDocument {
        override fun toString(): String = "'='"
    }

    /**
     * New line in the document.
     *
     * Rendering can change depending on where a new line occurs.
     * Standalone tags which do not render can remove new lines.
     * @param kind New line can be represented differently depending on which system you are.
     * @param render Mustache documents can have new lines which are not rendered due to
     *               their placement and applied rules.
     * @param last Special case when a new line at the end in a partial render occurs.
     *             The new line should not have any indentation after
     */
    data class NewLine(
        val kind: Kind,
        var render: Boolean = true,
        var last: Boolean = false,
    ) : MDocument {
        enum class Kind(val representation: String) {
            R("\r"), N("\n"), RN("\r\n"),
        }

        override fun toString(): String = when (kind) {
            Kind.R -> "${if (!render) "x" else ""}'\\r'"
            Kind.N -> "${if (!render) "x" else ""}'\\n'"
            Kind.RN -> "${if (!render) "x" else ""}'\\r\\n'"
        }
    }

    /**
     * Static part of the document to render as is.
     *
     * @param content part of the mustache document to render as is.
     * @param render Mustache documents can have blank static content which is not rendered due to
     *               their placement and applied rules.
     */
    data class Static(
        val content: String,
        var render: Boolean = true,
    ) : MDocument {
        override fun toString(): String = "${if (!render) "x" else ""}'$content'"
        val isBlank = content.isBlank()
    }

    /**
     * Partial tag in a mustache document: `'{{> partial }}'`
     *
     * @param name Name of the partial tag.
     */
    data class Partial(
        val name: String,
        var spaces: String? = null,
    ) : MDocument {
        override fun toString(): String = ">$name"
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

        override fun toString(): String = "<${if (escapeHtml) "" else "&"}$realName>"

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
        val parts: Array<MDocument>,
    ) : MDocument {

        private val realName get() = name.joinToString(".") { it }.ifEmpty { "root" }

        override fun toString(): String {
            val invertedStr = if (inverted) "^" else ""
            val parts = parts.joinToString(", ", ", [", "]") { it.toString() }
            return "{$invertedStr$realName$parts}"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

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