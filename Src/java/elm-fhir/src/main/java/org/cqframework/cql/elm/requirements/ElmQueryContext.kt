package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.VersionedIdentifier

class ElmQueryContext(
    private val libraryIdentifier: VersionedIdentifier,
    private val query: Query,
) {
    private var queryRequirements: ElmExpressionRequirement?
    private var definitionContext: ElmQueryAliasContext? = null
    private val aliasContexts: MutableList<ElmQueryAliasContext> = ArrayList()
    private var letDefinitionContext: ElmQueryLetContext? = null
    private val letContexts: MutableList<ElmQueryLetContext> = ArrayList()
    private val queryRequirement: ElmQueryRequirement

    init {
        this.queryRequirements = ElmConjunctiveRequirement(libraryIdentifier, query)
        this.queryRequirement = ElmQueryRequirement(libraryIdentifier, query)
    }

    fun enterLetDefinitionContext(letClause: LetClause) {
        require(letDefinitionContext == null) { "Let clause definition already in progress" }
        letDefinitionContext = ElmQueryLetContext(libraryIdentifier, letClause)
    }

    fun exitLetDefinitionContext(requirements: ElmRequirement?): ElmQueryLetContext {
        requireNotNull(letDefinitionContext) { "Let definition not in progress" }
        letContexts.add(letDefinitionContext!!)
        val result = letDefinitionContext
        result!!.setRequirements(requirements)
        letDefinitionContext = null
        return result
    }

    fun resolveLet(identifier: String?): ElmQueryLetContext? {
        for (letContext in letContexts) {
            if (letContext.identifier == identifier) {
                return letContext
            }
        }
        return null
    }

    fun getLetContext(letClause: LetClause?): ElmQueryLetContext? {
        for (letContext in letContexts) {
            if (letContext.letClause === letClause) {
                return letContext
            }
        }
        return null
    }

    fun enterAliasDefinitionContext(querySource: AliasedQuerySource) {
        require(definitionContext == null) { "Alias definition already in progress" }
        definitionContext = ElmQueryAliasContext(libraryIdentifier, querySource)
    }

    fun exitAliasDefinitionContext(requirements: ElmRequirement?): ElmQueryAliasContext {
        requireNotNull(definitionContext) { "Alias definition not in progress" }
        aliasContexts.add(definitionContext!!)
        val result = definitionContext
        result!!.setRequirements(requirements)
        definitionContext = null
        return result
    }

    fun resolveAlias(aliasName: String?): ElmQueryAliasContext? {
        for (aliasContext in aliasContexts) {
            if (aliasContext.alias == aliasName) {
                return aliasContext
            }
        }

        return null
    }

    private fun getAliasContext(querySource: AliasedQuerySource): ElmQueryAliasContext? {
        for (aliasContext in aliasContexts) {
            if (aliasContext.alias == querySource.alias) {
                return aliasContext
            }
        }

        return null
    }

    fun descopeAlias(querySource: AliasedQuerySource) {
        val aliasContext = getAliasContext(querySource)
        if (aliasContext != null) {
            aliasContexts.remove(aliasContext)
            queryRequirement.addDataRequirements(aliasContext.getRequirements())
        }
        aliasContexts.removeIf { x: ElmQueryAliasContext? -> x!!.alias == querySource.alias }
    }

    fun reportQueryRequirements(requirements: ElmRequirement?) {
        if (requirements is ElmExpressionRequirement) {
            queryRequirements = queryRequirements!!.combine(requirements)
        }
    }

    fun getQueryRequirement(
        childRequirements: ElmRequirement?,
        context: ElmRequirementsContext,
    ): ElmQueryRequirement {
        // Gather requirements from any lets in scope in the query
        for (letContext in letContexts) {
            queryRequirement.addDataRequirements(letContext.getRequirements())
        }

        // Gather requirements from any sources still in scope in the query
        for (aliasContext in aliasContexts) {
            queryRequirement.addDataRequirements(aliasContext.getRequirements())
        }

        // add child requirements gathered during the context
        queryRequirement.addChildRequirements(childRequirements)

        // distribute query requirements to each alias
        if (context.options.analyzeDataRequirements) {
            queryRequirement.distributeExpressionRequirement(queryRequirements, context)
        }

        return queryRequirement
    }
}
