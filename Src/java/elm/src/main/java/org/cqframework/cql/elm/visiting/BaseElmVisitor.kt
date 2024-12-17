package org.cqframework.cql.elm.visiting

import org.cqframework.cql.elm.tracking.Trackable
import org.hl7.elm.r1.Abs
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.Add
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Aggregate
import org.hl7.elm.r1.AggregateClause
import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.AllTrue
import org.hl7.elm.r1.And
import org.hl7.elm.r1.AnyTrue
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Avg
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.ByDirection
import org.hl7.elm.r1.ByExpression
import org.hl7.elm.r1.CanConvert
import org.hl7.elm.r1.CanConvertQuantity
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Ceiling
import org.hl7.elm.r1.Children
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Collapse
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.Convert
import org.hl7.elm.r1.ConvertQuantity
import org.hl7.elm.r1.ConvertsToBoolean
import org.hl7.elm.r1.ConvertsToDate
import org.hl7.elm.r1.ConvertsToDateTime
import org.hl7.elm.r1.ConvertsToDecimal
import org.hl7.elm.r1.ConvertsToInteger
import org.hl7.elm.r1.ConvertsToLong
import org.hl7.elm.r1.ConvertsToQuantity
import org.hl7.elm.r1.ConvertsToRatio
import org.hl7.elm.r1.ConvertsToString
import org.hl7.elm.r1.ConvertsToTime
import org.hl7.elm.r1.Count
import org.hl7.elm.r1.Current
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateFrom
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.DateTimeComponentFrom
import org.hl7.elm.r1.Descendents
import org.hl7.elm.r1.DifferenceBetween
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.Divide
import org.hl7.elm.r1.DurationBetween
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Ends
import org.hl7.elm.r1.EndsWith
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Except
import org.hl7.elm.r1.Exists
import org.hl7.elm.r1.Exp
import org.hl7.elm.r1.Expand
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.Filter
import org.hl7.elm.r1.First
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.Floor
import org.hl7.elm.r1.ForEach
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.GeometricMean
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.HighBoundary
import org.hl7.elm.r1.IdentifierRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.Implies
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Includes
import org.hl7.elm.r1.IndexOf
import org.hl7.elm.r1.Indexer
import org.hl7.elm.r1.Instance
import org.hl7.elm.r1.InstanceElement
import org.hl7.elm.r1.Intersect
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.IsFalse
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.IsTrue
import org.hl7.elm.r1.Iteration
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.LastPositionOf
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.Less
import org.hl7.elm.r1.LessOrEqual
import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.List
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Ln
import org.hl7.elm.r1.Log
import org.hl7.elm.r1.LowBoundary
import org.hl7.elm.r1.Lower
import org.hl7.elm.r1.Matches
import org.hl7.elm.r1.Max
import org.hl7.elm.r1.MaxValue
import org.hl7.elm.r1.Median
import org.hl7.elm.r1.Meets
import org.hl7.elm.r1.MeetsAfter
import org.hl7.elm.r1.MeetsBefore
import org.hl7.elm.r1.Message
import org.hl7.elm.r1.Min
import org.hl7.elm.r1.MinValue
import org.hl7.elm.r1.Mode
import org.hl7.elm.r1.Modulo
import org.hl7.elm.r1.Multiply
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.NaryExpression
import org.hl7.elm.r1.Negate
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.NotEqual
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.OperatorExpression
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.Overlaps
import org.hl7.elm.r1.OverlapsAfter
import org.hl7.elm.r1.OverlapsBefore
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.ParameterTypeSpecifier
import org.hl7.elm.r1.PointFrom
import org.hl7.elm.r1.PopulationStdDev
import org.hl7.elm.r1.PopulationVariance
import org.hl7.elm.r1.PositionOf
import org.hl7.elm.r1.Power
import org.hl7.elm.r1.Precision
import org.hl7.elm.r1.Predecessor
import org.hl7.elm.r1.Product
import org.hl7.elm.r1.ProperContains
import org.hl7.elm.r1.ProperIn
import org.hl7.elm.r1.ProperIncludedIn
import org.hl7.elm.r1.ProperIncludes
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.RelationshipClause
import org.hl7.elm.r1.Repeat
import org.hl7.elm.r1.ReplaceMatches
import org.hl7.elm.r1.ReturnClause
import org.hl7.elm.r1.Round
import org.hl7.elm.r1.SameAs
import org.hl7.elm.r1.SameOrAfter
import org.hl7.elm.r1.SameOrBefore
import org.hl7.elm.r1.SingletonFrom
import org.hl7.elm.r1.Size
import org.hl7.elm.r1.Slice
import org.hl7.elm.r1.Sort
import org.hl7.elm.r1.SortByItem
import org.hl7.elm.r1.SortClause
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.Starts
import org.hl7.elm.r1.StartsWith
import org.hl7.elm.r1.StdDev
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.Subtract
import org.hl7.elm.r1.Successor
import org.hl7.elm.r1.Sum
import org.hl7.elm.r1.TernaryExpression
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.TimeFrom
import org.hl7.elm.r1.TimeOfDay
import org.hl7.elm.r1.Times
import org.hl7.elm.r1.TimezoneFrom
import org.hl7.elm.r1.TimezoneOffsetFrom
import org.hl7.elm.r1.ToBoolean
import org.hl7.elm.r1.ToChars
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToDate
import org.hl7.elm.r1.ToDateTime
import org.hl7.elm.r1.ToDecimal
import org.hl7.elm.r1.ToInteger
import org.hl7.elm.r1.ToList
import org.hl7.elm.r1.ToLong
import org.hl7.elm.r1.ToQuantity
import org.hl7.elm.r1.ToRatio
import org.hl7.elm.r1.ToString
import org.hl7.elm.r1.ToTime
import org.hl7.elm.r1.Today
import org.hl7.elm.r1.Total
import org.hl7.elm.r1.Truncate
import org.hl7.elm.r1.TruncatedDivide
import org.hl7.elm.r1.Tuple
import org.hl7.elm.r1.TupleElement
import org.hl7.elm.r1.TupleElementDefinition
import org.hl7.elm.r1.TupleTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier
import org.hl7.elm.r1.UnaryExpression
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.Upper
import org.hl7.elm.r1.Variance
import org.hl7.elm.r1.Width
import org.hl7.elm.r1.With
import org.hl7.elm.r1.Without
import org.hl7.elm.r1.Xor

/*
Design notes:
There are two types of methods in this class:

 1. visitFields

 visitFields visits the fields of an object, traversing up the class hierarchy to visit superclass fields.

 2. visitXYZ, where XYZ is the name of an ELM class

 The visitXYZ methods come in two flavors:

 A. visits on abstract or base classes forward to the correct visit method for the concrete or derived class.
 B. visits on concrete or derived classes visit the fields of its base class using visitFields,
    and then visits the fields itself.

 TypeSpecifiers are considered to be terminal nodes in the ELM graph. TypeSpecifiers themselves are Elements and thus
 have recursive TypeSpecifiers, but these are not visited.
*/
/**
 * Provides the base implementation for an ElmVisitor.
 *
 * @param <T> The return type of the visit operation. Use [Void] for no return value.
 * @param <C> The type of context passed to each visit method operations with no return type.
 *   </C></T>
 */
@Suppress("LargeClass", "LongMethod", "CyclomaticComplexMethod", "TooManyFunctions")
abstract class BaseElmVisitor<T, C> : ElmVisitor<T, C> {
    /**
     * Provides the default result of a visit
     *
     * @return
     */
    protected open fun defaultResult(elm: Trackable?, context: C): T? {
        return null
    }

    /**
     * Provides for aggregation behavior of the results of a visit. Default behavior returns the
     * next result, ignoring the current aggregate.
     *
     * @param aggregate Current aggregate result
     * @param nextResult Next result to be aggregated
     * @return The result of aggregating the nextResult into aggregate
     */
    protected open fun aggregateResult(aggregate: T?, nextResult: T?): T? {
        return nextResult
    }

    /**
     * Visit an Element in an ELM tree. This method will be called for every node in the tree that
     * is a descendant of the Element type.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitElement(elm: Element, context: C): T? {
        return when (elm) {
            is Expression -> visitExpression(elm, context)
            is CaseItem -> visitCaseItem(elm, context)
            is LetClause -> visitLetClause(elm, context)
            is OperandDef -> visitOperandDef(elm, context)
            is ParameterDef -> visitParameterDef(elm, context)
            is SortByItem -> visitSortByItem(elm, context)
            is SortClause -> visitSortClause(elm, context)
            is TupleElementDefinition -> visitTupleElementDefinition(elm, context)
            is TypeSpecifier -> visitTypeSpecifier(elm, context)
            else -> throw IllegalArgumentException("Unknown Element type: " + elm.javaClass.name)
        }
    }

    /**
     * Visit a TypeSpecifier. This method will be called for every node in the tree that is a
     * descendant of the TypeSpecifier type.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTypeSpecifier(elm: TypeSpecifier, context: C): T? {
        return when (elm) {
            is NamedTypeSpecifier -> visitNamedTypeSpecifier(elm, context)
            is IntervalTypeSpecifier -> visitIntervalTypeSpecifier(elm, context)
            is ListTypeSpecifier -> visitListTypeSpecifier(elm, context)
            is TupleTypeSpecifier -> visitTupleTypeSpecifier(elm, context)
            is ChoiceTypeSpecifier -> visitChoiceTypeSpecifier(elm, context)
            is ParameterTypeSpecifier -> visitParameterTypeSpecifier(elm, context)
            else ->
                throw IllegalArgumentException("Unknown TypeSpecifier type: " + elm.javaClass.name)
        }
    }

    /**
     * Visit a ParameterTypeSpecifier. This method will be called for every node in the tree that is
     * a ParameterTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitParameterTypeSpecifier(elm: ParameterTypeSpecifier?, context: C): T? {
        return defaultResult(elm, context)
    }

    /**
     * Visit a NamedTypeSpecifier. This method will be called for every node in the tree that is a
     * NamedTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNamedTypeSpecifier(elm: NamedTypeSpecifier, context: C): T? {
        return defaultResult(elm, context)
    }

    /**
     * Visit an IntervalTypeSpecifier. This method will be called for every node in the tree that is
     * an IntervalTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIntervalTypeSpecifier(elm: IntervalTypeSpecifier, context: C): T? {
        var result = defaultResult(elm, context)

        if (elm.pointType != null) {
            val childResult = visitTypeSpecifier(elm.pointType!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ListTypeSpecifier. This method will be called for every node in the tree that is a
     * ListTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitListTypeSpecifier(elm: ListTypeSpecifier, context: C): T? {
        var result = defaultResult(elm, context)

        if (elm.elementType != null) {
            val childResult = visitTypeSpecifier(elm.elementType!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a TupleElementDefinition. This method will be called for every node in the tree that is
     * a TupleElementDefinition.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTupleElementDefinition(elm: TupleElementDefinition, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.elementType != null) {
            val childResult = visitTypeSpecifier(elm.elementType!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.type != null) {
            val childResult = visitTypeSpecifier(elm.type!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a TupleTypeSpecifier. This method will be called for every node in the tree that is a
     * TupleTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTupleTypeSpecifier(elm: TupleTypeSpecifier, context: C): T? {
        var result = defaultResult(elm, context)

        for (element in elm.element!!) {
            val childResult = visitTupleElementDefinition(element!!, context)
            result = aggregateResult(result, childResult)
        }
        return result
    }

    /**
     * Visit a ChoiceTypeSpecifier. This method will be called for every node in the tree that is a
     * ChoiceTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitChoiceTypeSpecifier(elm: ChoiceTypeSpecifier, context: C): T? {
        var result = defaultResult(elm, context)

        for (choice in elm.choice!!) {
            val childResult = visitTypeSpecifier(choice!!, context)
            result = aggregateResult(result, childResult)
        }

        for (type in elm.type!!) {
            val childResult = visitTypeSpecifier(type!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an Expression. This method will be called for every node in the tree that is an
     * Expression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExpression(elm: Expression, context: C): T? {
        return when (elm) {
            is AliasRef -> visitAliasRef(elm, context)
            is Case -> visitCase(elm, context)
            is Current -> visitCurrent(elm, context)
            is ExpressionRef -> visitExpressionRef(elm, context)
            is Filter -> visitFilter(elm, context)
            is ForEach -> visitForEach(elm, context)
            is IdentifierRef -> visitIdentifierRef(elm, context)
            is If -> visitIf(elm, context)
            is Instance -> visitInstance(elm, context)
            is Interval -> visitInterval(elm, context)
            is Iteration -> visitIteration(elm, context)
            is List -> visitList(elm, context)
            is Literal -> visitLiteral(elm, context)
            is MaxValue -> visitMaxValue(elm, context)
            is MinValue -> visitMinValue(elm, context)
            is Null -> visitNull(elm, context)
            is OperandRef -> visitOperandRef(elm, context)
            is ParameterRef -> visitParameterRef(elm, context)
            is Property -> visitProperty(elm, context)
            is Query -> visitQuery(elm, context)
            is QueryLetRef -> visitQueryLetRef(elm, context)
            is Repeat -> visitRepeat(elm, context)
            is Sort -> visitSort(elm, context)
            is Total -> visitTotal(elm, context)
            is Tuple -> visitTuple(elm, context)
            is AggregateExpression -> visitAggregateExpression(elm, context)
            is OperatorExpression -> visitOperatorExpression(elm, context)
            else -> throw IllegalArgumentException("Unknown Expression type: " + elm.javaClass.name)
        }
    }

    /**
     * Visit an OperatorExpression. This method will be called for every node in the tree that is an
     * OperatorExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOperatorExpression(elm: OperatorExpression, context: C): T? {
        return when (elm) {
            is Round -> visitRound(elm, context)
            is Combine -> visitCombine(elm, context)
            is Split -> visitSplit(elm, context)
            is SplitOnMatches -> visitSplitOnMatches(elm, context)
            is PositionOf -> visitPositionOf(elm, context)
            is LastPositionOf -> visitLastPositionOf(elm, context)
            is Substring -> visitSubstring(elm, context)
            is TimeOfDay -> visitTimeOfDay(elm, context)
            is Today -> visitToday(elm, context)
            is Now -> visitNow(elm, context)
            is Time -> visitTime(elm, context)
            is Date -> visitDate(elm, context)
            is DateTime -> visitDateTime(elm, context)
            is First -> visitFirst(elm, context)
            is Last -> visitLast(elm, context)
            is IndexOf -> visitIndexOf(elm, context)
            is Slice -> visitSlice(elm, context)
            is Children -> visitChildren(elm, context)
            is Descendents -> visitDescendents(elm, context)
            is Message -> visitMessage(elm, context)
            is UnaryExpression -> visitUnaryExpression(elm, context)
            is BinaryExpression -> visitBinaryExpression(elm, context)
            is TernaryExpression -> visitTernaryExpression(elm, context)
            is NaryExpression -> visitNaryExpression(elm, context)
            else ->
                throw IllegalArgumentException(
                    "Unknown OperatorExpression type: " + elm.javaClass.name
                )
        }
    }

    /**
     * Visit an UnaryExpression. This method will be called for every node in the tree that is an
     * UnaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitUnaryExpression(elm: UnaryExpression, context: C): T? {
        return when (elm) {
            is Abs -> visitAbs(elm, context)
            is As -> visitAs(elm, context)
            is Ceiling -> visitCeiling(elm, context)
            is CanConvert -> visitCanConvert(elm, context)
            is Convert -> visitConvert(elm, context)
            is ConvertsToBoolean -> visitConvertsToBoolean(elm, context)
            is ConvertsToDate -> visitConvertsToDate(elm, context)
            is ConvertsToDateTime -> visitConvertsToDateTime(elm, context)
            is ConvertsToDecimal -> visitConvertsToDecimal(elm, context)
            is ConvertsToInteger -> visitConvertsToInteger(elm, context)
            is ConvertsToLong -> visitConvertsToLong(elm, context)
            is ConvertsToQuantity -> visitConvertsToQuantity(elm, context)
            is ConvertsToRatio -> visitConvertsToRatio(elm, context)
            is ConvertsToString -> visitConvertsToString(elm, context)
            is ConvertsToTime -> visitConvertsToTime(elm, context)
            is DateFrom -> visitDateFrom(elm, context)
            is DateTimeComponentFrom -> visitDateTimeComponentFrom(elm, context)
            is Distinct -> visitDistinct(elm, context)
            is End -> visitEnd(elm, context)
            is Exists -> visitExists(elm, context)
            is Exp -> visitExp(elm, context)
            is Flatten -> visitFlatten(elm, context)
            is Floor -> visitFloor(elm, context)
            is Is -> visitIs(elm, context)
            is IsFalse -> visitIsFalse(elm, context)
            is IsNull -> visitIsNull(elm, context)
            is IsTrue -> visitIsTrue(elm, context)
            is Length -> visitLength(elm, context)
            is Ln -> visitLn(elm, context)
            is Lower -> visitLower(elm, context)
            is Negate -> visitNegate(elm, context)
            is Not -> visitNot(elm, context)
            is PointFrom -> visitPointFrom(elm, context)
            is Precision -> visitPrecision(elm, context)
            is Predecessor -> visitPredecessor(elm, context)
            is SingletonFrom -> visitSingletonFrom(elm, context)
            is Size -> visitSize(elm, context)
            is Start -> visitStart(elm, context)
            is Successor -> visitSuccessor(elm, context)
            is TimeFrom -> visitTimeFrom(elm, context)
            is TimezoneFrom -> visitTimezoneFrom(elm, context)
            is TimezoneOffsetFrom -> visitTimezoneOffsetFrom(elm, context)
            is ToBoolean -> visitToBoolean(elm, context)
            is ToConcept -> visitToConcept(elm, context)
            is ToChars -> visitToChars(elm, context)
            is ToDate -> visitToDate(elm, context)
            is ToDateTime -> visitToDateTime(elm, context)
            is ToDecimal -> visitToDecimal(elm, context)
            is ToInteger -> visitToInteger(elm, context)
            is ToLong -> visitToLong(elm, context)
            is ToList -> visitToList(elm, context)
            is ToQuantity -> visitToQuantity(elm, context)
            is ToRatio -> visitToRatio(elm, context)
            is ToString -> visitToString(elm, context)
            is ToTime -> visitToTime(elm, context)
            is Truncate -> visitTruncate(elm, context)
            is Upper -> visitUpper(elm, context)
            is Width -> visitWidth(elm, context)
            else ->
                throw IllegalArgumentException(
                    "Unknown UnaryExpression type: " + elm.javaClass.name
                )
        }
    }

    /**
     * Visit a BinaryExpression. This method will be called for every node in the tree that is a
     * BinaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitBinaryExpression(elm: BinaryExpression, context: C): T? {
        return when (elm) {
            is Add -> visitAdd(elm, context)
            is After -> visitAfter(elm, context)
            is And -> visitAnd(elm, context)
            is Before -> visitBefore(elm, context)
            is CanConvertQuantity -> visitCanConvertQuantity(elm, context)
            is Contains -> visitContains(elm, context)
            is ConvertQuantity -> visitConvertQuantity(elm, context)
            is Collapse -> visitCollapse(elm, context)
            is DifferenceBetween -> visitDifferenceBetween(elm, context)
            is Divide -> visitDivide(elm, context)
            is DurationBetween -> visitDurationBetween(elm, context)
            is Ends -> visitEnds(elm, context)
            is EndsWith -> visitEndsWith(elm, context)
            is Equal -> visitEqual(elm, context)
            is Equivalent -> visitEquivalent(elm, context)
            is Expand -> visitExpand(elm, context)
            is Greater -> visitGreater(elm, context)
            is GreaterOrEqual -> visitGreaterOrEqual(elm, context)
            is HighBoundary -> visitHighBoundary(elm, context)
            is Implies -> visitImplies(elm, context)
            is In -> visitIn(elm, context)
            is IncludedIn -> visitIncludedIn(elm, context)
            is Includes -> visitIncludes(elm, context)
            is Indexer -> visitIndexer(elm, context)
            is Less -> visitLess(elm, context)
            is LessOrEqual -> visitLessOrEqual(elm, context)
            is Log -> visitLog(elm, context)
            is LowBoundary -> visitLowBoundary(elm, context)
            is Matches -> visitMatches(elm, context)
            is Meets -> visitMeets(elm, context)
            is MeetsAfter -> visitMeetsAfter(elm, context)
            is MeetsBefore -> visitMeetsBefore(elm, context)
            is Modulo -> visitModulo(elm, context)
            is Multiply -> visitMultiply(elm, context)
            is NotEqual -> visitNotEqual(elm, context)
            is Or -> visitOr(elm, context)
            is Overlaps -> visitOverlaps(elm, context)
            is OverlapsAfter -> visitOverlapsAfter(elm, context)
            is OverlapsBefore -> visitOverlapsBefore(elm, context)
            is Power -> visitPower(elm, context)
            is ProperContains -> visitProperContains(elm, context)
            is ProperIn -> visitProperIn(elm, context)
            is ProperIncludedIn -> visitProperIncludedIn(elm, context)
            is ProperIncludes -> visitProperIncludes(elm, context)
            is SameAs -> visitSameAs(elm, context)
            is SameOrAfter -> visitSameOrAfter(elm, context)
            is SameOrBefore -> visitSameOrBefore(elm, context)
            is Starts -> visitStarts(elm, context)
            is StartsWith -> visitStartsWith(elm, context)
            is Subtract -> visitSubtract(elm, context)
            is Times -> visitTimes(elm, context)
            is TruncatedDivide -> visitTruncatedDivide(elm, context)
            is Xor -> visitXor(elm, context)
            else ->
                throw IllegalArgumentException(
                    "Unknown BinaryExpression type: " + elm.javaClass.name
                )
        }
    }

    /**
     * Visit a TernaryExpression. This method will be called for every node in the tree that is a
     * TernaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTernaryExpression(elm: TernaryExpression, context: C): T? {
        if (elm is ReplaceMatches) return visitReplaceMatches(elm, context)
        else throw IllegalArgumentException("Unknown TernaryExpression type: " + elm.javaClass.name)
    }

    /**
     * Visit a NaryExpression. This method will be called for every node in the tree that is a
     * NaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNaryExpression(elm: NaryExpression, context: C): T? {
        return when (elm) {
            is Coalesce -> visitCoalesce(elm, context)
            is Concatenate -> visitConcatenate(elm, context)
            is Except -> visitExcept(elm, context)
            is Intersect -> visitIntersect(elm, context)
            is Union -> visitUnion(elm, context)
            else ->
                throw IllegalArgumentException("Unknown NaryExpression type: " + elm.javaClass.name)
        }
    }

    /**
     * Visit an ExpressionDef. This method will be called for every node in the tree that is an
     * ExpressionDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExpressionDef(elm: ExpressionDef, context: C): T? {
        if (elm is FunctionDef) {
            return visitFunctionDef(elm, context)
        }

        return visitFields(elm, context)
    }

    /**
     * Visit a FunctionDef. This method will be called for every node in the tree that is a
     * FunctionDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitFunctionDef(elm: FunctionDef, context: C): T? {
        var result = visitFields(elm, context)

        for (operand in elm.operand!!) {
            val childResult = visitOperandDef(operand!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit AccessModifier. This method will be called for every node in the tree that is an
     * AccessModifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Suppress("UnusedParameter")
    fun visitAccessModifier(elm: AccessModifier?, context: C): T? {
        // NOTE: AccessModifier isn't trackable?
        return defaultResult(null, context)
    }

    /**
     * Visit an ExpressionRef. This method will be called for every node in the tree that is an
     * ExpressionRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExpressionRef(elm: ExpressionRef, context: C): T? {
        if (elm is FunctionRef) {
            return visitFunctionRef(elm, context)
        }

        return visitFields(elm, context)
    }

    /**
     * Visit a FunctionRef. This method will be called for every node in the tree that is a
     * FunctionRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitFunctionRef(elm: FunctionRef, context: C): T? {
        var result = visitFields(elm, context)

        for (element in elm.operand!!) {
            val childResult = visitExpression(element!!, context)
            result = aggregateResult(result, childResult)
        }

        for (s in elm.signature!!) {
            val childResult = visitTypeSpecifier(s!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ParameterDef. This method will be called for every node in the tree that is a
     * ParameterDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitParameterDef(elm: ParameterDef, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.parameterTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.parameterTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.default != null) {
            val childResult = visitExpression(elm.default!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ParameterRef. This method will be called for every node in the tree that is a
     * ParameterRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitParameterRef(elm: ParameterRef, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an OperandDef. This method will be called for every node in the tree that is an
     * OperandDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOperandDef(elm: OperandDef, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.operandTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.operandTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an OperandRef. This method will be called for every node in the tree that is an
     * OperandRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOperandRef(elm: OperandRef, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an IdentifierRef. This method will be called for every node in the tree that is an
     * IdentifierRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIdentifierRef(elm: IdentifierRef, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Literal. This method will be called for every node in the tree that is a Literal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLiteral(elm: Literal, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a TupleElement. This method will be called for every node in the tree that is a
     * TupleElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTupleElement(elm: TupleElement, context: C): T? {
        var result = defaultResult(elm, context)

        if (elm.value != null) {
            val childResult = visitExpression(elm.value!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Tuple. This method will be called for every node in the tree that is a Tuple.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTuple(elm: Tuple, context: C): T? {
        var result = visitFields(elm, context)

        for (element in elm.element!!) {
            val childResult = visitTupleElement(element!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an InstanceElement. This method will be called for every node in the tree that is an
     * InstanceElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitInstanceElement(elm: InstanceElement, context: C): T? {
        var result = defaultResult(elm, context)

        if (elm.value != null) {
            val childResult = visitExpression(elm.value!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an Instance. This method will be called for every node in the tree that is an Instance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitInstance(elm: Instance, context: C): T? {
        var result = visitFields(elm, context)

        for (element in elm.element!!) {
            val childResult = visitInstanceElement(element!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an Interval. This method will be called for every node in the tree that is an Interval.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitInterval(elm: Interval, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.low != null) {
            val childResult = visitExpression(elm.low!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.lowClosedExpression != null) {
            val childResult = visitExpression(elm.lowClosedExpression!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.high != null) {
            val childResult = visitExpression(elm.high!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.highClosedExpression != null) {
            val childResult = visitExpression(elm.highClosedExpression!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a List. This method will be called for every node in the tree that is a List.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitList(elm: List, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.typeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.typeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        for (element in elm.element!!) {
            val childResult = visitExpression(element!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an And. This method will be called for every node in the tree that is an And.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAnd(elm: And, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Or. This method will be called for every node in the tree that is an Or.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOr(elm: Or, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Xor. This method will be called for every node in the tree that is a Xor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitXor(elm: Xor, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Implies. This method will be called for every node in the tree that is an Implies.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitImplies(elm: Implies, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Not. This method will be called for every node in the tree that is a Not.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNot(elm: Not, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an If. This method will be called for every node in the tree that is an If.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIf(elm: If, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.condition != null) {
            val childResult = visitExpression(elm.condition!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.then != null) {
            val childResult = visitExpression(elm.then!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.`else` != null) {
            val childResult = visitExpression(elm.`else`!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a CaseItem. This method will be called for every node in the tree that is a CaseItem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCaseItem(elm: CaseItem, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.`when` != null) {
            val childResult = visitExpression(elm.`when`!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.then != null) {
            val childResult = visitExpression(elm.then!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Case. This method will be called for every node in the tree that is a Case.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCase(elm: Case, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.comparand != null) {
            val childResult = visitExpression(elm.comparand!!, context)
            result = aggregateResult(result, childResult)
        }

        for (ci in elm.caseItem!!) {
            val childResult = visitCaseItem(ci!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.`else` != null) {
            val childResult = visitExpression(elm.`else`!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Null. This method will be called for every node in the tree that is a Null.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNull(elm: Null, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an IsNull. This method will be called for every node in the tree that is an IsNull.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIsNull(elm: IsNull, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an IsTrue. This method will be called for every node in the tree that is an IsTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIsTrue(elm: IsTrue, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an IsFalse. This method will be called for every node in the tree that is an IsFalse.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIsFalse(elm: IsFalse, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Coalesce. This method will be called for every node in the tree that is a Coalesce.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCoalesce(elm: Coalesce, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Is. This method will be called for every node in the tree that is an Is.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIs(elm: Is, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.isTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.isTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an As. This method will be called for every node in the tree that is an As.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAs(elm: As, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.asTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.asTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Convert. This method will be called for every node in the tree that is a Convert.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvert(elm: Convert, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.toTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.toTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a CanConvert. This method will be called for every node in the tree that is a
     * CanConvert.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCanConvert(elm: CanConvert, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.toTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.toTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ConvertsToBoolean. This method will be called for every node in the tree that is a
     * ConvertsToBoolean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToBoolean(elm: ConvertsToBoolean, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToBoolean. This method will be called for every node in the tree that is a ToBoolean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToBoolean(elm: ToBoolean, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToChars. This method will be called for every node in the tree that is a ToChars.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToChars(elm: ToChars, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToConcept. This method will be called for every node in the tree that is a ToConcept.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToConcept(elm: ToConcept, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToDate. This method will be called for every node in the tree that is a
     * ConvertsToDate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToDate(elm: ConvertsToDate, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToDate. This method will be called for every node in the tree that is a ToDate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToDate(elm: ToDate, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToDateTime. This method will be called for every node in the tree that is a
     * ConvertsToDateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToDateTime(elm: ConvertsToDateTime, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToDateTime. This method will be called for every node in the tree that is a
     * ToDateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToDateTime(elm: ToDateTime, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToLong. This method will be called for every node in the tree that is a
     * ConvertsToLong.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToLong(elm: ConvertsToLong, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToLong. This method will be called for every node in the tree that is a ToLong.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToLong(elm: ToLong, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToDecimal. This method will be called for every node in the tree that is a
     * ConvertsToDecimal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToDecimal(elm: ConvertsToDecimal, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToDecimal. This method will be called for every node in the tree that is a ToDecimal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToDecimal(elm: ToDecimal, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToInteger. This method will be called for every node in the tree that is a
     * ConvertsToInteger.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToInteger(elm: ConvertsToInteger, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToInteger. This method will be called for every node in the tree that is a ToInteger.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToInteger(elm: ToInteger, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToList. This method will be called for every node in the tree that is a ToList.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToList(elm: ToList, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertQuantity. This method will be called for every node in the tree that is a
     * ConvertQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertQuantity(elm: ConvertQuantity, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a CanConvertQuantity. This method will be called for every node in the tree that is a
     * CanConvertQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCanConvertQuantity(elm: CanConvertQuantity, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToQuantity. This method will be called for every node in the tree that is a
     * ConvertsToQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToQuantity(elm: ConvertsToQuantity, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToQuantity. This method will be called for every node in the tree that is a
     * ToQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToQuantity(elm: ToQuantity, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToRatio. This method will be called for every node in the tree that is a
     * ConvertsToRatio.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToRatio(elm: ConvertsToRatio, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Ratio. This method will be called for every node in the tree that is a Ratio.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToRatio(elm: ToRatio, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToString. This method will be called for every node in the tree that is a
     * ConvertsToString.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToString(elm: ConvertsToString, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToString. This method will be called for every node in the tree that is a ToString.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToString(elm: ToString, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConvertsToTime. This method will be called for every node in the tree that is a
     * ConvertsToTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConvertsToTime(elm: ConvertsToTime, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ToTime. This method will be called for every node in the tree that is a ToTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToTime(elm: ToTime, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Equal. This method will be called for every node in the tree that is an Equal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitEqual(elm: Equal, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Equivalent. This method will be called for every node in the tree that is an
     * Equivalent.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitEquivalent(elm: Equivalent, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a NotEqual. This method will be called for every node in the tree that is a NotEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNotEqual(elm: NotEqual, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Less. This method will be called for every node in the tree that is a Less.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLess(elm: Less, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Greater. This method will be called for every node in the tree that is a Greater.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitGreater(elm: Greater, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a LessOrEqual. This method will be called for every node in the tree that is a
     * LessOrEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLessOrEqual(elm: LessOrEqual, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a GreaterOrEqual. This method will be called for every node in the tree that is a
     * GreaterOrEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitGreaterOrEqual(elm: GreaterOrEqual, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Add. This method will be called for every node in the tree that is an Add.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAdd(elm: Add, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Subtract. This method will be called for every node in the tree that is a Subtract.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSubtract(elm: Subtract, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Multiply. This method will be called for every node in the tree that is a Multiply.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMultiply(elm: Multiply, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Divide. This method will be called for every node in the tree that is a Divide.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDivide(elm: Divide, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a TruncatedDivide. This method will be called for every node in the tree that is a
     * TruncatedDivide.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTruncatedDivide(elm: TruncatedDivide, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Modulo. This method will be called for every node in the tree that is a Modulo.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitModulo(elm: Modulo, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Ceiling. This method will be called for every node in the tree that is a Ceiling.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCeiling(elm: Ceiling, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Floor. This method will be called for every node in the tree that is a Floor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitFloor(elm: Floor, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Truncate. This method will be called for every node in the tree that is a Truncate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTruncate(elm: Truncate, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Abs. This method will be called for every node in the tree that is an Abs.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAbs(elm: Abs, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Negate. This method will be called for every node in the tree that is a Negate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNegate(elm: Negate, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Round. This method will be called for every node in the tree that is a Round.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitRound(elm: Round, context: C): T? {
        var result = visitFields(elm, context)
        if (elm.operand != null) {
            val childResult = visitExpression(elm.operand!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.precision != null) {
            val childResult = visitExpression(elm.precision!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Ln. This method will be called for every node in the tree that is a Ln.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLn(elm: Ln, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Exp. This method will be called for every node in the tree that is an Exp.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExp(elm: Exp, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Log. This method will be called for every node in the tree that is a Log.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLog(elm: Log, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Power. This method will be called for every node in the tree that is a Power.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPower(elm: Power, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Successor. This method will be called for every node in the tree that is a Successor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSuccessor(elm: Successor, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Predecessor. This method will be called for every node in the tree that is a
     * Predecessor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPredecessor(elm: Predecessor, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a MinValue. This method will be called for every node in the tree that is a MinValue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMinValue(elm: MinValue, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a MaxValue. This method will be called for every node in the tree that is a MaxValue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMaxValue(elm: MaxValue, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Precision. This method will be called for every node in the tree that is a Precision.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPrecision(elm: Precision, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a LowBoundary. This method will be called for every node in the tree that is a
     * LowBoundary.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLowBoundary(elm: LowBoundary, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a HighBoundary. This method will be called for every node in the tree that is a
     * HighBoundary.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitHighBoundary(elm: HighBoundary, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Concatenate. This method will be called for every node in the tree that is a
     * Concatenate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConcatenate(elm: Concatenate, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Combine. This method will be called for every node in the tree that is a Combine.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCombine(elm: Combine, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.separator != null) {
            val childResult = visitExpression(elm.separator!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Split. This method will be called for every node in the tree that is a Split.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSplit(elm: Split, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.stringToSplit != null) {
            val childResult = visitExpression(elm.stringToSplit!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.separator != null) {
            val childResult = visitExpression(elm.separator!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a SplitOnMatches. This method will be called for every node in the tree that is a
     * SplitOnMatches.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSplitOnMatches(elm: SplitOnMatches, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.stringToSplit != null) {
            val childResult = visitExpression(elm.stringToSplit!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.separatorPattern != null) {
            val childResult = visitExpression(elm.separatorPattern!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Length. This method will be called for every node in the tree that is a Length.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLength(elm: Length, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Upper. This method will be called for every node in the tree that is an Upper.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitUpper(elm: Upper, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Lower. This method will be called for every node in the tree that is a Lower.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLower(elm: Lower, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Indexer. This method will be called for every node in the tree that is an Indexer.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIndexer(elm: Indexer, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a PositionOf. This method will be called for every node in the tree that is a
     * PositionOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPositionOf(elm: PositionOf, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.pattern != null) {
            val childResult = visitExpression(elm.pattern!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.string != null) {
            val childResult = visitExpression(elm.string!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a LastPositionOf. This method will be called for every node in the tree that is a
     * LastPositionOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLastPositionOf(elm: LastPositionOf, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.pattern != null) {
            val childResult = visitExpression(elm.pattern!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.string != null) {
            val childResult = visitExpression(elm.string!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Substring. This method will be called for every node in the tree that is a Substring.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSubstring(elm: Substring, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.stringToSub != null) {
            val childResult = visitExpression(elm.stringToSub!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.startIndex != null) {
            val childResult = visitExpression(elm.startIndex!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.length != null) {
            val childResult = visitExpression(elm.length!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a StartsWith. This method will be called for every node in the tree that is a
     * StartsWith.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitStartsWith(elm: StartsWith, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an EndsWith. This method will be called for every node in the tree that is an EndsWith.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitEndsWith(elm: EndsWith, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Matches. This method will be called for every node in the tree that is a Matches.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMatches(elm: Matches, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ReplaceMatches. This method will be called for every node in the tree that is a
     * ReplaceMatches.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitReplaceMatches(elm: ReplaceMatches, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a DurationBetween. This method will be called for every node in the tree that is a
     * DurationBetween.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDurationBetween(elm: DurationBetween, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a DifferenceBetween. This method will be called for every node in the tree that is a
     * DifferenceBetween.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDifferenceBetween(elm: DifferenceBetween, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a DateFrom. This method will be called for every node in the tree that is a DateFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDateFrom(elm: DateFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a TimeFrom. This method will be called for every node in the tree that is a TimeFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTimeFrom(elm: TimeFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a TimezoneFrom. This method will be called for every node in the tree that is a
     * TimezoneFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTimezoneFrom(elm: TimezoneFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a TimezoneOffsetFrom. This method will be called for every node in the tree that is a
     * TimezoneOffsetFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTimezoneOffsetFrom(elm: TimezoneOffsetFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a DateTimeComponentFrom. This method will be called for every node in the tree that is
     * a DateTimeComponentFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDateTimeComponentFrom(elm: DateTimeComponentFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a TimeOfDay. This method will be called for every node in the tree that is a TimeOfDay.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTimeOfDay(elm: TimeOfDay, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Today. This method will be called for every node in the tree that is a Today.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitToday(elm: Today, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Now. This method will be called for every node in the tree that is a Now.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitNow(elm: Now, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a DateTime. This method will be called for every node in the tree that is a DateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDateTime(elm: DateTime, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.year != null) {
            val childResult = visitExpression(elm.year!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.month != null) {
            val childResult = visitExpression(elm.month!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.day != null) {
            val childResult = visitExpression(elm.day!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.hour != null) {
            val childResult = visitExpression(elm.hour!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.minute != null) {
            val childResult = visitExpression(elm.minute!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.second != null) {
            val childResult = visitExpression(elm.second!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.millisecond != null) {
            val childResult = visitExpression(elm.millisecond!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.timezoneOffset != null) {
            val childResult = visitExpression(elm.timezoneOffset!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Date. This method will be called for every node in the tree that is a Date.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDate(elm: Date, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.year != null) {
            val childResult = visitExpression(elm.year!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.month != null) {
            val childResult = visitExpression(elm.month!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.day != null) {
            val childResult = visitExpression(elm.day!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Time. This method will be called for every node in the tree that is a Time.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTime(elm: Time, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.hour != null) {
            val childResult = visitExpression(elm.hour!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.minute != null) {
            val childResult = visitExpression(elm.minute!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.second != null) {
            val childResult = visitExpression(elm.second!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.millisecond != null) {
            val childResult = visitExpression(elm.millisecond!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a SameAs. This method will be called for every node in the tree that is a SameAs.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSameAs(elm: SameAs, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a SameOrBefore. This method will be called for every node in the tree that is a
     * SameOrBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSameOrBefore(elm: SameOrBefore, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a SameOrAfter. This method will be called for every node in the tree that is a
     * SameOrAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSameOrAfter(elm: SameOrAfter, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Width. This method will be called for every node in the tree that is a Width.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitWidth(elm: Width, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Size. This method will be called for every node in the tree that is a Size.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSize(elm: Size, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a PointFrom. This method will be called for every node in the tree that is a PointFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPointFrom(elm: PointFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Start. This method will be called for every node in the tree that is a Start.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitStart(elm: Start, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an End. This method will be called for every node in the tree that is an End.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitEnd(elm: End, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Contains. This method will be called for every node in the tree that is a Contains.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitContains(elm: Contains, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ProperContains. This method will be called for every node in the tree that is a
     * ProperContains.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProperContains(elm: ProperContains, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an In. This method will be called for every node in the tree that is an In.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIn(elm: In, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ProperIn. This method will be called for every node in the tree that is a ProperIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProperIn(elm: ProperIn, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Includes. This method will be called for every node in the tree that is an Includes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIncludes(elm: Includes, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an IncludedIn. This method will be called for every node in the tree that is an
     * IncludedIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIncludedIn(elm: IncludedIn, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ProperIncludes. This method will be called for every node in the tree that is a
     * ProperIncludes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProperIncludes(elm: ProperIncludes, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ProperIncludedIn. This method will be called for every node in the tree that is a
     * ProperIncludedIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProperIncludedIn(elm: ProperIncludedIn, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Before. This method will be called for every node in the tree that is a Before.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitBefore(elm: Before, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an After. This method will be called for every node in the tree that is an After.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAfter(elm: After, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Meets. This method will be called for every node in the tree that is a Meets.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMeets(elm: Meets, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a MeetsBefore. This method will be called for every node in the tree that is a
     * MeetsBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMeetsBefore(elm: MeetsBefore, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a MeetsAfter. This method will be called for every node in the tree that is a
     * MeetsAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMeetsAfter(elm: MeetsAfter, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Overlaps. This method will be called for every node in the tree that is an Overlaps.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOverlaps(elm: Overlaps, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an OverlapsBefore. This method will be called for every node in the tree that is an
     * OverlapsBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOverlapsBefore(elm: OverlapsBefore, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an OverlapsAfter. This method will be called for every node in the tree that is an
     * OverlapsAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOverlapsAfter(elm: OverlapsAfter, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Starts. This method will be called for every node in the tree that is a Starts.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitStarts(elm: Starts, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Ends. This method will be called for every node in the tree that is an Ends.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitEnds(elm: Ends, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Collapse. This method will be called for every node in the tree that is a Collapse.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCollapse(elm: Collapse, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Expand. This method will be called for every node in the tree that is an Expand.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExpand(elm: Expand, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Union. This method will be called for every node in the tree that is a Union.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitUnion(elm: Union, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Intersect. This method will be called for every node in the tree that is an
     * Intersect.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIntersect(elm: Intersect, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Except. This method will be called for every node in the tree that is an Except.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExcept(elm: Except, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Exists. This method will be called for every node in the tree that is an Exists.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExists(elm: Exists, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Times. This method will be called for every node in the tree that is a Times.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTimes(elm: Times, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Filter. This method will be called for every node in the tree that is a Filter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitFilter(elm: Filter, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.condition != null) {
            val childResult = visitExpression(elm.condition!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a First. This method will be called for every node in the tree that is a First.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitFirst(elm: First, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Last. This method will be called for every node in the tree that is a Last.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLast(elm: Last, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Slice. This method will be called for every node in the tree that is a Slice.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSlice(elm: Slice, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.startIndex != null) {
            val childResult = visitExpression(elm.startIndex!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.endIndex != null) {
            val childResult = visitExpression(elm.endIndex!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Children. This method will be called for every node in the tree that is a Children.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitChildren(elm: Children, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Descendents. This method will be called for every node in the tree that is a
     * Descendents.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDescendents(elm: Descendents, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Message. This method will be called for every node in the tree that is a Message.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMessage(elm: Message, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.condition != null) {
            val childResult = visitExpression(elm.condition!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.code != null) {
            val childResult = visitExpression(elm.code!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.severity != null) {
            val childResult = visitExpression(elm.severity!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.message != null) {
            val childResult = visitExpression(elm.message!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an IndexOf. This method will be called for every node in the tree that is an IndexOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIndexOf(elm: IndexOf, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.element != null) {
            val childResult = visitExpression(elm.element!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Flatten. This method will be called for every node in the tree that is a Flatten.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitFlatten(elm: Flatten, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Sort. This method will be called for every node in the tree that is a Sort.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSort(elm: Sort, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        for (sbi in elm.by!!) {
            val childResult = visitSortByItem(sbi!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ForEach. This method will be called for every node in the tree that is a ForEach.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitForEach(elm: ForEach, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.element != null) {
            val childResult = visitExpression(elm.element!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Repeat. This method will be called for every node in the tree that is a Repeat.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitRepeat(elm: Repeat, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.element != null) {
            val childResult = visitExpression(elm.element!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Distinct. This method will be called for every node in the tree that is a Distinct.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDistinct(elm: Distinct, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Current. This method will be called for every node in the tree that is a Current.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCurrent(elm: Current, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Iteration. This method will be called for every node in the tree that is an
     * Iteration.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIteration(elm: Iteration, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Total. This method will be called for every node in the tree that is a Total.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitTotal(elm: Total, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a SingletonFrom. This method will be called for every node in the tree that is a
     * SingletonFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSingletonFrom(elm: SingletonFrom, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an AggregateExpression. This method will be called for every node in the tree that is
     * an AggregateExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAggregateExpression(elm: AggregateExpression, context: C): T? {
        return when (elm) {
            is Aggregate -> visitAggregate(elm, context)
            is Count -> visitCount(elm, context)
            is Sum -> visitSum(elm, context)
            is Product -> visitProduct(elm, context)
            is Min -> visitMin(elm, context)
            is Max -> visitMax(elm, context)
            is Avg -> visitAvg(elm, context)
            is GeometricMean -> visitGeometricMean(elm, context)
            is Median -> visitMedian(elm, context)
            is Mode -> visitMode(elm, context)
            is Variance -> visitVariance(elm, context)
            is StdDev -> visitStdDev(elm, context)
            is PopulationVariance -> visitPopulationVariance(elm, context)
            is PopulationStdDev -> visitPopulationStdDev(elm, context)
            is AllTrue -> visitAllTrue(elm, context)
            is AnyTrue -> visitAnyTrue(elm, context)
            else ->
                throw IllegalArgumentException(
                    "Unsupported AggregateExpression type: " + elm.javaClass.name
                )
        }
    }

    /**
     * Visit an Aggregate. This method will be called for every node in the tree that is an
     * Aggregate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAggregate(elm: Aggregate, context: C): T? {
        var result = visitFields(elm as AggregateExpression, context)

        if (elm.initialValue != null) {
            val childResult = visitExpression(elm.initialValue!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.iteration != null) {
            val childResult = visitExpression(elm.iteration!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Count. This method will be called for every node in the tree that is a Count.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCount(elm: Count, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Sum. This method will be called for every node in the tree that is a Sum.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSum(elm: Sum, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Product. This method will be called for every node in the tree that is a Product.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProduct(elm: Product, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a GeometricMean. This method will be called for every node in the tree that is a
     * GeometricMean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitGeometricMean(elm: GeometricMean, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Min. This method will be called for every node in the tree that is a Min.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMin(elm: Min, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Max. This method will be called for every node in the tree that is a Max.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMax(elm: Max, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an Avg. This method will be called for every node in the tree that is an Avg.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAvg(elm: Avg, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Median. This method will be called for every node in the tree that is a Median.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMedian(elm: Median, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Mode. This method will be called for every node in the tree that is a Mode.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitMode(elm: Mode, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Variance. This method will be called for every node in the tree that is a Variance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitVariance(elm: Variance, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a PopulationVariance. This method will be called for every node in the tree that is a
     * PopulationVariance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPopulationVariance(elm: PopulationVariance, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a StdDev. This method will be called for every node in the tree that is a StdDev.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitStdDev(elm: StdDev, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a PopulationStdDev. This method will be called for every node in the tree that is a
     * PopulationStdDev.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitPopulationStdDev(elm: PopulationStdDev, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an AllTrue. This method will be called for every node in the tree that is an AllTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAllTrue(elm: AllTrue, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit an AnyTrue. This method will be called for every node in the tree that is an AnyTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAnyTrue(elm: AnyTrue, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Property. This method will be called for every node in the tree that is a Property.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProperty(elm: Property, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an AliasedQuerySource. This method will be called for every node in the tree that is an
     * AliasedQuerySource.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAliasedQuerySource(elm: AliasedQuerySource, context: C): T? {
        if (elm is RelationshipClause) {
            return visitRelationshipClause(elm, context)
        }

        return visitFields(elm, context)
    }

    /**
     * Visit a LetClause. This method will be called for every node in the tree that is a LetClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLetClause(elm: LetClause, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.expression != null) {
            val childResult = visitExpression(elm.expression!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a RelationshipClause. This method will be called for every node in the tree that is a
     * RelationshipClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitRelationshipClause(elm: RelationshipClause, context: C): T? {
        return when (elm) {
            is With -> {
                visitWith(elm, context)
            }
            is Without -> {
                visitWithout(elm, context)
            }
            else -> {
                throw IllegalArgumentException(
                    "Unknown RelationshipClause type: " + elm.javaClass.name
                )
            }
        }
    }

    /**
     * Visit a With. This method will be called for every node in the tree that is a With.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitWith(elm: With, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a Without. This method will be called for every node in the tree that is a Without.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitWithout(elm: Without, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a SortByItem. This method will be called for every node in the tree that is a
     * SortByItem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSortByItem(elm: SortByItem, context: C): T? {
        return when (elm) {
            is ByDirection -> {
                visitByDirection(elm, context)
            }
            is ByColumn -> {
                visitByColumn(elm, context)
            }
            is ByExpression -> {
                visitByExpression(elm, context)
            }
            else -> throw IllegalArgumentException("Unknown SortByItem type: " + elm.javaClass.name)
        }
    }

    /**
     * Visit a ByDirection. This method will be called for every node in the tree that is a
     * ByDirection.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitByDirection(elm: ByDirection, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ByColumn. This method will be called for every node in the tree that is a ByColumn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitByColumn(elm: ByColumn, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a ByExpression. This method will be called for every node in the tree that is a
     * ByExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitByExpression(elm: ByExpression, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.expression != null) {
            val childResult = visitExpression(elm.expression!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a SortClause. This method will be called for every node in the tree that is a
     * SortClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSortClause(elm: SortClause, context: C): T? {
        var result = visitFields(elm, context)

        for (sbi in elm.by!!) {
            val childResult = visitSortByItem(sbi!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an AggregateClause. This method will be called for every node in the tree that is an
     * AggregateClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAggregateClause(elm: AggregateClause, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.expression != null) {
            val childResult = visitExpression(elm.expression!!, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.starting != null) {
            val childResult = visitExpression(elm.starting!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ReturnClause. This method will be called for every node in the tree that is a
     * ReturnClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitReturnClause(elm: ReturnClause, context: C): T? {
        var result = visitFields(elm, context)

        if (elm.expression != null) {
            val childResult = visitExpression(elm.expression!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Query. This method will be called for every node in the tree that is a Query.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitQuery(elm: Query, context: C): T? {
        var result = visitFields(elm, context)

        for (source in elm.source!!) {
            val childResult = visitAliasedQuerySource(source!!, context)
            result = aggregateResult(result, childResult)
        }
        for (let in elm.let!!) {
            val childResult = visitLetClause(let!!, context)
            result = aggregateResult(result, childResult)
        }

        for (r in elm.relationship!!) {
            val childResult = visitRelationshipClause(r!!, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.where != null) {
            val childResult = visitExpression(elm.where!!, context)
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

        return result
    }

    /**
     * Visit an AliasRef. This method will be called for every node in the tree that is an AliasRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAliasRef(elm: AliasRef, context: C): T? {
        return visitFields(elm, context)
    }

    /**
     * Visit a QueryLetRef. This method will be called for every node in the tree that is a
     * QueryLetRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitQueryLetRef(elm: QueryLetRef, context: C): T? {
        return visitFields(elm, context)
    }

    protected fun visitFields(elm: Element, context: C): T? {
        var result = defaultResult(elm, context)

        if (elm.resultTypeSpecifier != null) {
            val childResult = visitTypeSpecifier(elm.resultTypeSpecifier!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    protected fun visitFields(elm: Expression, context: C): T? {
        return visitFields(elm as Element, context)
    }

    protected fun visitFields(elm: RelationshipClause, context: C): T? {
        var result = visitFields(elm as AliasedQuerySource, context)

        if (elm.suchThat != null) {
            val childResult = visitExpression(elm.suchThat!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * visits the fields of an AggregateExpression
     *
     * @param elm
     * @param context
     * @return
     */
    protected fun visitFields(elm: AggregateExpression, context: C): T? {
        var result = visitFields(elm as Expression, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source!!, context)
            result = aggregateResult(result, childResult)
        }

        for (s in elm.signature!!) {
            val childResult = visitTypeSpecifier(s!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit the fields of an ExpressionDef
     *
     * @param elm
     * @param context
     * @return
     */
    protected fun visitFields(elm: ExpressionDef, context: C): T? {
        var result = visitFields(elm as Element, context)

        if (elm.accessLevel != null) {
            val childResult = visitAccessModifier(elm.accessLevel, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.expression != null) {
            val childResult = visitExpression(elm.expression!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visits the fields of an UnaryExpression
     *
     * @param elm
     * @param context
     * @return
     */
    protected fun visitFields(elm: UnaryExpression, context: C): T? {
        var result = visitFields(elm as OperatorExpression, context)

        if (elm.operand != null) {
            val childResult = visitExpression(elm.operand!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * visits the fields of an NaryExpression
     *
     * @param elm
     * @param context
     * @return
     */
    protected fun visitFields(elm: NaryExpression, context: C): T? {
        var result = visitFields(elm as OperatorExpression, context)

        for (e in elm.operand!!) {
            val childResult = visitExpression(e!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visits the fields of a TernaryExpression
     *
     * @param elm
     * @param context
     * @return
     */
    protected fun visitFields(elm: TernaryExpression, context: C): T? {
        var result = visitFields(elm as OperatorExpression, context)

        for (s in elm.operand!!) {
            val childResult = visitExpression(s!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visits the fields of an OperatorExpression
     *
     * @param elm
     * @param context
     * @return
     */
    protected fun visitFields(elm: OperatorExpression, context: C): T? {
        var result = visitFields(elm as Expression, context)

        for (s in elm.signature!!) {
            val childResult = visitTypeSpecifier(s!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * visits the fields of a BinaryExpression
     *
     * @param elm
     * @param context
     * @return
     */
    protected open fun visitFields(elm: BinaryExpression, context: C): T? {
        var result = visitFields(elm as OperatorExpression, context)

        for (e in elm.operand!!) {
            val childResult = visitExpression(e!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    protected open fun visitFields(elm: AliasedQuerySource, context: C): T? {
        var result = visitFields(elm as Element, context)

        if (elm.expression != null) {
            val childResult = visitExpression(elm.expression!!, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }
}
