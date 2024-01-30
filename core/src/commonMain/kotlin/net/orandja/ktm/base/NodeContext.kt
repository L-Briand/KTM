package net.orandja.ktm.base

import net.orandja.ktm.composition.tokenizeTagName

/**
 * By design [MContext.Map] or [MContext.List] might contain other contexts inside.
 * It allows sections to be properly rendered.
 *
 * - Template : `{{#section}}{{tag}}{{/section}}`
 * - Data : `{ "section" : { "tag" : "hello" } }`
 *
 * However, when rendering, we might want to use an upper bounded context.
 *
 * - Template : `{{#section}}{{tag}}{{/section}}`
 * - data : `{ "section" : true, "tag" : "hello" }`
 *
 * [NodeContext] is used for this case, the caller can try to get it with the [current] context.
 * If not found it tries on [parent].
 *
 * To search specific context with name use [find] or [findValue].
 */
open class NodeContext(
    val current: MContext,
    private val parent: NodeContext? = null,
) {
    fun findValue(tag: String): CharSequence? {
        return find(tokenizeTagName(tag))?.accept(this, TagRenderVisitor)
    }

    fun find(tag: String): MContext? = find(tokenizeTagName(tag))

    fun find(
        name: Array<out String>,
    ): MContext? = when (name.size) {
        0 -> this.current
        1 -> findUp(name[0], true)
        else -> findDown(name.iterator(), true)
    }

    private fun findDown(
        tags: Iterator<String>,
        first: Boolean,
    ): MContext? {
        val tag = tags.next()
        // Now we search recursively
        val node = findUp(tag, first) ?: return null
        // End of the branch we found a matching tag!
        if (!tags.hasNext()) return node
        return node.accept(this, nextNodeVisitor)?.findDown(tags, false)
    }

    private fun findUp(tag: String, checkOnParent: Boolean): MContext? =
        current.accept(tag, contextVisitor) ?: if (checkOnParent) parent?.findUp(tag, true) else null

    private val contextVisitor = ContextVisitor()

    inner class ContextVisitor : MContext.Visitor.Default<String, MContext?>(null) {
        override fun map(data: String, map: MContext.Map): MContext? = map.get(this@NodeContext, data)
        override fun delegate(data: String, delegate: MContext.Delegate): MContext? =
            delegate.get(this@NodeContext).accept(data, this)
    }

    private val nextNodeVisitor = NextNodeVisitor()

    inner class NextNodeVisitor : MContext.Visitor.Default<NodeContext, NodeContext?>(null) {
        override fun map(data: NodeContext, map: MContext.Map): NodeContext = NodeContext(map, data)
        override fun delegate(data: NodeContext, delegate: MContext.Delegate): NodeContext? =
            delegate.get(data).accept(data, this)
    }

    override fun toString(): String = if (parent == null) "Node($current)"
    else "Node($current, parent=$parent)"
}
