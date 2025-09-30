package org.cqframework.cql.elm.requirements

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.VersionedIdentifier

class ElmQueryRequirement(libraryIdentifier: VersionedIdentifier?, query: Query?) :
    ElmExpressionRequirement(libraryIdentifier, query) {
    val query: Query?
        get() = element as Query?

    override fun getElement(): Query? {
        return this.query
    }

    private val dataRequirements: MutableSet<ElmDataRequirement> = LinkedHashSet()

    fun getDataRequirements(): Iterable<ElmDataRequirement> {
        return dataRequirements
    }

    fun addDataRequirements(dataRequirement: ElmDataRequirement) {
        requireNotNull(dataRequirement.querySource) {
            "Data requirement must be associated with an alias to be added to a query requirements"
        }
        dataRequirements.add(dataRequirement)
    }

    fun getDataRequirement(querySource: Element?): ElmDataRequirement? {
        if (querySource is AliasedQuerySource) {
            return getDataRequirement(querySource.alias)
        }

        if (querySource is LetClause) {
            return getDataRequirement(querySource.identifier)
        }

        return null
    }

    fun getDataRequirement(alias: String?): ElmDataRequirement? {
        for (dataRequirement in dataRequirements) {
            if (dataRequirement.alias == alias) {
                return dataRequirement
            }
        }

        return null
    }

    override fun hasRequirement(requirement: ElmRequirement?): Boolean {
        val superHasRequirement = super.hasRequirement(requirement)
        if (!superHasRequirement) {
            for (dataRequirement in dataRequirements) {
                if (dataRequirement.hasRequirement(requirement)) {
                    return true
                }
            }
        }
        return superHasRequirement
    }

    private fun distributeConditionRequirement(requirement: ElmConditionRequirement) {
        val dataRequirement = getDataRequirement(requirement.property!!.source)
        dataRequirement?.addConditionRequirement(requirement)
    }

    /**
     * Determine which side of the join to apply the requirement Should we apply it to both? Let the
     * decision be made later? Selecting left for now, arbitrary
     *
     * @param requirement
     */
    private fun distributeJoinRequirement(requirement: ElmJoinRequirement) {
        val leftDataRequirement = getDataRequirement(requirement.leftProperty!!.source)
        val rightDataRequirement = getDataRequirement(requirement.rightProperty!!.source)
        if (leftDataRequirement != null && rightDataRequirement != null) {
            leftDataRequirement.addJoinRequirement(requirement)
        }
    }

    private fun distributeNestedConditionRequirement(
        dataRequirement: ElmDataRequirement,
        path: String?,
        requirement: ElmConditionRequirement,
    ) {
        val qualifiedProperty = Property()
        val nestedProperty = requirement.property!!.property
        qualifiedProperty.path = String.format("%s.%s", path, nestedProperty!!.path)
        qualifiedProperty.scope = dataRequirement.alias
        qualifiedProperty.resultTypeName = nestedProperty.resultTypeName
        qualifiedProperty.resultTypeSpecifier = nestedProperty.resultTypeSpecifier
        qualifiedProperty.resultType = nestedProperty.resultType
        val qualifiedPropertyRequirement =
            ElmPropertyRequirement(
                libraryIdentifier,
                qualifiedProperty,
                dataRequirement.querySource!!,
                true,
            )
        // TODO: Validate that the comparand is context literal and scope stable
        val qualifiedCondition =
            ElmConditionRequirement(
                libraryIdentifier,
                requirement.expression,
                qualifiedPropertyRequirement,
                requirement.comparand!!,
            )
        dataRequirement.addConditionRequirement(qualifiedCondition)
    }

    /**
     * Distributes a nested query requirement through this query by detecting references from the
     * nested query to aliases in this query. This may result in a nested query against a property
     * of an alias in this query being absorbed as a qualified reference, or it may result in a
     * nested query being absorbed as a joined alias. Returns true if the requirement was absorbed
     * (and should therefore not be reported up the context), false if the requirement could not be
     * absorbed and should continue to be reported as a query requirement.
     *
     * @param requirement
     * @param context
     * @return
     */
    private fun distributeQueryRequirement(
        requirement: ElmQueryRequirement,
        context: ElmRequirementsContext,
    ): Boolean {
        // If the query is single source and the source is a property reference to an alias in the
        // current query
        // distribute the conjunctive requirements of the nested query as conjunctive requirements
        // against the
        // qualified property
        if (requirement.dataRequirements.size == 1) {
            for (nestedAlias in requirement.dataRequirements) {
                if (nestedAlias.expression is Property) {
                    val sourceProperty = nestedAlias.expression as Property?
                    if (sourceProperty!!.scope != null) {
                        val aliasDataRequirement = getDataRequirement(sourceProperty.scope)
                        if (
                            aliasDataRequirement != null &&
                                nestedAlias.getConjunctiveRequirement() != null
                        ) {
                            for (nestedRequirement in
                                nestedAlias.getConjunctiveRequirement()!!.arguments) {
                                // A conjunctive requirement against a nested query that is based on
                                // a property of the
                                // current query
                                // can be inferred as a conjunctive requirement against the
                                // qualified property in the
                                // current query
                                if (nestedRequirement is ElmConditionRequirement) {
                                    distributeNestedConditionRequirement(
                                        aliasDataRequirement,
                                        sourceProperty.path,
                                        nestedRequirement,
                                    )
                                    return true
                                }
                            }
                        }
                    }
                } else if (nestedAlias.expression is Retrieve) {
                    for (nestedRequirement in nestedAlias.getConjunctiveRequirement()!!.arguments) {
                        if (nestedRequirement is ElmConditionRequirement) {
                            if (nestedRequirement.comparand is ElmPropertyRequirement) {
                                val comparand =
                                    nestedRequirement.comparand as ElmPropertyRequirement?
                                val relatedRequirement = this.getDataRequirement(comparand!!.source)
                                if (relatedRequirement != null) {
                                    // Create a new data requirement referencing the retrieve
                                    val newRequirement =
                                        ElmDataRequirement(libraryIdentifier, nestedAlias.retrieve)
                                    // Create a new and unique alias for this data requirement
                                    val localId = context.generateLocalId()
                                    val aqs =
                                        AliasedQuerySource()
                                            .withExpression(newRequirement.retrieve)
                                            .withLocalId(localId)
                                            .withAlias(localId)
                                    newRequirement.querySource = aqs
                                    // Infer the condition requirement as a join requirement
                                    val leftProperty: Property =
                                        ElmCloner.clone(nestedRequirement.property!!.property)
                                            as Property
                                    leftProperty.source = null
                                    leftProperty.scope = localId
                                    val leftPropertyRequirement =
                                        ElmPropertyRequirement(
                                            libraryIdentifier,
                                            leftProperty,
                                            aqs,
                                            true,
                                        )
                                    val joinRequirement =
                                        ElmJoinRequirement(
                                            libraryIdentifier,
                                            nestedRequirement.expression,
                                            leftPropertyRequirement,
                                            comparand,
                                        )
                                    newRequirement
                                        .getConjunctiveRequirement()
                                        ?.combine(joinRequirement)
                                    // Report the new data requirement to this query
                                    addDataRequirements(newRequirement)
                                    // Indicate that this query requirement overall does not need to
                                    // be reported
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    fun addChildRequirements(childRequirements: ElmRequirement?) {
        // TODO: Placeholder to support processing child requirements gathered during the query
        // context processing
        // The property requirements have already been reported and processed, so this is currently
        // unnecessary
    }

    fun distributeExpressionRequirement(
        requirement: ElmExpressionRequirement?,
        context: ElmRequirementsContext,
    ): Boolean {
        when (requirement) {
            is ElmConjunctiveRequirement -> {
                for (expressionRequirement in requirement.arguments) {
                    distributeExpressionRequirement(expressionRequirement, context)
                }
                return true
            }

            is ElmDisjunctiveRequirement -> {
                // TODO: Distribute disjunctive requirements (requires union rewrite)
                return true
            }

            is ElmConditionRequirement -> {
                distributeConditionRequirement(requirement)
                return true
            }

            is ElmJoinRequirement -> {
                distributeJoinRequirement(requirement)
                return true
            }

            is ElmQueryRequirement -> {
                return distributeQueryRequirement(requirement, context)
            }

            else -> return false
        }
    }

    fun analyzeDataRequirements(context: ElmRequirementsContext) {
        // apply query requirements to retrieves

        for (dataRequirement in dataRequirements) {
            dataRequirement.applyDataRequirements(context, this)
        }
    }
}
