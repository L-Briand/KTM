package net.orandja.ktm.ksp.generation

import net.orandja.ktm.Ktm
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.contextOf
import org.intellij.lang.annotations.Language

sealed class AdapterToken(val kind: Kind) {

    companion object : KtmAdapter<AdapterToken> {
        override fun toMustacheContext(adapters: KtmAdapter.Provider, value: AdapterToken): MContext {
            return Ktm.ctx.make(adapters) {
                "kind" by delegate { contextOf<Kind>(value.kind) }
            }
        }
    }

    enum class Kind {
        CLASS, CLASS_FIELD, ENUM_CLASS;

        companion object {
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
            val Adapter = KtmAdapter<EnumClass> { adapters, value ->
                Ktm.ctx.make(adapters) {
                    like<AdapterToken>(value)
                    "package_name" by value.packageName
                    "simple_name" by value.simpleName
                    "sanitized_simple_name" by value.sanitizedSimpleName
                }
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
        val classTypeParameterCount: Int,
        val fields: List<Field>
    ) : AdapterToken(Kind.CLASS) {
        companion object {
            val Adapter =
                KtmAdapter<NormalClass> { adapters, value ->
                    Ktm.ctx.make(adapters) {
                        like<AdapterToken>(value)
                        "package_name" by value.packageName
                        "simple_name" by value.simpleName
                        "sanitized_simple_name" by value.sanitizedSimpleName
                        "fields" by contextOf(value.fields)
                        if (value.classTypeParameterCount == 0) "type_parameter" by no
                        else "type_parameter" by (0..<value.classTypeParameterCount).joinToString(", ", "<", ">")
                        { "*" }
                    }
                }

            @Language("mustache")
            val Template = """
                package {{ package_name }}
                
                import net.orandja.ktm.Ktm
                import net.orandja.ktm.adapters.KtmAdapter
                import net.orandja.ktm.composition.builder.ContextMapBuilder
                import net.orandja.ktm.contextOf
                import net.orandja.ktm.contextOfCallable
                import net.orandja.ktm.contextOfNodeCallable
                import net.orandja.ktm.base.MContext
                
                
                data object {{ sanitized_simple_name }}KtmAdapter : KtmAdapter<{{ simple_name }}{{{ type_parameter }}}> {
                    override fun toString(): String = "{{ simple_name }}KtmAdapter"
                    
                    override fun toMustacheContext(
                        adapters: KtmAdapter.Provider, 
                        value: {{ simple_name }}{{{ type_parameter }}}
                    ): MContext = MContext.Map { _, tag ->
                        when (tag) {
                            {{# fields }}
                            {{> _class_field }}
                            {{/fields}}
                            else -> null
                        }
                    }
                }
            """.trimIndent()
        }
    }

    data class Field(
        val name: String,
        val fieldName: String,
        val isCallable: Boolean,
        val nodeAsParameter: Boolean,
        val nodeAsReceiver: Boolean,
        val isFunction: Boolean,
    ) : AdapterToken(Kind.CLASS_FIELD) {
        companion object {
            val Adapter = KtmAdapter<Field> { adapters, value ->
                Ktm.ctx.make(adapters) {
                    like<AdapterToken>(value)
                    "name" by value.name
                    "fieldName" by value.fieldName
                    "isCallable" by value.isCallable
                    "isFunction" by value.isFunction
                    "node" by (value.nodeAsParameter || value.nodeAsReceiver)
                    "nodeReceiver" by (value.isCallable && value.isFunction && value.nodeAsReceiver)
                }
            }

            @Language("mustache")
            private val context = "adapters.contextOf{{#isCallable}}{{#node}}Node{{/node}}Callable{{/isCallable}}" +
                    "(value{{#isFunction}}::{{/isFunction}}{{^isFunction}}.{{/isFunction}}{{fieldName}})"

            @Language("mustache")
            private val nodeReceiverEdgeCase =
                "{{#nodeReceiver}}with(value) { Ktm.ctx.delegate { adapters.contextOf({{fieldName}}()) } }{{/nodeReceiver}}" +
                        "{{^nodeReceiver}}$context{{/nodeReceiver}}"

            @Language("mustache")
            val Template = "\"{{name}}\" -> $nodeReceiverEdgeCase\n"
        }
    }
}
