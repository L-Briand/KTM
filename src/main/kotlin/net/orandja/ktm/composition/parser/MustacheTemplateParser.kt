package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MToken

@Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
object MustacheTemplateParser {

    const val TOKEN_START = 0
    const val TOKEN_STOP = 1
    const val TOKEN_END = 2

    const val TAG_UNESCAPED_1 = '{'
    const val TAG_UNESCAPED_1_END = '}'
    const val TAG_UNESCAPED_2 = '&'
    const val TAG_PARTIAL = '>'
    const val TAG_COMMENT = '!'
    const val TAG_DELIMITER = '='
    const val TAG_DELIMITER_END = '='
    const val TAG_START_SECTION = '#'
    const val TAG_INVERTED_SECTION = '^'
    const val TAG_END_SECTION = '/'

    val whitespaceRegex = Regex("\\s+")

    private data class Node(
        val invert: Boolean,
        val parent: Node? = null,
        val start: Long = -1,
        val stop: Long = -1,
        val tagName: String? = null,
        val parts: MutableList<MToken> = mutableListOf(),
    )

    fun parse(context: MustacheTemplateContext): MToken.Section = with(context) {
        val root = Node(false)
        var node = root
        parseTokens { token ->
            when (token) {
                TOKEN_START -> {
                    addStatic(node, renderIdx, (idx - tkBegin.length))
                }

                TOKEN_STOP -> run {
                    val inTag = getInTag()
                    when (inTag.first()) {
                        TAG_UNESCAPED_1 -> {

                            if (inTag.length >= 2 && inTag[inTag.length - 1] == TAG_UNESCAPED_1_END) {
                                // Matching {{{ }}}
                                val tagName = inTag.subSequence(1, inTag.length - 1).trim()
                                if (tagName.isNotBlank()) node.parts += MToken.Tag(tagParts(tagName.toString()), false)
                            } else {
                                // Matching everything else in {{ }}
                                withTagContent(0, node, inTag) { node.parts += MToken.Tag(tagParts(it), true) }
                            }
                        }

                        TAG_DELIMITER -> {
                            // Matching {{= =}}, === ===
                            if (inTag.length >= 2 && inTag[inTag.length - 1] == TAG_DELIMITER_END) {
                                val tagName = inTag.subSequence(1, inTag.length - 1).trim()
                                if (tagName.isNotBlank()) {
                                    val split = tagName.split(whitespaceRegex)
                                    if (split.size == 2) {
                                        tkBegin = split[0]
                                        tkEnd = split[1]
                                    }
                                }
                            } else withTagContent(0, node, inTag) { node.parts += MToken.Tag(tagParts(it), true) }
                        }

                        TAG_UNESCAPED_2 -> withTagContent(1, node, inTag) {
                            node.parts += MToken.Tag(tagParts(it), false)
                        }

                        TAG_PARTIAL -> withTagContent(1, node, inTag) {
                            node.parts += MToken.Partial(it)
                        }

                        TAG_COMMENT -> Unit
                        TAG_START_SECTION -> withTagContent(1, node, inTag) {
                            node = Node(false, node, (renderIdx - tkBegin.length), idx, it)
                        }

                        TAG_INVERTED_SECTION -> withTagContent(1, node, inTag) {
                            node = Node(true, node, (renderIdx - tkBegin.length), idx, it)
                        }

                        TAG_END_SECTION -> withTagContent(1, node, inTag) {
                            if (it == node.tagName) {
                                val current = node
                                node = node.parent!!
                                node.parts += MToken.Section(
                                    tagParts(current.tagName!!),
                                    current.stop,
                                    (renderIdx - tkBegin.length),
                                    current.invert,
                                    current.parts,
                                )
                            } else {
                                addStatic(node, (renderIdx - tkBegin.length), idx)
                            }
                        }

                        else -> withTagContent(0, node, inTag) { node.parts += MToken.Tag(tagParts(it), true) }
                    }
                }

                TOKEN_END -> if (renderIdx < idx) addStatic(node, renderIdx, idx)
            }
            renderIdx = idx
        }
        if (root != node) {
            root.parts += MToken.Section(
                tagParts(node.tagName!!),
                node.stop, (renderIdx - tkBegin.length),
                node.invert,
                node.parts,
            )
        }

        return MToken.Section(null, 0L, if (idx <= 0L) 0L else idx, false, root.parts)
    }

    private inline fun MustacheTemplateContext.withTagContent(
        startAt: Int,
        node: Node,
        inTagContent: CharSequence,
        block: (String) -> Unit,
    ) {
        val tagName = inTagContent.subSequence(startAt, inTagContent.length).trim()
        if (tagName.isNotEmpty()) {
            block(tagName.toString())
        } else {
            addStatic(node, (renderIdx - tkBegin.length), idx)
        }
    }

    private inline fun tagParts(name: String): Array<String> {
        return name.split('.').filter { it.isNotEmpty() }.toTypedArray()
    }

    /** Add static part to the token */
    private inline fun addStatic(node: Node, start: Long, stop: Long) {
        if (start >= stop) return
        val last = node.parts.lastOrNull()
        if (last is MToken.Static && last.stop == start) {
            node.parts[node.parts.lastIndex] = MToken.Static(last.start, stop)
        } else {
            node.parts += MToken.Static(start, stop)
        }
    }

    /**
     * Call [onNew] every time a token is found in the document
     * Token is [MustacheTemplateContext.tkBegin] and [MustacheTemplateContext.tkEnd]
     *
     * This method alternates between the two components.
     */
    private inline fun MustacheTemplateContext.parseTokens(onNew: (token: Int) -> Unit) {
        while (true) {
            val current = next() ?: break
            when (searching) {
                TOKEN_START -> {
                    if (current == tkBegin[tkIdxCount]) tkIdxCount++ else tkIdxCount = 0
                    if (tkIdxCount == tkBegin.length) {
                        tagType = peek()
                        tkIdxCount = 0
                        onNew(searching)
                        searching = TOKEN_STOP
                    }
                }

                TOKEN_STOP -> {
                    inTagBuffer.append(current)
                    if (current == tkEnd[tkIdxCount]) tkIdxCount++ else tkIdxCount = 0
                    if (tkIdxCount == tkEnd.length) {
                        tkIdxCount = 0

                        if (tkEndSpecial && peek() == tkEnd[tkEnd.length - 1]) {
                            val reallySpecial = when (tagType) {
                                TAG_UNESCAPED_1 -> peek() == TAG_UNESCAPED_1_END
                                TAG_DELIMITER -> peek() == TAG_DELIMITER_END
                                else -> false
                            }
                            if (reallySpecial) {
                                inTagBuffer.append(peeked)
                                consume()
                            }
                        }
                        onNew(searching)
                        searching = TOKEN_START
                    }
                }
            }
        }
        onNew(TOKEN_END)
    }
}
