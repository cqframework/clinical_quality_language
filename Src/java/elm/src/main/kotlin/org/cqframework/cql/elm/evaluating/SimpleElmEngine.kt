package org.cqframework.cql.elm.evaluating

import java.math.BigDecimal
import javax.xml.namespace.QName
import org.hl7.elm.r1.Aggregate
import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.AnyInValueSet
import org.hl7.elm.r1.As
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CanConvert
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.Children
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Convert
import org.hl7.elm.r1.Current
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.DateTimeComponentFrom
import org.hl7.elm.r1.Descendents
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.Filter
import org.hl7.elm.r1.First
import org.hl7.elm.r1.ForEach
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.IdentifierRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.InCodeSystem
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IndexOf
import org.hl7.elm.r1.Instance
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.Iteration
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.LastPositionOf
import org.hl7.elm.r1.List
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.MaxValue
import org.hl7.elm.r1.Message
import org.hl7.elm.r1.MinValue
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.NaryExpression
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.OperatorExpression
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.PositionOf
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Repeat
import org.hl7.elm.r1.Round
import org.hl7.elm.r1.Slice
import org.hl7.elm.r1.Sort
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.SubsumedBy
import org.hl7.elm.r1.Subsumes
import org.hl7.elm.r1.TernaryExpression
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.ToList
import org.hl7.elm.r1.Total
import org.hl7.elm.r1.Tuple
import org.hl7.elm.r1.TupleTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier
import org.hl7.elm.r1.UnaryExpression
import org.hl7.elm.r1.ValueSetRef

/*
A simple ELM engine that is capable of limited evaluation of ELM
required for static analysis and other optimization use cases (such as
constant folding, data requirements analysis, limited constraint validation,
etc.)
*/
@Suppress("detekt:all")
class SimpleElmEngine {
    private fun literalsEqual(left: Literal?, right: Literal?): Boolean {
        return (left == null && right == null) ||
            (left != null &&
                left.valueType != null &&
                left.valueType == right!!.valueType &&
                stringsEqual(left.value, right.value))
    }

    fun booleansEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun integersEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun decimalsEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun decimalsEqual(left: BigDecimal?, right: BigDecimal?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        return left == right
    }

    fun quantitiesEqual(left: Quantity?, right: Quantity?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return true
        }

        return decimalsEqual(left.value, right.value) && stringsEqual(left.unit, right.unit)
    }

    fun stringsEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun dateTimesEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun dateRangesEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun stringsEqual(left: String?, right: String?): Boolean {
        return (left == null && right == null) || (left != null && left == right)
    }

    fun systemsEqual(left: CodeSystemRef?, right: CodeSystemRef?): Boolean {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null) ||
            (left != null &&
                stringsEqual(left.libraryName, right!!.libraryName) &&
                stringsEqual(left.name, right.name))
    }

    fun valueSetsEqual(left: ValueSetRef?, right: ValueSetRef?): Boolean {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null) ||
            (left != null &&
                stringsEqual(left.libraryName, right!!.libraryName) &&
                stringsEqual(left.name, right.name))
    }

    fun codesEqual(left: Expression?, right: Expression?): Boolean {
        return expressionsEqual(left, right)
    }

    fun qnamesEqual(left: QName?, right: QName?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        return left == right
    }

    fun typeSpecifiersEqual(left: TypeSpecifier?, right: TypeSpecifier?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        // NamedTypeSpecifier
        if (left is NamedTypeSpecifier) {
            if (right is NamedTypeSpecifier) {
                return qnamesEqual(left.name, right.name)
            }

            return false
        }

        // IntervalTypeSpecifier
        if (left is IntervalTypeSpecifier) {
            if (right is IntervalTypeSpecifier) {
                return typeSpecifiersEqual(left.pointType, right.pointType)
            }

            return false
        }

        // ListTypeSpecifier
        if (left is ListTypeSpecifier) {
            if (right is ListTypeSpecifier) {
                return typeSpecifiersEqual(left.elementType, right.elementType)
            }

            return false
        }

        // TupleTypeSpecifier
        if (left is TupleTypeSpecifier) {
            if (right is TupleTypeSpecifier) {
                val leftArg = left
                val rightArg = right
                if (
                    leftArg.element != null &&
                        rightArg.element != null &&
                        leftArg.element!!.size == rightArg.element!!.size
                ) {
                    for (i in leftArg.element!!.indices) {
                        val leftElement = leftArg.element!![i]
                        val rightElement = rightArg.element!![i]
                        if (
                            !typeSpecifiersEqual(
                                leftElement!!.elementType,
                                rightElement!!.elementType
                            ) ||
                                !typeSpecifiersEqual(
                                    leftElement.elementType,
                                    rightElement.elementType
                                ) ||
                                !stringsEqual(leftElement.name, rightElement.name)
                        ) {
                            return false
                        }
                    }

                    return true
                }

                return false
            }

            return false
        }

        // ChoiceTypeSpecifier
        if (left is ChoiceTypeSpecifier) {
            if (right is ChoiceTypeSpecifier) {
                val leftArg = left
                val rightArg = right
                if (
                    leftArg.choice != null &&
                        rightArg.choice != null &&
                        leftArg.choice!!.size == rightArg.choice!!.size
                ) {
                    for (i in leftArg.choice!!.indices) {
                        val leftType = leftArg.choice!![i]
                        val rightType = rightArg.choice!![i]
                        if (!typeSpecifiersEqual(leftType, rightType)) {
                            return false
                        }
                    }
                }

                if (
                    leftArg.choice != null &&
                        rightArg.choice != null &&
                        leftArg.choice!!.size == rightArg.choice!!.size
                ) {
                    for (i in leftArg.choice!!.indices) {
                        val leftType = leftArg.choice!![i]
                        val rightType = rightArg.choice!![i]
                        if (!typeSpecifiersEqual(leftType, rightType)) {
                            return false
                        }
                    }

                    return true
                }

                return false
            }

            return false
        }

        // False for the possibility of an unrecognized type specifier type
        return false
    }

    fun expressionsEqual(left: Expression?, right: Expression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left is Literal) {
            if (right is Literal) {
                return literalsEqual(left, right)
            }

            return false
        }

        if (left is Date) {
            if (right is Date) {
                val leftDate = left
                val rightDate = right

                return integersEqual(leftDate.year, rightDate.year) &&
                    integersEqual(leftDate.month, rightDate.month) &&
                    integersEqual(leftDate.day, rightDate.day)
            }

            return false
        }

        if (left is Time) {
            if (right is Time) {
                val leftTime = left
                val rightTime = right

                return integersEqual(leftTime.hour, rightTime.hour) &&
                    integersEqual(leftTime.minute, rightTime.minute) &&
                    integersEqual(leftTime.second, rightTime.second) &&
                    integersEqual(leftTime.millisecond, rightTime.millisecond)
            }

            return false
        }

        if (left is DateTime) {
            if (right is DateTime) {
                val leftDateTime = left
                val rightDateTime = right

                return integersEqual(leftDateTime.year, rightDateTime.year) &&
                    integersEqual(leftDateTime.month, rightDateTime.month) &&
                    integersEqual(leftDateTime.day, rightDateTime.day) &&
                    integersEqual(leftDateTime.hour, rightDateTime.hour) &&
                    integersEqual(leftDateTime.minute, rightDateTime.minute) &&
                    integersEqual(leftDateTime.second, rightDateTime.second) &&
                    integersEqual(leftDateTime.millisecond, rightDateTime.millisecond) &&
                    decimalsEqual(leftDateTime.timezoneOffset, rightDateTime.timezoneOffset)
            }

            return false
        }

        if (left is Interval) {
            if (right is Interval) {
                val leftInterval = left
                val rightInterval = right

                return booleansEqual(
                    leftInterval.lowClosedExpression,
                    rightInterval.lowClosedExpression
                ) &&
                    dateTimesEqual(leftInterval.low, rightInterval.low) &&
                    leftInterval.isLowClosed() == rightInterval.isLowClosed() &&
                    booleansEqual(
                        leftInterval.highClosedExpression,
                        rightInterval.highClosedExpression
                    ) &&
                    dateTimesEqual(leftInterval.high, rightInterval.high) &&
                    leftInterval.isHighClosed() == rightInterval.isHighClosed()
            }

            return false
        }

        // TODO: Strictly speaking this would need to resolve the parameter library since it's not
        // in the ELM if it's a
        // local parameter reference
        if (left is ParameterRef) {
            if (right is ParameterRef) {
                val leftParameter = left
                val rightParameter = right
                return stringsEqual(leftParameter.libraryName, rightParameter.libraryName) &&
                    stringsEqual(leftParameter.name, rightParameter.name)
            }

            return false
        }

        if (left is ValueSetRef) {
            if (right is ValueSetRef) {
                return valueSetsEqual(left, right)
            }

            return false
        }

        if (left is CodeSystemRef) {
            if (right is CodeSystemRef) {
                return systemsEqual(left, right)
            }

            return false
        }

        if (left is ConceptRef) {
            if (right is ConceptRef) {
                val leftConcept = left
                val rightConcept = right
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(leftConcept.libraryName, rightConcept.libraryName) &&
                    stringsEqual(leftConcept.name, rightConcept.name)
            }

            return false
        }

        if (left is CodeRef) {
            if (right is CodeRef) {
                val leftCode = left
                val rightCode = right
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(leftCode.libraryName, rightCode.libraryName) &&
                    stringsEqual(leftCode.name, rightCode.name)
            }

            return false
        }

        if (left is Code) {
            if (right is Code) {
                val leftCode = left
                val rightCode = right
                return stringsEqual(leftCode.code, rightCode.code) &&
                    systemsEqual(leftCode.system, rightCode.system)
            }

            return false
        }

        if (left is Concept) {
            if (right is Concept) {
                val leftConcept = left
                val rightConcept = right
                if (leftConcept.code != null && rightConcept.code != null) {
                    for (lc in leftConcept.code!!) {
                        for (rc in rightConcept.code!!) {
                            if (codesEqual(lc, rc)) {
                                return true
                            }
                        }
                    }
                }
            }

            return false
        }

        if (left is List) {
            if (right is List) {
                val leftList = left
                val rightList = right
                // TODO: Potentially use a hashSet to avoid order-dependence here
                if (leftList.element!!.size == rightList.element!!.size) {
                    for (i in leftList.element!!.indices) {
                        if (!codesEqual(leftList.element!![i], rightList.element!![i])) {
                            return false
                        }
                    }
                }
            }

            return false
        }

        if (left is ToList) {
            if (right is ToList) {
                val leftSingleton = left.operand
                val rightSingleton = right.operand
                return codesEqual(leftSingleton, rightSingleton)
            }

            return false
        }

        // Quantity
        if (left is Quantity) {
            if (right is Quantity) {
                return quantitiesEqual(left, right)
            }

            return false
        }

        // Ratio
        if (left is Ratio) {
            if (right is Ratio) {
                return quantitiesEqual(left.denominator, right.denominator) &&
                    quantitiesEqual(left.numerator, right.numerator)
            }

            return false
        }

        // TODO: Consider refactoring ComparableElmRequirement?
        // Retrieve

        // InCodeSystem
        if (left is InCodeSystem) {
            if (right is InCodeSystem) {
                val inCodeSystemLeft = left
                val inCodeSystemRight = right
                return expressionsEqual(inCodeSystemLeft.code, inCodeSystemRight.code) &&
                    systemsEqual(inCodeSystemLeft.codesystem, inCodeSystemRight.codesystem) &&
                    expressionsEqual(
                        inCodeSystemLeft.codesystemExpression,
                        inCodeSystemRight.codesystemExpression
                    )
            }

            return false
        }

        // AnyInCodeSystem
        if (left is AnyInCodeSystem) {
            if (right is AnyInCodeSystem) {
                val anyInCodeSystemLeft = left
                val anyInCodeSystemRight = right
                return expressionsEqual(anyInCodeSystemLeft.codes, anyInCodeSystemRight.codes) &&
                    systemsEqual(anyInCodeSystemLeft.codesystem, anyInCodeSystemRight.codesystem) &&
                    expressionsEqual(
                        anyInCodeSystemLeft.codesystemExpression,
                        anyInCodeSystemRight.codesystemExpression
                    )
            }

            return false
        }

        // InValueSet
        if (left is InValueSet) {
            if (right is InValueSet) {
                val inLeft = left
                val inRight = right
                return expressionsEqual(inLeft.code, inRight.code) &&
                    valueSetsEqual(inLeft.valueset, inRight.valueset) &&
                    expressionsEqual(inLeft.valuesetExpression, inRight.valuesetExpression)
            }

            return false
        }

        // AnyInValueSet
        if (left is AnyInValueSet) {
            if (right is AnyInValueSet) {
                val inLeft = left
                val inRight = right
                return expressionsEqual(inLeft.codes, inRight.codes) &&
                    valueSetsEqual(inLeft.valueset, inRight.valueset) &&
                    expressionsEqual(inLeft.valuesetExpression, inRight.valuesetExpression)
            }

            return false
        }

        // CalculateAge
        if (left is CalculateAge) {
            if (right is CalculateAge) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.operand, rightArg.operand) &&
                    leftArg.precision == rightArg.precision
            }

            return false
        }

        // Subsumes
        if (left is Subsumes) {
            if (right is Subsumes) {
                if (operandsEqual(left, right)) {
                    return true
                }

                return false
            }

            return false
        }

        // SubsumedBy
        if (left is SubsumedBy) {
            if (right is SubsumedBy) {
                if (operandsEqual(left, right)) {
                    return true
                }

                return false
            }

            return false
        }

        // AggregateExpression
        if (left is AggregateExpression) {
            if (right is AggregateExpression) {
                return aggregateExpressionsEqual(left, right)
            }

            return false
        }

        // OperatorExpression
        if (left is OperatorExpression) {
            if (right is OperatorExpression) {
                return operatorExpressionsEqual(left, right)
            }

            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        // AliasRef
        if (left is AliasRef) {
            if (right is AliasRef) {
                return stringsEqual(left.name, right.name)
            }

            return false
        }

        // Case
        if (left is Case) {
            if (right is Case) {
                val leftArg = left
                val rightArg = right
                if (!expressionsEqual(leftArg.comparand, rightArg.comparand)) {
                    return false
                }

                if (!expressionsEqual(leftArg.`else`, rightArg.`else`)) {
                    return false
                }

                if (
                    leftArg.caseItem != null &&
                        rightArg.caseItem != null &&
                        (leftArg.caseItem!!.size == rightArg.caseItem!!.size)
                ) {
                    for (i in leftArg.caseItem!!.indices) {
                        val leftCaseItem = leftArg.caseItem!![i]
                        val rightCaseItem = rightArg.caseItem!![i]
                        if (
                            !expressionsEqual(leftCaseItem!!.`when`, rightCaseItem!!.`when`) ||
                                !expressionsEqual(leftCaseItem.then, rightCaseItem.then)
                        ) {
                            return false
                        }
                    }

                    return true
                }

                return false
            }

            return false
        }

        // Current
        if (left is Current) {
            if (right is Current) {
                return stringsEqual(left.scope, right.scope)
            }

            return false
        }

        // FunctionRef
        if (left is FunctionRef) {
            if (right is FunctionRef) {
                val leftArg = left
                val rightArg = right
                return stringsEqual(leftArg.libraryName, rightArg.libraryName) &&
                    stringsEqual(leftArg.name, rightArg.name) &&
                    operandsEqual(leftArg, rightArg)
            }
        }

        // ExpressionRef
        if (left is ExpressionRef) {
            if (right is ExpressionRef) {
                val leftArg = left
                val rightArg = right
                return stringsEqual(leftArg.libraryName, rightArg.libraryName) &&
                    stringsEqual(leftArg.name, rightArg.name)
            }

            return false
        }

        // Filter
        if (left is Filter) {
            if (right is Filter) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    expressionsEqual(leftArg.condition, rightArg.condition) &&
                    stringsEqual(leftArg.scope, rightArg.scope)
            }

            return false
        }

        // ForEach
        if (left is ForEach) {
            if (right is ForEach) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    expressionsEqual(leftArg.element, rightArg.element) &&
                    stringsEqual(leftArg.scope, rightArg.scope)
            }

            return false
        }

        // IdentifierRef
        if (left is IdentifierRef) {
            if (right is IdentifierRef) {
                val leftArg = left
                val rightArg = right
                return stringsEqual(leftArg.libraryName, rightArg.libraryName) &&
                    stringsEqual(leftArg.name, rightArg.name)
            }

            return false
        }

        // If
        if (left is If) {
            if (right is If) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.condition, rightArg.condition) &&
                    expressionsEqual(leftArg.then, rightArg.then) &&
                    expressionsEqual(leftArg.`else`, rightArg.`else`)
            }

            return false
        }

        // Instance
        if (left is Instance) {
            if (right is Instance) {
                val leftArg = left
                val rightArg = right
                if (!qnamesEqual(leftArg.classType, rightArg.classType)) {
                    return false
                }

                if (
                    leftArg.element != null &&
                        rightArg.element != null &&
                        leftArg.element!!.size == rightArg.element!!.size
                ) {
                    for (i in leftArg.element!!.indices) {
                        val leftElement = leftArg.element!![i]
                        val rightElement = rightArg.element!![i]
                        if (
                            !stringsEqual(leftElement!!.name, rightElement!!.name) ||
                                !expressionsEqual(leftElement.value, rightElement.value)
                        ) {
                            return false
                        }
                    }

                    return true
                }

                return false
            }

            return false
        }

        // Iteration
        if (left is Iteration) {
            if (right is Iteration) {
                return stringsEqual(left.scope, right.scope)
            }

            return false
        }

        // MaxValue
        if (left is MaxValue) {
            if (right is MaxValue) {
                return qnamesEqual(left.valueType, right.valueType)
            }

            return false
        }

        // MinValue
        if (left is MinValue) {
            if (right is MinValue) {
                return qnamesEqual(left.valueType, right.valueType)
            }

            return false
        }

        // Null
        if (left is Null) {
            if (right is Null) {
                return true
            }

            return false
        }

        // OperandRef
        if (left is OperandRef) {
            if (right is OperandRef) {
                return stringsEqual(left.name, right.name)
            }

            return false
        }

        // Property
        if (left is Property) {
            if (right is Property) {
                val leftArg = left
                val rightArg = right
                return stringsEqual(leftArg.scope, rightArg.scope) &&
                    stringsEqual(leftArg.path, rightArg.path)
            }

            return false
        }

        // Query
        if (left is Query) {
            if (right is Query) {
                val leftArg = left
                val rightArg = right
            }

            return false
        }

        // QueryLetRef
        if (left is QueryLetRef) {
            if (right is QueryLetRef) {
                return stringsEqual(left.name, right.name)
            }

            return false
        }

        // Repeat
        if (left is Repeat) {
            if (right is Repeat) {
                val leftArg = left
                val rightArg = right
            }

            return false
        }

        // Sort
        if (left is Sort) {
            if (right is Sort) {
                val leftArg = left
                val rightArg = right
            }

            return false
        }

        // Total
        if (left is Total) {
            if (right is Total) {
                val leftArg = left
                val rightArg = right
            }

            return false
        }

        // Tuple
        if (left is Tuple) {
            if (right is Tuple) {
                val leftArg = left
                val rightArg = right
            }

            return false
        }

        return false
    }

    fun operandsEqual(left: FunctionRef, right: FunctionRef): Boolean {
        if (
            left.operand != null &&
                right.operand != null &&
                left.operand!!.size == right.operand!!.size
        ) {
            for (i in left.operand!!.indices) {
                if (!expressionsEqual(left.operand!![i], right.operand!![i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    fun operandsEqual(left: BinaryExpression, right: BinaryExpression): Boolean {
        if (
            left.operand != null &&
                right.operand != null &&
                left.operand!!.size == right.operand!!.size
        ) {
            for (i in left.operand!!.indices) {
                if (!expressionsEqual(left.operand!![i], right.operand!![i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    fun operandsEqual(left: TernaryExpression, right: TernaryExpression): Boolean {
        if (
            left.operand != null &&
                right.operand != null &&
                left.operand!!.size == right.operand!!.size
        ) {
            for (i in left.operand!!.indices) {
                if (!expressionsEqual(left.operand!![i], right.operand!![i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    fun operandsEqual(left: NaryExpression, right: NaryExpression): Boolean {
        if (
            left.operand != null &&
                right.operand != null &&
                left.operand!!.size == right.operand!!.size
        ) {
            for (i in left.operand!!.indices) {
                if (!expressionsEqual(left.operand!![i], right.operand!![i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    fun operatorExpressionsEqual(left: OperatorExpression?, right: OperatorExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        // UnaryExpression
        if (left is UnaryExpression) {
            if (right is UnaryExpression) {
                return unaryExpressionsEqual(left, right)
            }

            return false
        }

        // BinaryExpression
        if (left is BinaryExpression) {
            if (right is BinaryExpression) {
                return binaryExpressionsEqual(left, right)
            }

            return false
        }

        // TernaryExpression
        if (left is TernaryExpression) {
            if (right is TernaryExpression) {
                return ternaryExpressionsEqual(left, right)
            }

            return false
        }

        // NaryExpression
        if (left is NaryExpression) {
            if (right is NaryExpression) {
                return naryExpressionsEqual(left, right)
            }

            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        // Round
        if (left is Round) {
            if (right is Round) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.operand, rightArg.operand) &&
                    expressionsEqual(leftArg.precision, rightArg.precision)
            }

            return false
        }

        // Combine
        if (left is Combine) {
            if (right is Combine) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    expressionsEqual(leftArg.separator, rightArg.separator)
            }

            return false
        }

        // Split
        if (left is Split) {
            if (right is Split) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.stringToSplit, rightArg.stringToSplit) &&
                    expressionsEqual(leftArg.separator, rightArg.separator)
            }

            return false
        }

        // SplitOnMatches
        if (left is SplitOnMatches) {
            if (right is SplitOnMatches) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.stringToSplit, rightArg.stringToSplit) &&
                    expressionsEqual(leftArg.separatorPattern, rightArg.separatorPattern)
            }

            return false
        }

        // PositionOf
        if (left is PositionOf) {
            if (right is PositionOf) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.string, rightArg.string) &&
                    expressionsEqual(leftArg.pattern, rightArg.pattern)
            }

            return false
        }

        // LastPositionOf
        if (left is LastPositionOf) {
            if (right is LastPositionOf) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.string, rightArg.string) &&
                    expressionsEqual(leftArg.pattern, rightArg.pattern)
            }

            return false
        }

        // Substring
        if (left is Substring) {
            if (right is Substring) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.stringToSub, rightArg.stringToSub) &&
                    expressionsEqual(leftArg.startIndex, rightArg.startIndex) &&
                    expressionsEqual(leftArg.length, rightArg.length)
            }

            return false
        }

        // TimeOfDay
        // Today
        // Now

        // Time
        if (left is Time) {
            if (right is Time) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.hour, rightArg.hour) &&
                    expressionsEqual(leftArg.minute, rightArg.minute) &&
                    expressionsEqual(leftArg.second, rightArg.second) &&
                    expressionsEqual(leftArg.millisecond, rightArg.millisecond)
            }

            return false
        }

        // Date
        if (left is Date) {
            if (right is Date) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.year, rightArg.year) &&
                    expressionsEqual(leftArg.month, rightArg.month) &&
                    expressionsEqual(leftArg.day, rightArg.day)
            }

            return false
        }

        // DateTime
        if (left is DateTime) {
            if (right is DateTime) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.year, rightArg.year) &&
                    expressionsEqual(leftArg.month, rightArg.month) &&
                    expressionsEqual(leftArg.day, rightArg.day) &&
                    expressionsEqual(leftArg.hour, rightArg.hour) &&
                    expressionsEqual(leftArg.minute, rightArg.minute) &&
                    expressionsEqual(leftArg.second, rightArg.second) &&
                    expressionsEqual(leftArg.millisecond, rightArg.millisecond) &&
                    expressionsEqual(leftArg.timezoneOffset, rightArg.timezoneOffset)
            }

            return false
        }

        // First
        if (left is First) {
            if (right is First) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    stringsEqual(leftArg.orderBy, rightArg.orderBy)
            }

            return false
        }

        // Last
        if (left is Last) {
            if (right is Last) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    stringsEqual(leftArg.orderBy, rightArg.orderBy)
            }

            return false
        }

        // IndexOf
        if (left is IndexOf) {
            if (right is IndexOf) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    expressionsEqual(leftArg.element, rightArg.element)
            }

            return false
        }

        // Slice
        if (left is Slice) {
            if (right is Slice) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    expressionsEqual(leftArg.startIndex, rightArg.startIndex) &&
                    expressionsEqual(leftArg.endIndex, rightArg.endIndex)
            }

            return false
        }

        // Children
        if (left is Children) {
            if (right is Children) {
                return expressionsEqual(left.source, right.source)
            }

            return false
        }

        // Descendents
        if (left is Descendents) {
            if (right is Descendents) {
                return expressionsEqual(left.source, right.source)
            }

            return false
        }

        // Message
        if (left is Message) {
            if (right is Message) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.source, rightArg.source) &&
                    expressionsEqual(leftArg.code, rightArg.code) &&
                    expressionsEqual(leftArg.condition, rightArg.condition) &&
                    expressionsEqual(leftArg.message, rightArg.message) &&
                    expressionsEqual(leftArg.severity, rightArg.severity)
            }

            return false
        }

        // Generally speaking we would return false here, but because we've covered all the cases,
        // we return true
        return true
    }

    fun operandsEqual(left: UnaryExpression, right: UnaryExpression): Boolean {
        return expressionsEqual(left.operand, right.operand)
    }

    fun unaryExpressionsEqual(left: UnaryExpression?, right: UnaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        if (!operandsEqual(left, right)) {
            return false
        }

        // Abs
        // As
        if (left is As) {
            if (right is As) {
                val leftArg = left
                val rightArg = right
                return qnamesEqual(leftArg.asType, rightArg.asType) &&
                    typeSpecifiersEqual(leftArg.asTypeSpecifier, rightArg.asTypeSpecifier) &&
                    leftArg.isStrict() == rightArg.isStrict()
            }
        }
        // Ceiling
        // CanConvert
        if (left is CanConvert) {
            if (right is CanConvert) {
                val leftArg = left
                val rightArg = right
                return qnamesEqual(leftArg.toType, rightArg.toType) &&
                    typeSpecifiersEqual(leftArg.toTypeSpecifier, rightArg.toTypeSpecifier)
            }

            return false
        }
        // Convert
        if (left is Convert) {
            if (right is Convert) {
                val leftArg = left
                val rightArg = right
                return qnamesEqual(leftArg.toType, rightArg.toType) &&
                    typeSpecifiersEqual(leftArg.toTypeSpecifier, rightArg.toTypeSpecifier)
            }

            return false
        }
        // ConvertsToBoolean
        // ConvertsToDate
        // ConvertsToDateTime
        // ConvertsToDecimal
        // ConvertsToInteger
        // ConvertsToLong
        // ConvertsToQuantity
        // ConvertsToRatio
        // ConvertsToString
        // ConvertsToTime
        // DateFrom
        // DateTimeComponentFrom
        if (left is DateTimeComponentFrom) {
            if (right is DateTimeComponentFrom) {
                return left.precision == right.precision
            }
            return false
        }
        // Distinct
        // End
        // Exists
        // Exp
        // Flatten
        // Floor
        // Is
        if (left is Is) {
            if (right is Is) {
                val leftArg = left
                val rightArg = right
                return qnamesEqual(leftArg.isType, rightArg.isType) &&
                    typeSpecifiersEqual(leftArg.isTypeSpecifier, rightArg.isTypeSpecifier)
            }
            return false
        }

        // IsFalse
        // IsNull
        // IsTrue
        // Length
        // Ln
        // Lower
        // Negate
        // Not
        // PointFrom
        // Precision
        // Predecessor
        // SingletonFrom
        // Size
        // Start
        // Successor
        // TimeFrom
        // TimezoneFrom
        // TimezoneOffsetFrom
        // ToBoolean
        // ToConcept
        // ToChars
        // ToDate
        // ToDateTime
        // ToDecimal
        // ToInteger
        // ToLong
        // ToList
        // ToQuantity
        // ToRatio
        // ToString
        // ToTime
        // Truncate
        // Upper
        // Width

        // We've covered all the special cases above, so if we make it here, the expressions are
        // equal
        return true
    }

    fun binaryExpressionsEqual(left: BinaryExpression?, right: BinaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        if (!operandsEqual(left, right)) {
            return false
        }

        // TODO: Handle special cases for operators that have a precision modifier
        // Add
        // After
        // And
        // Before
        // CanConvertQuantity
        // Contains
        // ConvertQuantity
        // Collapse
        // DifferenceBetween
        // Divide
        // DurationBetween
        // Ends
        // EndsWith
        // Equal
        // Equivalent
        // Expand
        // Greater
        // GreaterOrEqual
        // HighBoundary
        // Implies
        // In
        // IncludedIn
        // Includes
        // Indexer
        // Less
        // LessOrEqual
        // Log
        // LowBoundary
        // Matches
        // Meets
        // MeetsAfter
        // MeetsBefore
        // Modulo
        // Multiply
        // NotEqual
        // Or
        // Overlaps
        // OverlapsAfter
        // OverlapsBefore
        // Power
        // ProperContains
        // ProperIn
        // ProperIncludedIn
        // ProperIncludes
        // SameAs
        // SameOrAfter
        // SameOrBefore
        // Starts
        // StartsWith
        // Subtract
        // Times
        // TruncatedDivide
        // Xor
        return true
    }

    fun ternaryExpressionsEqual(left: TernaryExpression?, right: TernaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        if (!operandsEqual(left, right)) {
            return false
        }

        // ReplaceMatches
        return true
    }

    fun naryExpressionsEqual(left: NaryExpression?, right: NaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        if (!operandsEqual(left, right)) {
            return false
        }

        // Coalesce
        // Concatenate
        // Except
        // Intersect
        // Union
        return false
    }

    fun aggregateExpressionsEqual(
        left: AggregateExpression?,
        right: AggregateExpression?
    ): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left.javaClass.canonicalName != right.javaClass.canonicalName) {
            return false
        }

        if (!expressionsEqual(left.source, right.source) || !stringsEqual(left.path, right.path)) {
            return false
        }

        // Aggregate
        if (left is Aggregate) {
            if (right is Aggregate) {
                val leftArg = left
                val rightArg = right
                return expressionsEqual(leftArg.initialValue, rightArg.initialValue) &&
                    expressionsEqual(leftArg.iteration, rightArg.iteration)
            }
        }

        // Count
        // Sum
        // Product
        // Min
        // Max
        // Avg
        // GeometricMean
        // Median
        // Mode
        // Variance
        // StdDev
        // PopulationVariance
        // PopulationStdDev
        // AllTrue
        // AnyTrue
        return true
    }
}
