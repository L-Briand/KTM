package net.orandja.ktm.composition

import net.orandja.ktm.base.*
import net.orandja.ktm.base.context.NodeContext

@Suppress("NOTHING_TO_INLINE")
open class MustacheRenderer : MRender {
    companion object : MustacheRenderer()

    override fun render(
        provider: MProvider,
        token: MToken,
        pool: MPool,
        node: NodeContext,
        writer: (CharSequence) -> Unit,
    ) = when (token) {
        is MToken.Static -> renderStatic(provider, token, writer)
        is MToken.Tag -> renderTag(token, node, writer)
        is MToken.Section -> renderSection(provider, token, node, pool, writer)
        is MToken.Partial -> renderPartial(token, node, pool, writer)
    }

    // region section

    protected inline fun renderSection(
        provider: MProvider,
        token: MToken.Section,
        node: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        var render = false
        node.collect(token.nameParts ?: emptyArray()) { newNode ->
            render = true
            if (token.inverted) {
                // inverted context
                val shouldRenderInverted = when (newNode.current) {
                    MContext.No -> true
                    is MContext.Multi -> !newNode.current.iterator(newNode).hasNext()
                    is MContext.Group, is MContext.Value, MContext.Yes -> false
                }
                if (shouldRenderInverted) {
                    renderSectionItems(provider, token, pool, NodeContext(newNode.current, node), writer)
                }
            } else {
                // Not inverted context
                when (newNode.current) {
                    is MContext.Group -> {
                        val nextNode = if (node == newNode) newNode else NodeContext(newNode.current, node)
                        renderSectionItems(provider, token, pool, nextNode, writer)
                    }

                    is MContext.Value, MContext.Yes -> renderSectionItems(provider, token, pool, newNode, writer)
                    is MContext.Multi -> {
                        val iterator = newNode.current.iterator(newNode)
                        while (iterator.hasNext()) {
                            val nextNode = NodeContext(iterator.next(), NodeContext(newNode.current, node))
                            renderSectionItems(provider, token, pool, nextNode, writer)
                        }
                    }

                    MContext.No -> Unit
                }
            }
            NodeContext.STOP
        }
        // Broken context in {{.}} should be considered falsey
        if (!render && token.inverted) {
            renderSectionItems(provider, token, pool, node, writer)
        }
    }

    protected inline fun renderSectionItems(
        provider: MProvider,
        token: MToken.Section,
        pool: MPool,
        node: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        for (part in token.parts) render(provider, part, pool, node, writer)
    }

    // endregion

    // region tag

    protected inline fun renderTag(
        token: MToken.Tag,
        node: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        if (token.escapeHtml) {
            value(token, node) { escape(it, writer) }
        } else {
            value(token, node, writer)
        }
    }

    protected inline fun value(
        token: MToken.Tag,
        node: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        node.collect(token.nameParts) { newNode ->
            when (newNode.current) {
                is MContext.Multi -> {
                    for (context in newNode.current.iterator(newNode)) {
                        if (context is MContext.Value) {
                            writer(context.get(newNode))
                        }
                    }
                    NodeContext.STOP
                }

                is MContext.Value -> {
                    writer(newNode.current.get(newNode))
                    NodeContext.STOP
                }

                is MContext.Group, MContext.No, MContext.Yes -> {
                    NodeContext.CONTINUE
                }
            }
        }
    }

    protected inline fun escape(cs: CharSequence, writer: (CharSequence) -> Unit) {
        var start = 0
        var idx = 0
        while (idx < cs.length) {
            when (cs[idx]) {
                '&' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&amp;")
                    start = idx + 1
                }

                '<' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&lt;")
                    start = idx + 1
                }

                '>' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&gt;")
                    start = idx + 1
                }

                '"' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&quot;")
                    start = idx + 1
                }

                '\'' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&#x27;")
                    start = idx + 1
                }

                '`' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&#x60;")
                    start = idx + 1
                }

                '=' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&#x3D;")
                    start = idx + 1
                }
            }
            idx++
        }
        if (start < idx) writer(cs.subSequence(start, idx))
    }

    // endregion

    // region others

    protected inline fun renderStatic(
        provider: MProvider,
        token: MToken.Static,
        writer: (CharSequence) -> Unit,
    ) = writer(provider.subSequence(token.start, token.stop))

    protected inline fun renderPartial(
        token: MToken.Partial,
        node: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        val document = pool[token.name] ?: return
        render(document, pool, node, writer)
    }

    // endregion
}
