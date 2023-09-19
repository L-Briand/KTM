package net.orandja.ktm.base

fun interface MRenderer {

    /**
     * Render the given mustache [document] into [writer].
     *
     * @param document Parsed representation of a mustache document.
     * @param context You need to have scoped context when rendering a section.
     *             If you don't find it inside the current context, maybe the parent have it.
     * @param pool Where you find other documents when a partial occurs.
     * @param writer Where you write parts of the rendered document.
     */
    fun render(document: MDocument, context: NodeContext, pool: MPool, writer: (CharSequence) -> Unit)

    /** Shortcut method to render the document as string. */
    fun render(document: MDocument, context: MContext, pool: MPool, writer: (CharSequence) -> Unit) =
        render(document, NodeContext(context), pool, writer)

    /** Shortcut method to render the document as string. */
    fun renderToString(document: MDocument, context: MContext, partial: MPool): String {
        val result = StringBuilder(128)
        render(document, NodeContext(context), partial) { result.append(it) }
        return result.toString()
    }
}
