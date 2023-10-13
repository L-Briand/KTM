package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.composition.builder.context.*

open class ContextFactory {

    open fun make(configuration: ContextMapBuilder.() -> Unit): MContext =
        ContextMapBuilder().apply(configuration).build()

    open fun makeList(configuration: ContextListBuilder.() -> Unit): MContext =
        ContextListBuilder().apply(configuration).build()

    val no = MContext.No
    val yes = MContext.Yes

    fun string(value: CharSequence) = ContextValue(value)
    fun stringDelegate(delegate: NodeContext.() -> CharSequence) = MContext.Value { it.delegate() }

    fun list(vararg items: String) = MContext.List { StringToValueContextIterator(items.iterator()) }
    fun list(items: Iterable<String>) = MContext.List { StringToValueContextIterator(items) }
    fun listDelegate(delegate: () -> Iterable<String>) =
        MContext.List { StringToValueContextIterator(delegate()) }

    fun map(vararg pairs: Pair<String, String>) = map(pairs.toMap())
    fun map(map: Map<String, String>) =
        MContext.Map { _, tag -> if (map.containsKey(tag)) string(map[tag]!!) else null }

    fun mapDelegate(delegate: (tag: String) -> String) = MContext.Map { _, tag -> string(delegate(tag)) }

    fun merge(vararg contexts: MContext.Map) = MultiMapContext(contexts.toList())
    fun merge(contexts: List<MContext.Map>) = MultiMapContext(contexts.toList())

    // Create contexts with MContexts directly


    fun ctxMap(context: Map<String, MContext>) = ContextMap(context)
    fun ctxMap(vararg context: Pair<String, MContext>) = ContextMap(mapOf(*context))

    fun ctxList(vararg context: MContext) = ContextList(context.toList())
    fun ctxList(contexts: Iterable<MContext>) = ContextList(contexts)
}