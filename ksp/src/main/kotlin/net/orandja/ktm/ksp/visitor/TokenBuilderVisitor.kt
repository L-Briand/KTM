package net.orandja.ktm.ksp.visitor

import com.google.devtools.ksp.isInternal
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.*
import net.orandja.either.Either
import net.orandja.either.Left
import net.orandja.either.Right
import net.orandja.either.requireLeft
import net.orandja.ktm.ksp.KtmLogger
import net.orandja.ktm.ksp.SymbolProcessorConfiguration.Companion.OPTION_ALLOW_INTERNAL_CLASS
import net.orandja.ktm.ksp.generation.AdapterToken.*
import net.orandja.ktm.ksp.visitor.VisitorResult.*


class TokenBuilderVisitor(private val ktmLogger: KtmLogger) :
    AbstractVisitor<TokenBuilderVisitor.Options, VisitorResult>(ktmLogger.copy("TokenBuilderVisitor"), Empty) {

    data class Options(
        val allowInternalClass: Boolean = false,
        val alreadyReadingClass: Boolean = false,
    )

    // PARSING CLASSES

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Options): VisitorResult {
        super.visitClassDeclaration(classDeclaration, data)

        // Return early if we found a class in the current reading class.
        if (data.alreadyReadingClass) return Empty

        val options = data.copy(alreadyReadingClass = true)
        // Assert class valid
        classDeclaration.assertVisible(options)?.let { return it }
        val file = classDeclaration.assertContainingFile().requireLeft { (it) -> return it }

        // Get information about class
        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()
        val simpleName = classDeclaration.parentDeclaration?.accept(ClassNameVisitor(ktmLogger), className) ?: className
        val sanitizedSimpleName = simpleName.replace('.', '_', false)

        if (classDeclaration.classKind == ClassKind.ENUM_CLASS) {
            val classToken = EnumClass(packageName, simpleName, sanitizedSimpleName)
            return ClassAdapterToken(file, classDeclaration, classToken)
        }

        // Search in all declared fields/functions/etc… for VisitorResult
        val visitorResults = classDeclaration.declarations.map { it.accept(this, options) }.toList()

        // Return error if any element is a failure
        val failures = visitorResults.mapNotNull { it as? Failure }
        if (failures.isNotEmpty()) return Failure("Several field invalid", classDeclaration, failures)

        // Transform results into ClassToken
        val classFieldTokens = visitorResults.mapNotNull { (it as? FieldToken)?.token }

        val classToken = NormalClass(
            packageName,
            simpleName,
            sanitizedSimpleName,
            classDeclaration.typeParameters.count(),
            classFieldTokens
        )
        return ClassAdapterToken(file, classDeclaration, classToken)
    }

    private fun KSClassDeclaration.assertContainingFile(): Either<KSFile, Failure> =
        if (containingFile == null) Right(Failure("Unable to find file of $this", this)) else Left(containingFile!!)

    private fun KSClassDeclaration.assertVisible(configuration: Options): Failure? {
        val isPublic = isPublic()
        val internalOk = isInternal() && configuration.allowInternalClass
        if (!isPublic && !internalOk) {
            return Failure(
                "@KtmContext should only be applied on public classes. " +
                        "To allow internal class set ksp configuration `$OPTION_ALLOW_INTERNAL_CLASS` to `true`",
                this,
            )
        }
        return null
    }

    // PARSING PROPERTY

    /** Transform a property to a [FieldToken] element for [visitClassDeclaration] to create [ClassAdapterToken] */
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Options): VisitorResult {
        super.visitPropertyDeclaration(property, data)

        if (!property.isPublic()) return Empty

        if (property.annotations.isKtmIgnore(data)) return Empty

        val name = property.simpleName.asString()
        val typeInformation = property.type.accept(this, data)

        // Name surcharge with @KtmName
        val ktmName = property.annotations.getKtmName(data)

        var isCallable = false
        var isNodeAsParameter = false
        var isNodeAsReceiver = false
        var isFunction = false

        when (typeInformation) {
            is Classifier -> {}
            is Callable -> {
                isCallable = true
                isNodeAsParameter = typeInformation.isNodeAsParameter
                isNodeAsReceiver = typeInformation.isNodeAsReceiver
                isFunction = typeInformation.isFunction
            }

            is Empty -> {
                logger.warn("Property cannot be converted", property)
                return Empty
            }

            else -> return Failure("Failed to parse type information.", property)
        }

        val token = Field(
            name = ktmName ?: name,
            fieldName = name,
            isCallable = isCallable,
            nodeAsParameter = isNodeAsParameter,
            nodeAsReceiver = isNodeAsReceiver,
            isFunction = isFunction,
        )

        return FieldToken(token, property)
    }

    // To get the type of property
    override fun visitTypeReference(typeReference: KSTypeReference, data: Options): VisitorResult {
        super.visitTypeReference(typeReference, data)

        val element = typeReference.element

        if (element != null) return element.accept(this, data)

        // When 'element' is 'null', it means the type is not specified (val value = 3)
        // There is no choice than to resolve the type
        val reference = typeReference.resolve()

        // Not a lambda, return type name as is
        if (!reference.isFunctionType) return Classifier(reference.toString(), typeReference)

        // 1 argument callable is return type
        if (reference.arguments.size == 1)
            return Callable(false, false, false, typeReference)

        // Like visitCallableReference check for first argument being a NodeContext
        // For callable `NodeContext.() -> T` is the same as `(NodeContext) -> T`
        // If there is more argument, don't handle it
        if (reference.arguments.size > 2) return Empty

        var isNodeParameter = false

        // Check for NodeContext on the first parameter of the function
        if (reference.arguments.isNotEmpty()) {
            val typeParameter = reference.arguments[0].type?.accept(this, data)
            if (typeParameter !is Classifier) return Empty
            isNodeParameter = typeParameter.name == "NodeContext"
            if (!isNodeParameter) return Empty
        }

        // isFunction false because it's a type reference
        return Callable(false, isNodeParameter, false, typeReference)
    }

    /** Called when [visitTypeReference] encounters a [KSClassifierReference]. */
    override fun visitClassifierReference(reference: KSClassifierReference, data: Options): VisitorResult {
        super.visitClassifierReference(reference, data)
        val name = reference.referencedName()
        return Classifier(name, reference)
    }

    /** Called when [visitTypeReference] encounters a [KSCallableReference]. */
    override fun visitCallableReference(reference: KSCallableReference, data: Options): VisitorResult {
        super.visitCallableReference(reference, data)

        // @KtmContext allows
        // - normal callable `() -> T`
        // - NodeContext receiver `NodeContext.() -> Unit`
        // - NodeContext parameter `(NodeContext) -> Unit`
        if (reference.functionParameters.size > 1) return Empty

        var isNodeParameter = false
        var isNodeReceiver = false

        // Check for NodeContext on the first parameter of the function
        if (reference.functionParameters.isNotEmpty()) {
            val typeParameter = reference.functionParameters[0].type.accept(this, data)
            if (typeParameter !is Classifier) return Empty
            isNodeParameter = typeParameter.name == "NodeContext"
            if (!isNodeParameter) return Empty
        }

        // Check for NodeContext as receiver (
        val receiver = reference.receiverType?.accept(this, data)
        if (receiver != null) {
            // If it has a receiver which is not NodeContext, it should not be handled
            if (receiver !is Classifier) return Empty
            isNodeReceiver = receiver.name == "NodeContext"
            if (!isNodeReceiver) return Empty
        }

        // Callable with NodeContext in both receiver and parameter is invalid
        if (isNodeParameter && isNodeReceiver) return Empty

        return Callable(false, isNodeParameter, isNodeReceiver, reference)
    }

    // PARSING FUNCTION

    /** A like [visitPropertyDeclaration] without type checks for NodeContext */
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Options): VisitorResult {
        super.visitFunctionDeclaration(function, data)

        // @KtmContext allows
        // - normal function `foo(): T`
        // - NodeContext receiver `NodeContext.foo() -> Unit`
        // - NodeContext parameter `foo(context: NodeContext) -> Unit`

        // Only handle public functions
        if (!function.isPublic()) return Empty

        // Name surcharge with @KtmName
        val ktmName = function.annotations.getKtmName(data)
        val name = function.simpleName.asString()

        // init constructor function should not be handled
        if (name == "<init>") return Empty

        // Only handle when NodeContext is the only parameter func(context: NodeContext)
        if (function.parameters.size > 1) return Empty

        var isNodeParameter = false
        var isNodeReceiver = false

        // Check for NodeContext on the first parameter of the function
        if (function.parameters.isNotEmpty()) {
            val typeParameter = function.parameters[0].type.accept(this, data)
            if (typeParameter !is Classifier) return Empty
            isNodeParameter = typeParameter.name == "NodeContext"
            if (!isNodeParameter) return Empty
        }

        // Check for NodeContext as receiver (
        val receiver = function.extensionReceiver?.accept(this, data)
        if (receiver != null) {
            // If it has a receiver which is not NodeContext, it should not be handled
            if (receiver !is Classifier) return Empty
            isNodeReceiver = receiver.name == "NodeContext"
            if (!isNodeReceiver) return Empty
        }

        // Function with NodeContext in both receiver and parameter is invalid
        if (isNodeParameter && isNodeReceiver) return Empty


        val token = Field(
            name = ktmName ?: name,
            fieldName = name,
            isCallable = true,
            nodeAsParameter = isNodeParameter,
            nodeAsReceiver = isNodeReceiver,
            isFunction = true,
        )
        return FieldToken(token, function)
    }

    private fun Sequence<KSAnnotation>.isKtmDynamic(data: Options) = find {
        val classifier = it.annotationType.accept(this@TokenBuilderVisitor, data)
        classifier is Classifier && classifier.name == "KtmDynamic"
    } != null

    private fun Sequence<KSAnnotation>.isKtmIgnore(data: Options) = find {
        val classifier = it.annotationType.accept(this@TokenBuilderVisitor, data)
        classifier is Classifier && classifier.name == "KtmIgnore"
    } != null

    private fun Sequence<KSAnnotation>.getKtmName(data: Options) = find {
        val classifier = it.annotationType.accept(this@TokenBuilderVisitor, data)
        classifier is Classifier && classifier.name == "KtmName"
    }?.arguments?.find { it.name?.asString() == "name" }?.value as String?

}