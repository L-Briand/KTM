package net.orandja.ktm.base

fun interface MRender {

    /**
     * Render part of mustache document.
     * The [token] can be any token of the document.
     *
     * @param provider The raw content provider, can be backed by anything that have MProvider interface.
     * @param token Root token corresponding to the source. (Made with Mustache.parse())
     * @param node You need to have scoped context when rendering a section.
     *             If you don't find it inside the current context, maybe the parent have it.
     * @param pool Where you find other documents when a partial occurs.
     * @param writer Where you write parts of the rendered document.
     */
    fun render(provider: MProvider, token: MToken, pool: MPool, node: CtxNode, writer: (CharSequence) -> Unit)

    /**
     * Shortcut method to render a full document.
     *
     * @param document Raw mustache document.
     * @param context Contains elements to render with [document]
     * @param writer Implementation of MRender should render parts of the output document by calling it.
     */
    fun render(document: MDocument, pool: MPool, context: CtxNode, writer: (CharSequence) -> Unit) {
        val provider = document.provider
        try {
            render(provider, document.tokens, pool, context, writer)
        } finally {
            provider.close()
        }
    }

    /**
     * Shortcut method to render a full document.
     *
     * @param document Raw mustache document.
     * @param context Contains elements to render with [document]
     * @param writer Implementation of MRender should render parts of the output document by calling it.
     */
    fun render(document: MDocument, pool: MPool, context: MContext, writer: (CharSequence) -> Unit) =
        render(document.provider, document.tokens, pool, CtxNode(context), writer)

    /** Shortcut method to render the document as string. */
    fun renderToString(document: MDocument, partial: MPool, context: MContext): String {
        val length = document.tokens.toRender.last - document.tokens.toRender.first
        if (length > Int.MAX_VALUE.toLong()) error("Unable to render document to string. Document is too long.")
        val result = StringBuilder(length.toInt())
        render(document, partial, context) { result.append(it) }
        return result.toString()
    }
}
