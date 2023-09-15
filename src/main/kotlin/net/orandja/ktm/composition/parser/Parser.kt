package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument
import java.io.Reader
import java.io.StringReader

@Suppress("NOTHING_TO_INLINE")
class Parser {

    fun parse(document: String) = parse(ParserContext(StringReader(document)))
    fun parse(reader: Reader) = parse(ParserContext(reader))

    private data class Node(
        val invert: Boolean,
        val name: CharSequence = ".",
        val parent: Node? = null,
        val parts: MutableList<MDocument> = mutableListOf(),
    )

    fun parse(context: ParserContext): MDocument.Section = with(context) {
        val root = Node(false)
        var node = root
        parseTokens { token, content ->
            when (token) {
                Delimiter.START, Delimiter.END -> {
                    if (content.isNotEmpty()) node.parts += MDocument.Static(content.toString())
                }

                Delimiter.NEW_LINE -> {
                    if (content.isNotEmpty()) node.parts += MDocument.Static(content.toString())
                    node.parts += MDocument.NewLine
                }

                // Stop delimiter should always be found with a tag type.
                Delimiter.STOP -> when (tagType!!) {

                    Tag.NORMAL -> {
                        if (content.isBlank()) addRaw(node, content)
                        else node.parts += MDocument.Tag(toTokenName(content.trim()), true)
                    }

                    Tag.COMMENT -> node.parts += MDocument.Comment

                    // For tags with opening char, we are sure to have at least one non-space.
                    // Might be false if implementation of Tag change

                    Tag.PARTIAL -> {
                        val idx = content.firstNonWhiteSpace()
                        val tagName = content.subSequence(idx + 1, content.length).trim()
                        if (tagName.isBlank()) addRaw(node, content)
                        else node.parts += MDocument.Partial(tagName.toString())
                    }

                    Tag.UNESCAPED_2 -> {
                        val idx = content.firstNonWhiteSpace()
                        val tagName = content.subSequence(idx + 1, content.length).trim()
                        if (tagName.isBlank()) addRaw(node, content)
                        else node.parts += MDocument.Tag(toTokenName(tagName), false)
                    }

                    Tag.UNESCAPED_1 -> {
                        val end = content.lastNonWhiteSpace()
                        if (content[end] != Tag.UNESCAPED_1.close) addRaw(node, content) else {
                            val tagName = content.trim().subSequence(1, content.length - 1).trim()
                            if (tagName.isBlank()) addRaw(node, content)
                            else node.parts += MDocument.Tag(toTokenName(tagName), false)
                        }
                    }

                    Tag.DELIMITER -> {
                        val end = content.lastNonWhiteSpace()
                        if (content[end] != Tag.DELIMITER.close) addRaw(node, content) else {
                            val tagName = content.trim().subSequence(1, content.length - 1).trim()
                            if (tagName.isBlank()) addRaw(node, content) else {
                                val newDelimiters = tagName.split("\\s+".toRegex())
                                if (newDelimiters.size != 2) addRaw(node, content)
                                else {
                                    startDelim = newDelimiters.first()
                                    stopDelim = newDelimiters.last()
                                }
                            }
                        }
                    }

                    Tag.START_SECTION -> {
                        val idx = content.firstNonWhiteSpace()
                        val tagName = content.subSequence(idx + 1, content.length).trim()
                        if (tagName.isBlank()) addRaw(node, content) else {
                            node = Node(false, tagName, node)
                        }
                    }

                    Tag.INVERTED_SECTION -> {
                        val idx = content.firstNonWhiteSpace()
                        val tagName = content.subSequence(idx + 1, content.length).trim()
                        if (tagName.isBlank()) addRaw(node, content) else {
                            node = Node(true, tagName, node)
                        }
                    }

                    Tag.END_SECTION -> {
                        val idx = content.firstNonWhiteSpace()
                        val tagName = content.subSequence(idx + 1, content.length).trim()
                        if (tagName.isBlank()) addRaw(node, content)
                        else if (tagName != node.name) addRaw(node, content)
                        else {
                            node.parent!!.parts += MDocument.Section(toTokenName(node.name), node.invert, node.parts.toTypedArray())
                            node = node.parent!!
                        }
                    }
                }
            }
        }

        // Ambiguous state where a section did not end in the document and is still open.
        // The end of the document acts like a close section tag. `'{{/ <anyName> }}'`
        if (root != node) root.parts += MDocument.Section(toTokenName(node.name), node.invert, node.parts.toTypedArray())

        MDocument.Section(emptyArray(), false, root.parts.toTypedArray())
    }

    /** When a tag cannot be deciphered, this method is called to add it as static content. */
    private inline fun ParserContext.addRaw(node: Node, content: CharSequence) {
        node.parts += MDocument.Static("$startDelim$content$stopDelim")
    }

    private inline fun toTokenName(name: CharSequence): Array<String> =
        name.split('.').filter { it.isNotEmpty() }.toTypedArray()

    /**
     * Call [onNew] every time a [Delimiter] is found in the document.
     */
    private inline fun ParserContext.parseTokens(onNew: (hitDelimiter: Delimiter, content: CharSequence) -> Unit) {
        while (true) {
            val current = when (searchingDelim) {
                Delimiter.START -> nextImportant() ?: break // end of stream
                Delimiter.STOP -> nextNonWhiteSpace() ?: break // end of stream
                else -> break
            }

            // Line breaks can be \r, \r\n or \n.
            // In any case it indicates an important break in the flow.
            // We do not check line breaks in tags {{\n name }} is a valid tag.
            if (searchingDelim == Delimiter.START) when (current) {
                '\r' -> {
                    if (peek() == '\n') consume()
                    onNew(Delimiter.NEW_LINE, getBuffer(trimEndBy = 2))
                    continue
                }

                '\n' -> {
                    onNew(Delimiter.NEW_LINE, getBuffer(trimEndBy = 1))
                    continue
                }
            }

            // We search the current tag type only when we're in a tag.
            if (searchingDelim == Delimiter.STOP && tagType == null) {
                tagType = Tag.getTagFromChar(current)
            }

            when (searchingDelim) {
                Delimiter.START -> {
                    // Searching for startDelim in the document (i.e. '{{')
                    if (current == startDelim[delimIdxCount]) delimIdxCount++ else delimIdxCount = 0
                    if (delimIdxCount == startDelim.length) {
                        delimIdxCount = 0
                        onNew(searchingDelim, getBuffer(trimEndBy = startDelim.length))
                        searchingDelim = Delimiter.STOP
                        tagType = null
                    }
                }

                Delimiter.STOP -> {
                    // Searching for stopDelim in the document (i.e. '}}')
                    if (current == stopDelim[delimIdxCount]) delimIdxCount++ else delimIdxCount = 0
                    if (delimIdxCount == stopDelim.length) {
                        delimIdxCount = 0

                        // Check if we need to read the next char based on [isStopDelimSpecial]
                        // If '{{{ }}' is found, it will read '{ '
                        // If '{{{ }}}' is  found, it will read '{ }'
                        // TODO: Check perf without run
                        if (isStopDelimSpecial && peek() == stopDelim[stopDelim.length - 1]) run {
                            val nextChar = peek() ?: return@run
                            if (nextChar == tagType?.close) consume()
                        }
                        onNew(searchingDelim, getBuffer(trimEndBy = stopDelim.length))
                        searchingDelim = Delimiter.START
                        tagType = null
                    }
                }

                else -> throw IllegalStateException("Parser cannot search for $searchingDelim")
            }
        }
        onNew(Delimiter.END, getBuffer(trimEndBy = 0))
    }

    /** Try to find the next non-space or line break in the document. */
    private inline fun ParserContext.nextImportant(): Char? {
        while (true) {
            val current = next() ?: return null
            return when {
                !current.isWhitespace() -> current
                current == '\n' -> current
                current == '\r' -> current
                else -> {
                    // If a space is between two delimiter characters, it invalidates the reading
                    delimIdxCount = 0
                    continue
                }
            }
        }
    }

    /** Try to find the next non-space char in the document. */
    private inline fun ParserContext.nextNonWhiteSpace(): Char? {
        while (true) {
            val current = next() ?: return null
            if (!current.isWhitespace()) return current
            // If a space is between two delimiter characters, it invalidates the reading
            delimIdxCount = 0
        }
    }

    /** Find the first non-space character in the string */
    private inline fun CharSequence.firstNonWhiteSpace(): Int {
        for (i in indices) if (!get(i).isWhitespace()) return i
        return -1
    }

    /** Find the last non-space character in the string */
    private inline fun CharSequence.lastNonWhiteSpace(): Int {
        for (i in indices.reversed()) if (!get(i).isWhitespace()) return i
        return -1
    }
}