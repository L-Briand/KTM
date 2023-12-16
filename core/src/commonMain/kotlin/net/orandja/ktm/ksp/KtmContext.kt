package net.orandja.ktm.ksp

import net.orandja.ktm.base.MContext

/**
 * Define a class that can be transformed to a [MContext]
 *
 * The ktm ksp plugin will create an extension function named '`toMustacheContext()`' on the class.
 * Use it to transform the instance into a MContext.
 *
 * Usage:
 * ```kotlin
 * @KtmContext
 * data class User(val name: String, val age: Int?)
 *
 * AutoKtmAdaptersModule.setDefault()
 *
 * val user: KtmContext = User("Jon", 33).toMustacheContext()
 * "Hello {{ name }}".render(context)) // Hello Jon
 * ```
 *
 * @see KtmName
 * @see KtmDynamic
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KtmContext
