package net.orandja.ktm.base

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
        fun get(node: NodeContext): CharSequence
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
        fun get(node: NodeContext, tag: String): MContext?
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
        fun iterator(node: NodeContext): Iterator<MContext>
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.list(context, this)
    }

    /**
     * Special Contextual element.
     * This is used to create contexts on the fly.
     * This element should return any other element.
     */
    fun interface Delegate : MContext {
        fun get(node: NodeContext): MContext
        override fun <In, Out> accept(context: In, visitor: Visitor<In, Out>) = visitor.delegate(context, this)
    }

    /**
     * By providing a [visitor], one can decide what to do.
     * Depending on the [MContext] kind, the corresponding [Visitor] method will be called.
     *
     * For an example, see [TagRenderVisitor] which is used in [NodeContext.findValue]
     */
    fun <In, Out> accept(context: In, visitor: Visitor<In, Out>): Out

    /**
     * Visitor interface for [MContext]
     */
    interface Visitor<in In, out Out> {
        fun no(data: In, value: No): Out
        fun yes(data: In, value: Yes): Out
        fun value(data: In, value: Value): Out
        fun map(data: In, map: Map): Out
        fun list(data: In, list: List): Out
        fun delegate(data: In, delegate: Delegate): Out

        /** Visitor class with [default] [Out] return type on each method */
        open class Default<in In, out Out>(val default: Out) : Visitor<In, Out> {
            override fun no(data: In, value: No): Out = default
            override fun yes(data: In, value: Yes): Out = default
            override fun value(data: In, value: Value): Out = default
            override fun map(data: In, map: Map): Out = default
            override fun list(data: In, list: List): Out = default
            override fun delegate(data: In, delegate: Delegate): Out = default
        }
    }
}
