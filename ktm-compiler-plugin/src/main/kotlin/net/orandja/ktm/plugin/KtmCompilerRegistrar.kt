package net.orandja.ktm.plugin

import net.orandja.ktm.plugin.log.KtmLogger
import net.orandja.ktm.plugin.log.MessageCollectorLogger
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KtmCompilerRegistrar(
    private val logger: KtmLogger
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +{ session: FirSession -> Test(session, logger) }
    }
}

class Test(
    session: FirSession,
    private val logger: KtmLogger
) : FirDeclarationGenerationExtension(session) {
    override fun generateConstructors(
        context: MemberGenerationContext
    ): List<FirConstructorSymbol> {
        logger.warning("Invoking generateConstructors with context: $context")
        return super.generateConstructors(context)
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        logger.warning("Invoking generateFunctions with callableId: $callableId, context: $context")
        return super.generateFunctions(callableId, context)
    }

    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? {
        logger.warning("Invoking generateNestedClassLikeDeclaration with owner: $owner, name: $name, context: $context")

        val ktmContextRef = FqName("net.orandja.ktm.annotation.KtmContext")
        val predicate = DeclarationPredicate.create { annotated(ktmContextRef) }
        if (session.predicateBasedProvider.matches(predicate, owner)) {
            logger.error("KtmContext annotation can only be used on top level classes or objects.")
        }
        return super.generateNestedClassLikeDeclaration(owner, name, context)
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        logger.warning("Invoking generateProperties with callableId: $callableId, context: $context")
        return super.generateProperties(callableId, context)
    }

    override fun getCallableNamesForClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext
    ): Set<Name> {
        logger.warning("Invoking getCallableNamesForClass with classSymbol: $classSymbol, context: $context")
        return super.getCallableNamesForClass(classSymbol, context)
    }

    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext
    ): Set<Name> {
        logger.warning("Invoking getNestedClassifiersNames with classSymbol: $classSymbol, context: $context")
        return super.getNestedClassifiersNames(classSymbol, context)
    }
}