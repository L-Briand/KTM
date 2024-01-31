package net.orandja.ktm.ksp

/**
 * This annotation can be applied to functions and properties to override its name during contextual transformation.
 *
 * Usage:
 * ```kotlin
 * @KtmContext
 * data class UserInfo(
 *     val firstName: String,
 *     val lastName: String,
 * ) {
 *     @KtmName("name")
 *     fun fullName() = "$firstName $lastName"
 * }
 * val context = UserInfo("John", "Doe").toMustacheContext()
 * "Hello {{ name }}!".render(context) // Hello John Doe!
 * ```
 * @param name The name of the element (empty to keep field name)
 *
 * @see KtmContext
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class KtmName(
    val name: String,
)