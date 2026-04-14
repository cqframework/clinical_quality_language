package org.cqframework.cql.cql2elm

import kotlin.reflect.KClass
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.IdentifierRef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef

/**
 * Tracks identifier contexts across global and local scopes and emits a warning when a newly pushed
 * identifier shadows an existing one. Lookups and mutations delegate to the underlying
 * [ScopeManager] stacks so the existing scope lifecycle remains authoritative.
 */
internal class IdentifierHidingChecker(
    private val scopeManager: ScopeManager,
    private val reportWarning: (String, Element?) -> Unit,
) {
    fun pushIdentifier(identifierRef: IdentifierRef, element: Element?, scope: IdentifierScope) {
        val identifier = identifierRef.name!!
        val localStack = scopeManager.localIdentifierStack
        val localMatch =
            if (localStack.isNotEmpty()) findMatching(localStack.peek(), identifier) else null
        val globalMatch = findMatching(scopeManager.globalIdentifiers, identifier)
        if (globalMatch != null || localMatch != null) {
            val matchedContext = globalMatch ?: localMatch!!
            val matchedOnFunctionOverloads =
                matchedContext.trackableSubclass == FunctionDef::class && element is FunctionDef
            if (!matchedOnFunctionOverloads) {
                reportWarning(
                    resolveWarningMessage(matchedContext.identifier, identifier, element),
                    identifierRef,
                )
            }
        }
        if (element !is Literal) {
            val trackableOrNull: KClass<out Element>? =
                if (element == null) null else element::class
            if (scope == IdentifierScope.GLOBAL) {
                scopeManager.globalIdentifiers.add(
                    IdentifierContext(identifierRef, trackableOrNull)
                )
            } else {
                scopeManager.localIdentifierStack
                    .peek()
                    .add(IdentifierContext(identifierRef, trackableOrNull))
            }
        }
    }

    fun popIdentifier(scope: IdentifierScope) {
        if (scope == IdentifierScope.GLOBAL) {
            scopeManager.globalIdentifiers.removeLast()
        } else {
            scopeManager.localIdentifierStack.peek().removeLast()
        }
    }

    private fun findMatching(
        identifierContext: Collection<IdentifierContext>,
        identifier: String,
    ): IdentifierContext? = identifierContext.firstOrNull { it.identifier == identifier }

    private fun resolveWarningMessage(
        matchedIdentifier: String?,
        identifierParam: String,
        element: Element?,
    ): String {
        val elementString = lookupElementWarning(element)
        return if (element is Literal) {
            "String literal '$identifierParam' matches the identifier $matchedIdentifier. " +
                "Consider whether the identifier was intended instead."
        } else {
            "$elementString identifier $identifierParam is hiding another identifier of the same name."
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun lookupElementWarning(element: Any?): String =
        when (element) {
            is ExpressionDef -> "An expression"
            is ParameterDef -> "A parameter"
            is ValueSetDef -> "A valueset"
            is CodeSystemDef -> "A codesystem"
            is CodeDef -> "A code"
            is ConceptDef -> "A concept"
            is IncludeDef -> "An include"
            is AliasedQuerySource -> "An alias"
            is LetClause -> "A let"
            is OperandDef -> "An operand"
            is UsingDef -> "A using"
            is Literal -> "A literal"
            else -> "An [unknown structure]"
        }
}
