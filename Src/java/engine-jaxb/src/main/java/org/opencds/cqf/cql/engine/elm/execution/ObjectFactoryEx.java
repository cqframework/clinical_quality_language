package org.opencds.cqf.cql.engine.elm.execution;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.cqframework.cql.elm.execution.*;

@XmlRegistry
public class ObjectFactoryEx extends ObjectFactory {
    @Override
    public Abs createAbs() { return new AbsEvaluator(); }

    @Override
    public Add createAdd() { return new AddEvaluator(); }

    @Override
    public After createAfter() { return new AfterEvaluator(); }

    @Override
    public AliasRef createAliasRef() { return new AliasRefEvaluator(); }

    @Override
    public AllTrue createAllTrue() { return new AllTrueEvaluator(); }

    @Override
    public And createAnd() { return new AndEvaluator(); }

    @Override
    public AnyTrue createAnyTrue() { return new AnyTrueEvaluator(); }

    @Override
    public AnyInCodeSystem createAnyInCodeSystem() { return new AnyInCodeSystemEvaluator(); }

    @Override
    public AnyInValueSet createAnyInValueSet() { return new AnyInValueSetEvaluator(); }

    @Override
    public As createAs() { return new AsEvaluator(); }

    @Override
    public Avg createAvg() { return new AvgEvaluator(); }

    @Override
    public Before createBefore() { return new BeforeEvaluator(); }

    @Override
    public CalculateAge createCalculateAge() { return new CalculateAgeEvaluator(); }

    @Override
    public CalculateAgeAt createCalculateAgeAt() { return new CalculateAgeAtEvaluator(); }

    @Override
    public Case createCase() { return new CaseEvaluator(); }

    @Override
    public Ceiling createCeiling() { return new CeilingEvaluator(); }

    @Override
    public Children createChildren() { return new ChildrenEvaluator(); }

    @Override
    public Coalesce createCoalesce() { return new CoalesceEvaluator(); }

    @Override
    public Code createCode() { return new CodeEvaluator(); }

    @Override
    public CodeRef createCodeRef() { return new CodeRefEvaluator(); }

    @Override
    public CodeSystemRef createCodeSystemRef() { return new CodeSystemRefEvaluator(); }

    @Override
    public Collapse createCollapse() { return new CollapseEvaluator(); }

    @Override
    public Combine createCombine() { return new CombineEvaluator(); }

    @Override
    public Concatenate createConcatenate() { return new ConcatenateEvaluator(); }

    @Override
    public Concept createConcept() { return new ConceptEvaluator(); }

    @Override
    public ConceptRef createConceptRef() { return new ConceptRefEvaluator(); }

    @Override
    public Contains createContains() { return new ContainsEvaluator(); }

    @Override
    public Convert createConvert() { return new ConvertEvaluator(); }

    @Override
    public ConvertQuantity createConvertQuantity() { return new ConvertQuantityEvaluator(); }

    @Override
    public ConvertsToBoolean createConvertsToBoolean() { return new ConvertsToBooleanEvaluator(); }

    @Override
    public ConvertsToDate createConvertsToDate() { return new ConvertsToDateEvaluator(); }

    @Override
    public ConvertsToDateTime createConvertsToDateTime() { return new ConvertsToDateTimeEvaluator(); }

    @Override
    public ConvertsToDecimal createConvertsToDecimal() { return new ConvertsToDecimalEvaluator(); }

    @Override
    public ConvertsToInteger createConvertsToInteger() { return new ConvertsToIntegerEvaluator(); }

    @Override
    public ConvertsToLong createConvertsToLong() { return new ConvertsToLongEvaluator(); }

    @Override
    public ConvertsToQuantity createConvertsToQuantity() { return new ConvertsToQuantityEvaluator(); }

    @Override
    public ConvertsToString createConvertsToString() { return new ConvertsToStringEvaluator(); }

    @Override
    public ConvertsToTime createConvertsToTime() { return new ConvertsToTimeEvaluator(); }

    @Override
    public Count createCount() { return new CountEvaluator(); }

    @Override
    public Date createDate() { return new DateEvaluator(); }

    @Override
    public DateTime createDateTime() { return new DateTimeEvaluator(); }

    @Override
    public DateFrom createDateFrom() { return new DateFromEvaluator(); }

    @Override
    public DateTimeComponentFrom createDateTimeComponentFrom() { return new DateTimeComponentFromEvaluator(); }

    @Override
    public Descendents createDescendents() { return new DescendentsEvaluator(); }

    @Override
    public DifferenceBetween createDifferenceBetween() { return new DifferenceBetweenEvaluator(); }

    @Override
    public Distinct createDistinct() { return new DistinctEvaluator(); }

    @Override
    public Divide createDivide() { return new DivideEvaluator(); }

    @Override
    public DurationBetween createDurationBetween() { return new DurationBetweenEvaluator(); }

    @Override
    public End createEnd() { return new EndEvaluator(); }

    @Override
    public Ends createEnds() { return new EndsEvaluator(); }

    @Override
    public EndsWith createEndsWith() { return new EndsWithEvaluator(); }

    @Override
    public Equal createEqual() { return new EqualEvaluator(); }

    @Override
    public Equivalent createEquivalent() { return new EquivalentEvaluator(); }

    @Override
    public Except createExcept() { return new ExceptEvaluator(); }

    @Override
    public Exists createExists() { return new ExistsEvaluator(); }

    @Override
    public Exp createExp() { return new ExpEvaluator(); }

    @Override
    public Expand createExpand() { return new ExpandEvaluator(); }

    @Override
    public ExpressionDef createExpressionDef() { return new ExpressionDefEvaluator(); }

    @Override
    public ExpressionRef createExpressionRef() { return new ExpressionRefEvaluator(); }

    @Override
    public Filter createFilter() { return new FilterEvaluator(); }

    @Override
    public First createFirst() { return new FirstEvaluator(); }

    @Override
    public Flatten createFlatten() { return new FlattenEvaluator(); }

    @Override
    public Floor createFloor() { return new FloorEvaluator(); }

    @Override
    public ForEach createForEach() { return new ForEachEvaluator(); }

    @Override
    public FunctionRef createFunctionRef() { return new FunctionRefEvaluator(); }

    @Override
    public GeometricMean createGeometricMean() { return new GeometricMeanEvaluator(); }

    @Override
    public Greater createGreater() { return new GreaterEvaluator(); }

    @Override
    public GreaterOrEqual createGreaterOrEqual() { return new GreaterOrEqualEvaluator(); }

    @Override
    public HighBoundary createHighBoundary() { return new HighBoundaryEvaluator(); }

    @Override
    public IdentifierRef createIdentifierRef() { return new IdentifierRefEvaluator(); }

    @Override
    public If createIf() { return new IfEvaluator(); }

     @Override
     public Implies createImplies() { return new ImpliesEvaluator(); }

    @Override
    public IncludedIn createIncludedIn() { return new IncludedInEvaluator(); }

    @Override
    public Includes createIncludes() { return new IncludesEvaluator(); }

    @Override
    public Indexer createIndexer() { return new IndexerEvaluator(); }

    @Override
    public IndexOf createIndexOf() { return new IndexOfEvaluator(); }

    @Override
    public In createIn() { return new InEvaluator(); }

    @Override
    public InCodeSystem createInCodeSystem() { return new InCodeSystemEvaluator(); }

    @Override
    public InValueSet createInValueSet() { return new InValueSetEvaluator(); }

    @Override
    public Instance createInstance() { return new InstanceEvaluator(); }

    @Override
    public Intersect createIntersect() { return new IntersectEvaluator(); }

    @Override
    public Interval createInterval() { return new IntervalEvaluator(); }

    @Override
    public Is createIs() { return new IsEvaluator(); }

    @Override
    public IsFalse createIsFalse() { return new IsFalseEvaluator(); }

    @Override
    public IsNull createIsNull() { return new IsNullEvaluator(); }

    @Override
    public IsTrue createIsTrue() { return new IsTrueEvaluator(); }

    @Override
    public Last createLast() { return new LastEvaluator(); }

    @Override
    public LastPositionOf createLastPositionOf() { return new LastPositionOfEvaluator(); }

    @Override
    public Length createLength() { return new LengthEvaluator(); }

    @Override
    public Less createLess() { return new LessEvaluator(); }

    @Override
    public LessOrEqual createLessOrEqual() { return new LessOrEqualEvaluator(); }

    @Override
    public List createList() { return new ListEvaluator(); }

    @Override
    public Literal createLiteral() { return new LiteralEvaluator(); }

    @Override
    public Ln createLn() { return new LnEvaluator(); }

    @Override
    public Log createLog() { return new LogEvaluator(); }

    @Override
    public LowBoundary createLowBoundary() { return new LowBoundaryEvaluator(); }

    @Override
    public Lower createLower() { return new LowerEvaluator(); }

    @Override
    public Matches createMatches() { return new MatchesEvaluator(); }

    @Override
    public MaxValue createMaxValue() { return new MaxValueEvaluator(); }

    @Override
    public Max createMax() { return new MaxEvaluator(); }

    @Override
    public Median createMedian() { return new MedianEvaluator(); }

    @Override
    public Meets createMeets() { return new MeetsEvaluator(); }

    @Override
    public MeetsAfter createMeetsAfter() { return new MeetsAfterEvaluator(); }

    @Override
    public MeetsBefore createMeetsBefore() { return new MeetsBeforeEvaluator(); }

    @Override
    public Message createMessage() { return new MessageEvaluator(); }

    @Override
    public MinValue createMinValue() { return new MinValueEvaluator(); }

    @Override
    public Min createMin() { return new MinEvaluator(); }

    @Override
    public Mode createMode() { return new ModeEvaluator(); }

    @Override
    public Modulo createModulo() { return new ModuloEvaluator(); }

    @Override
    public Multiply createMultiply() { return new MultiplyEvaluator(); }

    @Override
    public Negate createNegate() { return new NegateEvaluator(); }

    @Override
    public NotEqual createNotEqual() { return new NotEqualEvaluator(); }

    @Override
    public Not createNot() { return new NotEvaluator(); }

    @Override
    public Now createNow() { return new NowEvaluator(); }

    @Override
    public Null createNull() { return new NullEvaluator(); }

    @Override
    public OperandRef createOperandRef() { return new OperandRefEvaluator(); }

    @Override
    public Or createOr() { return new OrEvaluator(); }

    @Override
    public Overlaps createOverlaps() { return new OverlapsEvaluator(); }

    @Override
    public OverlapsBefore createOverlapsBefore() { return new OverlapsBeforeEvaluator(); }

    @Override
    public OverlapsAfter createOverlapsAfter() { return new OverlapsAfterEvaluator(); }

    @Override
    public ParameterRef createParameterRef() { return new ParameterRefEvaluator(); }

    @Override
    public PointFrom createPointFrom() { return new PointFromEvaluator(); }

    @Override
    public PopulationStdDev createPopulationStdDev() { return new PopulationStdDevEvaluator(); }

    @Override
    public PopulationVariance createPopulationVariance() { return new PopulationVarianceEvaluator(); }

    @Override
    public PositionOf createPositionOf() { return new PositionOfEvaluator(); }

    @Override
    public Power createPower() { return new PowerEvaluator(); }

    @Override
    public Precision createPrecision() { return new PrecisionEvaluator(); }

    @Override
    public Predecessor createPredecessor() { return new PredecessorEvaluator(); }

    @Override
    public Product createProduct() { return new ProductEvaluator(); }

    @Override
    public ProperContains createProperContains() { return new ProperContainsEvaluator(); }

    @Override
    public ProperIn createProperIn() { return new ProperInEvaluator(); }

    @Override
    public ProperIncludes createProperIncludes() { return new ProperIncludesEvaluator(); }

    @Override
    public ProperIncludedIn createProperIncludedIn() { return new ProperIncludedInEvaluator(); }

    @Override
    public Property createProperty() { return new PropertyEvaluator(); }

    @Override
    public Quantity createQuantity() { return new QuantityEvaluator(); }

    @Override
    public Query createQuery() { return new QueryEvaluator(); }

    @Override
    public QueryLetRef createQueryLetRef() { return new QueryLetRefEvaluator(); }

    @Override
    public Ratio createRatio() { return new RatioEvaluator(); }

    @Override
    public Repeat createRepeat() { return new RepeatEvaluator(); }

    @Override
    public ReplaceMatches createReplaceMatches() { return new ReplaceMatchesEvaluator(); }

    @Override
    public Retrieve createRetrieve() { return new RetrieveEvaluator(); }

    @Override
    public Round createRound() { return new RoundEvaluator(); }

    @Override
    public SameAs createSameAs() { return new SameAsEvaluator(); }

    @Override
    public SameOrAfter createSameOrAfter() { return new SameOrAfterEvaluator(); }

    @Override
    public SameOrBefore createSameOrBefore() { return new SameOrBeforeEvaluator(); }

    @Override
    public SingletonFrom createSingletonFrom() { return new SingletonFromEvaluator(); }

    @Override
    public Size createSize() { return new SizeEvaluator(); }

    @Override
    public Slice createSlice() { return new SliceEvaluator(); }

    @Override
    public Split createSplit() { return new SplitEvaluator(); }

    @Override
    public SplitOnMatches createSplitOnMatches() { return new SplitOnMatchesEvaluator(); }

    @Override
    public Start createStart() { return new StartEvaluator(); }

    @Override
    public Starts createStarts() { return new StartsEvaluator(); }

    @Override
    public StartsWith createStartsWith() { return new StartsWithEvaluator(); }

    @Override
    public StdDev createStdDev() { return new StdDevEvaluator(); }

    @Override
    public Substring createSubstring() { return new SubstringEvaluator(); }

    @Override
    public Subtract createSubtract() { return new SubtractEvaluator(); }

    @Override
    public Successor createSuccessor() { return new SuccessorEvaluator(); }

    @Override
    public Sum createSum() { return new SumEvaluator(); }

    @Override
    public Time createTime() { return new TimeEvaluator(); }

    @Override
    public TimeOfDay createTimeOfDay() { return new TimeOfDayEvaluator(); }

    @Override
    public TimeFrom createTimeFrom() { return new TimeFromEvaluator(); }

    @Override
    public TimezoneOffsetFrom createTimezoneOffsetFrom() { return new TimezoneOffsetFromEvaluator(); }

    @Override
    public Today createToday() { return new TodayEvaluator(); }

    @Override
    public ToBoolean createToBoolean() { return new ToBooleanEvaluator(); }

    @Override
    public ToConcept createToConcept() { return new ToConceptEvaluator(); }

    @Override
    public ToDecimal createToDecimal() { return new ToDecimalEvaluator(); }

    @Override
    public ToDate createToDate() { return new ToDateEvaluator(); }

    @Override
    public ToDateTime createToDateTime() { return new ToDateTimeEvaluator(); }

    @Override
    public ToInteger createToInteger() { return new ToIntegerEvaluator(); }

    @Override
    public ToLong createToLong() { return new ToLongEvaluator(); }

    @Override
    public ToList createToList() { return new ToListEvaluator(); }

    @Override
    public ToQuantity createToQuantity() { return new ToQuantityEvaluator(); }

    @Override
    public ToRatio createToRatio() { return new ToRatioEvaluator(); }

    @Override
    public ToString createToString() { return new ToStringEvaluator(); }

    @Override
    public ToTime createToTime() { return new ToTimeEvaluator(); }

    @Override
    public TruncatedDivide createTruncatedDivide() { return new TruncatedDivideEvaluator(); }

    @Override
    public Truncate createTruncate() { return new TruncateEvaluator(); }

    @Override
    public Tuple createTuple() { return new TupleEvaluator(); }

    @Override
    public Union createUnion() { return new UnionEvaluator(); }

    @Override
    public Upper createUpper() { return new UpperEvaluator(); }

    @Override
    public Variance createVariance() { return new VarianceEvaluator(); }

    @Override
    public ValueSetRef createValueSetRef() { return new ValueSetRefEvaluator(); }

    @Override
    public Width createWidth() { return new WidthEvaluator(); }

    @Override
    public Xor createXor() { return new XorEvaluator(); }

    // The JAXB implementations does not recursively search the superclass for annotations
    @Override
    @XmlElementDecl(namespace = "urn:hl7-org:elm:r1", name = "library")
    public JAXBElement<Library> createLibrary(Library value) {
        return new JAXBElement<Library>(new QName("urn:hl7-org:elm:r1", "library"), Library.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Narrative }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Narrative }{@code >}
     */
    @XmlElementDecl(namespace = "urn:hl7-org:cql-annotations:r1", name = "s", scope = Narrative.class)
    public JAXBElement<Narrative> createNarrativeS(Narrative value) {
        return new JAXBElement<Narrative>(new QName("urn:hl7-org:cql-annotations:r1", "s"), Narrative.class, Narrative.class, value);
    }

}
