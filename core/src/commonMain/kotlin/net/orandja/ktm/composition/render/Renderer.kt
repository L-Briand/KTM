@file:Suppress("ReplaceSizeZeroCheckWithIsEmpty")

package net.orandja.ktm.composition.render

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.TagRenderVisitor

/**
 * Renderer is a class that provides methods for rendering Mustache templates.
 *
 * @property renderToString Shortcut method to render the document as a string.
 * @property render Shortcut method to render the document without a node context.
 *
 * @see PartialRenderer
 */
@Suppress("NOTHING_TO_INLINE")
open class Renderer {

    companion object {
        private val escapes = mapOf(
            '&' to "&amp;",
            '<' to "&lt;",
            '>' to "&gt;",
            '"' to "&quot;",
            '\'' to "&#x27;",
            '`' to "&#x60;",
            '=' to "&#x3D;",
        )
    }

    // Sugar

    fun renderToString(document: MDocument, context: MContext): String {
        val result = StringBuilder(128)
        render(document, MContext.Node(context)) { result.append(it) }
        return result.toString()
    }

    fun render(
        document: MDocument,
        context: MContext,
        writer: (CharSequence) -> Unit,
    ) = render(document, MContext.Node(context), writer)


    // Rendering section

    open fun render(
        document: MDocument,
        context: MContext.Node,
        writer: (CharSequence) -> Unit,
    ) {
        // TODO: Test if visitor pattern speeds things up here too.
        when (document) {
            is MDocument.Static -> renderStatic(document, writer)
            is MDocument.Tag -> renderTag(document, context, writer)
            is MDocument.Section -> renderSection(document, context, writer)
            is MDocument.Partial -> renderPartial(document, context, writer)
        }
    }

    protected open fun renderSection(
        document: MDocument.Section,
        context: MContext.Node,
        writer: (CharSequence) -> Unit,
    ) {
        val node = context.resolve(document.name)?.current
        when {
            document.inverted -> {
                if (node == null) for (part in document.parts) render(part, context, writer)
                else if (node.accept(context, UseInvertedVisitor)) {
                    val newNode = MContext.Node(node, context)
                    for (part: MDocument in document.parts) render(part, newNode, writer)
                }
            }

            node != null -> {
                val visitorInput = SectionVisitor.Input(context) { newNode ->
                    for (part in document.parts) render(part, newNode, writer)
                }
                node.accept(visitorInput, SectionVisitor)
            }
        }
    }

    protected open fun renderPartial(
        document: MDocument.Partial,
        context: MContext.Node,
        writer: (CharSequence) -> Unit
    ) {
        val partial = context.resolve(document.name)?.current ?: return
        val newDocument = partial.accept(context, PartialDocumentFinderVisitor) ?: return
        val spaces = document.padding
        if (spaces.length == 0) render(newDocument, context, writer)
        else {
            writer(spaces)
            PartialRenderer(spaces).render(newDocument, context, writer)
        }
    }

    protected open fun renderTag(
        document: MDocument.Tag,
        context: MContext.Node,
        writer: (CharSequence) -> Unit,
    ) {
        val node = context.resolve(document.name)?.current ?: return
        val toPrint = node.accept(context, TagRenderVisitor)
        if (toPrint != null) {
            if (document.escapeHtml) escape(toPrint, writer) else writer(toPrint)
        }
    }

    protected open fun escape(cs: CharSequence, writer: (CharSequence) -> Unit) {
        var start = 0
        var idx = 0
        while (idx < cs.length) {
            val escape = escapes[cs[idx]]
            if (escape != null) {
                if (start < idx) writer(cs.subSequence(start, idx))
                writer(escape)
                start = idx + 1
            }
            idx++
        }
        if (start < idx) writer(cs.subSequence(start, idx))
    }


    protected open fun renderStatic(document: MDocument.Static, writer: (CharSequence) -> Unit) =
        writer(document.content)

    /**
     * Detect if the input [MContext.Node] can be used for inverted sections.
     */
    private object UseInvertedVisitor : MContext.Visitor<MContext.Node, Boolean> {
        override fun no(data: MContext.Node, no: MContext.No) = true
        override fun list(data: MContext.Node, list: MContext.List): Boolean {
            return !list.iterator(data).hasNext()
        }

        override fun delegate(data: MContext.Node, delegate: MContext.Delegate): Boolean =
            delegate.get(data).accept(data, this)

        // Others
        override fun value(data: MContext.Node, value: MContext.Value): Boolean = false

        // FIXME: Should an empty map act like an empty list ?
        override fun map(data: MContext.Node, map: MContext.Map): Boolean = false
        override fun document(data: MContext.Node, document: MContext.Document): Boolean = false
        override fun yes(data: MContext.Node, yes: MContext.Yes): Boolean = false
    }


    /**
     * Find the nodes that need to be rendered for a section.
     */
    private object SectionVisitor : MContext.Visitor<SectionVisitor.Input, Unit> {
        data class Input(val node: MContext.Node, val onNewNode: (MContext.Node) -> Unit)

        override fun yes(data: Input, yes: MContext.Yes) = data.onNewNode(data.node)
        override fun value(data: Input, value: MContext.Value) =
            if (data.node.current == value) data.onNewNode(data.node)
            else data.onNewNode(MContext.Node(value, data.node))

        override fun map(data: Input, map: MContext.Map) =
            if (data.node.current == map) data.onNewNode(data.node)
            else data.onNewNode(MContext.Node(map, data.node))

        override fun list(data: Input, list: MContext.List) {
            for (context in list.iterator(data.node)) data.onNewNode(MContext.Node(context, data.node))
        }

        override fun delegate(data: Input, delegate: MContext.Delegate) = delegate.get(data.node).accept(data, this)

        // Other
        override fun no(data: Input, no: MContext.No) {}
        override fun document(data: Input, document: MContext.Document) {}
    }

    /**
     * Find a document inside the [MContext.Node]
     */
    private object PartialDocumentFinderVisitor
        : MContext.Visitor<MContext.Node, MDocument?> {
        override fun document(data: MContext.Node, document: MContext.Document): MDocument = document.get(data)
        override fun value(data: MContext.Node, value: MContext.Value): MDocument =
            Ktm.parser.fromString(value.get(data))

        override fun delegate(data: MContext.Node, delegate: MContext.Delegate): MDocument? =
            delegate.get(data).accept(data, this)

        // Others
        override fun no(data: MContext.Node, no: MContext.No): MDocument? = null
        override fun yes(data: MContext.Node, yes: MContext.Yes): MDocument? = null
        override fun map(data: MContext.Node, map: MContext.Map): MDocument? = null
        override fun list(data: MContext.Node, list: MContext.List): MDocument? = null
    }
}