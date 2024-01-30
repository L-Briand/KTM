package net.orandja.ktm.base

/**
 * Precompiled parts of a mustache document.
 * Combined with [MContext], you can render a document.
 */
sealed interface MDocument {

    /**
     * Static part of the document to render as is.
     *
     * @property content part of the mustache document to render as is.
     * @property sectionLast Whenever this element is the last element of the Section.
     */
    class Static(
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
     * @property name Name of the partial tag.
     * @property padding padding to add on each new line during partial render
     */
    class Partial(
        val name: Array<String>,
        var padding: CharSequence,
    ) : MDocument {
        private val realName get() = name.joinToString(".") { it }.ifEmpty { "." }
        override fun toString(): String = ">$realName[${padding.length}]"
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
    class Tag(val name: Array<String>, val escapeHtml: Boolean) : MDocument {
        private val realName get() = name.joinToString(".") { it }.ifEmpty { "." }
        override fun toString(): String = "{{${if (escapeHtml) "" else "&"}$realName}}"
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
    class Section(
        val name: Array<String>,
        val inverted: Boolean,
        val parts: ArrayList<MDocument> = ArrayList(10),
    ) : MDocument {

        private val realName
            get() = if (name.size == 1 && name[0] == ".") "."
            else name.joinToString(".") { it }.ifEmpty { "root" }

        override fun toString(): String {
            val invertedStr = if (inverted) "^" else ""
            val parts = parts.joinToString(", ", "(", ")") {
                it.toString().replace("\n", "\\n").replace("\r", "\\r")
            }
            return "$invertedStr$realName$parts"
        }
    }
}