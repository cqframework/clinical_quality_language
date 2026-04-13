package org.cqframework.cql.cql2elm.elm

import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.IntervalType
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.And
import org.hl7.elm.r1.As
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Retrieve

/**
 * Promotes date-range predicates in a query's `where` clause into `Retrieve.dateProperty` /
 * `Retrieve.dateRange` on the source retrieve when it is safe to do so.
 *
 * Extracted from [org.cqframework.cql.cql2elm.Cql2ElmVisitor] so the optimization can be maintained
 * as a distinct ELM transformation.
 */
@Suppress("USELESS_CAST", "ComplexCondition", "ReturnCount")
class DateRangeOptimizer(private val libraryBuilder: LibraryBuilder) {
    /**
     * If the query source is a retrieve and the where clause contains a qualifying `IncludedIn` /
     * `In` / `And`, rewrite the retrieve's date range and strip the absorbed predicate from the
     * where clause.
     *
     * @return the (possibly rewritten) where clause, or `null` if the where clause was fully
     *   absorbed into the retrieve.
     */
    fun optimize(where: Expression?, aqs: AliasedQuerySource): Expression? {
        var result = where
        if (aqs.expression is Retrieve) {
            val retrieve = aqs.expression as Retrieve
            val alias = aqs.alias
            if (
                (result is IncludedIn || result is In) &&
                    attemptDateRangeOptimization(result as BinaryExpression, retrieve, alias!!)
            ) {
                result = null
            } else if (result is And && attemptDateRangeOptimization(result, retrieve, alias!!)) {
                result = consolidateAnd(result)
            }
        }
        return result
    }

    private fun attemptDateRangeOptimization(
        during: BinaryExpression,
        retrieve: Retrieve,
        alias: String,
    ): Boolean {
        if (retrieve.dateProperty != null || retrieve.dateRange != null) {
            return false
        }
        val left = during.operand[0]
        val right = during.operand[1]
        val propertyPath = getPropertyPath(left, alias)
        if (propertyPath != null && isRHSEligibleForDateRangeOptimization(right)) {
            retrieve.dateProperty = propertyPath
            retrieve.dateRange = right
            return true
        }
        return false
    }

    private fun getPropertyPath(reference: Expression, alias: String): String? {
        var ref = reference
        ref = getConversionReference(ref)
        ref = getChoiceSelection(ref)
        if (ref is Property) {
            val property = ref
            if (alias == property.scope) {
                return property.path
            } else if (property.source != null) {
                val subPath = getPropertyPath(property.source!!, alias)
                if (subPath != null) {
                    return "$subPath.${property.path}"
                }
            }
        }
        return null
    }

    private fun getConversionReference(reference: Expression): Expression {
        if (reference is FunctionRef) {
            val functionRef: FunctionRef = reference
            if (
                (functionRef.operand.size == 1) &&
                    (functionRef.resultType != null) &&
                    (functionRef.operand[0].resultType != null)
            ) {
                val o =
                    libraryBuilder.conversionMap.getConversionOperator(
                        functionRef.operand[0].resultType!!,
                        functionRef.resultType!!,
                    )
                if (
                    ((o != null) &&
                        (o.libraryName != null) &&
                        (o.libraryName == functionRef.libraryName) &&
                        (o.name == functionRef.name))
                ) {
                    return functionRef.operand[0]
                }
            }
        }
        return reference
    }

    private fun getChoiceSelection(reference: Expression): Expression {
        if (reference is As) {
            if (reference.operand != null && reference.operand!!.resultType is ChoiceType) {
                return reference.operand!!
            }
        }
        return reference
    }

    private fun attemptDateRangeOptimization(and: And, retrieve: Retrieve, alias: String): Boolean {
        if (retrieve.dateProperty != null || retrieve.dateRange != null) {
            return false
        }
        for (i in and.operand.indices) {
            val operand = and.operand[i]
            if (
                (operand is IncludedIn || operand is In) &&
                    attemptDateRangeOptimization(operand as BinaryExpression, retrieve, alias)
            ) {
                and.operand[i] = libraryBuilder.createLiteral(true)
                return true
            } else if (operand is And && attemptDateRangeOptimization(operand, retrieve, alias)) {
                return true
            }
        }
        return false
    }

    private fun consolidateAnd(and: And): Expression {
        var result: Expression = and
        val lhs = and.operand[0]
        val rhs = and.operand[1]
        when {
            isBooleanLiteral(lhs, true) -> result = rhs
            isBooleanLiteral(rhs, true) -> result = lhs
            lhs is And -> and.operand[0] = consolidateAnd(lhs)
            rhs is And -> and.operand[1] = consolidateAnd(rhs)
        }
        return result
    }

    private fun isBooleanLiteral(expression: Expression, bool: Boolean): Boolean {
        if (expression !is Literal) return false
        val booleanType = libraryBuilder.resolveTypeName("System", "Boolean")
        if (expression.valueType != libraryBuilder.dataTypeToQName(booleanType)) return false
        return bool == expression.value.toBoolean()
    }

    private fun isRHSEligibleForDateRangeOptimization(rhs: Expression): Boolean {
        val dateTime = libraryBuilder.resolveTypeName("System", "DateTime")!!
        return rhs.resultType!!.isSubTypeOf(dateTime) ||
            rhs.resultType!!.isSubTypeOf(IntervalType(dateTime))
    }
}
