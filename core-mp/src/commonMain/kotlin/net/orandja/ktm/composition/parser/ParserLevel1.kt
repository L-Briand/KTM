package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MDocument.*

@Suppress("NOTHING_TO_INLINE")
class ParserLevel1 {

    private inner class StandaloneLine(
        var startingIndex: Int = 0,
    ) {
        inline fun onNewLine(index: Int, documents: List<MDocument>) {
            if (startingIndex == -1) {
                startingIndex = index + 1
                return
            }
            if (documents.getOrNull(index - 1) is NewLine) {
                startingIndex = index + 1
                return
            }
            for (i in (startingIndex .. index)) {
                if (i in documents.indices) documents[i].disable()
                startingIndex = index + 1
            }
        }

        inline fun reset() {
            startingIndex = -1
        }
    }

    fun applyRules(section: Section) {
        val documents = mutableListOf<MDocument>()
        flatten(section, documents)
        val standalone = StandaloneLine()
        documents.forEachIndexed { idx, element ->
            when (element) {
                Empty -> Unit
                is NewLine -> standalone.onNewLine(idx, documents)
                is Static -> if (!element.isBlank) standalone.reset()
                is Section -> Unit
                is MDocument.Tag -> standalone.reset()
                is Partial -> {
                    val p1 = documents.getOrNull(idx - 1)
                    val p2 = documents.getOrNull(idx - 2)
                    if (p1 is Static && p1.isBlank && (p2 is NewLine || p2 == null)) {
                        element.spaces = p1.content
                    }
                }
            }
        }
        standalone.onNewLine(documents.size, documents)
    }

    private fun flatten(section: Section, output: MutableList<MDocument>) {
        section.parts.forEachIndexed { idx, it ->
            if (it is Section) {
                output += Empty
                flatten(it, output)
                output += Empty
            } else {
                output += it
                // For partial rendering, the last new line should not have any indentation after
                if(it is NewLine && section.parts.size - 1 == idx) {
                    it.last = true
                }
            }
        }
    }

    private inline fun MDocument.disable() = when (this) {
        is NewLine -> this.render = false
        is Static -> this.render = false
        else -> Unit
    }
}