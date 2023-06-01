package net.orandja.ktm.test

import kotlinx.serialization.json.*
import net.orandja.ktm.Mustache
import net.orandja.ktm.base.MContext

fun jsonToContext(json: JsonElement?): MContext = when (json) {
    null -> MContext.No
    JsonNull -> MContext.No
    is JsonObject -> toObject(json)
    is JsonArray -> toArray(json)
    is JsonPrimitive -> toPrimitive(json)
}

fun toObject(json: JsonObject): MContext =
    Mustache.context.builder.groupDelegate { tag ->
        json[tag]?.let { jsonToContext(it) }
    }

fun toArray(json: JsonArray): MContext =
    Mustache.context.builder.listDelegate {
        json.map { jsonToContext(it) }.iterator()
    }

fun toPrimitive(json: JsonPrimitive): MContext {
    when (json.booleanOrNull) {
        true -> return MContext.Yes
        false -> return MContext.No
        null -> Unit
    }
    return Mustache.context.builder.value(json.content)
}
