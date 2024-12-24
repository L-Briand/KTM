@file:Suppress("ReplaceSizeCheckWithIsNotEmpty", "ReplaceSizeZeroCheckWithIsEmpty")

package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.StringCharStream
import net.orandja.ktm.composition.iterableDottedString
import net.orandja.ktm.composition.tokenizeDelimitedString

object Parser {

    /**
     * Converts a given source character sequence into an [MDocument] instance.
     *
     * @param source The character sequence to parse into a mustache document structure.
     * @return An [MDocument] representing the parsed mustache document.
     */
    fun fromString(source: CharSequence): MDocument = parse(StringCharStream(source))

    /**
     * Parses a character stream into a [MDocument.Section].
     *
     * @param stream The character stream representing the input mustache document.
     * @return The parsed document [MDocument].
     */
    fun parse(stream: CharStream): MDocument.Section =
        ParserContext(TokenParser.parse(TokenParserContext(stream)).iterator()).parse()


    private fun ParserContext.parse(): MDocument.Section {
        var next: Token?

        while (true) {
            // read all tokens representing a line
            while (true) {
                next = peekNext()
                if (next == null) break // End of stream
                if (next.isOpaque) isStantaloneLine = false
                if (next.type == Token.TAG_PARTIAL) containPartial = true
                if (next.isNewLine) break
            }

            // An empty new line should be rendered as is.
            if (peeked.size == 1) {
                register(peeked.removeAt(0))
            }
            // If the line is not transparent (standalone in the spec), we render it
            else if (!isStantaloneLine) {
                for (peek in peeked) register(peek)
                drop()
            }
            // If the line is white, we register only tags elements
            else {
                for (peek in peeked) {
                    if (containPartial && peek.type == Token.WHITE_CONTENT) partialPadding.append(peek.content)
                    if (peek.isTag) register(peek)
                }
                drop()
            }

            if (next == null) break
            // cleanup for next line
            isStantaloneLine = true
            if (containPartial) {
                containPartial = false
                partialPadding.clear()
            }
        }
        val result = nodes[0]
        while (nodes.size > 0) {
            if (nodes[nodes.size - 1].parts.size != 0) {
                val lastElement = nodes[nodes.size - 1].parts[nodes[nodes.size - 1].parts.size - 1]
                (lastElement as? MDocument.Static)?.sectionLast = true
            }
            nodes.removeAt(nodes.size - 1)
        }
        return result
    }

    private fun ParserContext.register(token: Token) {
        when (token.type) {
            Token.TAG_NORMAL, Token.TAG_ESCAPE_1, Token.TAG_ESCAPE_2 -> {
                nodes[nodes.size - 1].parts.add(
                    MDocument.Tag(token.content.iterableDottedString(), token.type == Token.TAG_NORMAL)
                )
            }

            Token.TAG_PARTIAL -> {
                nodes[nodes.size - 1].parts.add(
                    MDocument.Partial(token.content.iterableDottedString(), partialPadding.toString())
                )
            }

            Token.TAG_SECTION, Token.TAG_INVERT -> {
                val newNode = MDocument.Section(token.content.iterableDottedString(), token.type == Token.TAG_INVERT)
                nodes[nodes.size - 1].parts += newNode
                nodes += newNode
            }

            Token.TAG_CLOSE -> {
                if (nodes[nodes.size - 1].parts.size != 0) {
                    val lastElement = nodes[nodes.size - 1].parts[nodes[nodes.size - 1].parts.size - 1]
                    (lastElement as? MDocument.Static)?.sectionLast = true
                }
                if (iteratorEquals(
                        tokenizeDelimitedString(token.content).iterator(), nodes[nodes.size - 1].name.iterator()
                    )
                ) {
                    nodes.removeAt(nodes.size - 1)
                }
            }

            Token.STATIC_CONTENT, Token.WHITE_CONTENT -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append(token.content)
            }

            Token.NEW_LINE_R -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append(token.content)
            }

            Token.NEW_LINE_N -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append(token.content)
            }

            Token.NEW_LINE_RN -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append(token.content)
            }
        }
    }

    private fun ParserContext.getOrCreateLastStaticDocument(): MDocument.Static {
        val node = nodes[nodes.size - 1]
        return if (node.parts.size == 0) {
            val staticDoc = MDocument.Static()
            node.parts.add(staticDoc)
            staticDoc
        } else if (node.parts[node.parts.size - 1] is MDocument.Static) {
            node.parts[node.parts.size - 1] as MDocument.Static
        } else {
            val staticDoc = MDocument.Static()
            node.parts.add(staticDoc)
            staticDoc
        }
    }

    private inline fun <reified T> iteratorEquals(it1: Iterator<T>, it2: Iterator<T>): Boolean {
        while (true) {
            var hasNext1 = it1.hasNext()
            var hasNext2 = it2.hasNext()
            if (hasNext1 != hasNext2) return false
            else if (!hasNext1 && !hasNext2) return true
            else if (it1.next() != it2.next()) return false
        }
    }

}
