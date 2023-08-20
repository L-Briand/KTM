package net.orandja.ktm.base

/**
 * Used when nested contexts occurs.
 *
 * By design [MContext.Group] or [MContext.Multi] might contain other contexts inside.
 * It allows sections to properly render. Example:
 *
 * - template : `{{#section}}{{tag}}{{/section}}`
 * - data : `{ "section" : { "tag" : "hello" } }`
 *
 * However, when rendering we might want to use an upper bounded context. Example :
 *
 * - template : `{{#section}}{{tag}}{{/section}}`
 * - data : `{ "section" : true, "tag" : "hello" }`
 *
 * [CtxNode] is used in this case, the caller can try to get it with the [current] context.
 * if not found it tries on [parent]
 */
open class CtxNode(
    val current: MContext,
    val parent: CtxNode? = null,
) {

    companion object {
        const val CONTINUE = false
        const val STOP = true
    }

    /** Fetch the first element of a given tag and cast it as value */
    fun getValue(tag: String): CharSequence? = (get(tag) as? MContext.Value)?.get(this)

    /** fetch the first element of a given tag. */
    fun get(tag: String): MContext? {
        var result: MContext? = null
        nodes(tagName(tag)) {
            result = it.current
            STOP
        }
        return result
    }

    /** Same as [collectNodes] taking a fully qualified tag with dots*/
    fun getAll(tag: String) = collectNodes(*tagName(tag)).map { it.current }


    /** util function to collect or [nodes] */
    fun collectNodes(vararg parts: String): List<CtxNode> {
        val result = mutableListOf<CtxNode>()
        nodes(parts) {
            result += it
            CONTINUE
        }
        return result
    }


    /**
     * Iterates over [parts] which should be a list of all the tag components.
     * Given "my.special.tag" It first searches for the "my" context, then "special" inside it, then "tag".
     * Each time it founds something it publish it through the [onNew] lambda.
     *
     * Example : Special is a list containing multiple tags
     * - `{{ special.tag }}`
     * - `{ "special" : [ { "tag" : "hello" }, { "tag": "world" } ]
     */
    fun nodes(
        parts: Array<out String>,
        onNew: (CtxNode) -> Boolean
    ): Boolean {
        if (parts.isEmpty()) {
            return onNew(this)
        }
        if (parts.size == 1) {
            return onNew(node(parts[0], true) ?: return CONTINUE)
        }
        return nodes(TagIterator(parts), onNew)
    }

    private fun nodes(
        parts: TagIterator,
        onNew: (CtxNode) -> Boolean,
    ): Boolean {
        if (!parts.hasNext()) return CONTINUE
        val tag = parts.next()
        // try to find the "tag.name" instead of "tag" then "name" inside
        if (node(parts.concatenated(), parts.isFirst())?.let(onNew) == STOP) return STOP
        val node = node(tag, parts.isFirst()) ?: return CONTINUE
        if (!parts.hasNext()) {
            return onNew(node)
        }

        when (node.current) {
            is MContext.Group -> return node.nodes(parts, onNew)
            is MContext.Multi -> {
                val iterator = node.current.iterator(this)
                for (context in iterator) {
                    if (CtxNode(context, node).nodes(parts, onNew)) return STOP
                    parts.previous()
                }
            }

            else -> return CONTINUE
        }
        return CONTINUE
    }

    /**
     * Get a direct node from a simple tag name
     */
    fun node(tag: String, checkOnParent: Boolean): CtxNode? {
        if (tag == ".") return this
        val render = (current as? MContext.Group)?.get(this, tag)
        if (render != null) return CtxNode(render, this)
        return if (checkOnParent) parent?.node(tag, checkOnParent) else null
    }

    /** Simple iterator that can go backward */
    private class TagIterator(val source: Array<out String>) : Iterator<String> {
        private var idx = -1
        override fun hasNext(): Boolean = idx + 1 < source.size
        override fun next(): String = source[++idx]
        fun previous() = idx--
        fun isFirst() = idx == 0
        fun concatenated() = source.slice(idx until source.size).joinToString(".") { it }
    }

    /** Split the tag into an array. It keeps the tag '.' in case of value list render */
    private fun tagName(name: String): Array<out String> {
        if (name.isEmpty()) return emptyArray()
        if (name == ".") return arrayOf(".")
        val split = name.split('.').filter { it.isNotEmpty() }
        return if (split.isEmpty()) arrayOf(name) else split.toTypedArray()
    }

    override fun toString(): String = "CtxNode(current=$current, parent=$parent)"
}
