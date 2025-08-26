package org.cqframework.cql.elm

import org.hl7.elm.r1.Abs
import org.hl7.elm.r1.Add
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Aggregate
import org.hl7.elm.r1.AggregateClause
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.AllTrue
import org.hl7.elm.r1.And
import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.AnyInValueSet
import org.hl7.elm.r1.AnyTrue
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Avg
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.ByDirection
import org.hl7.elm.r1.ByExpression
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CalculateAgeAt
import org.hl7.elm.r1.CanConvert
import org.hl7.elm.r1.CanConvertQuantity
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Ceiling
import org.hl7.elm.r1.Children
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeFilterElement
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.Collapse
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.ContextDef
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
import org.hl7.elm.r1.DateFilterElement
import org.hl7.elm.r1.DateFrom
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.DateTimeComponentFrom
import org.hl7.elm.r1.Descendants
import org.hl7.elm.r1.Descendents
import org.hl7.elm.r1.DifferenceBetween
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.Divide
import org.hl7.elm.r1.DurationBetween
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Ends
import org.hl7.elm.r1.EndsWith
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Except
import org.hl7.elm.r1.Exists
import org.hl7.elm.r1.Exp
import org.hl7.elm.r1.Expand
import org.hl7.elm.r1.ExpandValueSet
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
import org.hl7.elm.r1.InCodeSystem
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Includes
import org.hl7.elm.r1.IndexOf
import org.hl7.elm.r1.Indexer
import org.hl7.elm.r1.Instance
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
import org.hl7.elm.r1.Library
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
import org.hl7.elm.r1.Negate
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.NotEqual
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.ObjectFactory
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.OtherFilterElement
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
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Repeat
import org.hl7.elm.r1.ReplaceMatches
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ReturnClause
import org.hl7.elm.r1.Round
import org.hl7.elm.r1.SameAs
import org.hl7.elm.r1.SameOrAfter
import org.hl7.elm.r1.SameOrBefore
import org.hl7.elm.r1.Search
import org.hl7.elm.r1.SingletonFrom
import org.hl7.elm.r1.Size
import org.hl7.elm.r1.Slice
import org.hl7.elm.r1.Sort
import org.hl7.elm.r1.SortClause
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.Starts
import org.hl7.elm.r1.StartsWith
import org.hl7.elm.r1.StdDev
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.SubsumedBy
import org.hl7.elm.r1.Subsumes
import org.hl7.elm.r1.Subtract
import org.hl7.elm.r1.Successor
import org.hl7.elm.r1.Sum
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
import org.hl7.elm.r1.TupleElementDefinition
import org.hl7.elm.r1.TupleTypeSpecifier
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.Upper
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.Variance
import org.hl7.elm.r1.Width
import org.hl7.elm.r1.With
import org.hl7.elm.r1.Without
import org.hl7.elm.r1.Xor

/*
 * Extends the ObjectFactory to allow for assigning ids to the elements created by the factory. Elements are
 * given monotonically increasing ids.
 *
 */
@Suppress("detekt:all")
open class IdObjectFactory : ObjectFactory() {
    private var nextId = 0

    /** returns the next id and increments the counter */
    open fun nextId(): String {
        return nextId++.toString()
    }

    override fun createAbs(): Abs {
        return super.createAbs().withLocalId(nextId())
    }

    override fun createAdd(): Add {
        return super.createAdd().withLocalId(nextId())
    }

    override fun createAfter(): After {
        return super.createAfter().withLocalId(nextId())
    }

    override fun createAggregate(): Aggregate {
        return super.createAggregate().withLocalId(nextId())
    }

    override fun createAggregateClause(): AggregateClause {
        return super.createAggregateClause().withLocalId(nextId())
    }

    override fun createAliasRef(): AliasRef {
        return super.createAliasRef().withLocalId(nextId())
    }

    override fun createAliasedQuerySource(): AliasedQuerySource {
        return super.createAliasedQuerySource().withLocalId(nextId())
    }

    override fun createAllTrue(): AllTrue {
        return super.createAllTrue().withLocalId(nextId())
    }

    override fun createAnd(): And {
        return super.createAnd().withLocalId(nextId())
    }

    override fun createAnyInCodeSystem(): AnyInCodeSystem {
        return super.createAnyInCodeSystem().withLocalId(nextId())
    }

    override fun createAnyInValueSet(): AnyInValueSet {
        return super.createAnyInValueSet().withLocalId(nextId())
    }

    override fun createAnyTrue(): AnyTrue {
        return super.createAnyTrue().withLocalId(nextId())
    }

    override fun createAs(): As {
        return super.createAs().withLocalId(nextId())
    }

    override fun createAvg(): Avg {
        return super.createAvg().withLocalId(nextId())
    }

    override fun createBefore(): Before {
        return super.createBefore().withLocalId(nextId())
    }

    override fun createByColumn(): ByColumn {
        return super.createByColumn().withLocalId(nextId())
    }

    override fun createByDirection(): ByDirection {
        return super.createByDirection().withLocalId(nextId())
    }

    override fun createByExpression(): ByExpression {
        return super.createByExpression().withLocalId(nextId())
    }

    override fun createCalculateAge(): CalculateAge {
        return super.createCalculateAge().withLocalId(nextId())
    }

    override fun createCalculateAgeAt(): CalculateAgeAt {
        return super.createCalculateAgeAt().withLocalId(nextId())
    }

    override fun createCanConvert(): CanConvert {
        return super.createCanConvert().withLocalId(nextId())
    }

    override fun createCanConvertQuantity(): CanConvertQuantity {
        return super.createCanConvertQuantity().withLocalId(nextId())
    }

    override fun createCase(): Case {
        return super.createCase().withLocalId(nextId())
    }

    override fun createCaseItem(): CaseItem {
        return super.createCaseItem().withLocalId(nextId())
    }

    override fun createCeiling(): Ceiling {
        return super.createCeiling().withLocalId(nextId())
    }

    override fun createChildren(): Children {
        return super.createChildren().withLocalId(nextId())
    }

    override fun createChoiceTypeSpecifier(): ChoiceTypeSpecifier {
        return super.createChoiceTypeSpecifier().withLocalId(nextId())
    }

    override fun createCoalesce(): Coalesce {
        return super.createCoalesce().withLocalId(nextId())
    }

    override fun createCode(): Code {
        return super.createCode().withLocalId(nextId())
    }

    override fun createCodeDef(): CodeDef {
        return super.createCodeDef().withLocalId(nextId())
    }

    override fun createCodeFilterElement(): CodeFilterElement {
        return super.createCodeFilterElement().withLocalId(nextId())
    }

    override fun createCodeRef(): CodeRef {
        return super.createCodeRef().withLocalId(nextId())
    }

    override fun createCodeSystemDef(): CodeSystemDef {
        return super.createCodeSystemDef().withLocalId(nextId())
    }

    override fun createCodeSystemRef(): CodeSystemRef {
        return super.createCodeSystemRef().withLocalId(nextId())
    }

    override fun createCollapse(): Collapse {
        return super.createCollapse().withLocalId(nextId())
    }

    override fun createCombine(): Combine {
        return super.createCombine().withLocalId(nextId())
    }

    override fun createConcatenate(): Concatenate {
        return super.createConcatenate().withLocalId(nextId())
    }

    override fun createConcept(): Concept {
        return super.createConcept().withLocalId(nextId())
    }

    override fun createConceptDef(): ConceptDef {
        return super.createConceptDef().withLocalId(nextId())
    }

    override fun createConceptRef(): ConceptRef {
        return super.createConceptRef().withLocalId(nextId())
    }

    override fun createContains(): Contains {
        return super.createContains().withLocalId(nextId())
    }

    override fun createContextDef(): ContextDef {
        return super.createContextDef().withLocalId(nextId())
    }

    override fun createConvert(): Convert {
        return super.createConvert().withLocalId(nextId())
    }

    override fun createConvertQuantity(): ConvertQuantity {
        return super.createConvertQuantity().withLocalId(nextId())
    }

    override fun createConvertsToBoolean(): ConvertsToBoolean {
        return super.createConvertsToBoolean().withLocalId(nextId())
    }

    override fun createConvertsToDate(): ConvertsToDate {
        return super.createConvertsToDate().withLocalId(nextId())
    }

    override fun createConvertsToDateTime(): ConvertsToDateTime {
        return super.createConvertsToDateTime().withLocalId(nextId())
    }

    override fun createConvertsToDecimal(): ConvertsToDecimal {
        return super.createConvertsToDecimal().withLocalId(nextId())
    }

    override fun createConvertsToInteger(): ConvertsToInteger {
        return super.createConvertsToInteger().withLocalId(nextId())
    }

    override fun createConvertsToLong(): ConvertsToLong {
        return super.createConvertsToLong().withLocalId(nextId())
    }

    override fun createConvertsToQuantity(): ConvertsToQuantity {
        return super.createConvertsToQuantity().withLocalId(nextId())
    }

    override fun createConvertsToRatio(): ConvertsToRatio {
        return super.createConvertsToRatio().withLocalId(nextId())
    }

    override fun createConvertsToString(): ConvertsToString {
        return super.createConvertsToString().withLocalId(nextId())
    }

    override fun createConvertsToTime(): ConvertsToTime {
        return super.createConvertsToTime().withLocalId(nextId())
    }

    override fun createCount(): Count {
        return super.createCount().withLocalId(nextId())
    }

    override fun createCurrent(): Current {
        return super.createCurrent().withLocalId(nextId())
    }

    override fun createDate(): Date {
        return super.createDate().withLocalId(nextId())
    }

    override fun createDateFilterElement(): DateFilterElement {
        return super.createDateFilterElement().withLocalId(nextId())
    }

    override fun createDateFrom(): DateFrom {
        return super.createDateFrom().withLocalId(nextId())
    }

    override fun createDateTime(): DateTime {
        return super.createDateTime().withLocalId(nextId())
    }

    override fun createDateTimeComponentFrom(): DateTimeComponentFrom {
        return super.createDateTimeComponentFrom().withLocalId(nextId())
    }

    /**
     * Deprecated, use Descendants
     *
     * CQL 1.5.3 corrected the spelling to Descendants
     *
     * @deprecated since 3.28.0
     */
    @Deprecated("Use createDescendants instead", replaceWith = ReplaceWith("createDescendants"))
    override fun createDescendents(): Descendents {
        return super.createDescendents().withLocalId(nextId())
    }

    override fun createDescendants(): Descendants {
        return super.createDescendants().withLocalId(nextId())
    }

    override fun createDifferenceBetween(): DifferenceBetween {
        return super.createDifferenceBetween().withLocalId(nextId())
    }

    override fun createDistinct(): Distinct {
        return super.createDistinct().withLocalId(nextId())
    }

    override fun createDivide(): Divide {
        return super.createDivide().withLocalId(nextId())
    }

    override fun createDurationBetween(): DurationBetween {
        return super.createDurationBetween().withLocalId(nextId())
    }

    override fun createEnd(): End {
        return super.createEnd().withLocalId(nextId())
    }

    override fun createEnds(): Ends {
        return super.createEnds().withLocalId(nextId())
    }

    override fun createEndsWith(): EndsWith {
        return super.createEndsWith().withLocalId(nextId())
    }

    override fun createEqual(): Equal {
        return super.createEqual().withLocalId(nextId())
    }

    override fun createEquivalent(): Equivalent {
        return super.createEquivalent().withLocalId(nextId())
    }

    override fun createExcept(): Except {
        return super.createExcept().withLocalId(nextId())
    }

    override fun createExists(): Exists {
        return super.createExists().withLocalId(nextId())
    }

    override fun createExp(): Exp {
        return super.createExp().withLocalId(nextId())
    }

    override fun createExpand(): Expand {
        return super.createExpand().withLocalId(nextId())
    }

    override fun createExpandValueSet(): ExpandValueSet {
        return super.createExpandValueSet().withLocalId(nextId())
    }

    override fun createExpressionDef(): ExpressionDef {
        return super.createExpressionDef().withLocalId(nextId())
    }

    override fun createExpressionRef(): ExpressionRef {
        return super.createExpressionRef().withLocalId(nextId())
    }

    override fun createFilter(): Filter {
        return super.createFilter().withLocalId(nextId())
    }

    override fun createFirst(): First {
        return super.createFirst().withLocalId(nextId())
    }

    override fun createFlatten(): Flatten {
        return super.createFlatten().withLocalId(nextId())
    }

    override fun createFloor(): Floor {
        return super.createFloor().withLocalId(nextId())
    }

    override fun createForEach(): ForEach {
        return super.createForEach().withLocalId(nextId())
    }

    override fun createFunctionDef(): FunctionDef {
        return super.createFunctionDef().withLocalId(nextId())
    }

    override fun createFunctionRef(): FunctionRef {
        return super.createFunctionRef().withLocalId(nextId())
    }

    override fun createGeometricMean(): GeometricMean {
        return super.createGeometricMean().withLocalId(nextId())
    }

    override fun createGreater(): Greater {
        return super.createGreater().withLocalId(nextId())
    }

    override fun createGreaterOrEqual(): GreaterOrEqual {
        return super.createGreaterOrEqual().withLocalId(nextId())
    }

    override fun createHighBoundary(): HighBoundary {
        return super.createHighBoundary().withLocalId(nextId())
    }

    override fun createIdentifierRef(): IdentifierRef {
        return super.createIdentifierRef().withLocalId(nextId())
    }

    override fun createIf(): If {
        return super.createIf().withLocalId(nextId())
    }

    override fun createImplies(): Implies {
        return super.createImplies().withLocalId(nextId())
    }

    override fun createIn(): In {
        return super.createIn().withLocalId(nextId())
    }

    override fun createInCodeSystem(): InCodeSystem {
        return super.createInCodeSystem().withLocalId(nextId())
    }

    override fun createInValueSet(): InValueSet {
        return super.createInValueSet().withLocalId(nextId())
    }

    override fun createIncludeDef(): IncludeDef {
        return super.createIncludeDef().withLocalId(nextId())
    }

    override fun createIncludeElement(): IncludeElement {
        return super.createIncludeElement().withLocalId(nextId())
    }

    override fun createIncludedIn(): IncludedIn {
        return super.createIncludedIn().withLocalId(nextId())
    }

    override fun createIncludes(): Includes {
        return super.createIncludes().withLocalId(nextId())
    }

    override fun createIndexOf(): IndexOf {
        return super.createIndexOf().withLocalId(nextId())
    }

    override fun createIndexer(): Indexer {
        return super.createIndexer().withLocalId(nextId())
    }

    override fun createInstance(): Instance {
        return super.createInstance().withLocalId(nextId())
    }

    override fun createIntersect(): Intersect {
        return super.createIntersect().withLocalId(nextId())
    }

    override fun createInterval(): Interval {
        return super.createInterval().withLocalId(nextId())
    }

    override fun createIntervalTypeSpecifier(): IntervalTypeSpecifier {
        return super.createIntervalTypeSpecifier().withLocalId(nextId())
    }

    override fun createIs(): Is {
        return super.createIs().withLocalId(nextId())
    }

    override fun createIsFalse(): IsFalse {
        return super.createIsFalse().withLocalId(nextId())
    }

    override fun createIsNull(): IsNull {
        return super.createIsNull().withLocalId(nextId())
    }

    override fun createIsTrue(): IsTrue {
        return super.createIsTrue().withLocalId(nextId())
    }

    override fun createIteration(): Iteration {
        return super.createIteration().withLocalId(nextId())
    }

    override fun createLast(): Last {
        return super.createLast().withLocalId(nextId())
    }

    override fun createLastPositionOf(): LastPositionOf {
        return super.createLastPositionOf().withLocalId(nextId())
    }

    override fun createLength(): Length {
        return super.createLength().withLocalId(nextId())
    }

    override fun createLess(): Less {
        return super.createLess().withLocalId(nextId())
    }

    override fun createLessOrEqual(): LessOrEqual {
        return super.createLessOrEqual().withLocalId(nextId())
    }

    override fun createLetClause(): LetClause {
        return super.createLetClause().withLocalId(nextId())
    }

    override fun createLibrary(): Library {
        return super.createLibrary().withLocalId(nextId())
    }

    override fun createList(): List {
        return super.createList().withLocalId(nextId())
    }

    override fun createListTypeSpecifier(): ListTypeSpecifier {
        return super.createListTypeSpecifier().withLocalId(nextId())
    }

    override fun createLiteral(): Literal {
        return super.createLiteral().withLocalId(nextId())
    }

    override fun createLn(): Ln {
        return super.createLn().withLocalId(nextId())
    }

    override fun createLog(): Log {
        return super.createLog().withLocalId(nextId())
    }

    override fun createLowBoundary(): LowBoundary {
        return super.createLowBoundary().withLocalId(nextId())
    }

    override fun createLower(): Lower {
        return super.createLower().withLocalId(nextId())
    }

    override fun createMatches(): Matches {
        return super.createMatches().withLocalId(nextId())
    }

    override fun createMax(): Max {
        return super.createMax().withLocalId(nextId())
    }

    override fun createMaxValue(): MaxValue {
        return super.createMaxValue().withLocalId(nextId())
    }

    override fun createMedian(): Median {
        return super.createMedian().withLocalId(nextId())
    }

    override fun createMeets(): Meets {
        return super.createMeets().withLocalId(nextId())
    }

    override fun createMeetsAfter(): MeetsAfter {
        return super.createMeetsAfter().withLocalId(nextId())
    }

    override fun createMeetsBefore(): MeetsBefore {
        return super.createMeetsBefore().withLocalId(nextId())
    }

    override fun createMessage(): Message {
        return super.createMessage().withLocalId(nextId())
    }

    override fun createMin(): Min {
        return super.createMin().withLocalId(nextId())
    }

    override fun createMinValue(): MinValue {
        return super.createMinValue().withLocalId(nextId())
    }

    override fun createMode(): Mode {
        return super.createMode().withLocalId(nextId())
    }

    override fun createModulo(): Modulo {
        return super.createModulo().withLocalId(nextId())
    }

    override fun createMultiply(): Multiply {
        return super.createMultiply().withLocalId(nextId())
    }

    override fun createNamedTypeSpecifier(): NamedTypeSpecifier {
        return super.createNamedTypeSpecifier().withLocalId(nextId())
    }

    override fun createNegate(): Negate {
        return super.createNegate().withLocalId(nextId())
    }

    override fun createNot(): Not {
        return super.createNot().withLocalId(nextId())
    }

    override fun createNotEqual(): NotEqual {
        return super.createNotEqual().withLocalId(nextId())
    }

    override fun createNow(): Now {
        return super.createNow().withLocalId(nextId())
    }

    override fun createNull(): Null {
        return super.createNull().withLocalId(nextId())
    }

    override fun createOperandDef(): OperandDef {
        return super.createOperandDef().withLocalId(nextId())
    }

    override fun createOperandRef(): OperandRef {
        return super.createOperandRef().withLocalId(nextId())
    }

    override fun createOr(): Or {
        return super.createOr().withLocalId(nextId())
    }

    override fun createOtherFilterElement(): OtherFilterElement {
        return super.createOtherFilterElement().withLocalId(nextId())
    }

    override fun createOverlaps(): Overlaps {
        return super.createOverlaps().withLocalId(nextId())
    }

    override fun createOverlapsAfter(): OverlapsAfter {
        return super.createOverlapsAfter().withLocalId(nextId())
    }

    override fun createOverlapsBefore(): OverlapsBefore {
        return super.createOverlapsBefore().withLocalId(nextId())
    }

    override fun createParameterDef(): ParameterDef {
        return super.createParameterDef().withLocalId(nextId())
    }

    override fun createParameterRef(): ParameterRef {
        return super.createParameterRef().withLocalId(nextId())
    }

    override fun createParameterTypeSpecifier(): ParameterTypeSpecifier {
        return super.createParameterTypeSpecifier().withLocalId(nextId())
    }

    override fun createPointFrom(): PointFrom {
        return super.createPointFrom().withLocalId(nextId())
    }

    override fun createPopulationStdDev(): PopulationStdDev {
        return super.createPopulationStdDev().withLocalId(nextId())
    }

    override fun createPopulationVariance(): PopulationVariance {
        return super.createPopulationVariance().withLocalId(nextId())
    }

    override fun createPositionOf(): PositionOf {
        return super.createPositionOf().withLocalId(nextId())
    }

    override fun createPower(): Power {
        return super.createPower().withLocalId(nextId())
    }

    override fun createPrecision(): Precision {
        return super.createPrecision().withLocalId(nextId())
    }

    override fun createPredecessor(): Predecessor {
        return super.createPredecessor().withLocalId(nextId())
    }

    override fun createProduct(): Product {
        return super.createProduct().withLocalId(nextId())
    }

    override fun createProperContains(): ProperContains {
        return super.createProperContains().withLocalId(nextId())
    }

    override fun createProperIn(): ProperIn {
        return super.createProperIn().withLocalId(nextId())
    }

    override fun createProperIncludedIn(): ProperIncludedIn {
        return super.createProperIncludedIn().withLocalId(nextId())
    }

    override fun createProperIncludes(): ProperIncludes {
        return super.createProperIncludes().withLocalId(nextId())
    }

    override fun createProperty(): Property {
        return super.createProperty().withLocalId(nextId())
    }

    override fun createQuantity(): Quantity {
        return super.createQuantity().withLocalId(nextId())
    }

    override fun createQuery(): Query {
        return super.createQuery().withLocalId(nextId())
    }

    override fun createQueryLetRef(): QueryLetRef {
        return super.createQueryLetRef().withLocalId(nextId())
    }

    override fun createRatio(): Ratio {
        return super.createRatio().withLocalId(nextId())
    }

    override fun createRepeat(): Repeat {
        return super.createRepeat().withLocalId(nextId())
    }

    override fun createReplaceMatches(): ReplaceMatches {
        return super.createReplaceMatches().withLocalId(nextId())
    }

    override fun createRetrieve(): Retrieve {
        return super.createRetrieve().withLocalId(nextId())
    }

    override fun createReturnClause(): ReturnClause {
        return super.createReturnClause().withLocalId(nextId())
    }

    override fun createRound(): Round {
        return super.createRound().withLocalId(nextId())
    }

    override fun createSameAs(): SameAs {
        return super.createSameAs().withLocalId(nextId())
    }

    override fun createSameOrAfter(): SameOrAfter {
        return super.createSameOrAfter().withLocalId(nextId())
    }

    override fun createSameOrBefore(): SameOrBefore {
        return super.createSameOrBefore().withLocalId(nextId())
    }

    override fun createSearch(): Search {
        return super.createSearch().withLocalId(nextId())
    }

    override fun createSingletonFrom(): SingletonFrom {
        return super.createSingletonFrom().withLocalId(nextId())
    }

    override fun createSize(): Size {
        return super.createSize().withLocalId(nextId())
    }

    override fun createSlice(): Slice {
        return super.createSlice().withLocalId(nextId())
    }

    override fun createSort(): Sort {
        return super.createSort().withLocalId(nextId())
    }

    override fun createSortClause(): SortClause {
        return super.createSortClause().withLocalId(nextId())
    }

    override fun createSplit(): Split {
        return super.createSplit().withLocalId(nextId())
    }

    override fun createSplitOnMatches(): SplitOnMatches {
        return super.createSplitOnMatches().withLocalId(nextId())
    }

    override fun createStart(): Start {
        return super.createStart().withLocalId(nextId())
    }

    override fun createStarts(): Starts {
        return super.createStarts().withLocalId(nextId())
    }

    override fun createStartsWith(): StartsWith {
        return super.createStartsWith().withLocalId(nextId())
    }

    override fun createStdDev(): StdDev {
        return super.createStdDev().withLocalId(nextId())
    }

    override fun createSubstring(): Substring {
        return super.createSubstring().withLocalId(nextId())
    }

    override fun createSubsumedBy(): SubsumedBy {
        return super.createSubsumedBy().withLocalId(nextId())
    }

    override fun createSubsumes(): Subsumes {
        return super.createSubsumes().withLocalId(nextId())
    }

    override fun createSubtract(): Subtract {
        return super.createSubtract().withLocalId(nextId())
    }

    override fun createSuccessor(): Successor {
        return super.createSuccessor().withLocalId(nextId())
    }

    override fun createSum(): Sum {
        return super.createSum().withLocalId(nextId())
    }

    override fun createTime(): Time {
        return super.createTime().withLocalId(nextId())
    }

    override fun createTimeFrom(): TimeFrom {
        return super.createTimeFrom().withLocalId(nextId())
    }

    override fun createTimeOfDay(): TimeOfDay {
        return super.createTimeOfDay().withLocalId(nextId())
    }

    override fun createTimes(): Times {
        return super.createTimes().withLocalId(nextId())
    }

    override fun createTimezoneFrom(): TimezoneFrom {
        return super.createTimezoneFrom().withLocalId(nextId())
    }

    override fun createTimezoneOffsetFrom(): TimezoneOffsetFrom {
        return super.createTimezoneOffsetFrom().withLocalId(nextId())
    }

    override fun createToBoolean(): ToBoolean {
        return super.createToBoolean().withLocalId(nextId())
    }

    override fun createToChars(): ToChars {
        return super.createToChars().withLocalId(nextId())
    }

    override fun createToConcept(): ToConcept {
        return super.createToConcept().withLocalId(nextId())
    }

    override fun createToDate(): ToDate {
        return super.createToDate().withLocalId(nextId())
    }

    override fun createToDateTime(): ToDateTime {
        return super.createToDateTime().withLocalId(nextId())
    }

    override fun createToDecimal(): ToDecimal {
        return super.createToDecimal().withLocalId(nextId())
    }

    override fun createToInteger(): ToInteger {
        return super.createToInteger().withLocalId(nextId())
    }

    override fun createToList(): ToList {
        return super.createToList().withLocalId(nextId())
    }

    override fun createToLong(): ToLong {
        return super.createToLong().withLocalId(nextId())
    }

    override fun createToQuantity(): ToQuantity {
        return super.createToQuantity().withLocalId(nextId())
    }

    override fun createToRatio(): ToRatio {
        return super.createToRatio().withLocalId(nextId())
    }

    override fun createToString(): ToString {
        return super.createToString().withLocalId(nextId())
    }

    override fun createToTime(): ToTime {
        return super.createToTime().withLocalId(nextId())
    }

    override fun createToday(): Today {
        return super.createToday().withLocalId(nextId())
    }

    override fun createTotal(): Total {
        return super.createTotal().withLocalId(nextId())
    }

    override fun createTruncate(): Truncate {
        return super.createTruncate().withLocalId(nextId())
    }

    override fun createTruncatedDivide(): TruncatedDivide {
        return super.createTruncatedDivide().withLocalId(nextId())
    }

    override fun createTuple(): Tuple {
        return super.createTuple().withLocalId(nextId())
    }

    override fun createTupleElementDefinition(): TupleElementDefinition {
        return super.createTupleElementDefinition().withLocalId(nextId())
    }

    override fun createTupleTypeSpecifier(): TupleTypeSpecifier {
        return super.createTupleTypeSpecifier().withLocalId(nextId())
    }

    override fun createUnion(): Union {
        return super.createUnion().withLocalId(nextId())
    }

    override fun createUpper(): Upper {
        return super.createUpper().withLocalId(nextId())
    }

    override fun createUsingDef(): UsingDef {
        return super.createUsingDef().withLocalId(nextId())
    }

    override fun createValueSetDef(): ValueSetDef {
        return super.createValueSetDef().withLocalId(nextId())
    }

    override fun createValueSetRef(): ValueSetRef {
        return super.createValueSetRef().withLocalId(nextId())
    }

    override fun createVariance(): Variance {
        return super.createVariance().withLocalId(nextId())
    }

    override fun createWidth(): Width {
        return super.createWidth().withLocalId(nextId())
    }

    override fun createWith(): With {
        return super.createWith().withLocalId(nextId())
    }

    override fun createWithout(): Without {
        return super.createWithout().withLocalId(nextId())
    }

    override fun createXor(): Xor {
        return super.createXor().withLocalId(nextId())
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
fun IdObjectFactory.commonCreateExpression(expressionType: String): Expression {
    return when (expressionType) {
        "Abs" -> createAbs()
        "Add" -> createAdd()
        "After" -> createAfter()
        "Aggregate" -> createAggregate()
        "AliasRef" -> createAliasRef()
        "AllTrue" -> createAllTrue()
        "And" -> createAnd()
        "AnyInCodeSystem" -> createAnyInCodeSystem()
        "AnyInValueSet" -> createAnyInValueSet()
        "AnyTrue" -> createAnyTrue()
        "As" -> createAs()
        "Avg" -> createAvg()
        "Before" -> createBefore()
        "CalculateAge" -> createCalculateAge()
        "CalculateAgeAt" -> createCalculateAgeAt()
        "CanConvert" -> createCanConvert()
        "CanConvertQuantity" -> createCanConvertQuantity()
        "Case" -> createCase()
        "Ceiling" -> createCeiling()
        "Children" -> createChildren()
        "Coalesce" -> createCoalesce()
        "Code" -> createCode()
        "CodeRef" -> createCodeRef()
        "CodeSystemRef" -> createCodeSystemRef()
        "Collapse" -> createCollapse()
        "Combine" -> createCombine()
        "Concatenate" -> createConcatenate()
        "Concept" -> createConcept()
        "ConceptRef" -> createConceptRef()
        "Contains" -> createContains()
        "Convert" -> createConvert()
        "ConvertQuantity" -> createConvertQuantity()
        "ConvertsToBoolean" -> createConvertsToBoolean()
        "ConvertsToDate" -> createConvertsToDate()
        "ConvertsToDateTime" -> createConvertsToDateTime()
        "ConvertsToDecimal" -> createConvertsToDecimal()
        "ConvertsToInteger" -> createConvertsToInteger()
        "ConvertsToLong" -> createConvertsToLong()
        "ConvertsToQuantity" -> createConvertsToQuantity()
        "ConvertsToRatio" -> createConvertsToRatio()
        "ConvertsToString" -> createConvertsToString()
        "ConvertsToTime" -> createConvertsToTime()
        "Count" -> createCount()
        "Current" -> createCurrent()
        "Date" -> createDate()
        "DateFrom" -> createDateFrom()
        "DateTime" -> createDateTime()
        "DateTimeComponentFrom" -> createDateTimeComponentFrom()
        "Descendents" -> createDescendents()
        "DifferenceBetween" -> createDifferenceBetween()
        "Distinct" -> createDistinct()
        "Divide" -> createDivide()
        "DurationBetween" -> createDurationBetween()
        "End" -> createEnd()
        "Ends" -> createEnds()
        "EndsWith" -> createEndsWith()
        "Equal" -> createEqual()
        "Equivalent" -> createEquivalent()
        "Except" -> createExcept()
        "Exists" -> createExists()
        "Exp" -> createExp()
        "Expand" -> createExpand()
        "ExpandValueSet" -> createExpandValueSet()
        "ExpressionRef" -> createExpressionRef()
        "Filter" -> createFilter()
        "First" -> createFirst()
        "Flatten" -> createFlatten()
        "Floor" -> createFloor()
        "ForEach" -> createForEach()
        "FunctionRef" -> createFunctionRef()
        "GeometricMean" -> createGeometricMean()
        "Greater" -> createGreater()
        "GreaterOrEqual" -> createGreaterOrEqual()
        "HighBoundary" -> createHighBoundary()
        "IdentifierRef" -> createIdentifierRef()
        "If" -> createIf()
        "Implies" -> createImplies()
        "In" -> createIn()
        "InCodeSystem" -> createInCodeSystem()
        "InValueSet" -> createInValueSet()
        "IncludedIn" -> createIncludedIn()
        "Includes" -> createIncludes()
        "IndexOf" -> createIndexOf()
        "Indexer" -> createIndexer()
        "Instance" -> createInstance()
        "Intersect" -> createIntersect()
        "Interval" -> createInterval()
        "Is" -> createIs()
        "IsFalse" -> createIsFalse()
        "IsNull" -> createIsNull()
        "IsTrue" -> createIsTrue()
        "Iteration" -> createIteration()
        "Last" -> createLast()
        "LastPositionOf" -> createLastPositionOf()
        "Length" -> createLength()
        "Less" -> createLess()
        "LessOrEqual" -> createLessOrEqual()
        "List" -> createList()
        "Literal" -> createLiteral()
        "Ln" -> createLn()
        "Log" -> createLog()
        "LowBoundary" -> createLowBoundary()
        "Lower" -> createLower()
        "Matches" -> createMatches()
        "Max" -> createMax()
        "MaxValue" -> createMaxValue()
        "Median" -> createMedian()
        "Meets" -> createMeets()
        "MeetsAfter" -> createMeetsAfter()
        "MeetsBefore" -> createMeetsBefore()
        "Message" -> createMessage()
        "Min" -> createMin()
        "MinValue" -> createMinValue()
        "Mode" -> createMode()
        "Modulo" -> createModulo()
        "Multiply" -> createMultiply()
        "Negate" -> createNegate()
        "Not" -> createNot()
        "NotEqual" -> createNotEqual()
        "Now" -> createNow()
        "Null" -> createNull()
        "OperandRef" -> createOperandRef()
        "Or" -> createOr()
        "Overlaps" -> createOverlaps()
        "OverlapsAfter" -> createOverlapsAfter()
        "OverlapsBefore" -> createOverlapsBefore()
        "ParameterRef" -> createParameterRef()
        "PointFrom" -> createPointFrom()
        "PopulationStdDev" -> createPopulationStdDev()
        "PopulationVariance" -> createPopulationVariance()
        "PositionOf" -> createPositionOf()
        "Power" -> createPower()
        "Precision" -> createPrecision()
        "Predecessor" -> createPredecessor()
        "Product" -> createProduct()
        "ProperContains" -> createProperContains()
        "ProperIn" -> createProperIn()
        "ProperIncludedIn" -> createProperIncludedIn()
        "ProperIncludes" -> createProperIncludes()
        "Property" -> createProperty()
        "Quantity" -> createQuantity()
        "Query" -> createQuery()
        "QueryLetRef" -> createQueryLetRef()
        "Ratio" -> createRatio()
        "Repeat" -> createRepeat()
        "ReplaceMatches" -> createReplaceMatches()
        "Retrieve" -> createRetrieve()
        "Round" -> createRound()
        "SameAs" -> createSameAs()
        "SameOrAfter" -> createSameOrAfter()
        "SameOrBefore" -> createSameOrBefore()
        "Search" -> createSearch()
        "SingletonFrom" -> createSingletonFrom()
        "Size" -> createSize()
        "Slice" -> createSlice()
        "Sort" -> createSort()
        "Split" -> createSplit()
        "SplitOnMatches" -> createSplitOnMatches()
        "Start" -> createStart()
        "Starts" -> createStarts()
        "StartsWith" -> createStartsWith()
        "StdDev" -> createStdDev()
        "Substring" -> createSubstring()
        "SubsumedBy" -> createSubsumedBy()
        "Subsumes" -> createSubsumes()
        "Subtract" -> createSubtract()
        "Successor" -> createSuccessor()
        "Sum" -> createSum()
        "Time" -> createTime()
        "TimeFrom" -> createTimeFrom()
        "TimeOfDay" -> createTimeOfDay()
        "Times" -> createTimes()
        "TimezoneFrom" -> createTimezoneFrom()
        "TimezoneOffsetFrom" -> createTimezoneOffsetFrom()
        "ToBoolean" -> createToBoolean()
        "ToChars" -> createToChars()
        "ToConcept" -> createToConcept()
        "ToDate" -> createToDate()
        "ToDateTime" -> createToDateTime()
        "ToDecimal" -> createToDecimal()
        "ToInteger" -> createToInteger()
        "ToList" -> createToList()
        "ToLong" -> createToLong()
        "ToQuantity" -> createToQuantity()
        "ToRatio" -> createToRatio()
        "ToString" -> createToString()
        "ToTime" -> createToTime()
        "Today" -> createToday()
        "Total" -> createTotal()
        "Truncate" -> createTruncate()
        "TruncatedDivide" -> createTruncatedDivide()
        "Tuple" -> createTuple()
        "Union" -> createUnion()
        "Upper" -> createUpper()
        "ValueSetRef" -> createValueSetRef()
        "Variance" -> createVariance()
        "Width" -> createWidth()
        "Xor" -> createXor()
        else -> throw IllegalArgumentException("Unknown expression type: $expressionType")
    }
}

expect fun IdObjectFactory.createExpression(expressionType: String): Expression
