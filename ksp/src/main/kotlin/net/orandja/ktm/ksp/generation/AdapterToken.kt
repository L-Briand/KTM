package net.orandja.ktm.ksp.generation

import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.contextOf
import org.intellij.lang.annotations.Language

sealed class AdapterToken(val kind: Kind) {

    enum class Kind {
        CLASS, CLASS_FIELD, ENUM_CLASS;

        companion object {
            internal val Adapter = KtmMapAdapter<AdapterToken> {
                "kind" by contextOf<Kind>(it.kind)
            }

            @Language("mustache")
            val Template = "{{#kind.CLASS}}{{>_class}}{{/kind.CLASS}}" +
                    "{{#kind.ENUM_CLASS}}{{>_enum_class}}{{/kind.ENUM_CLASS}}"

        }
    }

    data class EnumClass(
        val packageName: String,
        val simpleName: String,
        val sanitizedSimpleName: String,
    ) : AdapterToken(Kind.ENUM_CLASS) {
        companion object {
            val Adapter = KtmMapAdapter<EnumClass> {
                configureLike<AdapterToken>(it)
                "package_name" by it.packageName
                "simple_name" by it.simpleName
                "sanitized_simple_name" by it.sanitizedSimpleName
            }

            @Language("mustache")
            val Template = """
                package {{ package_name }}
                
                import net.orandja.ktm.adapters.KtmAdapter
                import net.orandja.ktm.base.MContext
                import net.orandja.ktm.EnumMustacheContext
                
                
                data object {{ sanitized_simple_name }}KtmAdapter : KtmAdapter<{{ simple_name }}> {
                    override fun toMustacheContext(
                        adapters: KtmAdapter.Provider,
                        value: {{ simple_name }}
                    ): MContext = value.EnumMustacheContext(adapters)
                }
            """.trimIndent()
        }
    }

    data class NormalClass(
        val packageName: String,
        val simpleName: String,
        val sanitizedSimpleName: String,
        val fields: List<Field>
    ) : AdapterToken(Kind.CLASS) {
        companion object {
            val Adapter = KtmMapAdapter<NormalClass> {
                configureLike<AdapterToken>(it)
                "package_name" by it.packageName
                "simple_name" by it.simpleName
                "sanitized_simple_name" by it.sanitizedSimpleName
                "fields" by contextOf(it.fields)
            }

            @Language("mustache")
            val Template = """
                package {{ package_name }}
                
                import net.orandja.ktm.Ktm
                import net.orandja.ktm.adapters.KtmAdapter
                import net.orandja.ktm.adapters.KtmMapAdapter
                import net.orandja.ktm.composition.builder.ContextMapBuilder
                import net.orandja.ktm.contextOf
                import net.orandja.ktm.contextOfCallable
                import net.orandja.ktm.contextOfNodeCallable
                import net.orandja.ktm.base.MContext
                
                
                data object {{ sanitized_simple_name }}KtmAdapter : KtmMapAdapter<{{ simple_name }}> {
                    override fun toString(): String = "{{ simple_name }}KtmAdapter"
                    override fun ContextMapBuilder.configure(value: {{ simple_name }}) {
                        {{# fields }}
                        {{> _class_field }}
                        {{/fields}}
                    }
                }
            """.trimIndent()
        }
    }

    data class Field(
        val name: String,
        val fieldName: String,
        val isCallable: Boolean,
        val isDynamic: Boolean,
        val nodeAsParameter: Boolean,
        val nodeAsReceiver: Boolean,
        val isFunction: Boolean,
    ) : AdapterToken(Kind.CLASS_FIELD) {
        companion object {
            val Adapter = KtmMapAdapter<Field> {
                configureLike<AdapterToken>(it)
                "name" by it.name
                "fieldName" by it.fieldName
                "isCallable" by it.isCallable
                "isDynamic" by if (it.isCallable) false else it.isDynamic
                "isFunction" by it.isFunction
                "node" by (it.nodeAsParameter || it.nodeAsReceiver)
                "nodeReceiver" by (it.isCallable && it.isFunction && it.nodeAsReceiver)
            }

            @Language("mustache")
            private val context = "contextOf{{#isCallable}}{{#node}}Node{{/node}}Callable{{/isCallable}}" +
                    "(value{{#isFunction}}::{{/isFunction}}{{^isFunction}}.{{/isFunction}}{{fieldName}})"

            @Language("mustache")
            private val nodeReceiverEdgeCase =
                "{{#nodeReceiver}}with(value) { Ktm.ctx.delegate { contextOf({{fieldName}}()) } }{{/nodeReceiver}}" +
                        "{{^nodeReceiver}}$context{{/nodeReceiver}}"

            @Language("mustache")
            private val dynamic =
                "{{#isDynamic}}Ktm.ctx.delegate { {{/isDynamic}}$nodeReceiverEdgeCase{{#isDynamic}} }{{/isDynamic}}"

            @Language("mustache")
            val Template = "\"{{name}}\" by $dynamic\n"
        }
    }
}
