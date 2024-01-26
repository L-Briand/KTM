package net.orandja.ktm.composition.builder

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.composition.builder.context.*

/**
 * Helper class for creating different types of contexts used in Mustache templating.
 */
@Suppress("NOTHING_TO_INLINE")
open class ContextFactory {

    inline fun make(
        adapters: KtmAdapter.Provider = Ktm.adapters,
        configuration: ContextMapBuilder.() -> Unit
    ): MContext = ContextMapBuilder(adapters).apply { configuration() }.build()

    inline fun makeList(
        adapters: KtmAdapter.Provider = Ktm.adapters,
        configuration: ContextListBuilder.() -> Unit
    ): MContext = ContextListBuilder(adapters).apply { configuration() }.build()

    val no = MContext.No
    val yes = MContext.Yes

    inline fun string(value: CharSequence?) = if (value == null) no else ctxValue(value)
    inline fun string(value: CharSequence) = ctxValue(value)

    inline fun bool(value: Boolean?) = if (value == true) yes else no
    inline fun bool(value: Boolean) = if (value) yes else no

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
    fun ctxMap(vararg context: Pair<String, MContext>) = ContextMap(mutableMapOf(*context))
    fun ctxMap(context: Map<String, MContext>) =
        ContextMap(if (context is MutableMap) context else context.toMutableMap())

    fun ctxList(vararg context: MContext) = ContextList(context.toList())
    fun ctxList(contexts: Iterable<MContext>) = ContextList(contexts)

    // Create delegated contexts

    fun delegate(delegate: NodeContext.() -> MContext?) = Delegated(delegate)
    fun delegateValue(delegate: NodeContext.() -> CharSequence) = DelegatedValue(delegate)
    fun delegateMap(delegate: NodeContext.(tag: String) -> MContext?) = DelegatedMap(delegate)
    fun delegateList(delegate: NodeContext.() -> Iterator<MContext>) = DelegatedList(delegate)
}