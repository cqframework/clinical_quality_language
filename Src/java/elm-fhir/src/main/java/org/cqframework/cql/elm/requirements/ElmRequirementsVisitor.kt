package org.cqframework.cql.elm.requirements

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.requirements.ElmDataRequirement.Companion.inferFrom
import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.AnyInValueSet
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.IdentifierRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.InCodeSystem
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.NaryExpression
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.RelationshipClause
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.TernaryExpression
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.TimeOfDay
import org.hl7.elm.r1.Today
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.With
import org.hl7.elm.r1.Without

/*
This class implements an ELM Visitor to perform static analysis of data and dependency requirements
for ELM trees.

# Overall optimization/dependency tracing strategy:
Two different types of requirements, reported requirements and inferred requirements.
Reported requirements are tracked at the ExpressionDef level and rolled-up across expression defs
Inferred requirements are inferred through expressions and reported at query boundaries
  (or reported directly if the retrieve is not defined as part of a query source definition)

The visitor is focused on two main tasks:
* Gathering dependencies (any artifacts or declarations used)
* Inferring data requirements (the minimum set of retrieves required to achieve a successful evaluation)

Dependencies in any given visit are tracked cumulatively at the root
Data Requirements must be tracked as inferred per expression def

So in the context, when a requirement is reported, if it's a data requirement, it's tracked at the expression def level,
whereas if it's a dependency, it's always tracked at the root.

# Where clause optimization strategy:
Visit a where clause looking for sargeable conditions of the form:

    A op B

Where:
* A is an order-preserving expression with a single property reference to a property of some source in the current query context
* op is a positive relative comparison operation (=, >, <, >=, <=)
* B is a functional, repeatable, and deterministic context literal expression with respect to the current query context

Gather sargeable conditions as Lists of conditions. At an AND, combine conditions from sub-nodes.
At an OR, the result is separate lists of condition lists.
At an AND, if there are already lists of lists, the condition is too complex for analysis (i.e. it's not in DNF or CNF)

At a property, return an ElmPropertyRequirement
At a literal return an ElmExpressionRequirement w/ contextLiteral true
At a parameter return an ElmExpressionRequirement w/ contextLiteral true
At a unary expression, return an ElmExpressionRequirement w/ contextLiteral false
At a binary comparison expression, return ElmConditionRequirement if possible
At a logical expression, return ElmConjunctiveRequirement or ElmDisjunctiveRequirement

 */
class ElmRequirementsVisitor : BaseElmLibraryVisitor<ElmRequirement?, ElmRequirementsContext>() {
    override fun defaultResult(elm: Element, context: ElmRequirementsContext): ElmRequirement? {
        return null
    }

    public override fun aggregateResult(
        aggregate: ElmRequirement?,
        nextResult: ElmRequirement?,
    ): ElmRequirement? {
        if (aggregate == null) {
            return nextResult
        }

        if (nextResult == null) {
            return aggregate
        }

        if (aggregate is ElmRequirements) {
            aggregate.reportRequirement(nextResult)
            return aggregate
        }

        val requirements = ElmRequirements(aggregate.libraryIdentifier, aggregate.element)
        requirements.reportRequirement(aggregate)
        requirements.reportRequirement(nextResult)
        return requirements
    }

    override fun visitExpressionDef(
        elm: ExpressionDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        var result: ElmRequirement? = null
        context.enterExpressionDef(elm)
        val pertinenceTagFound = context.enterPertinenceContext(elm)

        try {
            result = super.visitExpressionDef(elm, context)
        } finally {
            context.exitExpressionDef(result)
            if (pertinenceTagFound) {
                context.exitPertinenceContext()
            }
        }
        return result
    }

    override fun visitFunctionDef(
        elm: FunctionDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportFunctionDef(elm)
        return super.visitFunctionDef(elm, context)
    }

    override fun visitExpressionRef(
        elm: ExpressionRef,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        var result: ElmRequirement?
        result =
            if (elm is FunctionRef) {
                visitFunctionRef(elm, context)
            } else {
                context.reportExpressionRef(elm)
            }
        if (result != null) {
            // If the expression ref is to a retrieve or a single-source query, surface it
            // as an "inferred" requirement
            // in the referencing scope
            if (result is ElmDataRequirement) {
                val inferredRequirement = inferFrom(result)
                // Should be being reported as a data requirement...
                // context.reportRetrieve(inferredRequirement.getRetrieve());
                result = inferredRequirement
            } else if (result is ElmQueryRequirement) {
                val inferredRequirement = inferFrom(result)
                // Should be being reported as a data requirement...
                // context.reportRetrieve(inferredRequirement.getRetrieve());
                result = inferredRequirement
            }
            return result
        }
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitFunctionRef(
        elm: FunctionRef,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        context.reportFunctionRef(elm)
        val result = super.visitFunctionRef(elm, context)

        // If the result is a data requirement and the function is cardinality-reducing,
        // Return as an operator requirement, rather than returning the result
        if (result is ElmDataRequirement) {
            if (
                elm.operand.size != 1 ||
                    (elm.operand[0].resultType is ListType && elm.resultType !is ListType)
            ) {
                // Note that the assumption here is that the data requirement has already been
                // reported to the context
                return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
                    .combine(result)
            }
        }

        if (result != null) {
            return result
        }

        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitParameterDef(
        elm: ParameterDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportParameterDef(elm)
        return super.visitParameterDef(elm, context)
    }

    override fun visitParameterRef(
        elm: ParameterRef,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        context.reportParameterRef(elm)
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitRetrieve(elm: Retrieve, context: ElmRequirementsContext): ElmRequirement {
        // TODO: childResult reporting?
        val elmPertinenceContext = context.peekPertinenceContext()
        super.visitRetrieve(elm, context)

        val result = ElmDataRequirement(context.getCurrentLibraryIdentifier(), elm)
        if (elmPertinenceContext != null) {
            result.pertinenceContext = elmPertinenceContext
        }
        // If not analyzing requirements, or in a query context, report the data
        // requirement
        // If in a query context, the requirement will be reported as an inferred
        // requirement at the query boundary
        if (!context.options.analyzeDataRequirements || !context.inQueryContext()) {
            context.reportRequirements(result, null)
        }
        return result
    }

    override fun visitCodeSystemDef(
        elm: CodeSystemDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportCodeSystemDef(elm)
        return super.visitCodeSystemDef(elm, context)
    }

    override fun visitValueSetDef(
        elm: ValueSetDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportValueSetDef(elm)
        return super.visitValueSetDef(elm, context)
    }

    override fun visitCodeSystemRef(
        elm: CodeSystemRef,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        context.reportCodeSystemRef(elm)
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitValueSetRef(
        elm: ValueSetRef,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        context.reportValueSetRef(elm)
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitLibrary(elm: Library, context: ElmRequirementsContext): ElmRequirement? {
        context.enterLibrary(elm.identifier)
        try {
            return super.visitLibrary(elm, context)
        } finally {
            context.exitLibrary()
        }
    }

    override fun visitIncludeDef(
        elm: IncludeDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportIncludeDef(elm)
        return super.visitIncludeDef(elm, context)
    }

    override fun visitContextDef(
        elm: ContextDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportContextDef(elm)
        return super.visitContextDef(elm, context)
    }

    override fun visitCodeRef(elm: CodeRef, context: ElmRequirementsContext): ElmRequirement {
        context.reportCodeRef(elm)
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitCodeDef(elm: CodeDef, context: ElmRequirementsContext): ElmRequirement? {
        context.reportCodeDef(elm)
        return super.visitCodeDef(elm, context)
    }

    override fun visitConceptRef(elm: ConceptRef, context: ElmRequirementsContext): ElmRequirement {
        context.reportConceptRef(elm)
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitConceptDef(
        elm: ConceptDef,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        context.reportConceptDef(elm)
        return super.visitConceptDef(elm, context)
    }

    /**
     * If both sides are column references that point to the same column in the same alias the
     * condition is a tautology If both sides are column references that point to different columns
     * in the same alias the condition is a constraint If both sides are column references that
     * point to different aliases the condition is a join If one side or the other is a column
     * reference the condition is a potentially sargeable condition
     *
     * @param elm
     * @param context
     * @param left
     * @param right
     * @return
     */
    private fun inferConditionRequirement(
        elm: Expression,
        context: ElmRequirementsContext,
        left: ElmRequirement?,
        right: ElmRequirement?,
    ): ElmRequirement {
        val leftProperty = left as? ElmPropertyRequirement
        val rightProperty = right as? ElmPropertyRequirement
        if (leftProperty != null && leftProperty.inCurrentScope) {
            if (rightProperty != null && rightProperty.inCurrentScope) {
                if (leftProperty.source === rightProperty.source) {
                    return ElmConstraintRequirement(
                        context.getCurrentLibraryIdentifier(),
                        elm,
                        leftProperty,
                        rightProperty,
                    )
                } else if (
                    leftProperty.source is AliasedQuerySource &&
                        rightProperty.source is AliasedQuerySource
                ) {
                    return ElmJoinRequirement(
                        context.getCurrentLibraryIdentifier(),
                        elm,
                        leftProperty,
                        rightProperty,
                    )
                }
            }
            if (right is ElmExpressionRequirement) {
                return ElmConditionRequirement(
                    context.getCurrentLibraryIdentifier(),
                    elm,
                    leftProperty,
                    right,
                )
            }
        } else if (rightProperty != null && rightProperty.inCurrentScope) {
            if (leftProperty != null && leftProperty.inCurrentScope) {
                if (leftProperty.source == rightProperty.source) {
                    return ElmConstraintRequirement(
                        context.getCurrentLibraryIdentifier(),
                        elm,
                        leftProperty,
                        rightProperty,
                    )
                } else if (
                    leftProperty.source is AliasedQuerySource &&
                        rightProperty.source is AliasedQuerySource
                ) {
                    return ElmJoinRequirement(
                        context.getCurrentLibraryIdentifier(),
                        elm,
                        leftProperty,
                        rightProperty,
                    )
                }
            }
            if (left is ElmExpressionRequirement) {
                return ElmConditionRequirement(
                    context.getCurrentLibraryIdentifier(),
                    elm,
                    right,
                    left,
                )
            }
        }

        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    public override fun visitFields(
        elm: BinaryExpression,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        // Override visit children behavior to determine whether to create an
        // ElmConditionRequirement
        require(elm.operand.size == 2) { "BinaryExpression must have two operands." }

        when (elm.javaClass.simpleName) {
            "Equal",
            "Equivalent",
            "SameAs",
            "Greater",
            "GreaterOrEqual",
            "SameOrAfter",
            "After",
            "Less",
            "LessOrEqual",
            "SameOrBefore",
            "Before",
            "In",
            "Contains" -> {
                val left = visitElement(elm.operand[0], context)
                val right = visitElement(elm.operand[1], context)
                return inferConditionRequirement(elm, context, left, right)
            }

            "And" -> {
                val left = visitElement(elm.operand[0], context)
                val right = visitElement(elm.operand[1], context)

                if (left is ElmExpressionRequirement && right is ElmExpressionRequirement) {
                    return ElmConjunctiveRequirement(context.getCurrentLibraryIdentifier(), elm)
                        .combine(left)
                        .combine(right)
                } else if (left is ElmExpressionRequirement && right == null) {
                    return left
                } else if (right is ElmExpressionRequirement && left == null) {
                    return right
                }

                return aggregateResult(left, right)
            }

            "Or" -> {
                val left = visitElement(elm.operand[0], context)
                val right = visitElement(elm.operand[1], context)

                if (left is ElmExpressionRequirement && right is ElmExpressionRequirement) {
                    return ElmDisjunctiveRequirement(context.getCurrentLibraryIdentifier(), elm)
                        .combine(left)
                        .combine(right)
                } else if (left is ElmExpressionRequirement && right == null) {
                    return left
                } else if (right is ElmExpressionRequirement && left == null) {
                    return right
                }

                return aggregateResult(left, right)
            }

            "Xor",
            "Implies",
            "Starts",
            "Ends",
            "Includes",
            "IncludedIn",
            "Meets",
            "MeetsBefore",
            "MeetsAfter",
            "Overlaps",
            "OverlapsBefore",
            "OverlapsAfter",
            "ProperIncludes",
            "ProperIncludedIn" -> {
                super.visitFields(elm, context)
                return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
            }

            else -> {
                super.visitFields(elm, context)
                return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
            }
        }
    }

    override fun visitTernaryExpression(
        elm: TernaryExpression,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        val requirements = super.visitTernaryExpression(elm, context)
        return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
            .combine(requirements)
    }

    override fun visitNaryExpression(
        elm: NaryExpression,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        val requirements = super.visitNaryExpression(elm, context)
        return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
            .combine(requirements)
    }

    override fun visitOperandRef(elm: OperandRef, context: ElmRequirementsContext): ElmRequirement {
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitIdentifierRef(
        elm: IdentifierRef,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitLiteral(elm: Literal, context: ElmRequirementsContext): ElmRequirement {
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitInterval(elm: Interval, context: ElmRequirementsContext): ElmRequirement {
        val result = super.visitInterval(elm, context)
        val finalResult = ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
        finalResult.combine(result)
        return finalResult
    }

    override fun visitIf(elm: If, context: ElmRequirementsContext): ElmRequirement {
        // TODO: Rewrite the if as equivalent logic
        val result = ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
        var childResult: ElmRequirement?

        if (elm.condition != null) {
            childResult = this.visitElement(elm.condition!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }

        if (elm.then != null) {
            childResult = this.visitElement(elm.then!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }

        if (elm.`else` != null) {
            childResult = this.visitElement(elm.`else`!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }

        return result
    }

    override fun visitCase(elm: Case, context: ElmRequirementsContext): ElmRequirement {
        // TODO: Rewrite the case as equivalent logic
        val result = ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
        var childResult: ElmRequirement?
        if (elm.comparand != null) {
            childResult = this.visitElement(elm.comparand!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }

        for (ci in elm.caseItem) {
            childResult = this.visitElement(ci, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }

        if (elm.`else` != null) {
            childResult = this.visitElement(elm.`else`!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }

        return result
    }

    override fun visitNull(elm: Null, context: ElmRequirementsContext): ElmRequirement {
        return ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitSplit(elm: Split, context: ElmRequirementsContext): ElmRequirement? {
        // If the separator is a literal, infer based only on the string to split
        // argument
        if (elm.separator is Literal) {
            return visitElement(elm.stringToSplit!!, context)
        }
        return super.visitSplit(elm, context)
    }

    override fun visitTimeOfDay(elm: TimeOfDay, context: ElmRequirementsContext): ElmRequirement {
        return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitToday(elm: Today, context: ElmRequirementsContext): ElmRequirement {
        return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitNow(elm: Now, context: ElmRequirementsContext): ElmRequirement {
        return ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
    }

    override fun visitDateTime(elm: DateTime, context: ElmRequirementsContext): ElmRequirement {
        val result = ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
        if (elm.year != null) {
            val childResult = visitExpression(elm.year!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.month != null) {
            val childResult = visitExpression(elm.month!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.day != null) {
            val childResult = visitExpression(elm.day!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.hour != null) {
            val childResult = visitExpression(elm.hour!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.minute != null) {
            val childResult = visitExpression(elm.minute!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.second != null) {
            val childResult = visitExpression(elm.second!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.millisecond != null) {
            val childResult = visitExpression(elm.millisecond!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.timezoneOffset != null) {
            val childResult = visitExpression(elm.timezoneOffset!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        return result
    }

    override fun visitDate(elm: Date, context: ElmRequirementsContext): ElmRequirement {
        val result = ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
        if (elm.year != null) {
            val childResult = visitExpression(elm.year!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.month != null) {
            val childResult = visitExpression(elm.month!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.day != null) {
            val childResult = visitExpression(elm.day!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        return result
    }

    override fun visitTime(elm: Time, context: ElmRequirementsContext): ElmRequirement {
        val result = ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
        if (elm.hour != null) {
            val childResult = visitExpression(elm.hour!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.minute != null) {
            val childResult = visitExpression(elm.minute!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.second != null) {
            val childResult = visitExpression(elm.second!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        if (elm.millisecond != null) {
            val childResult = visitExpression(elm.millisecond!!, context)
            if (childResult is ElmExpressionRequirement) {
                result.combine(childResult)
            }
        }
        return result
    }

    override fun visitProperty(elm: Property, context: ElmRequirementsContext): ElmRequirement? {
        val visitResult = super.visitProperty(elm, context)

        // If the visit returns a property requirement, report as a qualified property
        if (visitResult is ElmPropertyRequirement) {
            // The child is a property reference
            // Construct a new qualified property reference to report
            val visitPropertyRequirement = visitResult
            val qualifiedProperty = Property()
            val sourceProperty = visitPropertyRequirement.property
            qualifiedProperty.source = sourceProperty!!.source
            qualifiedProperty.scope = sourceProperty.scope
            qualifiedProperty.resultTypeName = elm.resultTypeName
            qualifiedProperty.resultTypeSpecifier = elm.resultTypeSpecifier
            qualifiedProperty.localId = sourceProperty.localId
            qualifiedProperty.path = sourceProperty.path + "." + elm.path
            qualifiedProperty.resultType = sourceProperty.resultType
            return context.reportProperty(qualifiedProperty)
        }

        val propertyRequirement = context.reportProperty(elm)
        val result = aggregateResult(propertyRequirement, visitResult)
        return result
    }

    public override fun visitFields(
        elm: AliasedQuerySource,
        context: ElmRequirementsContext,
    ): ElmRequirement {
        // Override visit behavior because we need to exit the definition context prior
        // to traversing the such that
        // condition
        // Such that traversal happens in the visitChildren relationship
        var result = defaultResult(elm, context)
        var aliasContext: ElmQueryAliasContext?
        context.currentQueryContext.enterAliasDefinitionContext(elm)
        try {
            if (elm.expression != null) {
                val childResult = visitElement(elm.expression!!, context)
                result = aggregateResult(result, childResult)
            }
        } finally {
            aliasContext = context.currentQueryContext.exitAliasDefinitionContext(result)
        }
        // If this is an operator requirement, report it directly to the context,
        // otherwise the context it contains will
        // not be reported
        // since query requirements are abstracted to an ElmDataRequirement
        if (result is ElmOperatorRequirement) {
            context.reportRequirements(result, null)
        }
        return aliasContext.getRequirements()
    }

    override fun visitWith(elm: With, context: ElmRequirementsContext): ElmRequirement {
        var result = visitFields((elm as AliasedQuerySource?)!!, context)

        if (elm.suchThat != null) {
            val childResult = visitExpression(elm.suchThat!!, context)!!
            context.currentQueryContext.reportQueryRequirements(childResult)
            result = aggregateResult(result, childResult)!!
        }

        context.currentQueryContext.descopeAlias(elm)

        return result
    }

    override fun visitWithout(elm: Without, context: ElmRequirementsContext): ElmRequirement {
        var result = visitFields((elm as AliasedQuerySource?)!!, context)

        if (elm.suchThat != null) {
            val childResult = visitExpression(elm.suchThat!!, context)
            result = aggregateResult(result, childResult)!!
        }

        context.currentQueryContext.descopeAlias(elm)

        return result
    }

    override fun visitAliasedQuerySource(
        elm: AliasedQuerySource,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        if (elm is RelationshipClause) {
            return visitRelationshipClause(elm, context)
        }

        return visitFields(elm, context)
    }

    override fun visitLetClause(elm: LetClause, context: ElmRequirementsContext): ElmRequirement {
        var result = defaultResult(elm, context)
        var letContext: ElmQueryLetContext?
        context.currentQueryContext.enterLetDefinitionContext(elm)
        try {
            if (elm.expression != null) {
                val childResult = super.visitLetClause(elm, context)
                result = aggregateResult(result, childResult)
            }
        } finally {
            letContext = context.currentQueryContext.exitLetDefinitionContext(result)
        }
        return letContext.getRequirements()
    }

    private fun visitFields(elm: Query, context: ElmRequirementsContext): ElmRequirement? {
        var result = visitFields((elm as Expression?)!!, context)

        for (source in elm.source) {
            val childResult = visitAliasedQuerySource(source, context)
            result = aggregateResult(result, childResult)
        }
        for (let in elm.let) {
            val childResult = visitLetClause(let, context)
            result = aggregateResult(result, childResult)
        }

        for (r in elm.relationship) {
            val childResult = visitRelationshipClause(r, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.where != null) {
            val childResult = visitExpression(elm.where!!, context)
            // This is the one line that's different between this implementation
            // and the super implementation
            context.currentQueryContext.reportQueryRequirements(childResult)
            result = aggregateResult(result, childResult)
        }
        if (elm.`return` != null) {
            val childResult = visitReturnClause(elm.`return`!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.aggregate != null) {
            val childResult = visitAggregateClause(elm.aggregate!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.sort != null) {
            val childResult = visitSortClause(elm.sort!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.resultTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.resultTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }
        return result
    }

    override fun visitQuery(elm: Query, context: ElmRequirementsContext): ElmRequirement {
        var childResult: ElmRequirement?
        var queryContext: ElmQueryContext?
        context.enterQueryContext(elm)
        try {
            childResult = visitFields(elm, context)
        } finally {
            queryContext = context.exitQueryContext()
        }
        val result = queryContext.getQueryRequirement(childResult, context)
        result.analyzeDataRequirements(context)
        context.reportRequirements(result, null)
        return result
    }

    override fun visitInCodeSystem(
        elm: InCodeSystem,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        if (elm.code != null && (elm.codesystem != null || elm.codesystemExpression != null)) {
            val left = visitElement(elm.code!!, context)
            val right =
                if (elm.codesystem != null) visitElement(elm.codesystem!!, context)
                else visitElement(elm.codesystemExpression!!, context)

            return inferConditionRequirement(elm, context, left, right)
        }
        return super.visitInCodeSystem(elm, context)
    }

    override fun visitAnyInCodeSystem(
        elm: AnyInCodeSystem,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        if (elm.codes != null && (elm.codesystem != null || elm.codesystemExpression != null)) {
            val left = visitElement(elm.codes!!, context)
            val right =
                if (elm.codesystem != null) visitElement(elm.codesystem!!, context)
                else visitElement(elm.codesystemExpression!!, context)

            return inferConditionRequirement(elm, context, left, right)
        }
        return super.visitAnyInCodeSystem(elm, context)
    }

    override fun visitInValueSet(
        elm: InValueSet,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        if (elm.code != null && (elm.valueset != null || elm.valuesetExpression != null)) {
            val left = visitElement(elm.code!!, context)
            val right =
                if (elm.valueset != null) visitElement(elm.valueset!!, context)
                else visitElement(elm.valuesetExpression!!, context)

            return inferConditionRequirement(elm, context, left, right)
        }
        return super.visitInValueSet(elm, context)
    }

    override fun visitAnyInValueSet(
        elm: AnyInValueSet,
        context: ElmRequirementsContext,
    ): ElmRequirement? {
        if (elm.codes != null && (elm.valueset != null || elm.valuesetExpression != null)) {
            val left = visitElement(elm.codes!!, context)
            val right =
                if (elm.valueset != null) visitElement(elm.valueset!!, context)
                else visitElement(elm.valuesetExpression!!, context)

            return inferConditionRequirement(elm, context, left, right)
        }
        return super.visitAnyInValueSet(elm, context)
    }

    override fun visitUsingDef(elm: UsingDef, context: ElmRequirementsContext): ElmRequirement? {
        context.reportUsingDef(elm)
        return super.visitUsingDef(elm, context)
    }
}
