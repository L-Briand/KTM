package net.orandja.ktm.test.spec

import kotlinx.serialization.json.*
import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.NodeContext

fun jsonToContext(json: JsonElement?): MContext = when (json) {
    null -> MContext.No
    JsonNull -> MContext.No
    is JsonObject -> toObject(json)
    is JsonArray -> toArray(json)
    is JsonPrimitive -> toPrimitive(json)
}

fun toObject(json: JsonObject): MContext = object : MContext.Map {
    override fun get(node: NodeContext, tag: String): MContext? = json[tag]?.let { jsonToContext(it) }

    override fun toString(): String = json.toString()
}

fun toArray(json: JsonArray): MContext = object : MContext.List {
    override fun iterator(node: NodeContext): Iterator<MContext> = object : Iterator<MContext> {
        val base = json.iterator()
        override fun hasNext(): Boolean = base.hasNext()
        override fun next(): MContext = jsonToContext(base.next())
    }

    override fun toString(): String = json.toString()
}

fun toPrimitive(json: JsonPrimitive): MContext {
    when (json.booleanOrNull) {
        true -> return MContext.Yes
        false -> return MContext.No
        null -> Unit
    }
    return Ktm.ctx.value(json.content)
}
