package net.orandja.ktm.base

/**
 * Precompiled parts of a document to render it.
 * To render a document you need both the tokens and source.
 */
sealed interface MToken {

    /**
     * Static part of the document to render as is.
     *
     * @param toRender where the content is in the document.
     */
    class Static(val start: Long, val stop: Long) : MToken {
        override fun toString(): String = "Static($start - $stop)"
    }

    /**
     * A `{{> partial }}` tag
     */
    class Partial(val name: String) : MToken {
        override fun toString(): String = "Partial($name)"
    }

    /**
     * A Tag inside the document.
     * Tags inside the document. Can be represented as :
     *
     * - `{{ field }}`: normal
     * - `{{ parent.field }}`: compounded.
     * - `{{{ field }}}`: Escaped
     * - `{{& field }}`: Escaped
     * - `{{ . }}`: Special case when a list process only a value.
     *
     * @param nameParts Split name of the tag.
     * @param escapeHtml If the render of it has to be escaped
     */
    class Tag(val nameParts: Array<String>, val escapeHtml: Boolean) : MToken {
        override fun toString(): String = "Tag('${nameParts.joinToString(".") { it }}', $escapeHtml)"
    }

    /**
     * A whole section of a mustache document. `{{#section}} whole section {{/section}}`
     *
     * @param nameParts null if it's the root of the document.
     * @param inverted true if the section is inverted (`{{^section}}{{/section}}`)
     * @param parts all tokens to renders in order inside this section.
     */
    class Section(
        val nameParts: Array<String>?,
        val start: Long, // Might be used to cache parts of a document when rendering
        val stop: Long,
        val inverted: Boolean,
        val parts: Collection<MToken>,
    ) : MToken {
        override fun toString(): String =
            "Section('${nameParts?.joinToString(".") { it } ?: ""}', $start - $stop, $inverted)"

        companion object {
            @JvmStatic
            val EMPTY = Section(null, 0L, 0L, false, emptyList())
        }
    }
}
