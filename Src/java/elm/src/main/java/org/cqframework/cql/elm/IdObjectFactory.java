package org.cqframework.cql.elm;

import jakarta.xml.bind.JAXBElement;
import org.hl7.elm.r1.*;

/*
 * Extends the ObjectFactory to allow for decorating the elements created by the factory. If no decorator is provided, nodes are
 * given monotonically increasing ids.
 *
 */
public class IdObjectFactory extends ObjectFactory {

    private int nextId = 0;

    /**
     * returns the next id and increments the counter
     */
    public String nextId() {
        return Integer.toString(nextId++);
    }

    @Override
    public Abs createAbs() {
        return super.createAbs().withLocalId(nextId());
    }

    @Override
    public Add createAdd() {
        return super.createAdd().withLocalId(nextId());
    }

    @Override
    public After createAfter() {
        return super.createAfter().withLocalId(nextId());
    }

    @Override
    public Aggregate createAggregate() {
        return super.createAggregate().withLocalId(nextId());
    }

    @Override
    public AggregateClause createAggregateClause() {
        return super.createAggregateClause().withLocalId(nextId());
    }

    @Override
    public AliasRef createAliasRef() {
        return super.createAliasRef().withLocalId(nextId());
    }

    @Override
    public AliasedQuerySource createAliasedQuerySource() {
        return super.createAliasedQuerySource().withLocalId(nextId());
    }

    @Override
    public AllTrue createAllTrue() {
        return super.createAllTrue().withLocalId(nextId());
    }

    @Override
    public And createAnd() {
        return super.createAnd().withLocalId(nextId());
    }

    @Override
    public AnyInCodeSystem createAnyInCodeSystem() {
        return super.createAnyInCodeSystem().withLocalId(nextId());
    }

    @Override
    public AnyInValueSet createAnyInValueSet() {
        return super.createAnyInValueSet().withLocalId(nextId());
    }

    @Override
    public AnyTrue createAnyTrue() {
        return super.createAnyTrue().withLocalId(nextId());
    }

    @Override
    public As createAs() {
        return super.createAs().withLocalId(nextId());
    }

    @Override
    public Avg createAvg() {
        return super.createAvg().withLocalId(nextId());
    }

    @Override
    public Before createBefore() {
        return super.createBefore().withLocalId(nextId());
    }

    @Override
    public ByColumn createByColumn() {
        return super.createByColumn().withLocalId(nextId());
    }

    @Override
    public ByDirection createByDirection() {
        return super.createByDirection().withLocalId(nextId());
    }

    @Override
    public ByExpression createByExpression() {
        return super.createByExpression().withLocalId(nextId());
    }

    @Override
    public CalculateAge createCalculateAge() {
        return super.createCalculateAge().withLocalId(nextId());
    }

    @Override
    public CalculateAgeAt createCalculateAgeAt() {
        return super.createCalculateAgeAt().withLocalId(nextId());
    }

    @Override
    public CanConvert createCanConvert() {
        return super.createCanConvert().withLocalId(nextId());
    }

    @Override
    public CanConvertQuantity createCanConvertQuantity() {
        return super.createCanConvertQuantity().withLocalId(nextId());
    }

    @Override
    public Case createCase() {
        return super.createCase().withLocalId(nextId());
    }

    @Override
    public CaseItem createCaseItem() {
        return super.createCaseItem().withLocalId(nextId());
    }

    @Override
    public Ceiling createCeiling() {
        return super.createCeiling().withLocalId(nextId());
    }

    @Override
    public Children createChildren() {
        return super.createChildren().withLocalId(nextId());
    }

    @Override
    public ChoiceTypeSpecifier createChoiceTypeSpecifier() {
        return super.createChoiceTypeSpecifier().withLocalId(nextId());
    }

    @Override
    public Coalesce createCoalesce() {
        return super.createCoalesce().withLocalId(nextId());
    }

    @Override
    public Code createCode() {
        return super.createCode().withLocalId(nextId());
    }

    @Override
    public CodeDef createCodeDef() {
        return super.createCodeDef().withLocalId(nextId());
    }

    @Override
    public CodeFilterElement createCodeFilterElement() {
        return super.createCodeFilterElement().withLocalId(nextId());
    }

    @Override
    public CodeRef createCodeRef() {
        return super.createCodeRef().withLocalId(nextId());
    }

    @Override
    public CodeSystemDef createCodeSystemDef() {
        return super.createCodeSystemDef().withLocalId(nextId());
    }

    @Override
    public CodeSystemRef createCodeSystemRef() {
        return super.createCodeSystemRef().withLocalId(nextId());
    }

    @Override
    public Collapse createCollapse() {
        return super.createCollapse().withLocalId(nextId());
    }

    @Override
    public Combine createCombine() {
        return super.createCombine().withLocalId(nextId());
    }

    @Override
    public Concatenate createConcatenate() {
        return super.createConcatenate().withLocalId(nextId());
    }

    @Override
    public Concept createConcept() {
        return super.createConcept().withLocalId(nextId());
    }

    @Override
    public ConceptDef createConceptDef() {
        return super.createConceptDef().withLocalId(nextId());
    }

    @Override
    public ConceptRef createConceptRef() {
        return super.createConceptRef().withLocalId(nextId());
    }

    @Override
    public Contains createContains() {
        return super.createContains().withLocalId(nextId());
    }

    @Override
    public ContextDef createContextDef() {
        return super.createContextDef().withLocalId(nextId());
    }

    @Override
    public Convert createConvert() {
        return super.createConvert().withLocalId(nextId());
    }

    @Override
    public ConvertQuantity createConvertQuantity() {
        return super.createConvertQuantity().withLocalId(nextId());
    }

    @Override
    public ConvertsToBoolean createConvertsToBoolean() {
        return super.createConvertsToBoolean().withLocalId(nextId());
    }

    @Override
    public ConvertsToDate createConvertsToDate() {
        return super.createConvertsToDate().withLocalId(nextId());
    }

    @Override
    public ConvertsToDateTime createConvertsToDateTime() {
        return super.createConvertsToDateTime().withLocalId(nextId());
    }

    @Override
    public ConvertsToDecimal createConvertsToDecimal() {
        return super.createConvertsToDecimal().withLocalId(nextId());
    }

    @Override
    public ConvertsToInteger createConvertsToInteger() {
        return super.createConvertsToInteger().withLocalId(nextId());
    }

    @Override
    public ConvertsToLong createConvertsToLong() {
        return super.createConvertsToLong().withLocalId(nextId());
    }

    @Override
    public ConvertsToQuantity createConvertsToQuantity() {
        return super.createConvertsToQuantity().withLocalId(nextId());
    }

    @Override
    public ConvertsToRatio createConvertsToRatio() {
        return super.createConvertsToRatio().withLocalId(nextId());
    }

    @Override
    public ConvertsToString createConvertsToString() {
        return super.createConvertsToString().withLocalId(nextId());
    }

    @Override
    public ConvertsToTime createConvertsToTime() {
        return super.createConvertsToTime().withLocalId(nextId());
    }

    @Override
    public Count createCount() {
        return super.createCount().withLocalId(nextId());
    }

    @Override
    public Current createCurrent() {
        return super.createCurrent().withLocalId(nextId());
    }

    @Override
    public Date createDate() {
        return super.createDate().withLocalId(nextId());
    }

    @Override
    public DateFilterElement createDateFilterElement() {
        return super.createDateFilterElement().withLocalId(nextId());
    }

    @Override
    public DateFrom createDateFrom() {
        return super.createDateFrom().withLocalId(nextId());
    }

    @Override
    public DateTime createDateTime() {
        return super.createDateTime().withLocalId(nextId());
    }

    @Override
    public DateTimeComponentFrom createDateTimeComponentFrom() {
        return super.createDateTimeComponentFrom().withLocalId(nextId());
    }

    /*
     * Deprecated, use Descendants
     *
     * CQL 1.5.3 corrected the spelling to Descendants
     *
     * @deprecated since 3.28.0
     */
    @Override
    @Deprecated(since = "3.28.0")
    public Descendents createDescendents() {
        return super.createDescendents().withLocalId(nextId());
    }

    @Override
    public Descendants createDescendants() {
        return super.createDescendants().withLocalId(nextId());
    }

    @Override
    public DifferenceBetween createDifferenceBetween() {
        return super.createDifferenceBetween().withLocalId(nextId());
    }

    @Override
    public Distinct createDistinct() {
        return super.createDistinct().withLocalId(nextId());
    }

    @Override
    public Divide createDivide() {
        return super.createDivide().withLocalId(nextId());
    }

    @Override
    public DurationBetween createDurationBetween() {
        return super.createDurationBetween().withLocalId(nextId());
    }

    @Override
    public End createEnd() {
        return super.createEnd().withLocalId(nextId());
    }

    @Override
    public Ends createEnds() {
        return super.createEnds().withLocalId(nextId());
    }

    @Override
    public EndsWith createEndsWith() {
        return super.createEndsWith().withLocalId(nextId());
    }

    @Override
    public Equal createEqual() {
        return super.createEqual().withLocalId(nextId());
    }

    @Override
    public Equivalent createEquivalent() {
        return super.createEquivalent().withLocalId(nextId());
    }

    @Override
    public Except createExcept() {
        return super.createExcept().withLocalId(nextId());
    }

    @Override
    public Exists createExists() {
        return super.createExists().withLocalId(nextId());
    }

    @Override
    public Exp createExp() {
        return super.createExp().withLocalId(nextId());
    }

    @Override
    public Expand createExpand() {
        return super.createExpand().withLocalId(nextId());
    }

    @Override
    public ExpandValueSet createExpandValueSet() {
        return super.createExpandValueSet().withLocalId(nextId());
    }

    @Override
    public ExpressionDef createExpressionDef() {
        return super.createExpressionDef().withLocalId(nextId());
    }

    @Override
    public ExpressionRef createExpressionRef() {
        return super.createExpressionRef().withLocalId(nextId());
    }

    @Override
    public Filter createFilter() {
        return super.createFilter().withLocalId(nextId());
    }

    @Override
    public First createFirst() {
        return super.createFirst().withLocalId(nextId());
    }

    @Override
    public Flatten createFlatten() {
        return super.createFlatten().withLocalId(nextId());
    }

    @Override
    public Floor createFloor() {
        return super.createFloor().withLocalId(nextId());
    }

    @Override
    public ForEach createForEach() {
        return super.createForEach().withLocalId(nextId());
    }

    @Override
    public FunctionDef createFunctionDef() {
        return super.createFunctionDef().withLocalId(nextId());
    }

    @Override
    public FunctionRef createFunctionRef() {
        return super.createFunctionRef().withLocalId(nextId());
    }

    @Override
    public GeometricMean createGeometricMean() {
        return super.createGeometricMean().withLocalId(nextId());
    }

    @Override
    public Greater createGreater() {
        return super.createGreater().withLocalId(nextId());
    }

    @Override
    public GreaterOrEqual createGreaterOrEqual() {
        return super.createGreaterOrEqual().withLocalId(nextId());
    }

    @Override
    public HighBoundary createHighBoundary() {
        return super.createHighBoundary().withLocalId(nextId());
    }

    @Override
    public IdentifierRef createIdentifierRef() {
        return super.createIdentifierRef().withLocalId(nextId());
    }

    @Override
    public If createIf() {
        return super.createIf().withLocalId(nextId());
    }

    @Override
    public Implies createImplies() {
        return super.createImplies().withLocalId(nextId());
    }

    @Override
    public In createIn() {
        return super.createIn().withLocalId(nextId());
    }

    @Override
    public InCodeSystem createInCodeSystem() {
        return super.createInCodeSystem().withLocalId(nextId());
    }

    @Override
    public InValueSet createInValueSet() {
        return super.createInValueSet().withLocalId(nextId());
    }

    @Override
    public IncludeDef createIncludeDef() {
        return super.createIncludeDef().withLocalId(nextId());
    }

    @Override
    public IncludeElement createIncludeElement() {
        return super.createIncludeElement().withLocalId(nextId());
    }

    @Override
    public IncludedIn createIncludedIn() {
        return super.createIncludedIn().withLocalId(nextId());
    }

    @Override
    public Includes createIncludes() {
        return super.createIncludes().withLocalId(nextId());
    }

    @Override
    public IndexOf createIndexOf() {
        return super.createIndexOf().withLocalId(nextId());
    }

    @Override
    public Indexer createIndexer() {
        return super.createIndexer().withLocalId(nextId());
    }

    @Override
    public Instance createInstance() {
        return super.createInstance().withLocalId(nextId());
    }

    // @Override
    // public InstanceElement createInstanceElement() {
    //     return super.createInstanceElement().withLocalId(nextId());
    // }

    @Override
    public Intersect createIntersect() {
        return super.createIntersect().withLocalId(nextId());
    }

    @Override
    public Interval createInterval() {
        return super.createInterval().withLocalId(nextId());
    }

    @Override
    public IntervalTypeSpecifier createIntervalTypeSpecifier() {
        return super.createIntervalTypeSpecifier().withLocalId(nextId());
    }

    @Override
    public Is createIs() {
        return super.createIs().withLocalId(nextId());
    }

    @Override
    public IsFalse createIsFalse() {
        return super.createIsFalse().withLocalId(nextId());
    }

    @Override
    public IsNull createIsNull() {
        return super.createIsNull().withLocalId(nextId());
    }

    @Override
    public IsTrue createIsTrue() {
        return super.createIsTrue().withLocalId(nextId());
    }

    @Override
    public Iteration createIteration() {
        return super.createIteration().withLocalId(nextId());
    }

    @Override
    public Last createLast() {
        return super.createLast().withLocalId(nextId());
    }

    @Override
    public LastPositionOf createLastPositionOf() {
        return super.createLastPositionOf().withLocalId(nextId());
    }

    @Override
    public Length createLength() {
        return super.createLength().withLocalId(nextId());
    }

    @Override
    public Less createLess() {
        return super.createLess().withLocalId(nextId());
    }

    @Override
    public LessOrEqual createLessOrEqual() {
        return super.createLessOrEqual().withLocalId(nextId());
    }

    @Override
    public LetClause createLetClause() {
        return super.createLetClause().withLocalId(nextId());
    }

    @Override
    public Library createLibrary() {
        return super.createLibrary().withLocalId(nextId());
    }

    @Override
    public JAXBElement<Library> createLibrary(Library value) {
        return super.createLibrary(value);
    }

    // @Override
    // public CodeSystems createLibraryCodeSystems() {
    //     return super.createLibraryCodeSystems().withLocalId(nextId());
    // }

    // @Override
    // public Codes createLibraryCodes() {
    //     return super.createLibraryCodes().withLocalId(nextId());
    // }

    // @Override
    // public Concepts createLibraryConcepts() {
    //     return super.createLibraryConcepts().withLocalId(nextId());
    // }

    // @Override
    // public Contexts createLibraryContexts() {
    //     return super.createLibraryContexts().withLocalId(nextId());
    // }

    // @Override
    // public Includes createLibraryIncludes() {
    //     return super.createLibraryIncludes().withLocalId(nextId());
    // }

    // @Override
    // public Parameters createLibraryParameters() {
    //     return super.createLibraryParameters().withLocalId(nextId());
    // }

    // @Override
    // public Statements createLibraryStatements() {
    //     return super.createLibraryStatements().withLocalId(nextId());
    // }

    // @Override
    // public Usings createLibraryUsings() {
    //     return super.createLibraryUsings().withLocalId(nextId());
    // }

    // @Override
    // public ValueSets createLibraryValueSets() {
    //     return super.createLibraryValueSets().withLocalId(nextId());
    // }

    @Override
    public List createList() {
        return super.createList().withLocalId(nextId());
    }

    @Override
    public ListTypeSpecifier createListTypeSpecifier() {
        return super.createListTypeSpecifier().withLocalId(nextId());
    }

    @Override
    public Literal createLiteral() {
        return super.createLiteral().withLocalId(nextId());
    }

    @Override
    public Ln createLn() {
        return super.createLn().withLocalId(nextId());
    }

    @Override
    public Log createLog() {
        return super.createLog().withLocalId(nextId());
    }

    @Override
    public LowBoundary createLowBoundary() {
        return super.createLowBoundary().withLocalId(nextId());
    }

    @Override
    public Lower createLower() {
        return super.createLower().withLocalId(nextId());
    }

    @Override
    public Matches createMatches() {
        return super.createMatches().withLocalId(nextId());
    }

    @Override
    public Max createMax() {
        return super.createMax().withLocalId(nextId());
    }

    @Override
    public MaxValue createMaxValue() {
        return super.createMaxValue().withLocalId(nextId());
    }

    @Override
    public Median createMedian() {
        return super.createMedian().withLocalId(nextId());
    }

    @Override
    public Meets createMeets() {
        return super.createMeets().withLocalId(nextId());
    }

    @Override
    public MeetsAfter createMeetsAfter() {
        return super.createMeetsAfter().withLocalId(nextId());
    }

    @Override
    public MeetsBefore createMeetsBefore() {
        return super.createMeetsBefore().withLocalId(nextId());
    }

    @Override
    public Message createMessage() {
        return super.createMessage().withLocalId(nextId());
    }

    @Override
    public Min createMin() {
        return super.createMin().withLocalId(nextId());
    }

    @Override
    public MinValue createMinValue() {
        return super.createMinValue().withLocalId(nextId());
    }

    @Override
    public Mode createMode() {
        return super.createMode().withLocalId(nextId());
    }

    @Override
    public Modulo createModulo() {
        return super.createModulo().withLocalId(nextId());
    }

    @Override
    public Multiply createMultiply() {
        return super.createMultiply().withLocalId(nextId());
    }

    @Override
    public NamedTypeSpecifier createNamedTypeSpecifier() {
        return super.createNamedTypeSpecifier().withLocalId(nextId());
    }

    @Override
    public Negate createNegate() {
        return super.createNegate().withLocalId(nextId());
    }

    @Override
    public Not createNot() {
        return super.createNot().withLocalId(nextId());
    }

    @Override
    public NotEqual createNotEqual() {
        return super.createNotEqual().withLocalId(nextId());
    }

    @Override
    public Now createNow() {
        return super.createNow().withLocalId(nextId());
    }

    @Override
    public Null createNull() {
        return super.createNull().withLocalId(nextId());
    }

    @Override
    public OperandDef createOperandDef() {
        return super.createOperandDef().withLocalId(nextId());
    }

    @Override
    public OperandRef createOperandRef() {
        return super.createOperandRef().withLocalId(nextId());
    }

    @Override
    public Or createOr() {
        return super.createOr().withLocalId(nextId());
    }

    @Override
    public OtherFilterElement createOtherFilterElement() {
        return super.createOtherFilterElement().withLocalId(nextId());
    }

    @Override
    public Overlaps createOverlaps() {
        return super.createOverlaps().withLocalId(nextId());
    }

    @Override
    public OverlapsAfter createOverlapsAfter() {
        return super.createOverlapsAfter().withLocalId(nextId());
    }

    @Override
    public OverlapsBefore createOverlapsBefore() {
        return super.createOverlapsBefore().withLocalId(nextId());
    }

    @Override
    public ParameterDef createParameterDef() {
        return super.createParameterDef().withLocalId(nextId());
    }

    @Override
    public ParameterRef createParameterRef() {
        return super.createParameterRef().withLocalId(nextId());
    }

    @Override
    public ParameterTypeSpecifier createParameterTypeSpecifier() {
        return super.createParameterTypeSpecifier().withLocalId(nextId());
    }

    @Override
    public PointFrom createPointFrom() {
        return super.createPointFrom().withLocalId(nextId());
    }

    @Override
    public PopulationStdDev createPopulationStdDev() {
        return super.createPopulationStdDev().withLocalId(nextId());
    }

    @Override
    public PopulationVariance createPopulationVariance() {
        return super.createPopulationVariance().withLocalId(nextId());
    }

    @Override
    public PositionOf createPositionOf() {
        return super.createPositionOf().withLocalId(nextId());
    }

    @Override
    public Power createPower() {
        return super.createPower().withLocalId(nextId());
    }

    @Override
    public Precision createPrecision() {
        return super.createPrecision().withLocalId(nextId());
    }

    @Override
    public Predecessor createPredecessor() {
        return super.createPredecessor().withLocalId(nextId());
    }

    @Override
    public Product createProduct() {
        return super.createProduct().withLocalId(nextId());
    }

    @Override
    public ProperContains createProperContains() {
        return super.createProperContains().withLocalId(nextId());
    }

    @Override
    public ProperIn createProperIn() {
        return super.createProperIn().withLocalId(nextId());
    }

    @Override
    public ProperIncludedIn createProperIncludedIn() {
        return super.createProperIncludedIn().withLocalId(nextId());
    }

    @Override
    public ProperIncludes createProperIncludes() {
        return super.createProperIncludes().withLocalId(nextId());
    }

    @Override
    public Property createProperty() {
        return super.createProperty().withLocalId(nextId());
    }

    @Override
    public Quantity createQuantity() {
        return super.createQuantity().withLocalId(nextId());
    }

    @Override
    public Query createQuery() {
        return super.createQuery().withLocalId(nextId());
    }

    @Override
    public QueryLetRef createQueryLetRef() {
        return super.createQueryLetRef().withLocalId(nextId());
    }

    @Override
    public Ratio createRatio() {
        return super.createRatio().withLocalId(nextId());
    }

    @Override
    public Repeat createRepeat() {
        return super.createRepeat().withLocalId(nextId());
    }

    @Override
    public ReplaceMatches createReplaceMatches() {
        return super.createReplaceMatches().withLocalId(nextId());
    }

    @Override
    public Retrieve createRetrieve() {
        return super.createRetrieve().withLocalId(nextId());
    }

    @Override
    public ReturnClause createReturnClause() {
        return super.createReturnClause().withLocalId(nextId());
    }

    @Override
    public Round createRound() {
        return super.createRound().withLocalId(nextId());
    }

    @Override
    public SameAs createSameAs() {
        return super.createSameAs().withLocalId(nextId());
    }

    @Override
    public SameOrAfter createSameOrAfter() {
        return super.createSameOrAfter().withLocalId(nextId());
    }

    @Override
    public SameOrBefore createSameOrBefore() {
        return super.createSameOrBefore().withLocalId(nextId());
    }

    @Override
    public Search createSearch() {
        return super.createSearch().withLocalId(nextId());
    }

    @Override
    public SingletonFrom createSingletonFrom() {
        return super.createSingletonFrom().withLocalId(nextId());
    }

    @Override
    public Size createSize() {
        return super.createSize().withLocalId(nextId());
    }

    @Override
    public Slice createSlice() {
        return super.createSlice().withLocalId(nextId());
    }

    @Override
    public Sort createSort() {
        return super.createSort().withLocalId(nextId());
    }

    @Override
    public SortClause createSortClause() {
        return super.createSortClause().withLocalId(nextId());
    }

    @Override
    public Split createSplit() {
        return super.createSplit().withLocalId(nextId());
    }

    @Override
    public SplitOnMatches createSplitOnMatches() {
        return super.createSplitOnMatches().withLocalId(nextId());
    }

    @Override
    public Start createStart() {
        return super.createStart().withLocalId(nextId());
    }

    @Override
    public Starts createStarts() {
        return super.createStarts().withLocalId(nextId());
    }

    @Override
    public StartsWith createStartsWith() {
        return super.createStartsWith().withLocalId(nextId());
    }

    @Override
    public StdDev createStdDev() {
        return super.createStdDev().withLocalId(nextId());
    }

    @Override
    public Substring createSubstring() {
        return super.createSubstring().withLocalId(nextId());
    }

    @Override
    public SubsumedBy createSubsumedBy() {
        return super.createSubsumedBy().withLocalId(nextId());
    }

    @Override
    public Subsumes createSubsumes() {
        return super.createSubsumes().withLocalId(nextId());
    }

    @Override
    public Subtract createSubtract() {
        return super.createSubtract().withLocalId(nextId());
    }

    @Override
    public Successor createSuccessor() {
        return super.createSuccessor().withLocalId(nextId());
    }

    @Override
    public Sum createSum() {
        return super.createSum().withLocalId(nextId());
    }

    @Override
    public Time createTime() {
        return super.createTime().withLocalId(nextId());
    }

    @Override
    public TimeFrom createTimeFrom() {
        return super.createTimeFrom().withLocalId(nextId());
    }

    @Override
    public TimeOfDay createTimeOfDay() {
        return super.createTimeOfDay().withLocalId(nextId());
    }

    @Override
    public Times createTimes() {
        return super.createTimes().withLocalId(nextId());
    }

    @Override
    public TimezoneFrom createTimezoneFrom() {
        return super.createTimezoneFrom().withLocalId(nextId());
    }

    @Override
    public TimezoneOffsetFrom createTimezoneOffsetFrom() {
        return super.createTimezoneOffsetFrom().withLocalId(nextId());
    }

    @Override
    public ToBoolean createToBoolean() {
        return super.createToBoolean().withLocalId(nextId());
    }

    @Override
    public ToChars createToChars() {
        return super.createToChars().withLocalId(nextId());
    }

    @Override
    public ToConcept createToConcept() {
        return super.createToConcept().withLocalId(nextId());
    }

    @Override
    public ToDate createToDate() {
        return super.createToDate().withLocalId(nextId());
    }

    @Override
    public ToDateTime createToDateTime() {
        return super.createToDateTime().withLocalId(nextId());
    }

    @Override
    public ToDecimal createToDecimal() {
        return super.createToDecimal().withLocalId(nextId());
    }

    @Override
    public ToInteger createToInteger() {
        return super.createToInteger().withLocalId(nextId());
    }

    @Override
    public ToList createToList() {
        return super.createToList().withLocalId(nextId());
    }

    @Override
    public ToLong createToLong() {
        return super.createToLong().withLocalId(nextId());
    }

    @Override
    public ToQuantity createToQuantity() {
        return super.createToQuantity().withLocalId(nextId());
    }

    @Override
    public ToRatio createToRatio() {
        return super.createToRatio().withLocalId(nextId());
    }

    @Override
    public ToString createToString() {
        return super.createToString().withLocalId(nextId());
    }

    @Override
    public ToTime createToTime() {
        return super.createToTime().withLocalId(nextId());
    }

    @Override
    public Today createToday() {
        return super.createToday().withLocalId(nextId());
    }

    @Override
    public Total createTotal() {
        return super.createTotal().withLocalId(nextId());
    }

    @Override
    public Truncate createTruncate() {
        return super.createTruncate().withLocalId(nextId());
    }

    @Override
    public TruncatedDivide createTruncatedDivide() {
        return super.createTruncatedDivide().withLocalId(nextId());
    }

    @Override
    public Tuple createTuple() {
        return super.createTuple().withLocalId(nextId());
    }

    // @Override
    // public TupleElement createTupleElement() {
    //     return super.createTupleElement().withLocalId(nextId());
    // }

    @Override
    public TupleElementDefinition createTupleElementDefinition() {
        return super.createTupleElementDefinition().withLocalId(nextId());
    }

    @Override
    public TupleTypeSpecifier createTupleTypeSpecifier() {
        return super.createTupleTypeSpecifier().withLocalId(nextId());
    }

    @Override
    public Union createUnion() {
        return super.createUnion().withLocalId(nextId());
    }

    @Override
    public Upper createUpper() {
        return super.createUpper().withLocalId(nextId());
    }

    @Override
    public UsingDef createUsingDef() {
        return super.createUsingDef().withLocalId(nextId());
    }

    @Override
    public ValueSetDef createValueSetDef() {
        return super.createValueSetDef().withLocalId(nextId());
    }

    @Override
    public ValueSetRef createValueSetRef() {
        return super.createValueSetRef().withLocalId(nextId());
    }

    @Override
    public Variance createVariance() {
        return super.createVariance().withLocalId(nextId());
    }

    // @Override
    // public VersionedIdentifier createVersionedIdentifier() {
    //     return super.createVersionedIdentifier().withLocalId(nextId());
    // }

    @Override
    public Width createWidth() {
        return super.createWidth().withLocalId(nextId());
    }

    @Override
    public With createWith() {
        return super.createWith().withLocalId(nextId());
    }

    @Override
    public Without createWithout() {
        return super.createWithout().withLocalId(nextId());
    }

    @Override
    public Xor createXor() {
        return super.createXor().withLocalId(nextId());
    }
}
