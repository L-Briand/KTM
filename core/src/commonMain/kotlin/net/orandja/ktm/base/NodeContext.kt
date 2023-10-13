package net.orandja.ktm.base

/**
 * By design [MContext.Map] or [MContext.List] might contain other contexts inside.
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
 * [NodeContext] is used in this case, the caller can try to get it with the [current] context.
 * if not found it tries on [parent]
 */
open class NodeContext(
    val current: MContext,
    private val parent: NodeContext? = null,
) {

    companion object {
        const val CONTINUE = false
        const val STOP = true
    }

    fun getValue(name: String): CharSequence? {
        var result: CharSequence? = null
        collect(toTokenName(name)) { // Matching name
            if(it.current !is MContext.Value) return@collect false
            result = it.current.get(it)
            true
        }
        return result
    }

    /** Get the corresponding context from its name */
    fun get(name: String): MContext? {
        var result: MContext? = null
        collect(toTokenName(name)) { // Matching name
            result = it.current
            true
        }
        return result
    }

    private inline fun toTokenName(name: CharSequence): Array<String> =
        name.split('.').filter { it.isNotEmpty() }.toTypedArray()

    /**
     * Iterates over [parts] which should be a list of all the tag components.
     * Given "my.special.tag" It first searches for the "my" context, then "special" inside it, then "tag".
     * Each time it founds something it publish it through the [onNew] lambda.
     *
     * Renderer implementation can stop the search if they return [STOP] in [onNew]
     */
    fun collect(
        parts: Array<out String>,
        onNew: (NodeContext) -> Boolean,
    ): Boolean {
        if (parts.isEmpty()) return onNew(this)

        // No need to iterate on a single element.
        if (parts.size == 1) return onNew(node(parts[0], true) ?: return CONTINUE)

        // Try to collect recursively
        return collect(TagIterator(parts), onNew)
    }

    private fun collect(
        parts: TagIterator,
        onNew: (NodeContext) -> Boolean,
    ): Boolean {
        if (!parts.hasNext()) return CONTINUE
        val tag = parts.next()
        // try to find the "tag.name" instead of "tag" then "name" inside
        if (node(parts.concatenated(), parts.isFirst())?.let(onNew) == STOP) return STOP

        val node = node(tag, parts.isFirst()) ?: return CONTINUE
        if (!parts.hasNext()) return onNew(node)

        when (node.current) {
            is MContext.Map -> return node.collect(parts, onNew)
            is MContext.List -> {
                val iterator = node.current.iterator(this)
                for (context in iterator) {
                    if (NodeContext(context, node).collect(parts, onNew)) return STOP
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
    fun node(tag: String, checkOnParent: Boolean): NodeContext? {
        if (tag == ".") return this
        val render = (current as? MContext.Map)?.get(this, tag)
        if (render != null) return NodeContext(render, this)
        return if (checkOnParent) parent?.node(tag, true) else null
    }

    /** Simple iterator that can go backward */
    private class TagIterator(val source: Array<out String>) : Iterator<String> {
        private var idx = -1
        override fun hasNext(): Boolean = idx + 1 < source.size
        override fun next(): String = source[++idx]
        fun previous() = idx--
        fun isFirst() = idx == 0

        fun concatenated() = StringBuilder().apply {
            var first = true
            for (i in idx ..< source.size) {
                if (first) first = false
                else append('.')
                append(source[i])
            }
        }.toString()
    }

    override fun toString(): String =
        if (parent == null) "Node($current)"
        else "Node($current, parent=$parent)"
}
