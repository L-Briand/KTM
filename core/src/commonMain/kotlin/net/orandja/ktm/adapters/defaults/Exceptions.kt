package net.orandja.ktm.adapters.defaults

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.TypeKey

/** Thrown when a factory is called with a [TypeKey.noArgs] as input. */
internal class ArgumentCountException(
    nameType: String,
    types: List<TypeKey?>,
) : IllegalArgumentException(
    "Tried to create ${KtmAdapter::class.simpleName} for '$nameType' with invalid number of type arguments (${types.count()}): ${
        if (types.isEmpty()) "" else types.joinToString(", ", "<", ">") { it?.toString() ?: "*" }
    }"
)

/** Thrown when a factory does not handle a star projected argument. */
class StarProjectionException(
    nameType: String,
    position: Int,
    types: List<TypeKey?>
) : IllegalArgumentException(
    "Tried to create ${KtmAdapter::class.simpleName} for '$nameType' with star projected type arguments at position ($position): ${
        if (types.isEmpty()) "" else types.joinToString(", ", "<", ">") { it?.toString() ?: "*" }
    }"
)