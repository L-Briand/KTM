@file:Suppress("ReplaceSizeCheckWithIsNotEmpty")

package net.orandja.ktm.composition.parser

object TokenParser {

    private val RN = Token(Token.NEW_LINE_RN, Token.NO_CONTENT)
    private val N = Token(Token.NEW_LINE_N, Token.NO_CONTENT)
    private val R = Token(Token.NEW_LINE_R, Token.NO_CONTENT)
    private val WHITESPACES_REGEX = "\\s+".toRegex()

    internal fun parse(context: TokenParserContext): Sequence<Token> = sequence { parseTokens(context) }

    /**
     * Call [onNewTag] every time a [Delimiter] is found in the document.
     */
    private suspend fun SequenceScope<Token>.parseTokens(ctx: TokenParserContext) {
        while (true) {
            val current = ctx.next()
            if (current == Char.MAX_VALUE) break
            if (ctx.searchingDelimStart) {
                if (current == '\r') {
                    if (ctx.peek() == '\n') {
                        ctx.consume()
                        pushStatic(ctx, trimEndBy = 2)
                        yield(RN)
                        continue
                    } else {
                        pushStatic(ctx, trimEndBy = 1)
                        yield(R)
                        continue
                    }
                } else if (current == '\n') {
                    pushStatic(ctx, trimEndBy = 1)
                    yield(N)
                    continue
                }

                // Searching for startDelim in the document (i.e. '{{')
                if (current != ctx.startDelim[ctx.delimMatchIdx]) {
                    ctx.isWhiteContent = ctx.isWhiteContent and current.isWhitespace()
                    ctx.delimMatchIdx = 0
                    continue
                }
                ctx.delimMatchIdx++
                if (ctx.delimMatchIdx == ctx.startDelim.length) {
                    ctx.delimMatchIdx = 0
                    pushStatic(ctx, ctx.startDelim.length)
                    ctx.searchingDelimStart = false
                }
            } else {
                // We do not care about spaces in tag name unless delimiter.
                if (current.isWhitespace() && ctx.tagType != Token.TAG_DELIMITER) {
                    ctx.drop()
                    ctx.delimMatchIdx = 0
                    continue
                }

                // First non-white char is always the tag type discriminant
                if (ctx.tagType == Token.NONE) {
                    ctx.tagType = Token.tagTokenIndexFromChar(current)
                    if (ctx.tagType != Token.TAG_NORMAL) ctx.drop()
                    continue
                }

                // Searching for stopDelim in the document (i.e. '}}')
                if (current != ctx.stopDelim[ctx.delimMatchIdx]) {
                    ctx.delimMatchIdx = 0
                    continue
                }
                ctx.delimMatchIdx++
                if (ctx.delimMatchIdx == ctx.stopDelim.length) {
                    ctx.delimMatchIdx = 0

                    ctx.drop(count = ctx.stopDelim.length)
                    val closingTag = Token.TAG[ctx.tagType + 1]
                    if (closingTag != Char.MAX_VALUE) {
                        if (ctx.stopDelimiterBewareChar == closingTag) {
                            val peek = ctx.peek()
                            if (peek == ctx.stopDelim[ctx.stopDelim.length - 1] && peek == closingTag) {
                                ctx.consume()
                            }
                        }
                        val closingIndex = ctx.readBuffer.indexOfLast { closingTag == it }
                        if (closingIndex != -1) ctx.readBuffer.setLength(closingIndex)
                        else ctx.tagType = Token.TAG_NORMAL
                    }

                    val content = ctx.getBuffer(trimEndBy = 0)
                    if (content.isBlank()) {
                        ctx.isWhiteContent = false
                        ctx.readBuffer.append("$ctx.startDelim${Token.TAG[ctx.tagType]} <NO_NAME> $ctx.stopDelim")
                        pushStatic(ctx, trimEndBy = 0)
                    } else {
                        if (ctx.tagType == Token.TAG_DELIMITER) {
                            ctx.startDelim = content.startWord()
                            ctx.stopDelim = content.endWord()
                        }
                        yield(Token(ctx.tagType, content))
                    }
                    ctx.searchingDelimStart = true
                    ctx.tagType = Token.NONE
                }
            }
        }
        ctx.drop()
        pushStatic(ctx, 0)
    }

    private inline fun CharSequence.startWord(): CharSequence {
        var index = 0
        var start = -1
        var end = -1
        while(index < length) {
            if(start == -1) {
                if(!get(index).isWhitespace()) start = index
            } else if(end == -1) {
                if(get(index).isWhitespace()) {
                    end = index
                    return subSequence(start, end)
                }
            }
            index++
        }
        return subSequence(start, length)
    }

    private inline fun CharSequence.endWord(): CharSequence {
        var index = length - 1
        var start = -1
        var end = -1
        while(index >= 0) {
            if(end == -1) {
                if(!get(index).isWhitespace()) end = index + 1
            } else if(start == -1) {
                if(get(index).isWhitespace()) {
                    start = index + 1
                    return subSequence(start, end)
                }
            }
            index--
        }
        return subSequence(0, end)
    }

    private suspend fun SequenceScope<Token>.pushStatic(ctx: TokenParserContext, trimEndBy: Int) {
        val content = ctx.getBuffer(trimEndBy = trimEndBy)
        if (content.length != 0) {
            if (ctx.isWhiteContent) yield(Token(Token.WHITE_CONTENT, content))
            else yield(Token(Token.STATIC_CONTENT, content))
        }
        ctx.isWhiteContent = true
    }
}