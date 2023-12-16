package net.orandja.ktm.ksp.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import net.orandja.ktm.ksp.generation.AdapterToken


/**
 * Represents the result of the [TokenBuilderVisitor].
 *
 * @property baseNode The [KSNode] associated with the result.
 */
sealed class VisitorResult(val baseNode: KSNode? = null) {
    /**
     * Result used as default when nothing is found and not an error.
     */
    data object Empty : VisitorResult(null)

    /**
     * When the visitor cannot derive the code and find a suitable representation, it emits a failure.
     */
    data class Failure(val message: String, val node: KSNode?, val attached: List<Failure> = emptyList()) :
        VisitorResult(node)

    /** Returned by [TokenBuilderVisitor.visitClassifierReference] for caller to have the class reference name */
    data class Classifier(val name: String?, val node: KSNode) : VisitorResult(node)

    /** Returned by [TokenBuilderVisitor.visitCallableReference] when a property is callable */
    data class Callable(
        val isFunction: Boolean, val isNodeAsParameter: Boolean, val isNodeAsReceiver: Boolean, val node: KSNode
    ) : VisitorResult(node)

    /**
     * Result received by the processor when a normal class is found
     */
    data class ClassAdapterToken(
        val file: KSFile, val classDeclaration: KSClassDeclaration, val token: AdapterToken
    ) : VisitorResult(classDeclaration)

    /**
     * [TokenBuilderVisitor] returns this token when it parses a class.
     */
    data class FieldToken(val token: AdapterToken.Field, val node: KSNode?) : VisitorResult(node)
}