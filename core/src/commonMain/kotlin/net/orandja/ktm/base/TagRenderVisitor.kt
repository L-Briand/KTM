package net.orandja.ktm.base

/**
 * The `TagRenderVisitor` class is a singleton object that implements the [MContext.Visitor] interface.
 * It is responsible for rendering the values of tags in a Mustache template.
 *
 * If the visited [MContext] is a value, the value is rendered.
 * If the visited [MContext] is a delegated MContext, the call is forwarded to it.
 *
 * Example usage of Visitor:
 * ```kotlin
 * val context = Ktm.ctx.make { "name" by "john" }
 * var node = NodeContext(current = context)
 * val nameContext: MContext = node.find("name")!!
 * node = NodeContext(current = nameContext, parent = node)
 * val content = nameContext.accept(node, TagRenderVisitor)
 * assertEquals("john", content)
 * ```
 *
 * @property default The default result to return when a specific method is not overridden.
 * @see MContext.Visitor
 */
object TagRenderVisitor : MContext.Visitor.Default<NodeContext, CharSequence?>(null) {
    override fun value(data: NodeContext, value: MContext.Value): CharSequence = value.get(data)
    override fun delegate(data: NodeContext, delegate: MContext.Delegate): CharSequence? =
        delegate.get(data).accept(data, this)
}