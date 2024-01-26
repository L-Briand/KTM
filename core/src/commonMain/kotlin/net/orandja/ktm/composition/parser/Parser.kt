@file:Suppress("ReplaceSizeCheckWithIsNotEmpty", "ReplaceSizeZeroCheckWithIsEmpty")

package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.tokenizeTagName

object Parser {

    fun parse(stream: CharStream): MDocument.Section =
        ParserContext(TokenParser.parse(TokenParserContext(stream)).iterator()).parse()

    private fun ParserContext.parse(): MDocument.Section {
        var next: Token?

        while (true) {
            // read all tokens representing a line
            while (true) {
                next = peekNext()
                if (next == null) break
                if (next.isOpaque) isStantaloneLine = false
                if (next.type == Token.TAG_PARTIAL) containPartial = true
                if (next.isNewLine) break
            }

            // If two new lines are right next one another, the new line should be rendered
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
                    if (containPartial && peek.type == Token.WHITE_CONTENT)
                        partialPadding.append(peek.content)
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
                    MDocument.Tag(tokenizeTagName(token.content), token.type == Token.TAG_NORMAL)
                )
            }

            Token.TAG_PARTIAL -> {
                nodes[nodes.size - 1].parts.add(MDocument.Partial(token.content, partialPadding.toString()))
            }

            Token.TAG_SECTION, Token.TAG_INVERT -> {
                val newNode = MDocument.Section(tokenizeTagName(token.content), token.type == Token.TAG_INVERT)
                nodes[nodes.size - 1].parts += newNode
                nodes += newNode
            }

            Token.TAG_CLOSE -> {
                if (nodes[nodes.size - 1].parts.size != 0) {
                    val lastElement = nodes[nodes.size - 1].parts[nodes[nodes.size - 1].parts.size - 1]
                    (lastElement as? MDocument.Static)?.sectionLast = true
                }
                if (tokenizeTagName(token.content).contentEquals(nodes[nodes.size - 1].name)) {
                    nodes.removeAt(nodes.size - 1)
                }
            }

            Token.STATIC_CONTENT, Token.WHITE_CONTENT -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append(token.content)
            }

            Token.NEW_LINE_R -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append('\r')
            }

            Token.NEW_LINE_N -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append('\n')
            }

            Token.NEW_LINE_RN -> {
                val static = getOrCreateLastStaticDocument()
                static.content.append("\r\n")
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
}
