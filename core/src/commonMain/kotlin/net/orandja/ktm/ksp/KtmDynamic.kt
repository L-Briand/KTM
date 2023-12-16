package net.orandja.ktm.ksp

/**
 * This annotation can be used to indicate whether a field in a [KtmContext] dynamic or not.
 *
 * Example:
 * ```kotlin
 * @KtmContext
 * class Data(
 *     @KtmDynamic
 *     var foo: String,
 *     var bar: String,
 *     var baz: String
 * )
 *
 *
 *
 * val context = Data("a", "b", "c")
 * val template = "{{ foo }} - {{ bar }} - {{ baz }}"
 * template.render(context) // a - b - c
 * context.apply { a = "1"; b = "2"; c = "3" }
 * template.render(context) // 1 - b - c
 * ```
 *
 * @property dynamic Set to `true` if the element is considered dynamic, `false` otherwise.
 * @see KtmContext
 * @see KtmName
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class KtmDynamic
