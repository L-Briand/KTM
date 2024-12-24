package net.orandja.ktm.adapters

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Ktm does not use [KType] as the main object to determine the type of a class, it uses [TypeKey] instead.
 *
 * This is shortcut for creating a [TypeKey] (like [typeOf])
 */
inline fun <reified T> typeKey() = TypeKey(typeOf<T>())

/**
 * A comparable class (equals/hashcode) for [kotlin.reflect.KType].
 * Comparing [KType] is otherwise quite slow.
 *
 * @property type The associated Kotlin type.
 * @property arguments A list of [TypeKey] instances representing the generic type arguments of the main type,
 * or `null` if the type is a star projection.
 */
class TypeKey internal constructor(
    val type: KType,
    val arguments: List<TypeKey?>, // `null == star` projection. Variance from KTypeProjection is not useful
) {

    constructor(type: KType) : this(type, List(type.arguments.size) { type.arguments[it].type?.let(::TypeKey) })

    fun noArgs() = TypeKey(type, emptyList())

    override fun toString(): String = buildString {
        val type = type.classifier.toString()
            .replace(" (Kotlin reflection is not available)", "") // Yeah, sure I already know
        append(type)
        if (arguments.isNotEmpty()) {
            append(arguments.joinToString(", ", "<", ">") { it?.toString() ?: "*" })
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TypeKey

        if (type.classifier != other.type.classifier) return false
        if (arguments != other.arguments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.classifier.hashCode()
        result = 31 * result + arguments.hashCode()
        return result
    }
}