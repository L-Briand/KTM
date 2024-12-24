package net.orandja.ktm.base

/**
 * The `TagRenderVisitor` class is a singleton object that implements the [MContext.Visitor] interface.
 * It is responsible for rendering simple [MContext.Value] elements in a Mustache template.
 *
 * If the visited [MContext] is a value, the value is rendered.
 * If the visited [MContext] is a delegated MContext, the call is forwarded to it.
 *
 * @see MContext.Visitor
 * @see MContext.Node.findValue
 */
object TagRenderVisitor : MContext.Visitor<MContext.Node, CharSequence?> {
    override fun no(data: MContext.Node, no: MContext.No): CharSequence? = null
    override fun yes(data: MContext.Node, yes: MContext.Yes): CharSequence? = null
    override fun map(data: MContext.Node, map: MContext.Map): CharSequence? = null
    override fun list(data: MContext.Node, list: MContext.List): CharSequence? = null
    override fun document(data: MContext.Node, document: MContext.Document): CharSequence? = null

    override fun value(data: MContext.Node, value: MContext.Value): CharSequence = value.get(data)
    override fun delegate(data: MContext.Node, delegate: MContext.Delegate): CharSequence? =
        delegate.get(data).accept(data, this)
}