package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.fhir.ucum.UcumException;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhirpath.tests.Group;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.runtime.Code;

public class CQLOperationsR4Test extends TestFhirPath {

    private static FhirContext fhirContext = FhirContext.forCached(FhirVersionEnum.R4);
    private static R4FhirModelResolver fhirModelResolver = new CachedR4FhirModelResolver();
    private static RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(
            new SearchParameterResolver(fhirContext),
            fhirModelResolver,
            fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseR4"));
    private static CompositeDataProvider provider = new CompositeDataProvider(fhirModelResolver, retrieveProvider);

    @Test
    void test() throws UcumException {
        String[] listOfFiles = {
            "r4/tests-fhir-r4.xml",
            "cql/CqlAggregateFunctionsTest.xml",
            "cql/CqlAggregateTest.xml",
            "cql/CqlArithmeticFunctionsTest.xml",
            "cql/CqlComparisonOperatorsTest.xml",
            "cql/CqlConditionalOperatorsTest.xml",
            "cql/CqlDateTimeOperatorsTest.xml",
            "cql/CqlErrorsAndMessagingOperatorsTest.xml",
            "cql/CqlIntervalOperatorsTest.xml",
            "cql/CqlListOperatorsTest.xml",
            "cql/CqlLogicalOperatorsTest.xml",
            "cql/CqlNullologicalOperatorsTest.xml",
            "cql/CqlStringOperatorsTest.xml",
            "cql/CqlTypeOperatorsTest.xml",
            "cql/CqlTypesTest.xml",
            "cql/ValueLiteralsAndSelectors.xml"
        };

        List<DynamicTest> testsToRun = new ArrayList<>();
        for (String file : listOfFiles) {
            for (Group group : loadTestsFile(file).getGroup()) {
                for (org.hl7.fhirpath.tests.Test test : group.getTest()) {
                    if (!"2.1.0".equals(test.getVersion())) { // unsupported version
                        test(file, group, test);
                    }
                }
            }
        }
    }

    public static Set<String> SKIP = Sets.newHashSet(
            "cql/CqlAggregateTest/AggregateTests/RolledOutIntervals",
            "cql/CqlArithmeticFunctionsTest/Divide/Divide1Q1Q",
            "cql/CqlArithmeticFunctionsTest/Ln/Ln1000D",
            "cql/CqlArithmeticFunctionsTest/Ln/Ln1000",
            "cql/CqlArithmeticFunctionsTest/MinValue/LongMinValue",
            "cql/CqlArithmeticFunctionsTest/Multiply/Multiply1CMBy2CM",
            "cql/CqlComparisonOperatorsTest/Equal/QuantityEqCM1M01",
            "cql/CqlComparisonOperatorsTest/Equivalent/EquivEqCM1M01",
            "cql/CqlComparisonOperatorsTest/Greater/GreaterM1CM1",
            "cql/CqlComparisonOperatorsTest/Greater/GreaterM1CM10",
            "cql/CqlComparisonOperatorsTest/Greater Or Equal/GreaterOrEqualM1CM1",
            "cql/CqlComparisonOperatorsTest/Greater Or Equal/GreaterOrEqualM1CM10",
            "cql/CqlComparisonOperatorsTest/Less/LessM1CM1",
            "cql/CqlComparisonOperatorsTest/Less/LessM1CM10",
            "cql/CqlComparisonOperatorsTest/Less Or Equal/LessOrEqualM1CM1",
            "cql/CqlComparisonOperatorsTest/Less Or Equal/LessOrEqualM1CM10",
            "cql/CqlComparisonOperatorsTest/Not Equal/QuantityNotEqCM1M01",
            "cql/CqlDateTimeOperatorsTest/DateTimeComponentFrom/DateTimeComponentFromTimezone",
            "cql/CqlDateTimeOperatorsTest/Duration/DateTimeDurationBetweenYear",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainAdd",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainInterval",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainInterval2",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainMultiply",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainSubtract",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DurationInDaysA",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DurationInDaysAA",
            "cql/CqlDateTimeOperatorsTest/Uncertainty tests/TimeDurationBetweenHourDiffPrecision",
            "cql/CqlIntervalOperatorsTest/Intersect/TestIntersectNull",
            "cql/CqlIntervalOperatorsTest/Intersect/TestIntersectNull1",
            "cql/CqlIntervalOperatorsTest/Intersect/TestIntersectNull2",
            "cql/CqlIntervalOperatorsTest/Intersect/TestIntersectNull3",
            "cql/CqlIntervalOperatorsTest/Intersect/TestIntersectNull4",
            "cql/CqlIntervalOperatorsTest/Expand/ExpandIntervalPer2",
            "cql/CqlIntervalOperatorsTest/Expand/ExpandPer0D1",
            "cql/CqlIntervalOperatorsTest/Expand/ExpandPer1",
            "cql/CqlIntervalOperatorsTest/Expand/ExpandPer2Days",
            "cql/CqlIntervalOperatorsTest/Expand/ExpandPerMinute",
            "cql/CqlListOperatorsTest/Distinct/DistinctANullANull",
            "cql/CqlListOperatorsTest/Distinct/DistinctNullNullNull",
            "cql/CqlListOperatorsTest/Equivalent/Equivalent123AndABC",
            "cql/CqlListOperatorsTest/Equivalent/Equivalent123AndString123",
            "cql/CqlListOperatorsTest/Equivalent/EquivalentABCAnd123",
            "cql/CqlListOperatorsTest/Flatten/FlattenListNullAndNull",
            "cql/CqlListOperatorsTest/NotEqual/NotEqual123AndABC",
            "cql/CqlListOperatorsTest/NotEqual/NotEqual123AndString123",
            "cql/CqlListOperatorsTest/NotEqual/NotEqualABCAnd123",
            "cql/CqlListOperatorsTest/ProperContains/ProperContainsNullRightFalse",
            "cql/CqlListOperatorsTest/ProperContains/ProperContainsTimeNull",
            "cql/CqlListOperatorsTest/ProperIn/ProperInTimeNull",
            "cql/CqlListOperatorsTest/ProperlyIncludedIn/ProperlyIncludedInNulRight",
            "cql/CqlListOperatorsTest/ProperlyIncludes/ProperlyIncludesNullLeft",
            "cql/CqlListOperatorsTest/Union/UnionListNullAndListNull",
            "cql/CqlStringOperatorsTest/toString tests/DateTimeToString3",
            "cql/CqlTypeOperatorsTest/As/AsQuantity",
            "cql/CqlTypeOperatorsTest/As/CastAsQuantity",
            "cql/CqlTypeOperatorsTest/Convert/StringToDateTimeMalformed",
            "cql/CqlTypeOperatorsTest/Convert/StringToIntegerError",
            "cql/CqlTypeOperatorsTest/ToDateTime/ToDateTimeDate",
            "cql/CqlTypeOperatorsTest/ToDateTime/ToDateTimeMalformed",
            "cql/CqlTypeOperatorsTest/ToDateTime/ToDateTimeTimeUnspecified",
            "cql/CqlTypeOperatorsTest/ToTime/ToTime2",
            "cql/CqlTypeOperatorsTest/ToTime/ToTime3",
            "cql/CqlTypeOperatorsTest/ToTime/ToTime4",
            "cql/CqlTypeOperatorsTest/ToTime/ToTimeMalformed",
            "cql/CqlTypesTest/DateTime/DateTimeUncertain",
            "cql/CqlTypesTest/Time/TimeUpperBoundMillis",
            "cql/ValueLiteralsAndSelectors/Boolean/BooleanFalse",
            "cql/ValueLiteralsAndSelectors/Boolean/BooleanTrue",
            "cql/ValueLiteralsAndSelectors/Decimal/Decimal10Pow20",
            "cql/ValueLiteralsAndSelectors/Decimal/DecimalNeg10Pow20",
            "cql/ValueLiteralsAndSelectors/Decimal/DecimalNegTenthStep",
            "cql/ValueLiteralsAndSelectors/Decimal/DecimalPos10Pow20",
            "cql/ValueLiteralsAndSelectors/Decimal/DecimalPosTenthStep",
            "cql/ValueLiteralsAndSelectors/Decimal/DecimalTenthStep",
            "cql/ValueLiteralsAndSelectors/Null/Null",
            "r4/tests-fhir-r4/from-Zulip/(true and 'foo').empty()",
            "r4/tests-fhir-r4/from-Zulip/(true | 'foo').allTrue()",
            "r4/tests-fhir-r4/testAggregate/testAggregate1",
            "r4/tests-fhir-r4/testAggregate/testAggregate2",
            "r4/tests-fhir-r4/testAggregate/testAggregate3",
            "r4/tests-fhir-r4/testAggregate/testAggregate4",
            "r4/tests-fhir-r4/testAll/testAllTrue4",
            "r4/tests-fhir-r4/testCollectionBoolean/testCollectionBoolean2",
            "r4/tests-fhir-r4/testCollectionBoolean/testCollectionBoolean3",
            "r4/tests-fhir-r4/testCollectionBoolean/testCollectionBoolean4",
            "r4/tests-fhir-r4/testCollectionBoolean/testCollectionBoolean5",
            "r4/tests-fhir-r4/testCollectionBoolean/testCollectionBoolean6",
            "r4/tests-fhir-r4/testConformsTo/testConformsTo1",
            "r4/tests-fhir-r4/testConformsTo/testConformsTo2",
            "r4/tests-fhir-r4/testDistinct/testDistinct1",
            "r4/tests-fhir-r4/testDistinct/testDistinct2",
            "r4/tests-fhir-r4/testDistinct/testDistinct3",
            "r4/tests-fhir-r4/testDistinct/testDistinct5",
            "r4/tests-fhir-r4/testDistinct/testDistinct6",
            "r4/tests-fhir-r4/testDollar/testDollarOrderNotAllowed",
            "r4/tests-fhir-r4/testEquality/testEquality21",
            "r4/tests-fhir-r4/testEquality/testEquality22",
            "r4/tests-fhir-r4/testEquality/testEquality26",
            "r4/tests-fhir-r4/testEquality/testEquality27",
            "r4/tests-fhir-r4/testEquivalent/testEquivalent11",
            "r4/tests-fhir-r4/testEquivalent/testEquivalent17",
            "r4/tests-fhir-r4/testEquivalent/testEquivalent20",
            "r4/tests-fhir-r4/testEquivalent/testEquivalent21",
            "r4/tests-fhir-r4/testExclude/testExclude1",
            "r4/tests-fhir-r4/testExclude/testExclude2",
            "r4/tests-fhir-r4/testExclude/testExclude3",
            "r4/tests-fhir-r4/testExclude/testExclude4",
            "r4/tests-fhir-r4/testExtension/testExtension1",
            "r4/tests-fhir-r4/testExtension/testExtension2",
            "r4/tests-fhir-r4/testExtension/testExtension3",
            "r4/tests-fhir-r4/testFirstLast/testFirstLast1",
            "r4/tests-fhir-r4/testFirstLast/testFirstLast2",
            "r4/tests-fhir-r4/testGreaterThan/testGreaterThan26",
            "r4/tests-fhir-r4/testGreaterThan/testGreaterThan27",
            "r4/tests-fhir-r4/testGreatorOrEqual/testGreatorOrEqual26",
            "r4/tests-fhir-r4/testGreatorOrEqual/testGreatorOrEqual27",
            "r4/tests-fhir-r4/testIif/testIif1",
            "r4/tests-fhir-r4/testIif/testIif2",
            "r4/tests-fhir-r4/testIif/testIif3",
            "r4/tests-fhir-r4/testIif/testIif4",
            "r4/tests-fhir-r4/testIndexer/testIndexer1",
            "r4/tests-fhir-r4/testIntersect/testIntersect1",
            "r4/tests-fhir-r4/testIntersect/testIntersect2",
            "r4/tests-fhir-r4/testIntersect/testIntersect3",
            "r4/tests-fhir-r4/testIntersect/testIntersect4",
            "r4/tests-fhir-r4/testInvariants/extension('http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-scoring').exists() and extension('http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-scoring').value = 'ratio' implies group.population.where(code.coding.where(system = 'http://terminology.hl7.org/CodeSystem/measure-population').code = 'initial-population').count() in (1 | 2)",
            "r4/tests-fhir-r4/testLessOrEqual/testLessOrEqual26",
            "r4/tests-fhir-r4/testLessOrEqual/testLessOrEqual27",
            "r4/tests-fhir-r4/testLessThan/testLessThan26",
            "r4/tests-fhir-r4/testLessThan/testLessThan27",
            "r4/tests-fhir-r4/testLiterals/testDateGreaterThanDate",
            "r4/tests-fhir-r4/testLiterals/testDateNotEqualTimeMinute",
            "r4/tests-fhir-r4/testLiterals/testDateNotEqualTimeSecond",
            "r4/tests-fhir-r4/testLiterals/testDateNotEqualToday",
            "r4/tests-fhir-r4/testLiterals/testDateTimeGreaterThanDate1",
            "r4/tests-fhir-r4/testLiterals/testLiteralDateTimeTZGreater",
            "r4/tests-fhir-r4/testLiterals/testLiteralDateTimeTZLess",
            "r4/tests-fhir-r4/testLiterals/testLiteralDecimalGreaterThanIntegerTrue",
            "r4/tests-fhir-r4/testLiterals/testLiteralDecimalLessThanInteger",
            "r4/tests-fhir-r4/testLiterals/testLiteralDecimalLessThanInvalid",
            "r4/tests-fhir-r4/testMatches/testMatchesSingleLineMode1",
            "r4/tests-fhir-r4/testMatches/testMatchesWithinUrl1",
            "r4/tests-fhir-r4/testNEquality/testNEquality15",
            "r4/tests-fhir-r4/testNEquality/testNEquality16",
            "r4/tests-fhir-r4/testNEquality/testNEquality20",
            "r4/tests-fhir-r4/testNEquality/testNEquality21",
            "r4/tests-fhir-r4/testNEquality/testNEquality24",
            "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent13",
            "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent17",
            "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent20",
            "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent21",
            "r4/tests-fhir-r4/testNow/testNow1",
            "r4/tests-fhir-r4/testNow/testNow2",
            "r4/tests-fhir-r4/testNow/testNow3",
            "r4/tests-fhir-r4/testPower/testPower3",
            "r4/tests-fhir-r4/testPrecedence/testPrecedence3",
            "r4/tests-fhir-r4/testPrecedence/testPrecedence4",
            "r4/tests-fhir-r4/testQuantity/testQuantity1",
            "r4/tests-fhir-r4/testQuantity/testQuantity2",
            "r4/tests-fhir-r4/testQuantity/testQuantity3",
            "r4/tests-fhir-r4/testQuantity/testQuantity4",
            "r4/tests-fhir-r4/testQuantity/testQuantity5",
            "r4/tests-fhir-r4/testQuantity/testQuantity6",
            "r4/tests-fhir-r4/testQuantity/testQuantity7",
            "r4/tests-fhir-r4/testQuantity/testQuantity8",
            "r4/tests-fhir-r4/testQuantity/testQuantity9",
            "r4/tests-fhir-r4/testQuantity/testQuantity10",
            "r4/tests-fhir-r4/testQuantity/testQuantity11",
            "r4/tests-fhir-r4/testRepeat/testRepeat1",
            "r4/tests-fhir-r4/testRepeat/testRepeat2",
            "r4/tests-fhir-r4/testRepeat/testRepeat3",
            "r4/tests-fhir-r4/testRepeat/testRepeat4",
            "r4/tests-fhir-r4/testSelect/testSelect1",
            "r4/tests-fhir-r4/testSingle/testSingle2",
            "r4/tests-fhir-r4/testSkip/testSkip1",
            "r4/tests-fhir-r4/testSkip/testSkip3",
            "r4/tests-fhir-r4/testSqrt/testSqrt2",
            "r4/tests-fhir-r4/testSubSetOf/testSubSetOf1",
            "r4/tests-fhir-r4/testSubSetOf/testSubSetOf2",
            "r4/tests-fhir-r4/testSuperSetOf/testSuperSetOf1",
            "r4/tests-fhir-r4/testSuperSetOf/testSuperSetOf2",
            "r4/tests-fhir-r4/testTail/testTail1",
            "r4/tests-fhir-r4/testTail/testTail2",
            "r4/tests-fhir-r4/testTake/testTake2",
            "r4/tests-fhir-r4/testTake/testTake3",
            "r4/tests-fhir-r4/testTake/testTake4",
            "r4/tests-fhir-r4/testTimeOfDay/testTimeOfDay1",
            "r4/tests-fhir-r4/testToChars/testToChars1",
            "r4/tests-fhir-r4/testToday/testToday1",
            "r4/tests-fhir-r4/testToday/testToday2",
            "r4/tests-fhir-r4/testToday/testToday3",
            "r4/tests-fhir-r4/testToInteger/testToInteger4",
            "r4/tests-fhir-r4/testTrace/testTrace2",
            "r4/tests-fhir-r4/testType/testType1",
            "r4/tests-fhir-r4/testType/testType2",
            "r4/tests-fhir-r4/testType/testType3",
            "r4/tests-fhir-r4/testType/testType4",
            "r4/tests-fhir-r4/testType/testType6",
            "r4/tests-fhir-r4/testType/testType9",
            "r4/tests-fhir-r4/testType/testType10",
            "r4/tests-fhir-r4/testType/testType13",
            "r4/tests-fhir-r4/testType/testType14",
            "r4/tests-fhir-r4/testType/testType15",
            "r4/tests-fhir-r4/testType/testType16",
            "r4/tests-fhir-r4/testType/testType18",
            "r4/tests-fhir-r4/testType/testType19",
            "r4/tests-fhir-r4/testType/testType20",
            "r4/tests-fhir-r4/testType/testType21",
            "r4/tests-fhir-r4/testType/testType22",
            "r4/tests-fhir-r4/testType/testType23",
            "r4/tests-fhir-r4/testTypes/testBooleanLiteralConvertsToQuantity",
            "r4/tests-fhir-r4/testTypes/testBooleanLiteralIsNotSystemQuantity",
            "r4/tests-fhir-r4/testTypes/testDecimalLiteralIsNotQuantity",
            "r4/tests-fhir-r4/testTypes/testDecimalLiteralToDecimal",
            "r4/tests-fhir-r4/testTypes/testDecimalLiteralToIntegerIsEmpty",
            "r4/tests-fhir-r4/testTypes/testIntegerLiteralIsSystemInteger",
            "r4/tests-fhir-r4/testTypes/testIntegerLiteralToInteger",
            "r4/tests-fhir-r4/testTypes/testQuantityLiteralWeekToString",
            "r4/tests-fhir-r4/testTypes/testStringDecimalLiteralIsNotSystemQuantity",
            "r4/tests-fhir-r4/testTypes/testStringIntegerLiteralIsNotQuantity",
            "r4/tests-fhir-r4/testTypes/testStringLiteralToString",
            "r4/tests-fhir-r4/testTypes/testStringQuantityWeekConvertsToQuantityFalse",
            "r4/tests-fhir-r4/testUnion/testUnion4",
            "r4/tests-fhir-r4/testUnion/testUnion5",
            "r4/tests-fhir-r4/testUnion/testUnion8",
            "r4/tests-fhir-r4/testVariables/testVariables1",
            "r4/tests-fhir-r4/testVariables/testVariables2",
            "r4/tests-fhir-r4/testVariables/testVariables3",
            "r4/tests-fhir-r4/testVariables/testVariables4",
            "r4/tests-fhir-r4/testWhere/testWhere2",
            "r4/tests-fhir-r4/testWhere/testWhere3",
            "r4/tests-fhir-r4/testWhere/testWhere4");

    public String getTestName(String file, Group group, org.hl7.fhirpath.tests.Test test) {
        return file.replaceAll(".xml", "") + "/" + group.getName()
                + "/"
                + (test.getName() != null
                        ? test.getName()
                        : test.getExpression().getValue());
    }

    void test(String file, Group group, org.hl7.fhirpath.tests.Test test) throws UcumException {
        var name = getTestName(file, group, test);
        if (SKIP.contains(name)) {
            System.out.println("Skipping " + name);
            return;
        }
        System.out.println("Running " + name);
        runTest(test, "r4/input/", fhirContext, provider, fhirModelResolver);
    }

    @Override
    public Boolean compareResults(
            Object expectedResult,
            Object actualResult,
            State state,
            FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> resolver) {
        // Perform FHIR system-defined type conversions
        if (actualResult instanceof Enumeration) {
            actualResult = ((Enumeration<?>) actualResult).getValueAsString();
        } else if (actualResult instanceof BooleanType) {
            actualResult = ((BooleanType) actualResult).getValue();
        } else if (actualResult instanceof IntegerType) {
            actualResult = ((IntegerType) actualResult).getValue();
        } else if (actualResult instanceof DecimalType) {
            actualResult = ((DecimalType) actualResult).getValue();
        } else if (actualResult instanceof StringType) {
            actualResult = ((StringType) actualResult).getValue();
        } else if (actualResult instanceof BaseDateTimeType) {
            actualResult = resolver.toJavaPrimitive(actualResult, actualResult);
        } else if (actualResult instanceof Quantity) {
            Quantity quantity = (Quantity) actualResult;
            actualResult = new org.opencds.cqf.cql.engine.runtime.Quantity()
                    .withValue(quantity.getValue())
                    .withUnit(quantity.getUnit());
        } else if (actualResult instanceof Coding) {
            Coding coding = (Coding) actualResult;
            actualResult = new Code()
                    .withCode(coding.getCode())
                    .withDisplay(coding.getDisplay())
                    .withSystem(coding.getSystem())
                    .withVersion(coding.getVersion());
        }
        return EqualEvaluator.equal(expectedResult, actualResult, state);
    }
}
