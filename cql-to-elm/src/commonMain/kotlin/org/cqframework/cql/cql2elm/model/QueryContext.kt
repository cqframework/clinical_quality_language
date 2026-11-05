package org.cqframework.cql.cql2elm.model

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.LetClause

@Suppress("TooManyFunctions")
class QueryContext {
    private val sources = HashMap<String, AliasedQuerySource>()
    private val lets = HashMap<String, LetClause>()

    private fun internalAddQuerySource(source: AliasedQuerySource) {
        sources[source.alias!!] = source
    }

    // Adds a related (i.e. with or without) source, which does not change cardinality of the query
    fun addRelatedQuerySource(source: AliasedQuerySource) {
        internalAddQuerySource(source)
    }

    // Adds primary sources, which affect cardinality (any primary plural source results in a plural
    // query)
    fun addPrimaryQuerySources(sources: Collection<AliasedQuerySource>) {
        for (source in sources) {
            internalAddQuerySource(source)
            if (source.resultType is ListType) {
                isSingular = false
            }
        }
    }

    val querySources: Collection<AliasedQuerySource>
        get() = sources.values

    fun removeQuerySource(source: AliasedQuerySource) {
        sources.remove(source.alias)
    }

    fun removeQuerySources(sources: Collection<AliasedQuerySource>) {
        for (source in sources) {
            removeQuerySource(source)
        }
    }

    fun addLetClause(let: LetClause) {
        lets[let.identifier!!] = let
    }

    private fun removeLetClause(let: LetClause) {
        lets.remove(let.identifier)
    }

    fun removeLetClauses(lets: Collection<LetClause>) {
        for (let in lets) {
            removeLetClause(let)
        }
    }

    fun resolveAlias(identifier: String): AliasedQuerySource? {
        return sources[identifier]
    }

    fun resolveLet(identifier: String): LetClause? {
        return lets[identifier]
    }

    var isSingular: Boolean = true
        private set

    private var inSourceClauseValue = false

    fun enterSourceClause() {
        inSourceClauseValue = true
    }

    fun exitSourceClause() {
        inSourceClauseValue = false
    }

    fun inSourceClause(): Boolean {
        return inSourceClauseValue
    }

    private var inSortClauseValue = false

    fun enterSortClause() {
        inSortClauseValue = true
    }

    fun exitSortClause() {
        inSortClauseValue = false
    }

    fun inSortClause(): Boolean {
        return inSortClauseValue
    }

    var isImplicit: Boolean = false
    var resultElementType: DataType? = null
    var referencesSpecificContextValue = false
}
