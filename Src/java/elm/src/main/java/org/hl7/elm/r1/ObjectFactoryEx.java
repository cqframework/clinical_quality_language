package org.hl7.elm.r1;

import java.util.function.Function;

import jakarta.xml.bind.JAXBElement;

/*
 * Extends the ObjectFactory to allow for decorating the elements created by the factory. If no decorator is provided, the identity function is used.
 *
 */
public class ObjectFactoryEx extends ObjectFactory {

    private final Function<Element, Element> elementDecorator;

    public ObjectFactoryEx() {
        this(Function.identity());
    }

    public ObjectFactoryEx(Function<Element, Element> elementDecorator) {
        this.elementDecorator = elementDecorator;;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Element> T decorate(T t) {
        return (T)elementDecorator.apply(t);
    }

    @Override
    public Abs createAbs() {
        return decorate(super.createAbs());
    }

    @Override
    public Add createAdd() {
        return decorate(super.createAdd());
    }

    @Override
    public After createAfter() {
        return decorate(super.createAfter());
    }

    @Override
    public Aggregate createAggregate() {
        return decorate(super.createAggregate());
    }

    @Override
    public AggregateClause createAggregateClause() {
        return decorate(super.createAggregateClause());
    }

    @Override
    public AliasRef createAliasRef() {
        return decorate(super.createAliasRef());
    }

    @Override
    public AliasedQuerySource createAliasedQuerySource() {
        return decorate(super.createAliasedQuerySource());
    }

    @Override
    public AllTrue createAllTrue() {
        return decorate(super.createAllTrue());
    }

    @Override
    public And createAnd() {
        return decorate(super.createAnd());
    }

    @Override
    public AnyInCodeSystem createAnyInCodeSystem() {
        return decorate(super.createAnyInCodeSystem());
    }

    @Override
    public AnyInValueSet createAnyInValueSet() {
        return decorate(super.createAnyInValueSet());
    }

    @Override
    public AnyTrue createAnyTrue() {
        return decorate(super.createAnyTrue());
    }

    @Override
    public As createAs() {
        return decorate(super.createAs());
    }

    @Override
    public Avg createAvg() {
        return decorate(super.createAvg());
    }

    @Override
    public Before createBefore() {
        return decorate(super.createBefore());
    }

    @Override
    public ByColumn createByColumn() {
        return decorate(super.createByColumn());
    }

    @Override
    public ByDirection createByDirection() {
        return decorate(super.createByDirection());
    }

    @Override
    public ByExpression createByExpression() {
        return decorate(super.createByExpression());
    }

    @Override
    public CalculateAge createCalculateAge() {
        return decorate(super.createCalculateAge());
    }

    @Override
    public CalculateAgeAt createCalculateAgeAt() {
        return decorate(super.createCalculateAgeAt());
    }

    @Override
    public CanConvert createCanConvert() {
        return decorate(super.createCanConvert());
    }

    @Override
    public CanConvertQuantity createCanConvertQuantity() {
        return decorate(super.createCanConvertQuantity());
    }

    @Override
    public Case createCase() {
        return decorate(super.createCase());
    }

    @Override
    public CaseItem createCaseItem() {
        return decorate(super.createCaseItem());
    }

    @Override
    public Ceiling createCeiling() {
        return decorate(super.createCeiling());
    }

    @Override
    public Children createChildren() {
        return decorate(super.createChildren());
    }

    @Override
    public ChoiceTypeSpecifier createChoiceTypeSpecifier() {
        return decorate(super.createChoiceTypeSpecifier());
    }

    @Override
    public Coalesce createCoalesce() {
        return decorate(super.createCoalesce());
    }

    @Override
    public Code createCode() {
        return decorate(super.createCode());
    }

    @Override
    public CodeDef createCodeDef() {
        return decorate(super.createCodeDef());
    }

    @Override
    public CodeFilterElement createCodeFilterElement() {
        return decorate(super.createCodeFilterElement());
    }

    @Override
    public CodeRef createCodeRef() {
        return decorate(super.createCodeRef());
    }

    @Override
    public CodeSystemDef createCodeSystemDef() {
        return decorate(super.createCodeSystemDef());
    }

    @Override
    public CodeSystemRef createCodeSystemRef() {
        return decorate(super.createCodeSystemRef());
    }

    @Override
    public Collapse createCollapse() {
        return decorate(super.createCollapse());
    }

    @Override
    public Combine createCombine() {
        return decorate(super.createCombine());
    }

    @Override
    public Concatenate createConcatenate() {
        return decorate(super.createConcatenate());
    }

    @Override
    public Concept createConcept() {
        return decorate(super.createConcept());
    }

    @Override
    public ConceptDef createConceptDef() {
        return decorate(super.createConceptDef());
    }

    @Override
    public ConceptRef createConceptRef() {
        return decorate(super.createConceptRef());
    }

    @Override
    public Contains createContains() {
        return decorate(super.createContains());
    }

    @Override
    public ContextDef createContextDef() {
        return decorate(super.createContextDef());
    }

    @Override
    public Convert createConvert() {
        return decorate(super.createConvert());
    }

    @Override
    public ConvertQuantity createConvertQuantity() {
        return decorate(super.createConvertQuantity());
    }

    @Override
    public ConvertsToBoolean createConvertsToBoolean() {
        return decorate(super.createConvertsToBoolean());
    }

    @Override
    public ConvertsToDate createConvertsToDate() {
        return decorate(super.createConvertsToDate());
    }

    @Override
    public ConvertsToDateTime createConvertsToDateTime() {
        return decorate(super.createConvertsToDateTime());
    }

    @Override
    public ConvertsToDecimal createConvertsToDecimal() {
        return decorate(super.createConvertsToDecimal());
    }

    @Override
    public ConvertsToInteger createConvertsToInteger() {
        return decorate(super.createConvertsToInteger());
    }

    @Override
    public ConvertsToLong createConvertsToLong() {
        return decorate(super.createConvertsToLong());
    }

    @Override
    public ConvertsToQuantity createConvertsToQuantity() {
        return decorate(super.createConvertsToQuantity());
    }

    @Override
    public ConvertsToRatio createConvertsToRatio() {
        return decorate(super.createConvertsToRatio());
    }

    @Override
    public ConvertsToString createConvertsToString() {
        return decorate(super.createConvertsToString());
    }

    @Override
    public ConvertsToTime createConvertsToTime() {
        return decorate(super.createConvertsToTime());
    }

    @Override
    public Count createCount() {
        return decorate(super.createCount());
    }

    @Override
    public Current createCurrent() {
        return decorate(super.createCurrent());
    }

    @Override
    public Date createDate() {
        return decorate(super.createDate());
    }

    @Override
    public DateFilterElement createDateFilterElement() {
        return decorate(super.createDateFilterElement());
    }

    @Override
    public DateFrom createDateFrom() {
        return decorate(super.createDateFrom());
    }

    @Override
    public DateTime createDateTime() {
        return decorate(super.createDateTime());
    }

    @Override
    public DateTimeComponentFrom createDateTimeComponentFrom() {
        return decorate(super.createDateTimeComponentFrom());
    }

    @Override
    public Descendents createDescendents() {
        return decorate(super.createDescendents());
    }

    @Override
    public DifferenceBetween createDifferenceBetween() {
        return decorate(super.createDifferenceBetween());
    }

    @Override
    public Distinct createDistinct() {
        return decorate(super.createDistinct());
    }

    @Override
    public Divide createDivide() {
        return decorate(super.createDivide());
    }

    @Override
    public DurationBetween createDurationBetween() {
        return decorate(super.createDurationBetween());
    }

    @Override
    public End createEnd() {
        return decorate(super.createEnd());
    }

    @Override
    public Ends createEnds() {
        return decorate(super.createEnds());
    }

    @Override
    public EndsWith createEndsWith() {
        return decorate(super.createEndsWith());
    }

    @Override
    public Equal createEqual() {
        return decorate(super.createEqual());
    }

    @Override
    public Equivalent createEquivalent() {
        return decorate(super.createEquivalent());
    }

    @Override
    public Except createExcept() {
        return decorate(super.createExcept());
    }

    @Override
    public Exists createExists() {
        return decorate(super.createExists());
    }

    @Override
    public Exp createExp() {
        return decorate(super.createExp());
    }

    @Override
    public Expand createExpand() {
        return decorate(super.createExpand());
    }

    @Override
    public ExpandValueSet createExpandValueSet() {
        return decorate(super.createExpandValueSet());
    }

    @Override
    public ExpressionDef createExpressionDef() {
        return decorate(super.createExpressionDef());
    }

    @Override
    public ExpressionRef createExpressionRef() {
        return decorate(super.createExpressionRef());
    }

    @Override
    public Filter createFilter() {
        return decorate(super.createFilter());
    }

    @Override
    public First createFirst() {
        return decorate(super.createFirst());
    }

    @Override
    public Flatten createFlatten() {
        return decorate(super.createFlatten());
    }

    @Override
    public Floor createFloor() {
        return decorate(super.createFloor());
    }

    @Override
    public ForEach createForEach() {
        return decorate(super.createForEach());
    }

    @Override
    public FunctionDef createFunctionDef() {
        return decorate(super.createFunctionDef());
    }

    @Override
    public FunctionRef createFunctionRef() {
        return decorate(super.createFunctionRef());
    }

    @Override
    public GeometricMean createGeometricMean() {
        return decorate(super.createGeometricMean());
    }

    @Override
    public Greater createGreater() {
        return decorate(super.createGreater());
    }

    @Override
    public GreaterOrEqual createGreaterOrEqual() {
        return decorate(super.createGreaterOrEqual());
    }

    @Override
    public HighBoundary createHighBoundary() {
        return decorate(super.createHighBoundary());
    }

    @Override
    public IdentifierRef createIdentifierRef() {
        return decorate(super.createIdentifierRef());
    }

    @Override
    public If createIf() {
        return decorate(super.createIf());
    }

    @Override
    public Implies createImplies() {
        return decorate(super.createImplies());
    }

    @Override
    public In createIn() {
        return decorate(super.createIn());
    }

    @Override
    public InCodeSystem createInCodeSystem() {
        return decorate(super.createInCodeSystem());
    }

    @Override
    public InValueSet createInValueSet() {
        return decorate(super.createInValueSet());
    }

    @Override
    public IncludeDef createIncludeDef() {
        return decorate(super.createIncludeDef());
    }

    @Override
    public IncludeElement createIncludeElement() {
        return decorate(super.createIncludeElement());
    }

    @Override
    public IncludedIn createIncludedIn() {
        return decorate(super.createIncludedIn());
    }

    // @Override
    // public Includes createIncludes() {
    //     return decorate(super.createIncludes());
    // }

    @Override
    public IndexOf createIndexOf() {
        return decorate(super.createIndexOf());
    }

    @Override
    public Indexer createIndexer() {
        return decorate(super.createIndexer());
    }

    @Override
    public Instance createInstance() {
        return decorate(super.createInstance());
    }

    // @Override
    // public InstanceElement createInstanceElement() {
    //     return decorate(super.createInstanceElement());
    // }

    @Override
    public Intersect createIntersect() {
        return decorate(super.createIntersect());
    }

    @Override
    public Interval createInterval() {
        return decorate(super.createInterval());
    }

    @Override
    public IntervalTypeSpecifier createIntervalTypeSpecifier() {
        return decorate(super.createIntervalTypeSpecifier());
    }

    @Override
    public Is createIs() {
        return decorate(super.createIs());
    }

    @Override
    public IsFalse createIsFalse() {
        return decorate(super.createIsFalse());
    }

    @Override
    public IsNull createIsNull() {
        return decorate(super.createIsNull());
    }

    @Override
    public IsTrue createIsTrue() {
        return decorate(super.createIsTrue());
    }

    @Override
    public Iteration createIteration() {
        return decorate(super.createIteration());
    }

    @Override
    public Last createLast() {
        return decorate(super.createLast());
    }

    @Override
    public LastPositionOf createLastPositionOf() {
        return decorate(super.createLastPositionOf());
    }

    @Override
    public Length createLength() {
        return decorate(super.createLength());
    }

    @Override
    public Less createLess() {
        return decorate(super.createLess());
    }

    @Override
    public LessOrEqual createLessOrEqual() {
        return decorate(super.createLessOrEqual());
    }

    @Override
    public LetClause createLetClause() {
        return decorate(super.createLetClause());
    }

    @Override
    public Library createLibrary() {
        return decorate(super.createLibrary());
    }

    @Override
    public JAXBElement<Library> createLibrary(Library value) {
        return super.createLibrary(value);
    }

    // @Override
    // public CodeSystems createLibraryCodeSystems() {
    //     return decorate(super.createLibraryCodeSystems());
    // }

    // @Override
    // public Codes createLibraryCodes() {
    //     return decorate(super.createLibraryCodes());
    // }

    // @Override
    // public Concepts createLibraryConcepts() {
    //     return decorate(super.createLibraryConcepts());
    // }

    // @Override
    // public Contexts createLibraryContexts() {
    //     return decorate(super.createLibraryContexts());
    // }

    // @Override
    // public Includes createLibraryIncludes() {
    //     return decorate(super.createLibraryIncludes());
    // }

    // @Override
    // public Parameters createLibraryParameters() {
    //     return decorate(super.createLibraryParameters());
    // }

    // @Override
    // public Statements createLibraryStatements() {
    //     return decorate(super.createLibraryStatements());
    // }

    // @Override
    // public Usings createLibraryUsings() {
    //     return decorate(super.createLibraryUsings());
    // }

    // @Override
    // public ValueSets createLibraryValueSets() {
    //     return decorate(super.createLibraryValueSets());
    // }

    @Override
    public List createList() {
        return decorate(super.createList());
    }

    @Override
    public ListTypeSpecifier createListTypeSpecifier() {
        return decorate(super.createListTypeSpecifier());
    }

    @Override
    public Literal createLiteral() {
        return decorate(super.createLiteral());
    }

    @Override
    public Ln createLn() {
        return decorate(super.createLn());
    }

    @Override
    public Log createLog() {
        return decorate(super.createLog());
    }

    @Override
    public LowBoundary createLowBoundary() {
        return decorate(super.createLowBoundary());
    }

    @Override
    public Lower createLower() {
        return decorate(super.createLower());
    }

    @Override
    public Matches createMatches() {
        return decorate(super.createMatches());
    }

    @Override
    public Max createMax() {
        return decorate(super.createMax());
    }

    @Override
    public MaxValue createMaxValue() {
        return decorate(super.createMaxValue());
    }

    @Override
    public Median createMedian() {
        return decorate(super.createMedian());
    }

    @Override
    public Meets createMeets() {
        return decorate(super.createMeets());
    }

    @Override
    public MeetsAfter createMeetsAfter() {
        return decorate(super.createMeetsAfter());
    }

    @Override
    public MeetsBefore createMeetsBefore() {
        return decorate(super.createMeetsBefore());
    }

    @Override
    public Message createMessage() {
        return decorate(super.createMessage());
    }

    @Override
    public Min createMin() {
        return decorate(super.createMin());
    }

    @Override
    public MinValue createMinValue() {
        return decorate(super.createMinValue());
    }

    @Override
    public Mode createMode() {
        return decorate(super.createMode());
    }

    @Override
    public Modulo createModulo() {
        return decorate(super.createModulo());
    }

    @Override
    public Multiply createMultiply() {
        return decorate(super.createMultiply());
    }

    @Override
    public NamedTypeSpecifier createNamedTypeSpecifier() {
        return decorate(super.createNamedTypeSpecifier());
    }

    @Override
    public Negate createNegate() {
        return decorate(super.createNegate());
    }

    @Override
    public Not createNot() {
        return decorate(super.createNot());
    }

    @Override
    public NotEqual createNotEqual() {
        return decorate(super.createNotEqual());
    }

    @Override
    public Now createNow() {
        return decorate(super.createNow());
    }

    @Override
    public Null createNull() {
        return decorate(super.createNull());
    }

    @Override
    public OperandDef createOperandDef() {
        return decorate(super.createOperandDef());
    }

    @Override
    public OperandRef createOperandRef() {
        return decorate(super.createOperandRef());
    }

    @Override
    public Or createOr() {
        return decorate(super.createOr());
    }

    @Override
    public OtherFilterElement createOtherFilterElement() {
        return decorate(super.createOtherFilterElement());
    }

    @Override
    public Overlaps createOverlaps() {
        return decorate(super.createOverlaps());
    }

    @Override
    public OverlapsAfter createOverlapsAfter() {
        return decorate(super.createOverlapsAfter());
    }

    @Override
    public OverlapsBefore createOverlapsBefore() {
        return decorate(super.createOverlapsBefore());
    }

    @Override
    public ParameterDef createParameterDef() {
        return decorate(super.createParameterDef());
    }

    @Override
    public ParameterRef createParameterRef() {
        return decorate(super.createParameterRef());
    }

    @Override
    public ParameterTypeSpecifier createParameterTypeSpecifier() {
        return decorate(super.createParameterTypeSpecifier());
    }

    @Override
    public PointFrom createPointFrom() {
        return decorate(super.createPointFrom());
    }

    @Override
    public PopulationStdDev createPopulationStdDev() {
        return decorate(super.createPopulationStdDev());
    }

    @Override
    public PopulationVariance createPopulationVariance() {
        return decorate(super.createPopulationVariance());
    }

    @Override
    public PositionOf createPositionOf() {
        return decorate(super.createPositionOf());
    }

    @Override
    public Power createPower() {
        return decorate(super.createPower());
    }

    @Override
    public Precision createPrecision() {
        return decorate(super.createPrecision());
    }

    @Override
    public Predecessor createPredecessor() {
        return decorate(super.createPredecessor());
    }

    @Override
    public Product createProduct() {
        return decorate(super.createProduct());
    }

    @Override
    public ProperContains createProperContains() {
        return decorate(super.createProperContains());
    }

    @Override
    public ProperIn createProperIn() {
        return decorate(super.createProperIn());
    }

    @Override
    public ProperIncludedIn createProperIncludedIn() {
        return decorate(super.createProperIncludedIn());
    }

    @Override
    public ProperIncludes createProperIncludes() {
        return decorate(super.createProperIncludes());
    }

    @Override
    public Property createProperty() {
        return decorate(super.createProperty());
    }

    @Override
    public Quantity createQuantity() {
        return decorate(super.createQuantity());
    }

    @Override
    public Query createQuery() {
        return decorate(super.createQuery());
    }

    @Override
    public QueryLetRef createQueryLetRef() {
        return decorate(super.createQueryLetRef());
    }

    @Override
    public Ratio createRatio() {
        return decorate(super.createRatio());
    }

    @Override
    public Repeat createRepeat() {
        return decorate(super.createRepeat());
    }

    @Override
    public ReplaceMatches createReplaceMatches() {
        return decorate(super.createReplaceMatches());
    }

    @Override
    public Retrieve createRetrieve() {
        return decorate(super.createRetrieve());
    }

    @Override
    public ReturnClause createReturnClause() {
        return decorate(super.createReturnClause());
    }

    @Override
    public Round createRound() {
        return decorate(super.createRound());
    }

    @Override
    public SameAs createSameAs() {
        return decorate(super.createSameAs());
    }

    @Override
    public SameOrAfter createSameOrAfter() {
        return decorate(super.createSameOrAfter());
    }

    @Override
    public SameOrBefore createSameOrBefore() {
        return decorate(super.createSameOrBefore());
    }

    @Override
    public Search createSearch() {
        return decorate(super.createSearch());
    }

    @Override
    public SingletonFrom createSingletonFrom() {
        return decorate(super.createSingletonFrom());
    }

    @Override
    public Size createSize() {
        return decorate(super.createSize());
    }

    @Override
    public Slice createSlice() {
        return decorate(super.createSlice());
    }

    @Override
    public Sort createSort() {
        return decorate(super.createSort());
    }

    @Override
    public SortClause createSortClause() {
        return decorate(super.createSortClause());
    }

    @Override
    public Split createSplit() {
        return decorate(super.createSplit());
    }

    @Override
    public SplitOnMatches createSplitOnMatches() {
        return decorate(super.createSplitOnMatches());
    }

    @Override
    public Start createStart() {
        return decorate(super.createStart());
    }

    @Override
    public Starts createStarts() {
        return decorate(super.createStarts());
    }

    @Override
    public StartsWith createStartsWith() {
        return decorate(super.createStartsWith());
    }

    @Override
    public StdDev createStdDev() {
        return decorate(super.createStdDev());
    }

    @Override
    public Substring createSubstring() {
        return decorate(super.createSubstring());
    }

    @Override
    public SubsumedBy createSubsumedBy() {
        return decorate(super.createSubsumedBy());
    }

    @Override
    public Subsumes createSubsumes() {
        return decorate(super.createSubsumes());
    }

    @Override
    public Subtract createSubtract() {
        return decorate(super.createSubtract());
    }

    @Override
    public Successor createSuccessor() {
        return decorate(super.createSuccessor());
    }

    @Override
    public Sum createSum() {
        return decorate(super.createSum());
    }

    @Override
    public Time createTime() {
        return decorate(super.createTime());
    }

    @Override
    public TimeFrom createTimeFrom() {
        return decorate(super.createTimeFrom());
    }

    @Override
    public TimeOfDay createTimeOfDay() {
        return decorate(super.createTimeOfDay());
    }

    @Override
    public Times createTimes() {
        return decorate(super.createTimes());
    }

    @Override
    public TimezoneFrom createTimezoneFrom() {
        return decorate(super.createTimezoneFrom());
    }

    @Override
    public TimezoneOffsetFrom createTimezoneOffsetFrom() {
        return decorate(super.createTimezoneOffsetFrom());
    }

    @Override
    public ToBoolean createToBoolean() {
        return decorate(super.createToBoolean());
    }

    @Override
    public ToChars createToChars() {
        return decorate(super.createToChars());
    }

    @Override
    public ToConcept createToConcept() {
        return decorate(super.createToConcept());
    }

    @Override
    public ToDate createToDate() {
        return decorate(super.createToDate());
    }

    @Override
    public ToDateTime createToDateTime() {
        return decorate(super.createToDateTime());
    }

    @Override
    public ToDecimal createToDecimal() {
        return decorate(super.createToDecimal());
    }

    @Override
    public ToInteger createToInteger() {
        return decorate(super.createToInteger());
    }

    @Override
    public ToList createToList() {
        return decorate(super.createToList());
    }

    @Override
    public ToLong createToLong() {
        return decorate(super.createToLong());
    }

    @Override
    public ToQuantity createToQuantity() {
        return decorate(super.createToQuantity());
    }

    @Override
    public ToRatio createToRatio() {
        return decorate(super.createToRatio());
    }

    @Override
    public ToString createToString() {
        return decorate(super.createToString());
    }

    @Override
    public ToTime createToTime() {
        return decorate(super.createToTime());
    }

    @Override
    public Today createToday() {
        return decorate(super.createToday());
    }

    @Override
    public Total createTotal() {
        return decorate(super.createTotal());
    }

    @Override
    public Truncate createTruncate() {
        return decorate(super.createTruncate());
    }

    @Override
    public TruncatedDivide createTruncatedDivide() {
        return decorate(super.createTruncatedDivide());
    }

    @Override
    public Tuple createTuple() {
        return decorate(super.createTuple());
    }

    // @Override
    // public TupleElement createTupleElement() {
    //     return decorate(super.createTupleElement());
    // }

    @Override
    public TupleElementDefinition createTupleElementDefinition() {
        return decorate(super.createTupleElementDefinition());
    }

    @Override
    public TupleTypeSpecifier createTupleTypeSpecifier() {
        return decorate(super.createTupleTypeSpecifier());
    }

    @Override
    public Union createUnion() {
        return decorate(super.createUnion());
    }

    @Override
    public Upper createUpper() {
        return decorate(super.createUpper());
    }

    @Override
    public UsingDef createUsingDef() {
        return decorate(super.createUsingDef());
    }

    @Override
    public ValueSetDef createValueSetDef() {
        return decorate(super.createValueSetDef());
    }

    @Override
    public ValueSetRef createValueSetRef() {
        return decorate(super.createValueSetRef());
    }

    @Override
    public Variance createVariance() {
        return decorate(super.createVariance());
    }

    // @Override
    // public VersionedIdentifier createVersionedIdentifier() {
    //     return decorate(super.createVersionedIdentifier());
    // }

    @Override
    public Width createWidth() {
        return decorate(super.createWidth());
    }

    @Override
    public With createWith() {
        return decorate(super.createWith());
    }

    @Override
    public Without createWithout() {
        return decorate(super.createWithout());
    }

    @Override
    public Xor createXor() {
        return decorate(super.createXor());
    }
}
