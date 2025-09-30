package org.cqframework.cql.elm.requirements

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.requirements.ComparableElmRequirement.Companion.hasCodeFilter
import org.cqframework.cql.elm.requirements.ComparableElmRequirement.Companion.hasDateFilter
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SearchType
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.CodeFilterElement
import org.hl7.elm.r1.DateFilterElement
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.Search
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.elm.r1.With

class ElmDataRequirement : ElmExpressionRequirement {
    constructor(
        libraryIdentifier: VersionedIdentifier?,
        element: Retrieve?,
    ) : super(libraryIdentifier, element)

    constructor(
        libraryIdentifier: VersionedIdentifier?,
        element: Retrieve?,
        inferredFrom: Retrieve?,
    ) : super(libraryIdentifier, element) {
        this.inferredFrom = inferredFrom
    }

    val retrieve: Retrieve
        get() = element as Retrieve

    override fun getElement(): Retrieve {
        return this.retrieve
    }

    var inferredFrom: Retrieve? = null
        private set

    /** May be an AliasedQuerySource or a LetClause */
    var querySource: Element? = null

    @JvmField var pertinenceContext: ElmPertinenceContext? = null

    val alias: String?
        get() {
            return when (querySource) {
                is AliasedQuerySource -> {
                    (querySource as AliasedQuerySource).alias
                }

                is LetClause -> {
                    (querySource as LetClause).identifier
                }

                else -> {
                    throw IllegalArgumentException(
                        "Cannot determine alias from data requirement because source is not an AliasedQuerySource or LetClause"
                    )
                }
            }
        }

    override val expression: Expression?
        get() {
            return when (querySource) {
                is AliasedQuerySource -> {
                    (querySource as AliasedQuerySource).expression
                }

                is LetClause -> {
                    (querySource as LetClause).expression
                }

                else -> {
                    throw IllegalArgumentException(
                        "Cannot determine expression for data requirement because source is not an AliasedQuerySource or LetClause"
                    )
                }
            }
        }

    private var propertySet: MutableList<Property>? = null

    val properties: Iterable<Property>?
        get() = propertySet

    fun hasProperties(): Boolean {
        return propertySet != null
    }

    fun addProperty(property: Property) {
        // NOTE: This is conceptually a set, but the ELM model includes
        // annotations as part of the `equals` and `hashCode` implementations
        // so we must check for uniqueness manually
        if (propertySet == null) {
            propertySet = ArrayList()
        }

        val alreadyHasProperty =
            propertySet!!
                .stream()
                .filter { x: Property? -> x!!.path.equals(property.path) }
                .findFirst()
                .isPresent

        if (alreadyHasProperty) {
            return
        }

        propertySet!!.add(property)
    }

    fun removeProperty(property: Property?) {
        if (propertySet != null) {
            propertySet!!.remove(property!!)
        }
    }

    fun reportProperty(propertyRequirement: ElmPropertyRequirement) {
        addProperty(propertyRequirement.property!!)
    }

    private var conjunctiveRequirement: ElmConjunctiveRequirement? = null

    fun getConjunctiveRequirement(): ElmConjunctiveRequirement? {
        ensureConjunctiveRequirement()
        return conjunctiveRequirement
    }

    private fun ensureConjunctiveRequirement() {
        if (conjunctiveRequirement == null) {
            conjunctiveRequirement = ElmConjunctiveRequirement(libraryIdentifier, Null())
        }
    }

    fun addConditionRequirement(conditionRequirement: ElmConditionRequirement?) {
        ensureConjunctiveRequirement()
        conjunctiveRequirement!!.combine(conditionRequirement)
    }

    fun addJoinRequirement(joinRequirement: ElmJoinRequirement?) {
        ensureConjunctiveRequirement()
        conjunctiveRequirement!!.combine(joinRequirement)
    }

    // TODO:
    private fun extractStatedRequirements(retrieve: Retrieve) {
        if (retrieve.idProperty != null || retrieve.idSearch != null) {
            // Add as an OtherFilterElement
        }

        if (retrieve.codeProperty != null || retrieve.codeSearch != null) {
            // Build the left-hand as a Property (or Search) against the alias
            // The right-hand is the retrieve codes
            // Code comparator values are in, =, and ~ (may need to support ~in at some point...)
            // Property p = new
            // Property().withScope(this.getAlias()).withPath(retrieve.getCodeProperty());
            // ElmPropertyRequirement pr = new ElmPropertyRequirement(this.libraryIdentifier, p,
            // this.getQuerySource(),
            // true);
            // reportProperty(pr);
            // ElmExpressionRequirement vs = new ElmExpressionRequirement(this.libraryIdentifier,
            // retrieve.getCodes());
            // InValueSet ivs = new InValueSet().withCode(p);
            // if (retrieve.getCodes() instanceof ValueSetRef) {
            //    ivs.setValueset((ValueSetRef)retrieve.getCodes());
            // }
            // else {
            //    ivs.setValuesetExpression(retrieve.getCodes());
            // }
            // ElmConditionRequirement ecr = new ElmConditionRequirement(this.libraryIdentifier,
            // ivs, pr, vs);
            // addConditionRequirement(ecr);
        }

        if (
            retrieve.dateProperty != null ||
                retrieve.dateSearch != null ||
                retrieve.dateLowProperty != null ||
                retrieve.dateHighProperty != null
        ) {
            // Build the left-hand as a Property (or Search) against the alias
            // The right-hand is the date range
            // The comparator is always during (i.e. X >= start and X <= end)
        }
    }

    private fun applyConditionRequirementTo(
        conditionRequirement: ElmConditionRequirement,
        retrieve: Retrieve,
        context: ElmRequirementsContext,
    ) {
        if (retrieve.dataType == null) {
            // If the retrieve has no data type, it is neither useful nor possible to apply
            // requirements to it
            return
        }

        if (!conditionRequirement.isTargetable) {
            // If the comparand of the condition requirement is not targetable, requirements cannot
            // be applied
            return
        }

        // if the column is terminology-valued, express as a code filter
        // if the column is date-valued, express as a date filter
        // else express as an other filter
        val property = conditionRequirement.property!!.property
        // DataType propertyType = property.getResultType();
        // Use the comparison type due to the likelihood of conversion operators along the property
        val comparisonType = conditionRequirement.comparand!!.expression?.resultType
        if (comparisonType != null) {
            if (context.typeResolver.isTerminologyType(comparisonType)) {
                val codeFilter = CodeFilterElement()
                if (property is Search) {
                    codeFilter.search = property.path
                } else {
                    codeFilter.property = property!!.path
                }
                when (conditionRequirement.getElement()!!.javaClass.simpleName) {
                    "Equal" -> codeFilter.comparator = "="
                    "Equivalent" -> codeFilter.comparator = "~"
                    "In",
                    "InValueSet",
                    "AnyInValueSet" -> codeFilter.comparator = "in"
                }
                if (codeFilter.comparator != null && (conditionRequirement.isTargetable)) {
                    codeFilter.value = conditionRequirement.comparand!!.expression
                    if (!hasCodeFilter(retrieve.codeFilter, codeFilter)) {
                        retrieve.codeFilter.add(codeFilter)
                    }
                }
            } else if (context.typeResolver.isDateType(comparisonType)) {
                val dateFilter = DateFilterElement()
                if (property is Search) {
                    dateFilter.search = property.path
                } else {
                    dateFilter.property = property!!.path
                }
                // Determine operation and appropriate range
                // If right is interval-valued
                // If the operation is equal, equivalent, same as, in, or included in, the date
                // range is the comparand
                val comparand = conditionRequirement.comparand!!.expression
                if (conditionRequirement.isTargetable) {
                    if (context.typeResolver.isIntervalType(comparisonType)) {
                        when (conditionRequirement.getElement()!!.javaClass.simpleName) {
                            "Equal",
                            "Equivalent",
                            "SameAs",
                            "In",
                            "IncludedIn" -> dateFilter.value = comparand
                            "Before" ->
                                dateFilter.value =
                                    Interval()
                                        .withLowClosed(true)
                                        .withHigh(Start().withOperand(comparand))
                                        .withHighClosed(false)

                            "SameOrBefore" ->
                                dateFilter.value =
                                    Interval()
                                        .withLowClosed(true)
                                        .withHigh(Start().withOperand(comparand))
                                        .withHighClosed(true)

                            "After" ->
                                dateFilter.value =
                                    Interval()
                                        .withLow(End().withOperand(comparand))
                                        .withLowClosed(false)
                                        .withHighClosed(true)

                            "SameOrAfter" ->
                                dateFilter.value =
                                    Interval()
                                        .withLow(End().withOperand(comparand))
                                        .withLowClosed(true)
                                        .withHighClosed(true)

                            "Includes",
                            "Meets",
                            "MeetsBefore",
                            "MeetsAfter",
                            "Overlaps",
                            "OverlapsBefore",
                            "OverlapsAfter",
                            "Starts",
                            "Ends" -> {}
                        }
                    } else {
                        when (conditionRequirement.getElement()!!.javaClass.simpleName) {
                            "Equal",
                            "Equivalent",
                            "SameAs" ->
                                dateFilter.value =
                                    Interval()
                                        .withLow(comparand)
                                        .withLowClosed(true)
                                        .withHigh(comparand)
                                        .withHighClosed(true)

                            "Less",
                            "Before" ->
                                dateFilter.value =
                                    Interval()
                                        .withLowClosed(true)
                                        .withHigh(comparand)
                                        .withHighClosed(false)

                            "LessOrEqual",
                            "SameOrBefore" ->
                                dateFilter.value =
                                    Interval()
                                        .withLowClosed(true)
                                        .withHigh(comparand)
                                        .withHighClosed(true)

                            "Greater",
                            "After" ->
                                dateFilter.value =
                                    Interval()
                                        .withLow(comparand)
                                        .withLowClosed(false)
                                        .withHighClosed(true)

                            "GreaterOrEqual",
                            "SameOrAfter" ->
                                dateFilter.value =
                                    Interval()
                                        .withLow(comparand)
                                        .withLowClosed(true)
                                        .withHighClosed(true)
                        }
                    }
                }

                if (dateFilter.value != null) {
                    if (!hasDateFilter(retrieve.dateFilter, dateFilter)) {
                        retrieve.dateFilter.add(dateFilter)
                    }
                }
            } else {}
        }
    }

    private fun getRetrieveType(context: ElmRequirementsContext?, retrieve: Retrieve): ClassType? {
        val retrieveType = retrieve.resultType

        val elementType = if (retrieveType is ListType) retrieveType.elementType else retrieveType
        if (elementType is ClassType) {
            return elementType
        }
        return null
    }

    private fun applyJoinRequirementTo(
        joinRequirement: ElmJoinRequirement,
        retrieve: Retrieve?,
        context: ElmRequirementsContext,
        queryRequirements: ElmQueryRequirement,
    ) {
        val leftRequirement =
            queryRequirements.getDataRequirement(joinRequirement.leftProperty!!.source)
        val rightRequirement =
            queryRequirements.getDataRequirement(joinRequirement.rightProperty!!.source)
        if (leftRequirement != null && rightRequirement != null) {
            val leftRetrieve = leftRequirement.retrieve
            val rightRetrieve = rightRequirement.retrieve
            // Only report include possibility if the retrieves can both be tied to the data model
            if (leftRetrieve.dataType != null && rightRetrieve.dataType != null) {
                val leftRetrieveType = getRetrieveType(context, leftRetrieve)
                val rightRetrieveType = getRetrieveType(context, rightRetrieve)
                if (leftRetrieveType != null && rightRetrieveType != null) {
                    val leftSearch: SearchType?
                    val rightSearch: SearchType?
                    for (search in leftRetrieveType.getSearches()) {
                        if (
                            joinRequirement.leftProperty!!.property!!.path!!.startsWith(search.path)
                        ) {
                            if (search.type.isCompatibleWith(rightRetrieveType)) {
                                leftSearch = search
                                break
                            }
                        }
                    }
                    for (search in rightRetrieveType.getSearches()) {
                        if (
                            joinRequirement.rightProperty!!
                                .property!!
                                .path!!
                                .startsWith(search.path)
                        ) {
                            if (search.type.isCompatibleWith(leftRetrieveType)) {
                                rightSearch = search
                                break
                            }
                        }
                    }

                    // Search from the model info should be used to inform the selection, but will
                    // in general resolve to
                    // multiple choices
                    // May be a choice better left to the capabilityStatement-informed planning
                    // phase anyway
                }

                // In the absence of search information, either of these formulations is correct,
                // favor primary query
                // sources over withs
                if (leftRetrieve.localId == null) {
                    leftRetrieve.localId = context.generateLocalId()
                }
                if (rightRetrieve.localId == null) {
                    rightRetrieve.localId = context.generateLocalId()
                }
                if (rightRequirement.querySource is With) {
                    leftRetrieve.include.add(
                        IncludeElement()
                            .withIncludeFrom(rightRetrieve.localId)
                            .withRelatedDataType(rightRetrieve.dataType)
                            .withRelatedProperty(joinRequirement.leftProperty!!.property!!.path)
                            .withIsReverse(false)
                    )
                    rightRetrieve.includedIn = leftRetrieve.localId
                } else {
                    rightRetrieve.include.add(
                        IncludeElement()
                            .withIncludeFrom(leftRetrieve.localId)
                            .withRelatedDataType(leftRetrieve.dataType)
                            .withRelatedProperty(joinRequirement.rightProperty!!.property!!.path)
                            .withIsReverse(false)
                    )
                    leftRetrieve.includedIn = rightRetrieve.localId
                }
            }
        }
    }

    private fun applyTo(
        retrieve: Retrieve,
        context: ElmRequirementsContext,
        queryRequirements: ElmQueryRequirement,
    ) {
        // for each ConditionRequirement
        // apply to the retrieve
        for (conditionRequirement in getConjunctiveRequirement()!!.arguments) {
            if (conditionRequirement is ElmConditionRequirement) {
                applyConditionRequirementTo(conditionRequirement, retrieve, context)
            } else if (conditionRequirement is ElmJoinRequirement) {
                applyJoinRequirementTo(conditionRequirement, retrieve, context, queryRequirements)
            }
        }
    }

    fun applyDataRequirements(
        context: ElmRequirementsContext,
        queryRequirements: ElmQueryRequirement,
    ) {
        // If the source of the alias is a direct retrieve, query requirements can be applied
        // directly
        // Otherwise, the query requirements are applied to an "inferred" retrieve representing the
        // query source
        extractStatedRequirements(this.retrieve)
        applyTo(this.retrieve, context, queryRequirements)
    }

    companion object {
        private fun inferFrom(requirement: ElmDataRequirement): ElmDataRequirement {
            val inferredRetrieve = ElmCloner.clone(requirement.retrieve)
            val result =
                ElmDataRequirement(
                    requirement.libraryIdentifier,
                    inferredRetrieve,
                    requirement.retrieve,
                )
            if (requirement.hasProperties()) {
                for (p in requirement.properties!!) {
                    result.addProperty(p)
                }
            }
            return result
        }

        private fun inferFrom(requirement: ElmQueryRequirement): ElmDataRequirement {
            var singleSourceRequirement: ElmDataRequirement? = null
            for (dataRequirement in requirement.getDataRequirements()) {
                if (singleSourceRequirement == null) {
                    singleSourceRequirement = dataRequirement
                } else {
                    singleSourceRequirement = null
                    break
                }
            }
            if (singleSourceRequirement != null) {
                return Companion.inferFrom(singleSourceRequirement)
            }

            return ElmDataRequirement(
                requirement.libraryIdentifier,
                getRetrieve(requirement.query!!),
            )
        }

        @JvmStatic
        fun inferFrom(requirement: ElmExpressionRequirement): ElmDataRequirement {
            if (requirement is ElmDataRequirement) {
                return Companion.inferFrom(requirement)
            }
            if (requirement is ElmQueryRequirement) {
                return Companion.inferFrom(requirement)
            }
            return ElmDataRequirement(
                requirement.libraryIdentifier,
                getRetrieve(requirement.expression!!),
            )
        }

        // This is an "inferred" retrieve but from an expression context and so can't be
        // unambiguously tied to the
        // data layer, and is thus not subject to include optimizations
        private fun getRetrieve(expression: Expression): Retrieve {
            val retrieve =
                Retrieve()
                    .withLocalId(expression.localId)
                    .withLocator(expression.locator)
                    .withResultTypeName(expression.resultTypeName)
                    .withResultTypeSpecifier(expression.resultTypeSpecifier)
            retrieve.resultType = expression.resultType

            return retrieve
        }
    }
}
