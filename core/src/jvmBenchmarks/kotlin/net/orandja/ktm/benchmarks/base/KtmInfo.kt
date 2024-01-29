package net.orandja.ktm.benchmarks.base

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.contextOf
import net.orandja.ktm.toMustacheDocument

object KtmInfo {

    private val classLoader get() = KtmInfo::class.java.classLoader

    // This is what is generally build with @KtmContext annotation
    val adapters = Ktm.adapters.make {
        +KtmMapAdapter<_1_Context> {
            "first_name" by it.first_name
            "last_name" by it.last_name
        }
        +KtmMapAdapter<_2_Context> {
            "name" by it.name
            "value" by it.value
            "is_taxed" by it.is_taxed
            "rate" by it.rate

        }
        +KtmMapAdapter<_3_Context> {
            "users" by it.users
        }
        +KtmMapAdapter<_3_Context.Item> {
            "name" by it.name
            "summary" by it.summary
        }
    }

    val _1_raw_template = classLoader.readResource("_1_template.mustache")
    val _1_template = _1_raw_template.toMustacheDocument()
    val _1_context = adapters.contextOf(classLoader.readJsonResource<_1_Context>("_1_context.json"))

    val _2_raw_template = classLoader.readResource("_2_template.mustache")
    val _2_template = _2_raw_template.toMustacheDocument()
    val _2_context = adapters.contextOf(classLoader.readJsonResource<_2_Context>("_2_context.json"))

    val _3_raw_template = classLoader.readResource("_3_template.mustache")
    val _3_template = _3_raw_template.toMustacheDocument()
    val _3_context_x1 = adapters.contextOf(classLoader.readJsonResource<_3_Context>("_3_context.1.json"))
    val _3_context_x10 = adapters.contextOf(classLoader.readJsonResource<_3_Context>("_3_context.10.json"))
    val _3_context_x50 = adapters.contextOf(classLoader.readJsonResource<_3_Context>("_3_context.50.json"))
}