package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.NodeContext
import net.orandja.ktm.composition.builder.context.*

open class ContextFactory {

    fun make(configuration: ContextMapBuilder.() -> Unit): MContext =
        ContextMapBuilder().apply(configuration).build()

    fun makeList(configuration: ContextListBuilder.() -> Unit): MContext =
        ContextListBuilder().apply(configuration).build()

    val no = MContext.No
    val yes = MContext.Yes

    fun string(value: CharSequence?) = if (value == null) no else ctxValue(value)

    fun list(vararg items: String) = list(items.toList())
    fun list(items: Iterable<String?>?) = if (items == null) no else {
        val contexts = items.map(::string)
        if (contexts.isEmpty()) yes else ctxList(contexts)
    }

    fun map(vararg group: Pair<String, String>) = map(group.toMap())
    fun map(group: Map<String, String?>?) = if (group == null) no else {
        val contexts = group.mapValues { string(it.value) }
        if (contexts.isEmpty()) yes else ctxMap(contexts)
    }

    fun merge(vararg contexts: MContext.Map) = MultiMapContext(contexts.toList())
    fun merge(contexts: List<MContext.Map>) = MultiMapContext(contexts.toList())

    // Create contexts with MContexts directly

    fun ctxValue(value: CharSequence) = ContextValue(value)
    fun ctxMap(context: Map<String, MContext>) = ContextMap(context)
    fun ctxMap(vararg context: Pair<String, MContext>) = ContextMap(mapOf(*context))
    fun ctxList(vararg context: MContext) = ContextList(context.toList())
    fun ctxList(contexts: Iterable<MContext>) = ContextList(contexts)

    // Create delegated contexts

    fun delegate(delegate: NodeContext.() -> MContext) = Delegated(delegate)
    fun delegateString(delegate: NodeContext.() -> CharSequence) = DelegatedString(delegate)
    fun delegateMap(delegate: NodeContext.(tag: String) -> MContext) = DelegatedMap(delegate)
    fun delegateList(delegate: NodeContext.() -> Iterator<MContext>) = DelegatedList(delegate)
}