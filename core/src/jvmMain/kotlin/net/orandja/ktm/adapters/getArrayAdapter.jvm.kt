package net.orandja.ktm.adapters

import net.orandja.ktm.adapters.defaults.ArrayKtmAdapter
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
private fun KType.asKClass() = when (val classifier = classifier) {
    is KClass<*> -> classifier
    else -> error("Only KClass supported as type classifier, got $classifier")
} as KClass<Any>

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun getArrayFactory(type: TypeKey): KtmAdapter.Factory? =
    if(type.type.asKClass().qualifiedName == "kotlin.Array") ArrayKtmAdapter.Factory else null