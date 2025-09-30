package org.cqframework.cql.elm.requirements

import java.util.*
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.VersionedIdentifier

class ElmExpressionDefContext(
    val libraryIdentifier: VersionedIdentifier,
    @JvmField val expressionDef: ExpressionDef,
) {

    @JvmField
    val reportedRequirements: ElmRequirements = ElmRequirements(libraryIdentifier, expressionDef)

    fun reportRequirement(requirement: ElmRequirement?) {
        this.reportedRequirements.reportRequirement(requirement)
    }

    private val queryStack = Stack<ElmQueryContext>()

    fun enterQueryContext(query: Query) {
        queryStack.push(ElmQueryContext(libraryIdentifier, query))
    }

    fun exitQueryContext(): ElmQueryContext {
        val queryContext = queryStack.pop()
        return queryContext
    }

    val currentQueryContext: ElmQueryContext
        get() {
            require(!queryStack.empty()) { "Not in a query context" }

            return queryStack.peek()
        }

    fun inQueryContext(): Boolean {
        return !queryStack.empty()
    }

    fun resolveLet(letName: String?): ElmQueryLetContext {
        var letContext: ElmQueryLetContext? = null
        for (queryContext in queryStack) {
            letContext = queryContext.resolveLet(letName)
            if (letContext != null) {
                break
            }
        }

        requireNotNull(letContext) { String.format("Could not resolve let %s", letName) }

        return letContext
    }

    fun resolveAlias(aliasName: String?): ElmQueryAliasContext {
        var aliasContext: ElmQueryAliasContext? = null
        for (queryContext in queryStack) {
            aliasContext = queryContext.resolveAlias(aliasName)
            if (aliasContext != null) {
                break
            }
        }

        requireNotNull(aliasContext) { String.format("Could not resolve alias %s", aliasName) }

        return aliasContext
    }
}
