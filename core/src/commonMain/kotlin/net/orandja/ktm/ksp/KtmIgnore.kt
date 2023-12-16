package net.orandja.ktm.ksp

/**
 * Ignore a field in a class annotated by [KtmContext]
 *
 * Usage:
 * ```kotlin
 * @KtmContext
 * data class Foo(
 *     val foo: String,
 *     @KtmIgnore
 *     val bar: String,
 * )
 *
 * val context = Foo("foo", "bar").toMustacheContext()
 * val template = "'{{ foo }}' and '{{ bar }}'"
 * template.render(context) // 'foo' and ''
 * ```
 *
 * @see KtmContext
 * @see KtmDynamic
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class KtmIgnore