package net.orandja.ktm.ksp.visitor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*

/**
 * An abstract class that logs every visitor method.
 *
 * Given a [KSNode], use [KSNode.accept] with the child class in parameter to visit the node.
 *
 * @param In the type of input data to be passed to all visitor methods
 * @param Out the type of output data returned by all visitor methods
 * @property logger the logger instance used for logging
 * @property default the default value to be returned by all visitor methods
 *
 * @see KSVisitor
 */
abstract class AbstractVisitor<In, Out>(
    protected val logger: KSPLogger,
    private val default: Out,
) : KSVisitor<In, Out> {
    override fun visitAnnotated(annotated: KSAnnotated, data: In): Out {
        logger.logging("visitAnnotated($annotated)", annotated)
        return default
    }

    override fun visitAnnotation(annotation: KSAnnotation, data: In): Out {
        logger.logging("visitAnnotation($annotation)", annotation)
        return default
    }

    override fun visitCallableReference(reference: KSCallableReference, data: In): Out {
        logger.logging("visitCallableReference($reference)", reference)
        return default
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: In): Out {
        logger.logging("visitClassDeclaration($classDeclaration)", classDeclaration)
        return default
    }

    override fun visitClassifierReference(reference: KSClassifierReference, data: In): Out {
        logger.logging("visitClassifierReference($reference)", reference)
        return default
    }

    override fun visitDeclaration(declaration: KSDeclaration, data: In): Out {
        logger.logging("visitDeclaration($declaration)", declaration)
        return default
    }

    override fun visitDeclarationContainer(declarationContainer: KSDeclarationContainer, data: In): Out {
        logger.logging("visitDeclarationContainer($declarationContainer)", declarationContainer)
        return default
    }

    override fun visitDefNonNullReference(reference: KSDefNonNullReference, data: In): Out {
        logger.logging("visitDefNonNullReference($reference)", reference)
        return default
    }

    override fun visitDynamicReference(reference: KSDynamicReference, data: In): Out {
        logger.logging("visitDynamicReference($reference)", reference)
        return default
    }

    override fun visitFile(file: KSFile, data: In): Out {
        logger.logging("visitFile($file)", file)
        return default
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: In): Out {
        logger.logging("visitFunctionDeclaration($function)", function)
        return default
    }

    override fun visitModifierListOwner(modifierListOwner: KSModifierListOwner, data: In): Out {
        logger.logging("visitModifierListOwner($modifierListOwner)", modifierListOwner)
        return default
    }

    override fun visitNode(node: KSNode, data: In): Out {
        logger.logging("visitNode($node)", node)
        return default
    }

    override fun visitParenthesizedReference(reference: KSParenthesizedReference, data: In): Out {
        logger.logging("visitParenthesizedReference($reference)", reference)
        return default
    }

    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: In): Out {
        logger.logging("visitPropertyAccessor($accessor)", accessor)
        return default
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: In): Out {
        logger.logging("visitPropertyDeclaration($property)", property)
        return default
    }

    override fun visitPropertyGetter(getter: KSPropertyGetter, data: In): Out {
        logger.logging("visitPropertyGetter($getter)", getter)
        return default
    }

    override fun visitPropertySetter(setter: KSPropertySetter, data: In): Out {
        logger.logging("visitPropertySetter($setter)", setter)
        return default
    }

    override fun visitReferenceElement(element: KSReferenceElement, data: In): Out {
        logger.logging("visitReferenceElement($element)", element)
        return default
    }

    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: In): Out {
        logger.logging("visitTypeAlias($typeAlias)", typeAlias)
        return default
    }

    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: In): Out {
        logger.logging("visitTypeArgument($typeArgument)", typeArgument)
        return default
    }

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: In): Out {
        logger.logging("visitTypeParameter($typeParameter)", typeParameter)
        return default
    }

    override fun visitTypeReference(typeReference: KSTypeReference, data: In): Out {
        logger.logging("visitTypeReference($typeReference)", typeReference)
        return default
    }

    override fun visitValueArgument(valueArgument: KSValueArgument, data: In): Out {
        logger.logging("visitValueArgument($valueArgument)", valueArgument)
        return default
    }

    override fun visitValueParameter(valueParameter: KSValueParameter, data: In): Out {
        logger.logging("visitValueParameter($valueParameter)", valueParameter)
        return default
    }
}