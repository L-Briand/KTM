package net.orandja.ktm.ksp.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import net.orandja.ktm.ksp.KtmLogger

/**
 * In an inner class get compound SimpleName.
 *
 * ```kotlin
 * class A { class B { class C } }
 *
 * // classDeclaration is C
 * val className = classDeclaration.simpleName.asString() // C
 * val simpleName = classDeclaration.parentDeclaration!!
 *     .accept(ClassNameVisitor(...), className)
 * simpleName == "A.B.C"
 * ```
 */
class ClassNameVisitor(logger: KtmLogger) :
    AbstractVisitor<String, String?>(logger.copy("ClassNameVisitor"), null) {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: String): String {
        super.visitClassDeclaration(classDeclaration, data)

        val className = "${classDeclaration.simpleName.asString()}.$data"
        return classDeclaration.parentDeclaration?.accept(this, className) ?: className
    }
}