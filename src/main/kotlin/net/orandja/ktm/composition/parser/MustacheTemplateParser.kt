package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MToken

@Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
object MustacheTemplateParser {

    const val TOKEN_START = 0
    const val TOKEN_STOP = 1
    const val TOKEN_NEW_LINE = 2
    const val TOKEN_END = 3

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
        val tagPart: LongRange? = null,
        val tagName: String? = null,
        val parts: MutableList<MToken> = mutableListOf(),
    )

    fun parse(context: MustacheTemplateContext): MToken.Section = with(context) {
        val root = Node(false)
        var node = root
        parseTokens { token ->
            when (token) {
//                TOKEN_NEW_LINE -> {
//                    if (renderIdx < idx && lastIdxWhitespace != idx) addStatic(node, renderIdx until idx)
//                }

                TOKEN_START -> {
                    // if (renderIdx < idx - tokStart.length && lastIdxWhitespace != idx - tokStart.length)
                    addStatic(node, renderIdx until idx - tokStart.length)
                }

                TOKEN_STOP -> run {
                    val inTag = getInTag()
                    when (inTag.first()) {
                        TAG_UNESCAPED_1 -> {
                            if (inTag.length >= 2 && inTag[inTag.length - 1] == TAG_UNESCAPED_1_END) {
                                val tagName = inTag.subSequence(1, inTag.length - 1).trim()
                                if (tagName.isNotBlank()) node.parts += MToken.Tag(tagParts(tagName.toString()), false)
                            } else withTag(0, node, inTag) { node.parts += MToken.Tag(tagParts(it), true) }
                        }

                        TAG_DELIMITER -> {
                            if (inTag.length >= 2 && inTag[inTag.length - 1] == TAG_DELIMITER_END) {
                                val tagName = inTag.subSequence(1, inTag.length - 1).trim()
                                if (tagName.isNotBlank()) {
                                    val split = tagName.split(whitespaceRegex)
                                    if (split.size == 2) {
                                        tokStart = split[0]
                                        tokStop = split[1]
                                    }
                                }
                            } else withTag(0, node, inTag) { node.parts += MToken.Tag(tagParts(it), true) }
                        }

                        TAG_UNESCAPED_2 -> withTag(1, node, inTag) { node.parts += MToken.Tag(tagParts(it), false) }
                        TAG_PARTIAL -> withTag(1, node, inTag) { node.parts += MToken.Partial(it) }

                        TAG_COMMENT -> Unit
                        TAG_START_SECTION -> withTag(1, node, inTag) {
                            node = Node(false, node, renderIdx - tokStart.length until idx, it)
                            // lastIdxWhitespace = idx
                        }

                        TAG_INVERTED_SECTION -> withTag(1, node, inTag) {
                            node = Node(true, node, renderIdx - tokStart.length until idx, it)
                            // lastIdxWhitespace = idx
                        }

                        TAG_END_SECTION -> withTag(1, node, inTag) {
                            if (it == node.tagName) {
                                val current = node
                                node = node.parent!!
                                node.parts += MToken.Section(
                                    tagParts(current.tagName!!),
                                    current.tagPart!!.last until renderIdx - tokStart.length,
                                    current.invert,
                                    current.parts
                                )
                            } else {
                                addStatic(node, renderIdx - tokStart.length until idx)
                            }
                            // lastIdxWhitespace = idx
                        }

                        else -> withTag(0, node, inTag) { node.parts += MToken.Tag(tagParts(it), true) }
                    }
                }

                TOKEN_END -> if (renderIdx < idx) addStatic(node, renderIdx until idx)
            }
            renderIdx = idx
        }
        if (root != node) root.parts += MToken.Section(
            tagParts(node.tagName!!), node.tagPart!!.last until renderIdx - tokStart.length, node.invert, node.parts
        )
        val fullRange = if (idx <= 0L) 0L..0L else 0 until idx
        return MToken.Section(null, fullRange, false, root.parts)
    }

    private inline fun MustacheTemplateContext.withTag(
        startAt: Int, node: Node, inTag: CharSequence, block: (String) -> Unit
    ) {
        val tagName = inTag.subSequence(startAt, inTag.length).trim()
        if (tagName.isNotEmpty()) block(tagName.toString())
        else addStatic(node, renderIdx - tokStart.length until idx)
    }

    private inline fun tagParts(name: String): Array<String> {
        return name.split('.').filter { it.isNotEmpty() }.toTypedArray()
    }


    private inline fun addStatic(node: Node, range: LongRange) {
        if (!range.isEmpty()) {
            val last = node.parts.lastOrNull()
            if (last is MToken.Static && last.toRender.last + 1 == range.first) {
                node.parts[node.parts.lastIndex] = MToken.Static(last.toRender.first..range.last)
            } else node.parts += MToken.Static(range)
        }
    }

    private inline fun MustacheTemplateContext.parseTokens(
        onNew: (token: Int) -> Unit,
    ) {
        while (true) {
            val current = next() ?: break
            //lastIdxWhitespace = if (current.isWhitespace() && (lastIdxWhitespace + 1 == idx)) idx else lastIdxWhitespace
            when (searching) {
                TOKEN_START -> {
                    //if (current == '\n') {
                    //    onNew(TOKEN_NEW_LINE)
                    //    lastIdxWhitespace = idx
                    //} else {
                    if (current == tokStart[tokIdx]) tokIdx++ else tokIdx = 0
                    if (tokIdx == tokStart.length) {
                        tagType = peek()
                        tokIdx = 0
                        onNew(searching)
                        searching = TOKEN_STOP
                    }
                    //}
                }

                TOKEN_STOP -> {
                    inTagBuffer.append(current)
                    if (current == tokStop[tokIdx]) tokIdx++ else tokIdx = 0
                    if (tokIdx == tokStop.length) {
                        tokIdx = 0

                        if (teSpecial && peek() == tokStop[tokStop.length - 1]) {
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