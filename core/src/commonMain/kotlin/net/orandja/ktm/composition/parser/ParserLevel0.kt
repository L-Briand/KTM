package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument

@Suppress("NOTHING_TO_INLINE")
internal class ParserLevel0 {

    private data class Node(
        val invert: Boolean,
        val name: CharSequence = ".",
        val parent: Node? = null,
        val parts: MutableList<MDocument> = mutableListOf(),
    )

    /**
     * Parses the given `ParserLevel0Context` to extract structured content and constructs
     * an `MDocument.Section` representing the parsed document.
     *
     * @param context Utility class for the parser to keep track of the parsing process.
     * @return The parsed document represented as an `MDocument.Section`.
     */
    fun parse(context: ParserLevel0Context): MDocument.Section = with(context) {
        val root = Node(false)
        var node = root
        parseTokens { token, content ->
            when (token) {
                Delimiter.START, Delimiter.END -> {
                    if (content.isNotEmpty()) node.parts += MDocument.Static(content.toString())
                }

                Delimiter.NEW_LINE_N, Delimiter.NEW_LINE_R, Delimiter.NEW_LINE_RN -> {
                    if (content.isNotEmpty()) node.parts += MDocument.Static(content.toString())
                    node.parts += MDocument.NewLine(
                        when (token) {
                            Delimiter.NEW_LINE_R -> MDocument.NewLine.Kind.R
                            Delimiter.NEW_LINE_RN -> MDocument.NewLine.Kind.RN
                            Delimiter.NEW_LINE_N -> MDocument.NewLine.Kind.N
                            else -> error("unreachable")
                        }
                    )
                }

                // Stop delimiter should always be found with a tag type.
                Delimiter.STOP -> when (tagType !!) {

                    Tag.NORMAL -> {
                        if (content.isBlank()) addRaw(node, content)
                        else node.parts += MDocument.Tag(toTokenName(content.trim()), true)
                    }

                    Tag.COMMENT -> node.parts += MDocument.Comment

                    // For tags with opening char, we are sure to have at least one non-space char inside.
                    // This is so because we already have found the Tag type.

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
                                node.parts += MDocument.Delimiter
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
                            node.parent !!.parts += MDocument.Section(
                                toTokenName(node.name),
                                node.invert,
                                node.parts.toTypedArray()
                            )
                            node = node.parent !!
                        }
                    }
                }
            }
        }

        // Ambiguous state where a section did not end in the document and is still open.
        // The end of the document acts like a close section tag. `'{{/ <anyName> }}'`
        if (root != node) root.parts += MDocument.Section(
            toTokenName(node.name),
            node.invert,
            node.parts.toTypedArray()
        )

        MDocument.Section(emptyArray(), false, root.parts.toTypedArray())
    }

    /** When a tag cannot be deciphered, this method is called to add it as static content. */
    private inline fun ParserLevel0Context.addRaw(node: Node, content: CharSequence) {
        node.parts += MDocument.Static("$startDelim$content$stopDelim")
    }

    private inline fun toTokenName(name: CharSequence): Array<String> =
        name.split('.').filter { it.isNotEmpty() }.toTypedArray()

    /**
     * Call [onNew] every time a [Delimiter] is found in the document.
     */
    private inline fun ParserLevel0Context.parseTokens(onNew: (hitDelimiter: Delimiter, content: CharSequence) -> Unit) {
        while (true) {
            val current = when (searchingDelim) {
                // break is end of stream
                Delimiter.START -> nextImportant() ?: break
                Delimiter.STOP -> nextNonWhiteSpace() ?: break
                else -> break
            }

            // Line breaks can be \r, \r\n or \n.
            // In any case it indicates an important break in the flow.
            // We do not check line breaks in tags {{\n name }} should not impact rendered content.
            if (searchingDelim == Delimiter.START) when (current) {
                '\r' -> {
                    if (peek() == '\n') {
                        consume()
                        onNew(Delimiter.NEW_LINE_RN, getBuffer(trimEndBy = 2))
                    } else {
                        onNew(Delimiter.NEW_LINE_R, getBuffer(trimEndBy = 1))
                    }
                    continue
                }

                '\n' -> {
                    onNew(Delimiter.NEW_LINE_N, getBuffer(trimEndBy = 1))
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
                    if (current == startDelim[delimMatchIdx]) delimMatchIdx ++ else delimMatchIdx = 0
                    if (delimMatchIdx == startDelim.length) {
                        delimMatchIdx = 0
                        onNew(searchingDelim, getBuffer(trimEndBy = startDelim.length))
                        searchingDelim = Delimiter.STOP
                        tagType = null
                    }
                }

                Delimiter.STOP -> {
                    // Searching for stopDelim in the document (i.e. '}}')
                    if (current == stopDelim[delimMatchIdx]) delimMatchIdx ++ else delimMatchIdx = 0
                    if (delimMatchIdx == stopDelim.length) {
                        delimMatchIdx = 0

                        // Check if we need to read the next char based on [isStopDelimSpecial]
                        // If '{{{ }}' is found, it will read '{ '
                        // If '{{{ }}}' is  found, it will read '{ }'
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
    private inline fun ParserLevel0Context.nextImportant(): Char? {
        while (true) {
            val current = next() ?: return null
            return when {
                ! current.isWhitespace() -> current
                current == '\n' -> current
                current == '\r' -> current
                else -> {
                    // If a space is between two delimiter characters, it invalidates the reading
                    delimMatchIdx = 0
                    continue
                }
            }
        }
    }

    /** Try to find the next non-space char in the document. */
    private inline fun ParserLevel0Context.nextNonWhiteSpace(): Char? {
        while (true) {
            val current = next() ?: return null
            if (! current.isWhitespace()) return current
            // If a space is between two delimiter characters, it invalidates the reading
            delimMatchIdx = 0
        }
    }

    /** Find the first non-space character in the string */
    private inline fun CharSequence.firstNonWhiteSpace(): Int {
        for (i in indices) if (! get(i).isWhitespace()) return i
        return - 1
    }

    /** Find the last non-space character in the string */
    private inline fun CharSequence.lastNonWhiteSpace(): Int {
        for (i in indices.reversed()) if (! get(i).isWhitespace()) return i
        return - 1
    }
}