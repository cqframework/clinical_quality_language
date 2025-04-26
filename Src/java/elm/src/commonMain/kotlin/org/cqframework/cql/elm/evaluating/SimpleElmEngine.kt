package org.cqframework.cql.elm.evaluating

import org.cqframework.cql.elm.serializing.BigDecimal
import org.cqframework.cql.elm.serializing.QName
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
            (left?.valueType != null &&
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

    private fun decimalsEqual(left: BigDecimal?, right: BigDecimal?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        return left == right
    }

    private fun quantitiesEqual(left: Quantity?, right: Quantity?): Boolean {
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

    private fun systemsEqual(left: CodeSystemRef?, right: CodeSystemRef?): Boolean {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null) ||
            (left != null &&
                stringsEqual(left.libraryName, right!!.libraryName) &&
                stringsEqual(left.name, right.name))
    }

    private fun valueSetsEqual(left: ValueSetRef?, right: ValueSetRef?): Boolean {
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
                if (left.element.size == right.element.size) {
                    for (i in left.element.indices) {
                        val leftElement = left.element[i]
                        val rightElement = right.element[i]
                        if (
                            !typeSpecifiersEqual(
                                leftElement.elementType,
                                rightElement.elementType
                            ) || !stringsEqual(leftElement.name, rightElement.name)
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
                if (left.choice.size == right.choice.size) {
                    for (i in left.choice.indices) {
                        val leftType = left.choice[i]
                        val rightType = right.choice[i]
                        if (!typeSpecifiersEqual(leftType, rightType)) {
                            return false
                        }
                    }
                }

                if (left.choice.size == right.choice.size) {
                    for (i in left.choice.indices) {
                        val leftType = left.choice[i]
                        val rightType = right.choice[i]
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

    private fun expressionsEqual(left: Expression?, right: Expression?): Boolean {
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

                return integersEqual(left.year, right.year) &&
                    integersEqual(left.month, right.month) &&
                    integersEqual(left.day, right.day)
            }

            return false
        }

        if (left is Time) {
            if (right is Time) {

                return integersEqual(left.hour, right.hour) &&
                    integersEqual(left.minute, right.minute) &&
                    integersEqual(left.second, right.second) &&
                    integersEqual(left.millisecond, right.millisecond)
            }

            return false
        }

        if (left is DateTime) {
            if (right is DateTime) {

                return integersEqual(left.year, right.year) &&
                    integersEqual(left.month, right.month) &&
                    integersEqual(left.day, right.day) &&
                    integersEqual(left.hour, right.hour) &&
                    integersEqual(left.minute, right.minute) &&
                    integersEqual(left.second, right.second) &&
                    integersEqual(left.millisecond, right.millisecond) &&
                    decimalsEqual(left.timezoneOffset, right.timezoneOffset)
            }

            return false
        }

        if (left is Interval) {
            if (right is Interval) {

                return booleansEqual(left.lowClosedExpression, right.lowClosedExpression) &&
                    dateTimesEqual(left.low, right.low) &&
                    left.isLowClosed() == right.isLowClosed() &&
                    booleansEqual(left.highClosedExpression, right.highClosedExpression) &&
                    dateTimesEqual(left.high, right.high) &&
                    left.isHighClosed() == right.isHighClosed()
            }

            return false
        }

        // TODO: Strictly speaking this would need to resolve the parameter library since it's not
        // in the ELM if it's a
        // local parameter reference
        if (left is ParameterRef) {
            if (right is ParameterRef) {
                return stringsEqual(left.libraryName, right.libraryName) &&
                    stringsEqual(left.name, right.name)
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
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(left.libraryName, right.libraryName) &&
                    stringsEqual(left.name, right.name)
            }

            return false
        }

        if (left is CodeRef) {
            if (right is CodeRef) {
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(left.libraryName, right.libraryName) &&
                    stringsEqual(left.name, right.name)
            }

            return false
        }

        if (left is Code) {
            if (right is Code) {
                return stringsEqual(left.code, right.code) &&
                    systemsEqual(left.system, right.system)
            }

            return false
        }

        if (left is Concept) {
            if (right is Concept) {
                for (lc in left.code) {
                    for (rc in right.code) {
                        if (codesEqual(lc, rc)) {
                            return true
                        }
                    }
                }
            }

            return false
        }

        if (left is List) {
            // TODO: Potentially use a hashSet to avoid order-dependence here
            if (right is List && left.element.size == right.element.size) {
                for (i in left.element.indices) {
                    if (!codesEqual(left.element[i], right.element[i])) {
                        return false
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
                return expressionsEqual(left.code, right.code) &&
                    systemsEqual(left.codesystem, right.codesystem) &&
                    expressionsEqual(left.codesystemExpression, right.codesystemExpression)
            }

            return false
        }

        // AnyInCodeSystem
        if (left is AnyInCodeSystem) {
            if (right is AnyInCodeSystem) {
                return expressionsEqual(left.codes, right.codes) &&
                    systemsEqual(left.codesystem, right.codesystem) &&
                    expressionsEqual(left.codesystemExpression, right.codesystemExpression)
            }

            return false
        }

        // InValueSet
        if (left is InValueSet) {
            if (right is InValueSet) {
                return expressionsEqual(left.code, right.code) &&
                    valueSetsEqual(left.valueset, right.valueset) &&
                    expressionsEqual(left.valuesetExpression, right.valuesetExpression)
            }

            return false
        }

        // AnyInValueSet
        if (left is AnyInValueSet) {
            if (right is AnyInValueSet) {
                return expressionsEqual(left.codes, right.codes) &&
                    valueSetsEqual(left.valueset, right.valueset) &&
                    expressionsEqual(left.valuesetExpression, right.valuesetExpression)
            }

            return false
        }

        // CalculateAge
        if (left is CalculateAge) {
            if (right is CalculateAge) {
                return expressionsEqual(left.operand, right.operand) &&
                    left.precision == right.precision
            }

            return false
        }

        // Subsumes
        if (left is Subsumes) {
            if (right is Subsumes) {
                return operandsEqual(left, right)
            }

            return false
        }

        // SubsumedBy
        if (left is SubsumedBy) {
            if (right is SubsumedBy) {
                return operandsEqual(left, right)
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

        if (left::class != right::class) {
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
                if (!expressionsEqual(left.comparand, right.comparand)) {
                    return false
                }

                if (!expressionsEqual(left.`else`, right.`else`)) {
                    return false
                }

                if (left.caseItem.size == right.caseItem.size) {
                    for (i in left.caseItem.indices) {
                        val leftCaseItem = left.caseItem[i]
                        val rightCaseItem = right.caseItem[i]
                        if (
                            !expressionsEqual(leftCaseItem.`when`, rightCaseItem.`when`) ||
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
        if (left is FunctionRef && right is FunctionRef) {
            return stringsEqual(left.libraryName, right.libraryName) &&
                stringsEqual(left.name, right.name) &&
                operandsEqual(left, right)
        }

        // ExpressionRef
        if (left is ExpressionRef) {
            if (right is ExpressionRef) {
                return stringsEqual(left.libraryName, right.libraryName) &&
                    stringsEqual(left.name, right.name)
            }

            return false
        }

        // Filter
        if (left is Filter) {
            if (right is Filter) {
                return expressionsEqual(left.source, right.source) &&
                    expressionsEqual(left.condition, right.condition) &&
                    stringsEqual(left.scope, right.scope)
            }

            return false
        }

        // ForEach
        if (left is ForEach) {
            if (right is ForEach) {
                return expressionsEqual(left.source, right.source) &&
                    expressionsEqual(left.element, right.element) &&
                    stringsEqual(left.scope, right.scope)
            }

            return false
        }

        // IdentifierRef
        if (left is IdentifierRef) {
            if (right is IdentifierRef) {
                return stringsEqual(left.libraryName, right.libraryName) &&
                    stringsEqual(left.name, right.name)
            }

            return false
        }

        // If
        if (left is If) {
            if (right is If) {
                return expressionsEqual(left.condition, right.condition) &&
                    expressionsEqual(left.then, right.then) &&
                    expressionsEqual(left.`else`, right.`else`)
            }

            return false
        }

        // Instance
        if (left is Instance) {
            if (right is Instance) {
                if (!qnamesEqual(left.classType, right.classType)) {
                    return false
                }

                if (left.element.size == right.element.size) {
                    for (i in left.element.indices) {
                        val leftElement = left.element[i]
                        val rightElement = right.element[i]
                        if (
                            !stringsEqual(leftElement.name, rightElement.name) ||
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
            return right is Null
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
                return stringsEqual(left.scope, right.scope) && stringsEqual(left.path, right.path)
            }

            return false
        }

        // Query
        if (left is Query) {
            if (right is Query) {
                // TODO: Implement Query equality
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
                // TODO: Implement Repeat equality
            }

            return false
        }

        // Sort
        if (left is Sort) {
            if (right is Sort) {
                // TODO: Implement Sort equality
            }

            return false
        }

        // Total
        if (left is Total) {
            if (right is Total) {
                // TODO: Implement Total equality
            }

            return false
        }

        // Tuple
        if (left is Tuple) {
            if (right is Tuple) {
                // TODO: Implement Tuple equality
            }

            return false
        }

        return false
    }

    private fun operandsEqual(left: FunctionRef, right: FunctionRef): Boolean {
        if (left.operand.size == right.operand.size) {
            for (i in left.operand.indices) {
                if (!expressionsEqual(left.operand[i], right.operand[i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    private fun operandsEqual(left: BinaryExpression, right: BinaryExpression): Boolean {
        if (left.operand.size == right.operand.size) {
            for (i in left.operand.indices) {
                if (!expressionsEqual(left.operand[i], right.operand[i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    private fun operandsEqual(left: TernaryExpression, right: TernaryExpression): Boolean {
        if (left.operand.size == right.operand.size) {
            for (i in left.operand.indices) {
                if (!expressionsEqual(left.operand[i], right.operand[i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    private fun operandsEqual(left: NaryExpression, right: NaryExpression): Boolean {
        if (left.operand.size == right.operand.size) {
            for (i in left.operand.indices) {
                if (!expressionsEqual(left.operand[i], right.operand[i])) {
                    return false
                }
            }

            return true
        }

        return false
    }

    private fun operatorExpressionsEqual(
        left: OperatorExpression?,
        right: OperatorExpression?
    ): Boolean {
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

        if (left::class != right::class) {
            return false
        }

        // Round
        if (left is Round) {
            if (right is Round) {
                return expressionsEqual(left.operand, right.operand) &&
                    expressionsEqual(left.precision, right.precision)
            }

            return false
        }

        // Combine
        if (left is Combine) {
            if (right is Combine) {
                return expressionsEqual(left.source, right.source) &&
                    expressionsEqual(left.separator, right.separator)
            }

            return false
        }

        // Split
        if (left is Split) {
            if (right is Split) {
                return expressionsEqual(left.stringToSplit, right.stringToSplit) &&
                    expressionsEqual(left.separator, right.separator)
            }

            return false
        }

        // SplitOnMatches
        if (left is SplitOnMatches) {
            if (right is SplitOnMatches) {
                return expressionsEqual(left.stringToSplit, right.stringToSplit) &&
                    expressionsEqual(left.separatorPattern, right.separatorPattern)
            }

            return false
        }

        // PositionOf
        if (left is PositionOf) {
            if (right is PositionOf) {
                return expressionsEqual(left.string, right.string) &&
                    expressionsEqual(left.pattern, right.pattern)
            }

            return false
        }

        // LastPositionOf
        if (left is LastPositionOf) {
            if (right is LastPositionOf) {
                return expressionsEqual(left.string, right.string) &&
                    expressionsEqual(left.pattern, right.pattern)
            }

            return false
        }

        // Substring
        if (left is Substring) {
            if (right is Substring) {
                return expressionsEqual(left.stringToSub, right.stringToSub) &&
                    expressionsEqual(left.startIndex, right.startIndex) &&
                    expressionsEqual(left.length, right.length)
            }

            return false
        }

        // TimeOfDay
        // Today
        // Now

        // Time
        if (left is Time) {
            if (right is Time) {
                return expressionsEqual(left.hour, right.hour) &&
                    expressionsEqual(left.minute, right.minute) &&
                    expressionsEqual(left.second, right.second) &&
                    expressionsEqual(left.millisecond, right.millisecond)
            }

            return false
        }

        // Date
        if (left is Date) {
            if (right is Date) {
                return expressionsEqual(left.year, right.year) &&
                    expressionsEqual(left.month, right.month) &&
                    expressionsEqual(left.day, right.day)
            }

            return false
        }

        // DateTime
        if (left is DateTime) {
            if (right is DateTime) {
                return expressionsEqual(left.year, right.year) &&
                    expressionsEqual(left.month, right.month) &&
                    expressionsEqual(left.day, right.day) &&
                    expressionsEqual(left.hour, right.hour) &&
                    expressionsEqual(left.minute, right.minute) &&
                    expressionsEqual(left.second, right.second) &&
                    expressionsEqual(left.millisecond, right.millisecond) &&
                    expressionsEqual(left.timezoneOffset, right.timezoneOffset)
            }

            return false
        }

        // First
        if (left is First) {
            if (right is First) {
                return expressionsEqual(left.source, right.source) &&
                    stringsEqual(left.orderBy, right.orderBy)
            }

            return false
        }

        // Last
        if (left is Last) {
            if (right is Last) {
                return expressionsEqual(left.source, right.source) &&
                    stringsEqual(left.orderBy, right.orderBy)
            }

            return false
        }

        // IndexOf
        if (left is IndexOf) {
            if (right is IndexOf) {
                return expressionsEqual(left.source, right.source) &&
                    expressionsEqual(left.element, right.element)
            }

            return false
        }

        // Slice
        if (left is Slice) {
            if (right is Slice) {
                return expressionsEqual(left.source, right.source) &&
                    expressionsEqual(left.startIndex, right.startIndex) &&
                    expressionsEqual(left.endIndex, right.endIndex)
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
                return expressionsEqual(left.source, right.source) &&
                    expressionsEqual(left.code, right.code) &&
                    expressionsEqual(left.condition, right.condition) &&
                    expressionsEqual(left.message, right.message) &&
                    expressionsEqual(left.severity, right.severity)
            }

            return false
        }

        // Generally speaking we would return false here, but because we've covered all the cases,
        // we return true
        return true
    }

    private fun operandsEqual(left: UnaryExpression, right: UnaryExpression): Boolean {
        return expressionsEqual(left.operand, right.operand)
    }

    private fun unaryExpressionsEqual(left: UnaryExpression?, right: UnaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left::class != right::class) {
            return false
        }

        if (!operandsEqual(left, right)) {
            return false
        }

        // Abs
        // As
        if (left is As && right is As) {
            return qnamesEqual(left.asType, right.asType) &&
                typeSpecifiersEqual(left.asTypeSpecifier, right.asTypeSpecifier) &&
                left.isStrict() == right.isStrict()
        }
        // Ceiling
        // CanConvert
        if (left is CanConvert) {
            if (right is CanConvert) {
                return qnamesEqual(left.toType, right.toType) &&
                    typeSpecifiersEqual(left.toTypeSpecifier, right.toTypeSpecifier)
            }

            return false
        }
        // Convert
        if (left is Convert) {
            if (right is Convert) {
                return qnamesEqual(left.toType, right.toType) &&
                    typeSpecifiersEqual(left.toTypeSpecifier, right.toTypeSpecifier)
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
                return qnamesEqual(left.isType, right.isType) &&
                    typeSpecifiersEqual(left.isTypeSpecifier, right.isTypeSpecifier)
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

    private fun binaryExpressionsEqual(left: BinaryExpression?, right: BinaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left::class != right::class) {
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

    private fun ternaryExpressionsEqual(
        left: TernaryExpression?,
        right: TernaryExpression?
    ): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left::class != right::class) {
            return false
        }

        if (!operandsEqual(left, right)) {
            return false
        }

        // ReplaceMatches
        return true
    }

    private fun naryExpressionsEqual(left: NaryExpression?, right: NaryExpression?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left::class != right::class) {
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

    private fun aggregateExpressionsEqual(
        left: AggregateExpression?,
        right: AggregateExpression?
    ): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left::class != right::class) {
            return false
        }

        if (!expressionsEqual(left.source, right.source) || !stringsEqual(left.path, right.path)) {
            return false
        }

        // Aggregate
        if (left is Aggregate && right is Aggregate) {
            return expressionsEqual(left.initialValue, right.initialValue) &&
                expressionsEqual(left.iteration, right.iteration)
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
