package net.orandja.ktm.base

abstract class Node<T> internal constructor(
    val current: T,
    val parent: Node<T>? = null,
) {

    /**
     * Try to resolve a [Node] corresponding to the sequence represented by [reference].
     * Either in itself or in its parent.
     *
     * Examples are better than words, so:
     *
     * With a group like this `foo@{ a: bar@{ b: 1, c: 2 }, c: 3, d: 4 }`:
     *
     * - In @foo and @bar with `keys = [a, b]` it returns 1
     * - In @foo and @bar with `keys = [a]` it returns @bar
     *
     *
     * - In @foo with `keys = [b]` it will return null
     * - In @bar with `keys = [b]` it will return 1
     *
     *
     * - In @foo with `keys = [c]` it will return 3
     * - In @bar with `keys = [c]` it will return 2
     *
     *
     * - In @foo and @bar with `keys = [d]` it will return 4
     */
    fun resolve(vararg reference: CharSequence): Node<T>? = resolve(reference.iterator(), true)
    fun resolve(reference: Iterable<CharSequence>): Node<T>? = resolve(reference.iterator(), true)
    fun resolve(reference: Sequence<CharSequence>): Node<T>? = resolve(reference.iterator(), true)

    private fun resolve(keys: Iterator<CharSequence>, checkOnParent: Boolean): Node<T>? {
        if (!keys.hasNext()) return this
        return resolveDown(keys, keys.next(), checkOnParent)
    }

    private fun resolveDown(keys: Iterator<CharSequence>, key: CharSequence, checkOnParent: Boolean): Node<T>? {
        // We search for a matching item within the current Node
        val item = resolveElement(key)
        // We found an item matching the key. We only go deeper from now on.
        if (item != null) return createNode(item).resolve(keys, false)
        // We didn't find a matching key. Maybe the parent node has it.
        if (checkOnParent) return parent?.resolveDown(keys, key, true)
        return null
    }

    abstract fun resolveElement(key: CharSequence): T?
    abstract fun createNode(element: T): Node<T>
}