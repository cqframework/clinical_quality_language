package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.LibraryRef
import org.cqframework.cql.cql2elm.model.ResolvedIdentifierContext
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef

/**
 * Resolves CQL identifiers to ELM reference expressions.
 *
 * Handles the full identifier-resolution pipeline: type-specifier literal, query result element,
 * `$this`, iteration variables, query aliases, let clauses, function operands, definitions in the
 * current library, implicit context properties, and library-qualified member access.
 */
@Suppress(
    "TooManyFunctions",
    "ReturnCount",
    "NestedBlockDepth",
    "CyclomaticComplexMethod",
    "MaxLineLength",
)
internal class IdentifierResolver(
    private val context: Cql2ElmContext,
    private val objectFactory: IdObjectFactory,
) {
    private val scopeManager
        get() = context.scopeManager

    private val identifierResolvers: List<(String) -> Expression?> =
        listOf(
            ::resolveAsTypeSpecifierLiteral,
            ::resolveQueryResultElement,
            ::resolveQueryThisElement,
            ::resolveIterationVariable,
            ::resolveAliasIdentifier,
            ::resolveLetIdentifier,
            ::resolveOperandRef,
        )

    fun resolveIdentifier(identifier: String, mustResolve: Boolean): Expression? {
        for (resolver in identifierResolvers) {
            val result = resolver(identifier)
            if (result != null) return result
        }
        val resolvedIdentifierContext: ResolvedIdentifierContext = context.resolve(identifier)
        val elementRef = resolvedIdentifierContext.exactMatchElement?.let { buildElementRef(it) }
        if (elementRef != null) return elementRef

        val implicitContextRef = resolveImplicitContextProperty(identifier)
        if (implicitContextRef != null) return implicitContextRef

        if (mustResolve) {
            val message =
                resolvedIdentifierContext.warnCaseInsensitiveIfApplicable()
                    ?: "Could not resolve identifier $identifier in the current library."
            throw IllegalArgumentException(message)
        }
        return null
    }

    /**
     * An implicit context is one where the context has the same name as a parameter. Implicit
     * contexts allow FHIRPath expressions to resolve on the implicit context of the expression.
     */
    fun resolveImplicitContext(): ParameterRef? {
        if (!scopeManager.inLiteralContext() && inSpecificContext()) {
            val resolvedIdentifierContext: ResolvedIdentifierContext =
                context.resolve(scopeManager.currentExpressionContext())
            val optParameterDef = resolvedIdentifierContext.getElementOfType(ParameterDef::class)
            if (optParameterDef != null) {
                val contextParameter: ParameterDef = optParameterDef
                context.checkLiteralContext()
                val parameterRef: ParameterRef =
                    objectFactory.createParameterRef().withName(contextParameter.name)
                parameterRef.resultType = contextParameter.resultType
                requireNotNull(parameterRef.resultType) {
                    "Could not validate reference to parameter ${parameterRef.name} because its definition contains errors."
                }
                return parameterRef
            }
        }
        return null
    }

    fun resolveLibraryMemberAccessor(left: LibraryRef, memberIdentifier: String): Expression {
        val libraryName: String? = left.libraryName
        val referencedLibrary = context.resolveLibrary(libraryName)
        val resolvedIdentifierContext: ResolvedIdentifierContext =
            referencedLibrary.resolve(memberIdentifier)
        val element = resolvedIdentifierContext.exactMatchElement
        val ref = element?.let { buildDefinitionRef(it, libraryName, memberIdentifier) }
        return ref
            ?: throw IllegalArgumentException(
                "Could not resolve identifier $memberIdentifier in library ${referencedLibrary.identifier!!.id}."
            )
    }

    private fun resolveAsTypeSpecifierLiteral(identifier: String): Expression? =
        if (scopeManager.inTypeSpecifierContext()) context.createLiteral(identifier) else null

    private fun resolveIterationVariable(identifier: String): Expression? =
        when (identifier) {
            "\$index" -> {
                val iteration = objectFactory.createIteration()
                iteration.resultType = context.resolveTypeName("System", "Integer")
                iteration
            }
            "\$total" -> {
                val total = objectFactory.createTotal()
                total.resultType = context.resolveTypeName("System", "Decimal")
                total
            }
            else -> null
        }

    private fun resolveAliasIdentifier(identifier: String): Expression? {
        val alias = resolveAlias(identifier) ?: return null
        val ref = objectFactory.createAliasRef().withName(identifier)
        ref.resultType =
            if (alias.resultType is ListType) (alias.resultType as ListType).elementType
            else alias.resultType
        return ref
    }

    private fun resolveLetIdentifier(identifier: String): Expression? {
        val let = resolveQueryLet(identifier) ?: return null
        val ref = objectFactory.createQueryLetRef().withName(identifier)
        ref.resultType = let.resultType
        return ref
    }

    private fun resolveImplicitContextProperty(identifier: String): Expression? {
        val parameterRef = resolveImplicitContext() ?: return null
        val resolution =
            context.resolveProperty(parameterRef.resultType, identifier, false) ?: return null
        val contextAccessor =
            context.buildProperty(
                parameterRef,
                resolution.name,
                resolution.isSearch,
                resolution.type,
            )
        return context.applyTargetMap(contextAccessor, resolution.targetMap)
    }

    private fun buildElementRef(element: Element): Expression? {
        if (element is IncludeDef) {
            context.checkLiteralContext()
            return LibraryRef(objectFactory.nextId(), element.localIdentifier)
        }
        return buildDefinitionRef(element, libraryName = null, memberName = null)
    }

    @Suppress("LongMethod")
    private fun buildDefinitionRef(
        element: Element,
        libraryName: String?,
        memberName: String?,
    ): Expression? {
        val isLocal = libraryName == null
        val accessChecked: (AccessModifier?) -> Unit = { access ->
            if (isLocal) context.checkLiteralContext()
            else context.checkAccessLevel(libraryName, memberName!!, access!!)
        }
        val ref: Expression =
            when (element) {
                is ExpressionDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createExpressionRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = getExpressionDefResultType(element)
                    r
                }
                is ParameterDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createParameterRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                is ValueSetDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createValueSetRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    if (context.isCompatibleWith("1.5")) r.preserve = true
                    r
                }
                is CodeSystemDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createCodeSystemRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                is CodeDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createCodeRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                is ConceptDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createConceptRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                else -> return null
            }
        if (isLocal) {
            requireNotNull(ref.resultType) {
                val name = memberName ?: refName(ref)
                "Could not validate reference to ${definitionKindLabel(element)} $name because its definition contains errors."
            }
        }
        return ref
    }

    private fun refName(ref: Expression): String? =
        when (ref) {
            is ExpressionRef -> ref.name
            is ParameterRef -> ref.name
            is ValueSetRef -> ref.name
            is CodeSystemRef -> ref.name
            is CodeRef -> ref.name
            is ConceptRef -> ref.name
            else -> null
        }

    private fun definitionKindLabel(element: Element): String =
        when (element) {
            is ExpressionDef -> "expression"
            is ParameterDef -> "parameter"
            is ValueSetDef -> "valueset"
            is CodeSystemDef -> "codesystem"
            is CodeDef -> "code"
            is ConceptDef -> "concept"
            else -> element::class.simpleName ?: "definition"
        }

    private fun resolveQueryResultElement(identifier: String): Expression? {
        if (scopeManager.inQueryContext()) {
            val query = scopeManager.peekQueryContext()
            if (query.inSortClause() && !query.isSingular) {
                if (identifier == FP_THIS) {
                    val result = objectFactory.createIdentifierRef().withName(identifier)
                    result.resultType = query.resultElementType
                    return result
                }
                val resolution = context.resolveProperty(query.resultElementType, identifier, false)
                if (resolution != null) {
                    val result = objectFactory.createIdentifierRef().withName(resolution.name)
                    result.resultType = resolution.type
                    return context.applyTargetMap(result, resolution.targetMap)
                }
            }
        }
        return null
    }

    private fun resolveAlias(identifier: String): AliasedQuerySource? {
        if (scopeManager.inQueryContext()) {
            val queries = scopeManager.currentScope.queries
            for (i in queries.indices.reversed()) {
                val source = queries.elementAt(i).resolveAlias(identifier)
                if (source != null) {
                    return source
                }
            }
        }
        return null
    }

    private fun resolveQueryThisElement(identifier: String): Expression? {
        if (scopeManager.inQueryContext()) {
            val query = scopeManager.peekQueryContext()
            if (query.isImplicit) {
                val source = resolveAlias(FP_THIS)
                if (source != null) {
                    val aliasRef = objectFactory.createAliasRef().withName(FP_THIS)
                    if (source.resultType is ListType) {
                        aliasRef.resultType = (source.resultType as ListType).elementType
                    } else {
                        aliasRef.resultType = source.resultType
                    }
                    val result = context.resolveProperty(aliasRef.resultType, identifier, false)
                    if (result != null) {
                        return context.resolveAccessor(aliasRef, identifier)
                    }
                }
            }
        }
        return null
    }

    private fun resolveQueryLet(identifier: String): LetClause? {
        if (scopeManager.inQueryContext()) {
            val queries = scopeManager.currentScope.queries
            for (i in queries.indices.reversed()) {
                val let = queries.elementAt(i).resolveLet(identifier)
                if (let != null) {
                    return let
                }
            }
        }
        return null
    }

    private fun resolveOperandRef(identifier: String): OperandRef? {
        if (!scopeManager.functionDefs.empty()) {
            for (operand in scopeManager.functionDefs.peek().operand) {
                if (operand.name == identifier) {
                    return objectFactory
                        .createOperandRef()
                        .withName(identifier)
                        .withResultType(operand.resultType)
                }
            }
        }
        return null
    }

    private fun getExpressionDefResultType(expressionDef: ExpressionDef): DataType? {
        if (scopeManager.currentExpressionContext() == expressionDef.context) {
            return expressionDef.resultType
        }
        if (inSpecificContext()) {
            return expressionDef.resultType
        }
        if (inUnfilteredContext()) {
            if (
                scopeManager.inQueryContext() &&
                    scopeManager.currentScope.queries.peek().inSourceClause()
            ) {
                scopeManager.currentScope.queries.peek().referencesSpecificContextValue = true
            }
            val resultType: DataType = expressionDef.resultType!!
            return resultType as? ListType ?: ListType(resultType)
        }
        throw IllegalArgumentException(
            "Invalid context reference from ${scopeManager.currentExpressionContext()} context to ${expressionDef.context} context."
        )
    }

    private fun inSpecificContext(): Boolean = !inUnfilteredContext()

    private fun inUnfilteredContext(): Boolean =
        scopeManager.currentExpressionContext() == "Unfiltered" ||
            context.isCompatibilityLevel3 && scopeManager.currentExpressionContext() == "Population"
}
