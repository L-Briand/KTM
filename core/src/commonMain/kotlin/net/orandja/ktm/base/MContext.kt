package net.orandja.ktm.base

import net.orandja.ktm.composition.tokenizeDelimitedString

/**
 * Contextual part a mustache document.
 * Combined with [MDocument], you can render a document.
 */
sealed interface MContext {
    /**
     * A contextual element used to render:
     * - A section that should not render.
     * - An inverted sections.
     * - Nothing in a tag.
     */
    data object No : MContext {
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.no(context, this)
    }

    /**
     * A contextual element used to render a section without tags.
     *
     * Example:
     * ```handlebars
     * {{# section }} section without tags inside {{/ section }}
     * ```
     */
    data object Yes : MContext {
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.yes(context, this)
    }

    /**
     * A contextual element used to render a single tag.
     *
     * Example:
     * ```handlebars
     * Hello {{ name }}
     * ```
     */
    fun interface Value : MContext {
        fun get(node: Node): CharSequence
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.value(context, this)
    }

    /**
     * A contextual element used to render a Section with a specific tags inside.
     *
     * Example:
     * ```handlebars
     * {{#greeting}} Hello {{ name }} {{/greeting}}
     * ```
     */
    fun interface Map : MContext {
        fun get(node: Node, tag: CharSequence): MContext?
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.map(context, this)
    }

    /**
     * A contextual element used to render a Section multiple times.
     *
     * Example
     * ```handlebars
     * <ul>
     *   {{# element }}
     *   <li> {{name}} </li>
     *   {{/element}}
     * </ul>
     * ```
     */
    fun interface List : MContext {
        fun iterator(node: Node): Iterator<MContext>
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.list(context, this)
    }

    /**
     * A contextual element used when rendering a partial element in a document.
     * ```kotlin
     * {{> partial }}
     * ```
     */
    fun interface Document : MContext {
        fun get(node: Node): MDocument
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.document(context, this)
    }

    /**
     * Special Contextual element.
     * This is used to create contexts on the fly.
     * This element should return any other element.
     */
    fun interface Delegate : MContext {
        fun get(node: Node): MContext
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.delegate(context, this)
    }


    /**
     * By providing a [visitor], one can decide what to do.
     * Depending on the [MContext] kind, the corresponding [Visitor] method will be called.
     *
     * For an example, see [TagRenderVisitor] which is used in [Node.findValue]
     */
    fun <In, Out> accept(context: In, visitor: Visitor<In, Out>): Out

    /**
     * Visitor interface for [MContext]
     */
    interface Visitor<in In, out Out> {
        fun no(data: In, no: No): Out
        fun yes(data: In, yes: Yes): Out
        fun value(data: In, value: Value): Out
        fun map(data: In, map: Map): Out
        fun list(data: In, list: List): Out
        fun document(data: In, document: Document): Out
        fun delegate(data: In, delegate: Delegate): Out
    }

    /** A [net.orandja.ktm.base.Node] that can [resolve][resolve] [MContext][MContext] elements. */
    class Node(current: MContext, parent: Node? = null) : net.orandja.ktm.base.Node<MContext>(current, parent) {

        fun findValue(tag: String): CharSequence? =
            resolve(tokenizeDelimitedString(tag))?.current?.accept(this, TagRenderVisitor)

        fun findNode(tag: String): MContext? = resolve(tokenizeDelimitedString(tag))?.current

        data class Input(val node: Node, val key: CharSequence)

        /** Resolve the style for the given [Input] */
        private object NodeToElement : Visitor<Input, MContext?> {

            override fun no(data: Input, no: No): MContext? = null
            override fun yes(data: Input, yes: Yes): MContext? = null
            override fun value(data: Input, value: Value): MContext? = null
            override fun document(data: Input, document: Document): MContext? = null

            override fun list(data: Input, list: List): MContext? {
                val index = data.key.toString().toIntOrNull() ?: return null
                return Iterable { list.iterator(data.node) }.elementAtOrNull(index)
            }

            override fun map(data: Input, map: Map): MContext? = map.get(data.node, data.key)
            override fun delegate(data: Input, delegate: Delegate): MContext? =
                delegate.get(data.node).accept(data, this)
        }

        override fun resolveElement(key: CharSequence): MContext? =
            current.accept(Input(this, key), NodeToElement)

        /** Create a new node with the given node as parent */
        private object ElementToNode : Visitor<Node, Node> {
            override fun no(data: Node, no: No): Node = Node(no, data)
            override fun yes(data: Node, yes: Yes): Node = Node(yes, data)
            override fun value(data: Node, value: Value): Node = Node(value, data)
            override fun map(data: Node, map: Map): Node = Node(map, data)
            override fun list(data: Node, list: List): Node = Node(list, data)
            override fun document(data: Node, document: Document): Node = Node(document, data)
            override fun delegate(data: Node, delegate: Delegate): Node = delegate.get(data).accept(data, this)
        }

        override fun createNode(element: MContext): Node = element.accept(this, ElementToNode)
    }
}
