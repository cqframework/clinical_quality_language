package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.Abs
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
import org.hl7.elm.r1.Descendants
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

/**
 * This interface defines a complete generic visitor for an Elm tree
 *
 * NOTE: Three types of nodes are not Element types: TupleElement, InstanceElement, and
 * AccessModifier. Each has a default behavior described in the docs for their respective methods,
 * but the corresponding visit methods should be overridden if the default behavior is not desired.
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * @param <C> The type of context passed to each visit method operations with no return type.
 *   </C></T>
 */
@Suppress("TooManyFunctions")
interface ElmVisitor<T, C> {
    /**
     * Visit an Element in an ELM tree. This method will be called for every node in the tree that
     * is a descendant of the Element type.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitElement(elm: Element, context: C): T

    /**
     * Visit a TypeSpecifier. This method will be called for every node in the tree that is a
     * descendant of the TypeSpecifier type.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTypeSpecifier(elm: TypeSpecifier, context: C): T

    /**
     * Visit a NamedTypeSpecifier. This method will be called for every node in the tree that is a
     * NamedTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNamedTypeSpecifier(elm: NamedTypeSpecifier, context: C): T

    /**
     * Visit an IntervalTypeSpecifier. This method will be called for every node in the tree that is
     * an IntervalTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIntervalTypeSpecifier(elm: IntervalTypeSpecifier, context: C): T

    /**
     * Visit a ListTypeSpecifier. This method will be called for every node in the tree that is a
     * ListTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitListTypeSpecifier(elm: ListTypeSpecifier, context: C): T

    /**
     * Visit a TupleElementDefinition. This method will be called for every node in the tree that is
     * a TupleElementDefinition.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTupleElementDefinition(elm: TupleElementDefinition, context: C): T

    /**
     * Visit a TupleTypeSpecifier. This method will be called for every node in the tree that is a
     * TupleTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTupleTypeSpecifier(elm: TupleTypeSpecifier, context: C): T

    /**
     * Visit a ChoiceTypeSpecifier. This method will be called for every node in the tree that is a
     * ChoiceTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitChoiceTypeSpecifier(elm: ChoiceTypeSpecifier, context: C): T

    /**
     * Visit an Expression. This method will be called for every node in the tree that is an
     * Expression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExpression(elm: Expression, context: C): T

    /**
     * Visit an UnaryExpression. This method will be called for every node in the tree that is a
     * UnaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitUnaryExpression(elm: UnaryExpression, context: C): T

    /**
     * Visit an OperatorExpression. This method will be called for every node in the tree that is an
     * OperatorExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOperatorExpression(elm: OperatorExpression, context: C): T

    /**
     * Visit a BinaryExpression. This method will be called for every node in the tree that is a
     * BinaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitBinaryExpression(elm: BinaryExpression, context: C): T

    /**
     * Visit a TernaryExpression. This method will be called for every node in the tree that is a
     * TernaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTernaryExpression(elm: TernaryExpression, context: C): T

    /**
     * Visit a NaryExpression. This method will be called for every node in the tree that is a
     * NaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNaryExpression(elm: NaryExpression, context: C): T

    /**
     * Visit an ExpressionDef. This method will be called for every node in the tree that is a
     * ExpressionDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExpressionDef(elm: ExpressionDef, context: C): T

    /**
     * Visit a FunctionDef. This method will be called for every node in the tree that is a
     * FunctionDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitFunctionDef(elm: FunctionDef, context: C): T

    /**
     * Visit an ExpressionRef. This method will be called for every node in the tree that is a
     * ExpressionRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExpressionRef(elm: ExpressionRef, context: C): T

    /**
     * Visit a FunctionRef. This method will be called for every node in the tree that is a
     * FunctionRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitFunctionRef(elm: FunctionRef, context: C): T

    /**
     * Visit a ParameterDef. This method will be called for every node in the tree that is a
     * ParameterDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitParameterDef(elm: ParameterDef, context: C): T

    /**
     * Visit a ParameterRef. This method will be called for every node in the tree that is a
     * ParameterRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitParameterRef(elm: ParameterRef, context: C): T

    /**
     * Visit an OperandDef. This method will be called for every node in the tree that is a
     * OperandDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOperandDef(elm: OperandDef, context: C): T

    /**
     * Visit an OperandRef. This method will be called for every node in the tree that is a
     * OperandRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOperandRef(elm: OperandRef, context: C): T

    /**
     * Visit an IdentifierRef. This method will be called for every node in the tree that is a
     * IdentifierRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIdentifierRef(elm: IdentifierRef, context: C): T

    /**
     * Visit a Literal. This method will be called for every node in the tree that is a Literal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLiteral(elm: Literal, context: C): T

    /**
     * Visit a TupleElement. This method will be called for every node in the tree that is a
     * TupleElement.
     *
     * NOTE: The TupleElement does not inherit from Element, so it is not visited by the
     * visitElement method. Instead, the [TupleElement.value] is visited by the visitElement method.
     *
     * @param tupleElement the TupleElement
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTupleElement(tupleElement: TupleElement, context: C): T

    /**
     * Visit a Tuple. This method will be called for every node in the tree that is a Tuple.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTuple(elm: Tuple, context: C): T

    /**
     * Visit an InstanceElement. This method will be called for every node in the tree that is a
     * InstanceElement.
     *
     * NOTE: The InstanceElement does not inherit from Element, so it is not visited by the
     * visitElement method. Instead, the [InstanceElement.value] is visited by the visitElement
     * method.
     *
     * @param instanceElement the InstanceElement to visit
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitInstanceElement(instanceElement: InstanceElement, context: C): T

    /**
     * Visit an Instance. This method will be called for every node in the tree that is an Instance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitInstance(elm: Instance, context: C): T

    /**
     * Visit an Interval. This method will be called for every node in the tree that is an Interval.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitInterval(elm: Interval, context: C): T

    /**
     * Visit a List. This method will be called for every node in the tree that is a List.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitList(elm: List, context: C): T

    /**
     * Visit an And. This method will be called for every node in the tree that is an And.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAnd(elm: And, context: C): T

    /**
     * Visit an Or. This method will be called for every node in the tree that is an Or.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOr(elm: Or, context: C): T

    /**
     * Visit a Xor. This method will be called for every node in the tree that is a Xor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitXor(elm: Xor, context: C): T

    /**
     * Visit an Implies. This method will be called for every node in the tree that is an Implies.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitImplies(elm: Implies, context: C): T

    /**
     * Visit a Not. This method will be called for every node in the tree that is a Not.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNot(elm: Not, context: C): T

    /**
     * Visit an If. This method will be called for every node in the tree that is an If.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIf(elm: If, context: C): T

    /**
     * Visit a CaseItem. This method will be called for every node in the tree that is a CaseItem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCaseItem(elm: CaseItem, context: C): T

    /**
     * Visit a Case. This method will be called for every node in the tree that is a Case.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCase(elm: Case, context: C): T

    /**
     * Visit a Null. This method will be called for every node in the tree that is a Null.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNull(elm: Null, context: C): T

    /**
     * Visit an IsNull. This method will be called for every node in the tree that is an IsNull.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIsNull(elm: IsNull, context: C): T

    /**
     * Visit an IsTrue. This method will be called for every node in the tree that is an IsTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIsTrue(elm: IsTrue, context: C): T

    /**
     * Visit an IsFalse. This method will be called for every node in the tree that is an IsFalse.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIsFalse(elm: IsFalse, context: C): T

    /**
     * Visit a Coalesce. This method will be called for every node in the tree that is a Coalesce.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCoalesce(elm: Coalesce, context: C): T

    /**
     * Visit an Is. This method will be called for every node in the tree that is an Is.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIs(elm: Is, context: C): T

    /**
     * Visit an As. This method will be called for every node in the tree that is an As.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAs(elm: As, context: C): T

    /**
     * Visit a Convert. This method will be called for every node in the tree that is a Convert.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvert(elm: Convert, context: C): T

    /**
     * Visit a CanConvert. This method will be called for every node in the tree that is a
     * CanConvert.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCanConvert(elm: CanConvert, context: C): T

    /**
     * Visit a ConvertsToBoolean. This method will be called for every node in the tree that is a
     * ConvertsToBoolean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToBoolean(elm: ConvertsToBoolean, context: C): T

    /**
     * Visit a ToBoolean. This method will be called for every node in the tree that is a ToBoolean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToBoolean(elm: ToBoolean, context: C): T

    /**
     * Visit a ToChars. This method will be called for every node in the tree that is a ToChars.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToChars(elm: ToChars, context: C): T

    /**
     * Visit a ToConcept. This method will be called for every node in the tree that is a ToConcept.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToConcept(elm: ToConcept, context: C): T

    /**
     * Visit a ConvertsToDate. This method will be called for every node in the tree that is a
     * ConvertsToDate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToDate(elm: ConvertsToDate, context: C): T

    /**
     * Visit a ToDate. This method will be called for every node in the tree that is a ToDate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToDate(elm: ToDate, context: C): T

    /**
     * Visit a ConvertsToDateTime. This method will be called for every node in the tree that is a
     * ConvertsToDateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToDateTime(elm: ConvertsToDateTime, context: C): T

    /**
     * Visit a ToDateTime. This method will be called for every node in the tree that is a
     * ToDateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToDateTime(elm: ToDateTime, context: C): T

    /**
     * Visit a ConvertsToLong. This method will be called for every node in the tree that is a
     * ConvertsToLong.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToLong(elm: ConvertsToLong, context: C): T

    /**
     * Visit a ToLong. This method will be called for every node in the tree that is a ToLong.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToLong(elm: ToLong, context: C): T

    /**
     * Visit a ConvertsToDecimal. This method will be called for every node in the tree that is a
     * ConvertsToDecimal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToDecimal(elm: ConvertsToDecimal, context: C): T

    /**
     * Visit a ToDecimal. This method will be called for every node in the tree that is a ToDecimal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToDecimal(elm: ToDecimal, context: C): T

    /**
     * Visit a ConvertsToInteger. This method will be called for every node in the tree that is a
     * ConvertsToInteger.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToInteger(elm: ConvertsToInteger, context: C): T

    /**
     * Visit a ToInteger. This method will be called for every node in the tree that is a ToInteger.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToInteger(elm: ToInteger, context: C): T

    /**
     * Visit a ToList. This method will be called for every node in the tree that is a ToList.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToList(elm: ToList, context: C): T

    /**
     * Visit a ConvertQuantity. This method will be called for every node in the tree that is a
     * ConvertQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertQuantity(elm: ConvertQuantity, context: C): T

    /**
     * Visit a CanConvertQuantity. This method will be called for every node in the tree that is a
     * CanConvertQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCanConvertQuantity(elm: CanConvertQuantity, context: C): T

    /**
     * Visit a ConvertsToQuantity. This method will be called for every node in the tree that is a
     * ConvertsToQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToQuantity(elm: ConvertsToQuantity, context: C): T

    /**
     * Visit a ToQuantity. This method will be called for every node in the tree that is a
     * ToQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToQuantity(elm: ToQuantity, context: C): T

    /**
     * Visit a ConvertsToRatio. This method will be called for every node in the tree that is a
     * ConvertsToRatio.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToRatio(elm: ConvertsToRatio, context: C): T

    /**
     * Visit a Ratio. This method will be called for every node in the tree that is a Ratio.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToRatio(elm: ToRatio, context: C): T

    /**
     * Visit a ConvertsToString. This method will be called for every node in the tree that is a
     * ConvertsToString.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToString(elm: ConvertsToString, context: C): T

    /**
     * Visit a ToString. This method will be called for every node in the tree that is a ToString.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToString(elm: ToString, context: C): T

    /**
     * Visit a ConvertsToTime. This method will be called for every node in the tree that is a
     * ConvertsToTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConvertsToTime(elm: ConvertsToTime, context: C): T

    /**
     * Visit a ToTime. This method will be called for every node in the tree that is a ToTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToTime(elm: ToTime, context: C): T

    /**
     * Visit an Equal. This method will be called for every node in the tree that is an Equal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitEqual(elm: Equal, context: C): T

    /**
     * Visit an Equivalent. This method will be called for every node in the tree that is an
     * Equivalent.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitEquivalent(elm: Equivalent, context: C): T

    /**
     * Visit a NotEqual. This method will be called for every node in the tree that is a NotEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNotEqual(elm: NotEqual, context: C): T

    /**
     * Visit a Less. This method will be called for every node in the tree that is a Less.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLess(elm: Less, context: C): T

    /**
     * Visit a Greater. This method will be called for every node in the tree that is a Greater.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitGreater(elm: Greater, context: C): T

    /**
     * Visit a LessOrEqual. This method will be called for every node in the tree that is a
     * LessOrEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLessOrEqual(elm: LessOrEqual, context: C): T

    /**
     * Visit a GreaterOrEqual. This method will be called for every node in the tree that is a
     * GreaterOrEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitGreaterOrEqual(elm: GreaterOrEqual, context: C): T

    /**
     * Visit an Add. This method will be called for every node in the tree that is an Add.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAdd(elm: Add, context: C): T

    /**
     * Visit a Subtract. This method will be called for every node in the tree that is a Subtract.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSubtract(elm: Subtract, context: C): T

    /**
     * Visit a Multiply. This method will be called for every node in the tree that is a Multiply.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMultiply(elm: Multiply, context: C): T

    /**
     * Visit a Divide. This method will be called for every node in the tree that is a Divide.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDivide(elm: Divide, context: C): T

    /**
     * Visit a TruncatedDivide. This method will be called for every node in the tree that is a
     * TruncatedDivide.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTruncatedDivide(elm: TruncatedDivide, context: C): T

    /**
     * Visit a Modulo. This method will be called for every node in the tree that is a Modulo.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitModulo(elm: Modulo, context: C): T

    /**
     * Visit a Ceiling. This method will be called for every node in the tree that is a Ceiling.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCeiling(elm: Ceiling, context: C): T

    /**
     * Visit a Floor. This method will be called for every node in the tree that is a Floor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitFloor(elm: Floor, context: C): T

    /**
     * Visit a Truncate. This method will be called for every node in the tree that is a Truncate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTruncate(elm: Truncate, context: C): T

    /**
     * Visit an Abs. This method will be called for every node in the tree that is an Abs.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAbs(elm: Abs, context: C): T

    /**
     * Visit a Negate. This method will be called for every node in the tree that is a Negate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNegate(elm: Negate, context: C): T

    /**
     * Visit a Round. This method will be called for every node in the tree that is a Round.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitRound(elm: Round, context: C): T

    /**
     * Visit a Ln. This method will be called for every node in the tree that is a Ln.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLn(elm: Ln, context: C): T

    /**
     * Visit an Exp. This method will be called for every node in the tree that is an Exp.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExp(elm: Exp, context: C): T

    /**
     * Visit a Log. This method will be called for every node in the tree that is a Log.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLog(elm: Log, context: C): T

    /**
     * Visit a Power. This method will be called for every node in the tree that is a Power.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPower(elm: Power, context: C): T

    /**
     * Visit a Successor. This method will be called for every node in the tree that is a Successor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSuccessor(elm: Successor, context: C): T

    /**
     * Visit a Predecessor. This method will be called for every node in the tree that is a
     * Predecessor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPredecessor(elm: Predecessor, context: C): T

    /**
     * Visit a MinValue. This method will be called for every node in the tree that is a MinValue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMinValue(elm: MinValue, context: C): T

    /**
     * Visit a MaxValue. This method will be called for every node in the tree that is a MaxValue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMaxValue(elm: MaxValue, context: C): T

    /**
     * Visit a Precision. This method will be called for every node in the tree that is a Precision.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPrecision(elm: Precision, context: C): T

    /**
     * Visit a LowBoundary. This method will be called for every node in the tree that is a
     * LowBoundary.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLowBoundary(elm: LowBoundary, context: C): T

    /**
     * Visit a HighBoundary. This method will be called for every node in the tree that is a
     * HighBoundary.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitHighBoundary(elm: HighBoundary, context: C): T

    /**
     * Visit a Concatenate. This method will be called for every node in the tree that is a
     * Concatenate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConcatenate(elm: Concatenate, context: C): T

    /**
     * Visit a Combine. This method will be called for every node in the tree that is a Combine.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCombine(elm: Combine, context: C): T

    /**
     * Visit a Split. This method will be called for every node in the tree that is a Split.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSplit(elm: Split, context: C): T

    /**
     * Visit a SplitOnMatches. This method will be called for every node in the tree that is a
     * SplitOnMatches.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSplitOnMatches(elm: SplitOnMatches, context: C): T

    /**
     * Visit a Length. This method will be called for every node in the tree that is a Length.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLength(elm: Length, context: C): T

    /**
     * Visit an Upper. This method will be called for every node in the tree that is an Upper.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitUpper(elm: Upper, context: C): T

    /**
     * Visit a Lower. This method will be called for every node in the tree that is a Lower.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLower(elm: Lower, context: C): T

    /**
     * Visit an Indexer. This method will be called for every node in the tree that is an Indexer.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIndexer(elm: Indexer, context: C): T

    /**
     * Visit a PositionOf. This method will be called for every node in the tree that is a
     * PositionOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPositionOf(elm: PositionOf, context: C): T

    /**
     * Visit a LastPositionOf. This method will be called for every node in the tree that is a
     * LastPositionOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLastPositionOf(elm: LastPositionOf, context: C): T

    /**
     * Visit a Substring. This method will be called for every node in the tree that is a Substring.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSubstring(elm: Substring, context: C): T

    /**
     * Visit a StartsWith. This method will be called for every node in the tree that is a
     * StartsWith.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitStartsWith(elm: StartsWith, context: C): T

    /**
     * Visit an EndsWith. This method will be called for every node in the tree that is an EndsWith.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitEndsWith(elm: EndsWith, context: C): T

    /**
     * Visit a Matches. This method will be called for every node in the tree that is a Matches.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMatches(elm: Matches, context: C): T

    /**
     * Visit a ReplaceMatches. This method will be called for every node in the tree that is a
     * ReplaceMatches.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitReplaceMatches(elm: ReplaceMatches, context: C): T

    /**
     * Visit a DurationBetween. This method will be called for every node in the tree that is a
     * DurationBetween.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDurationBetween(elm: DurationBetween, context: C): T

    /**
     * Visit a DifferenceBetween. This method will be called for every node in the tree that is a
     * DifferenceBetween.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDifferenceBetween(elm: DifferenceBetween, context: C): T

    /**
     * Visit a DateFrom. This method will be called for every node in the tree that is a DateFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDateFrom(elm: DateFrom, context: C): T

    /**
     * Visit a TimeFrom. This method will be called for every node in the tree that is a TimeFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTimeFrom(elm: TimeFrom, context: C): T

    /**
     * Visit a TimezoneFrom. This method will be called for every node in the tree that is a
     * TimezoneFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTimezoneFrom(elm: TimezoneFrom, context: C): T

    /**
     * Visit a TimezoneOffsetFrom. This method will be called for every node in the tree that is a
     * TimezoneOffsetFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTimezoneOffsetFrom(elm: TimezoneOffsetFrom, context: C): T

    /**
     * Visit a DateTimeComponentFrom. This method will be called for every node in the tree that is
     * a DateTimeComponentFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDateTimeComponentFrom(elm: DateTimeComponentFrom, context: C): T

    /**
     * Visit a TimeOfDay. This method will be called for every node in the tree that is a TimeOfDay.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTimeOfDay(elm: TimeOfDay, context: C): T

    /**
     * Visit a Today. This method will be called for every node in the tree that is a Today.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitToday(elm: Today, context: C): T

    /**
     * Visit a Now. This method will be called for every node in the tree that is a Now.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitNow(elm: Now, context: C): T

    /**
     * Visit a DateTime. This method will be called for every node in the tree that is a DateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDateTime(elm: DateTime, context: C): T

    /**
     * Visit a Date. This method will be called for every node in the tree that is a Date.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDate(elm: Date, context: C): T

    /**
     * Visit a Time. This method will be called for every node in the tree that is a Time.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTime(elm: Time, context: C): T

    /**
     * Visit a SameAs. This method will be called for every node in the tree that is a SameAs.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSameAs(elm: SameAs, context: C): T

    /**
     * Visit a SameOrBefore. This method will be called for every node in the tree that is a
     * SameOrBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSameOrBefore(elm: SameOrBefore, context: C): T

    /**
     * Visit a SameOrAfter. This method will be called for every node in the tree that is a
     * SameOrAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSameOrAfter(elm: SameOrAfter, context: C): T

    /**
     * Visit a Width. This method will be called for every node in the tree that is a Width.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitWidth(elm: Width, context: C): T

    /**
     * Visit a Size. This method will be called for every node in the tree that is a Size.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSize(elm: Size, context: C): T

    /**
     * Visit a PointFrom. This method will be called for every node in the tree that is a PointFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPointFrom(elm: PointFrom, context: C): T

    /**
     * Visit a Start. This method will be called for every node in the tree that is a Start.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitStart(elm: Start, context: C): T

    /**
     * Visit an End. This method will be called for every node in the tree that is an End.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitEnd(elm: End, context: C): T

    /**
     * Visit a Contains. This method will be called for every node in the tree that is a Contains.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitContains(elm: Contains, context: C): T

    /**
     * Visit a ProperContains. This method will be called for every node in the tree that is a
     * ProperContains.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitProperContains(elm: ProperContains, context: C): T

    /**
     * Visit an In. This method will be called for every node in the tree that is an In.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIn(elm: In, context: C): T

    /**
     * Visit a ProperIn. This method will be called for every node in the tree that is a ProperIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitProperIn(elm: ProperIn, context: C): T

    /**
     * Visit an Includes. This method will be called for every node in the tree that is an Includes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIncludes(elm: Includes, context: C): T

    /**
     * Visit an IncludedIn. This method will be called for every node in the tree that is a
     * IncludedIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIncludedIn(elm: IncludedIn, context: C): T

    /**
     * Visit a ProperIncludes. This method will be called for every node in the tree that is a
     * ProperIncludes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitProperIncludes(elm: ProperIncludes, context: C): T

    /**
     * Visit a ProperIncludedIn. This method will be called for every node in the tree that is a
     * ProperIncludedIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitProperIncludedIn(elm: ProperIncludedIn, context: C): T

    /**
     * Visit a Before. This method will be called for every node in the tree that is a Before.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitBefore(elm: Before, context: C): T

    /**
     * Visit an After. This method will be called for every node in the tree that is an After.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAfter(elm: After, context: C): T

    /**
     * Visit a Meets. This method will be called for every node in the tree that is a Meets.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMeets(elm: Meets, context: C): T

    /**
     * Visit a MeetsBefore. This method will be called for every node in the tree that is a
     * MeetsBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMeetsBefore(elm: MeetsBefore, context: C): T

    /**
     * Visit a MeetsAfter. This method will be called for every node in the tree that is a
     * MeetsAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMeetsAfter(elm: MeetsAfter, context: C): T

    /**
     * Visit an Overlaps. This method will be called for every node in the tree that is an Overlaps.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOverlaps(elm: Overlaps, context: C): T

    /**
     * Visit an OverlapsBefore. This method will be called for every node in the tree that is a
     * OverlapsBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOverlapsBefore(elm: OverlapsBefore, context: C): T

    /**
     * Visit an OverlapsAfter. This method will be called for every node in the tree that is a
     * OverlapsAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOverlapsAfter(elm: OverlapsAfter, context: C): T

    /**
     * Visit a Starts. This method will be called for every node in the tree that is a Starts.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitStarts(elm: Starts, context: C): T

    /**
     * Visit an Ends. This method will be called for every node in the tree that is an Ends.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitEnds(elm: Ends, context: C): T

    /**
     * Visit a Collapse. This method will be called for every node in the tree that is a Collapse.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCollapse(elm: Collapse, context: C): T

    /**
     * Visit an Expand. This method will be called for every node in the tree that is an Expand.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExpand(elm: Expand, context: C): T

    /**
     * Visit a Union. This method will be called for every node in the tree that is a Union.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitUnion(elm: Union, context: C): T

    /**
     * Visit an Intersect. This method will be called for every node in the tree that is an
     * Intersect.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIntersect(elm: Intersect, context: C): T

    /**
     * Visit an Except. This method will be called for every node in the tree that is an Except.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExcept(elm: Except, context: C): T

    /**
     * Visit a Literal. This method will be called for every node in the tree that is a Literal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitExists(elm: Exists, context: C): T

    /**
     * Visit a Times. This method will be called for every node in the tree that is a Times.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTimes(elm: Times, context: C): T

    /**
     * Visit a Filter. This method will be called for every node in the tree that is a Filter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitFilter(elm: Filter, context: C): T

    /**
     * Visit a First. This method will be called for every node in the tree that is a First.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitFirst(elm: First, context: C): T

    /**
     * Visit a Last. This method will be called for every node in the tree that is a Last.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLast(elm: Last, context: C): T

    /**
     * Visit a Slice. This method will be called for every node in the tree that is a Slice.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSlice(elm: Slice, context: C): T

    /**
     * Visit a Children. This method will be called for every node in the tree that is a Children.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitChildren(elm: Children, context: C): T

    /**
     * Visit a Descendents. This method will be called for every node in the tree that is a
     * Descendents.
     *
     * Deprecated, use Descendants
     *
     * CQL 1.5.3 corrected the spelling to Descendants
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Deprecated("Use visitDescendants instead", replaceWith = ReplaceWith("visitDescendants"))
    fun visitDescendents(elm: Descendents, context: C): T

    /**
     * Visit a Descendants. This method will be called for every node in the tree that is a
     * Descendants.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDescendants(elm: Descendants, context: C): T

    /**
     * Visit a Message. This method will be called for every node in the tree that is a Message.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMessage(elm: Message, context: C): T

    /**
     * Visit an IndexOf. This method will be called for every node in the tree that is an IndexOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIndexOf(elm: IndexOf, context: C): T

    /**
     * Visit a Flatten. This method will be called for every node in the tree that is a Flatten.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitFlatten(elm: Flatten, context: C): T

    /**
     * Visit a Sort. This method will be called for every node in the tree that is a Sort.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSort(elm: Sort, context: C): T

    /**
     * Visit a ForEach. This method will be called for every node in the tree that is a ForEach.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitForEach(elm: ForEach, context: C): T

    /**
     * Visit a Repeat. This method will be called for every node in the tree that is a Repeat.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitRepeat(elm: Repeat, context: C): T

    /**
     * Visit a Distinct. This method will be called for every node in the tree that is a Distinct.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDistinct(elm: Distinct, context: C): T

    /**
     * Visit a Current. This method will be called for every node in the tree that is a Current.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCurrent(elm: Current, context: C): T

    /**
     * Visit an Iteration. This method will be called for every node in the tree that is an
     * Iteration.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIteration(elm: Iteration, context: C): T

    /**
     * Visit a Total. This method will be called for every node in the tree that is a Total.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitTotal(elm: Total, context: C): T

    /**
     * Visit a SingletonFrom. This method will be called for every node in the tree that is a
     * SingletonFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSingletonFrom(elm: SingletonFrom, context: C): T

    /**
     * Visit an AggregateExpression. This method will be called for every node in the tree that is a
     * AggregateExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAggregateExpression(elm: AggregateExpression, context: C): T

    /**
     * Visit an Aggregate. This method will be called for every node in the tree that is an
     * Aggregate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAggregate(elm: Aggregate, context: C): T

    /**
     * Visit a Count. This method will be called for every node in the tree that is a Count.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCount(elm: Count, context: C): T

    /**
     * Visit a Sum. This method will be called for every node in the tree that is a Sum.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSum(elm: Sum, context: C): T

    /**
     * Visit a Product. This method will be called for every node in the tree that is a Product.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitProduct(elm: Product, context: C): T

    /**
     * Visit a GeometricMean. This method will be called for every node in the tree that is a
     * GeometricMean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitGeometricMean(elm: GeometricMean, context: C): T

    /**
     * Visit a Min. This method will be called for every node in the tree that is a Min.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMin(elm: Min, context: C): T

    /**
     * Visit a Max. This method will be called for every node in the tree that is a Max.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMax(elm: Max, context: C): T

    /**
     * Visit an Avg. This method will be called for every node in the tree that is an Avg.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAvg(elm: Avg, context: C): T

    /**
     * Visit a Median. This method will be called for every node in the tree that is a Median.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMedian(elm: Median, context: C): T

    /**
     * Visit a Mode. This method will be called for every node in the tree that is a Mode.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitMode(elm: Mode, context: C): T

    /**
     * Visit a Variance. This method will be called for every node in the tree that is a Variance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitVariance(elm: Variance, context: C): T

    /**
     * Visit a PopulationVariance. This method will be called for every node in the tree that is a
     * PopulationVariance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPopulationVariance(elm: PopulationVariance, context: C): T

    /**
     * Visit a StdDev. This method will be called for every node in the tree that is a StdDev.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitStdDev(elm: StdDev, context: C): T

    /**
     * Visit a PopulationStdDev. This method will be called for every node in the tree that is a
     * PopulationStdDev.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitPopulationStdDev(elm: PopulationStdDev, context: C): T

    /**
     * Visit an AllTrue. This method will be called for every node in the tree that is an AllTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAllTrue(elm: AllTrue, context: C): T

    /**
     * Visit an AnyTrue. This method will be called for every node in the tree that is an AnyTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAnyTrue(elm: AnyTrue, context: C): T

    /**
     * Visit a Property. This method will be called for every node in the tree that is a Property.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitProperty(elm: Property, context: C): T

    /**
     * Visit an AliasedQuerySource. This method will be called for every node in the tree that is a
     * AliasedQuerySource.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAliasedQuerySource(elm: AliasedQuerySource, context: C): T

    /**
     * Visit a LetClause. This method will be called for every node in the tree that is a LetClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLetClause(elm: LetClause, context: C): T

    /**
     * Visit a RelationshipClause. This method will be called for every node in the tree that is a
     * RelationshipClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitRelationshipClause(elm: RelationshipClause, context: C): T

    /**
     * Visit a With. This method will be called for every node in the tree that is a With.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitWith(elm: With, context: C): T

    /**
     * Visit a Without. This method will be called for every node in the tree that is a Without.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitWithout(elm: Without, context: C): T

    /**
     * Visit a SortByItem. This method will be called for every node in the tree that is a
     * SortByItem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSortByItem(elm: SortByItem, context: C): T

    /**
     * Visit a ByDirection. This method will be called for every node in the tree that is a
     * ByDirection.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitByDirection(elm: ByDirection, context: C): T

    /**
     * Visit a ByColumn. This method will be called for every node in the tree that is a ByColumn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitByColumn(elm: ByColumn, context: C): T

    /**
     * Visit a ByExpression. This method will be called for every node in the tree that is a
     * ByExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitByExpression(elm: ByExpression, context: C): T

    /**
     * Visit a SortClause. This method will be called for every node in the tree that is a
     * SortClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSortClause(elm: SortClause, context: C): T

    /**
     * Visit an AggregateClause. This method will be called for every node in the tree that is an
     * AggregateClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAggregateClause(elm: AggregateClause, context: C): T

    /**
     * Visit a ReturnClause. This method will be called for every node in the tree that is a
     * ReturnClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitReturnClause(elm: ReturnClause, context: C): T

    /**
     * Visit a Query. This method will be called for every node in the tree that is a Query.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitQuery(elm: Query, context: C): T

    /**
     * Visit an AliasRef. This method will be called for every node in the tree that is an AliasRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAliasRef(elm: AliasRef, context: C): T

    /**
     * Visit a QueryLetRef. This method will be called for every node in the tree that is a
     * QueryLetRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitQueryLetRef(elm: QueryLetRef, context: C): T
}
