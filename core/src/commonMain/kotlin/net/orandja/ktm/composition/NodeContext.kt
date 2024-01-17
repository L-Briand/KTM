package net.orandja.ktm.composition

import net.orandja.ktm.base.MContext

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
 * To fetch contexts simple contexts use [search] or [searchValue].
 * For more complex rendering use [collect],
 */
open class NodeContext(
    val current: MContext,
    private val parent: NodeContext? = null,
) {

    companion object {
        /**
         * When you [collect] contexts from a [NodeContext]. Return [CONTINUE] to keep searching for what you need.
         */
        const val CONTINUE = false

        /**
         * When you [collect] contexts from a [NodeContext]. Return [STOP] to stop searching for what you need.
         */
        const val STOP = true
    }

    /**
     * Search for the first [MContext.Value] corresponding [tag] and get its value or null.
     *
     * Example:
     * ```kotlin
     * val context = Ktm.ctx.make { "hello" by make { "world" by "Happy world :)" } }
     * NodeContext(context).searchValue("hello.world")
     * ```
     */
    fun searchValue(tag: String): CharSequence? {
        var result: CharSequence? = null
        collect(tokenizeTagName(tag)) { // Matching name
            if (it.current !is MContext.Value) return@collect false
            result = it.current.get(it)
            STOP
        }
        return result
    }

    /**
     * Search for the first [MContext] corresponding to [tag] or null.
     *
     * Example:
     * ```kotlin
     * val context = Ktm.ctx.make { "hello" by make { "world" by "Happy world :)" } }
     * val inWorld = NodeContext(context).searchValue("hello")
     * ```
     */
    fun search(tag: String): MContext? {
        var result: MContext? = null
        collect(tokenizeTagName(tag)) { // Matching name
            result = it.current
            STOP
        }
        return result
    }

    /**
     * Iterates over [tags] which should be a list of all the tag components (use [tokenizeTagName]).
     * Given "my.special.tag", It first searches for the "my" context, then "special" context inside it, then "tag".
     *
     * When the context is found it publishes it through the [onNew] lambda.
     * Sometimes, multiple contexts can be found with the same tag.
     * In this case, The [onNew] lambda is called multiple times.
     *
     * Renderer implementation can stop the search if they return [STOP] (true) in [onNew]
     */
    fun collect(
        tags: Array<out String>,
        onNew: (NodeContext) -> Boolean,
    ): Boolean = when (tags.size) {
        0 -> {
            val follow = follow(current)
            if (follow == this) onNew(this)
            else onNew(NodeContext(follow, parent))
        }
        // No need to iterate on a single element.
        1 -> {
            val node = nodeOf(tags[0], true)
            if (node == null) CONTINUE else onNew(node)
        }
        // Try to collect recursively given the tag tokens
        else -> collectMultiContext(TagIterator(tags), onNew)
    }

    /**
     * Collects multiple contexts based on the provided tags and invokes a callback function on each matching context.
     *
     * @param tags The tags to search for.
     * @param onNew The callback function to be invoked on each matching context. Return true to stop the search.
     * @return true if the search was stopped by the callback function, false otherwise.
     */
    private fun collectMultiContext(
        tags: TagIterator,
        onNew: (NodeContext) -> Boolean,
    ): Boolean {
        val tag = tags.next()
        // try to find the "tag.name" instead of "tag" then "name" inside.
        if (nodeOf(tags.concatenated, tags.idx == 0)?.let(onNew) == STOP) return STOP

        // Now we search recursively
        val node = nodeOf(tag, tags.idx == 0) ?: return CONTINUE
        // End of the branch we found a matching tag!
        if (!tags.hasNext()) return onNew(node)

        // We search for the context in the branch
        when (node.current) {
            is MContext.Map -> return node.collectMultiContext(tags, onNew)
            is MContext.List -> {
                for (context in node.current.iterator(node)) {
                    if (NodeContext(follow(context), node).collectMultiContext(tags, onNew)) return STOP
                    tags.previous()
                }
            }

            else -> return CONTINUE
        }
        return CONTINUE
    }


    /**
     * Creates a new [NodeContext] based on the provided [tag] and [checkOnParent] flag.
     *
     * @param tag The name to be used for creating the node context.
     * @param checkOnParent Flag indicating whether to check on the parent for the node context if it is not found in the current context.
     * @return The created node context or null if not found.
     */
    private fun nodeOf(tag: String, checkOnParent: Boolean): NodeContext? {
        val result = (current as? MContext.Map)?.get(this, tag)
        return if (result != null) NodeContext(follow(result), this)
        else if (checkOnParent) parent?.nodeOf(tag, true) else null
    }

    /**
     * Follows the [source] context if the [current] context is a [MContext.Delegate].
     *
     * @param source The source MContext.
     * @return The followed MContext.
     */
    @Suppress("NOTHING_TO_INLINE")
    private fun follow(source: MContext): MContext =
        if (source !is MContext.Delegate) source
        else follow(source.get(this))

    /**
     * Iterator going through tokens of a tag. It can go backward.
     */
    private class TagIterator(val tagTokens: Array<out String>) : Iterator<String> {
        var idx = -1
        override fun hasNext(): Boolean = idx + 1 < tagTokens.size
        override fun next(): String = tagTokens[++idx]
        inline fun previous() = idx--

        val concatenated
            get() = if (idx == tagTokens.size - 1) tagTokens[idx]
            else buildString {
                for (i in idx..<tagTokens.size) {
                    append(tagTokens[i])
                    append('.')
                }
                deleteAt(length - 1)
            }
    }

    override fun toString(): String = if (parent == null) "Node($current)"
    else "Node($current, parent=$parent)"
}
