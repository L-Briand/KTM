package net.orandja.ktm.adapters

import kotlin.reflect.KType

class TypeKey(val type: KType) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeKey) return false

        if (type.classifier != other.type.classifier) return false
        if (type.arguments != other.type.arguments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.classifier.hashCode()
        result = 31 * result + type.arguments.hashCode()
        return result
    }
}