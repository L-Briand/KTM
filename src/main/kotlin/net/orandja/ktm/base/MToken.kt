package net.orandja.ktm.base

/** Precompiled parts of a document to render it. */
sealed class MToken {
    /**
     * Static part of the document to render as is.
     * @param toRender where the content is in the document
     */
    class Static(val toRender: LongRange) : MToken() {
        override fun toString(): String = "Static($toRender)"
    }

    /**
     * A Tag inside the document.
     * Tags inside the document. Can be represented as :
     *
     * - `{{ field }}`: normal
     * - `{{ parent.field }}`: compounded.
     * - `{{{ field }}}`: Escaped
     * - `{{& field }}`: Escaped
     * - `{{.}}`: Special case when a list pocess only a value.
     *
     * @param nameParts Split name of the tag.
     * @param escapeHtml If the render of it has to be escaped
     */
    class Tag(val nameParts: Array<String>, val escapeHtml: Boolean) : MToken() {
        override fun toString(): String = "Tag('${nameParts.joinToString(".") { it }}', $escapeHtml)"
    }

    /**
     * A `{{> partial }}` tag
     */
    class Partial(val name: String) : MToken() {
        override fun toString(): String = "Partial($name)"
    }

    /**
     * A whole section of a mustache document. `{{#section}} whole section {{/section}}`
     *
     * @param name null if it's the root of the document.
     * @param inverted true if the section is inverted (`{{^section}}{{/section}}`)
     * @param parts all tokens to renders in order inside this section.
     */
    class Section(
        val nameParts: Array<String>?,
        val toRender: LongRange, // Might be used to cache parts of a document when rendering
        val inverted: Boolean,
        val parts: Collection<MToken>,
    ) : MToken() {
        override fun toString(): String =
            "Section('${nameParts?.joinToString(".") { it } ?: ""}', $toRender, $inverted)"
    }
}
