package net.orandja.ktm

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.builder.context.ContextValue


/**
 * Converts an enum element to a Mustache context.
 *
 * Usage :
 * ```kotlin
 * enum class Kind { A, B }
 *
 * val context = Kind.A.EnumMustacheContext()
 * "{{#A}} A {{/A}{{#B}} B {{/B}}".render(context)) // A
 * ```
 *
 * @return the corresponding Mustache context for the enum element. Possible values are:
 * - [MContext.Yes] if the queried tag is the name of the receiver
 * - [MContext.No] if the queried element is in the enum list of type [T]
 * - null if the queried element is not from this Enum[T]
 *
 * @param T the type of the enum element.
 * @receiver the enum element to be converted to a Mustache context.
 */
inline fun <reified T : Enum<T>> T.EnumMustacheContext(): MContext = object : MContext.Map {
    private val values = enumValues<T>()

    override fun get(node: MContext.Node, tag: CharSequence): MContext? = when (tag) {
        name -> MContext.Yes
        in values.map { it.name } -> MContext.No
        "ordinal" -> ContextValue(ordinal.toString())
        "name" -> ContextValue(name)
        else -> null
    }

    override fun toString(): String = values.joinToString(
        prefix = "EnumContext<${T::class.simpleName}>[",
        separator = ", ",
        postfix = "]"
    ) { it.name }
}

/**
 * Quickly create an adapter for any enum class.
 *
 * ```kotlin
 * enum class Kind { A, B }
 * val adapter = Kind.EnumKtmAdapter()
 * val context = adapter.toMustacheContext(Kind.A)
 * "{{#A}} A {{/A}{{#B}} B {{/B}}".render(context) // A
 * ```
 *
 * @see EnumMustacheContext
 */
inline fun <reified T : Enum<T>> EnumKtmAdapter() = object : KtmAdapter<T> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: T): MContext = value.EnumMustacheContext()
    override fun toString(): String = "EnumKtmAdapter<${T::class.simpleName}>"
}
