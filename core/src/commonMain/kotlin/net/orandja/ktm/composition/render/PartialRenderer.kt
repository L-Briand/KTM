package net.orandja.ktm.composition.render

import net.orandja.ktm.base.MDocument

/**
 * PartialRenderer is a class that extends the Renderer class
 * and provides additional functionality for rendering Mustache templates with partials.
 *
 * @property spaces The spaces to be added before rendering the template.
 */
class PartialRenderer(private val spaces: CharSequence) : Renderer() {

    override fun renderStatic(document: MDocument.Static, writer: (CharSequence) -> Unit) {
        var written = 0
        var cache: Int
        val newLines = document.content.newLines().iterator()
        while (newLines.hasNext()) {
            cache = newLines.next()
            writer(document.content.subSequence(written, cache))
            written = cache
            // Do not write padding on the last new line of a section
            if (written != document.content.length || !document.sectionLast) writer(spaces)
        }
        if (written != document.content.length) writer(document.content.subSequence(written, document.content.length))
    }

    private fun CharSequence.newLines() = sequence<Int> {
        var index = 0
        while (index < length) {
            if (get(index) == '\r') {
                if (index + 1 < length && get(index + 1) == '\n') index++
                yield(index + 1)
            } else if (get(index) == '\n') yield(index + 1)
            index++
        }
    }
}