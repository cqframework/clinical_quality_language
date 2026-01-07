package org.hl7.fhirpath

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.common.collect.Sets
import org.hl7.fhir.r4.model.BaseDateTimeType
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhirpath.tests.Group
import org.hl7.fhirpath.tests.Test
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.runtime.Code

class CQLOperationsR4Test : TestFhirPath() {
    @ParameterizedTest(name = "{0}")
    @MethodSource("dataMethod")
    fun test(name: String, test: Test) {
        Assumptions.assumeFalse(SKIP.contains(name), "Skipping $name")
        runTest(test, "r4/input/", fhirContext, provider, fhirModelResolver)
    }

    override fun compareResults(
        expectedResult: Any?,
        actualResult: Any?,
        state: State?,
        resolver: FhirModelResolver<*, *, *, *, *, *, *, *>,
    ): Boolean? {
        // Perform FHIR system-defined type conversions
        var actualResult = actualResult
        when (actualResult) {
            is Enumeration<*> -> {
                actualResult = actualResult.valueAsString
            }

            is BooleanType -> {
                actualResult = actualResult.value
            }

            is IntegerType -> {
                actualResult = actualResult.value
            }

            is DecimalType -> {
                actualResult = actualResult.value
            }

            is StringType -> {
                actualResult = actualResult.value
            }

            is BaseDateTimeType -> {
                actualResult = resolver.toJavaPrimitive(actualResult, actualResult)
            }

            is Quantity -> {
                val quantity = actualResult
                actualResult =
                    org.opencds.cqf.cql.engine.runtime
                        .Quantity()
                        .withValue(quantity.getValue())
                        .withUnit(quantity.getUnit())
            }

            is Coding -> {
                val coding = actualResult
                actualResult =
                    Code()
                        .withCode(coding.getCode())
                        .withDisplay(coding.getDisplay())
                        .withSystem(coding.getSystem())
                        .withVersion(coding.getVersion())
            }
        }
        return EqualEvaluator.equal(expectedResult, actualResult, state)
    }

    companion object {
        private val fhirContext: FhirContext = FhirContext.forCached(FhirVersionEnum.R4)
        private val fhirModelResolver: R4FhirModelResolver = CachedR4FhirModelResolver()
        private val retrieveProvider =
            RestFhirRetrieveProvider(
                SearchParameterResolver(fhirContext),
                fhirModelResolver,
                fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseR4"),
            )
        private val provider = CompositeDataProvider(fhirModelResolver, retrieveProvider)

        @JvmStatic
        fun dataMethod(): Array<Array<Any>> {
            val listOfFiles =
                arrayOf(
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
                    "cql/ValueLiteralsAndSelectors.xml",
                )

            val testsToRun: MutableList<Array<Any>> = ArrayList()
            for (file in listOfFiles) {
                for (group in loadTestsFile(file)!!.getGroup()) {
                    for (test in group.getTest()) {
                        if ("2.1.0" != test.getVersion()) { // unsupported version
                            val name: String = getTestName(file, group, test)
                            testsToRun.add(arrayOf(name, test))
                        }
                    }
                }
            }
            return testsToRun.toTypedArray<Array<Any>>()
        }

        var SKIP: MutableSet<String> =
            Sets.newHashSet(
                "cql/CqlAggregateTest/AggregateTests/RolledOutIntervals",
                "cql/CqlArithmeticFunctionsTest/Divide/Divide1Q1Q",
                "cql/CqlArithmeticFunctionsTest/Ln/Ln1000D",
                "cql/CqlArithmeticFunctionsTest/Ln/Ln1000",
                "cql/CqlArithmeticFunctionsTest/MinValue/LongMinValue",
                "cql/CqlArithmeticFunctionsTest/Multiply/Multiply1CMBy2CM",
                "cql/CqlDateTimeOperatorsTest/Duration/DateTimeDurationBetweenYear",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainAdd",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainInterval",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainInterval2",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainMultiply",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DateTimeDurationBetweenUncertainSubtract",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DurationInDaysA",
                "cql/CqlDateTimeOperatorsTest/Uncertainty tests/DurationInDaysAA",
                "cql/CqlIntervalOperatorsTest/Intersect/TestIntersectNull",
                "cql/CqlIntervalOperatorsTest/Expand/ExpandPer0D1",
                "cql/CqlIntervalOperatorsTest/Expand/ExpandPer0D1IntervalOverload",
                "cql/CqlListOperatorsTest/Equal/EqualABCAnd123",
                "cql/CqlListOperatorsTest/Equal/Equal123AndABC",
                "cql/CqlListOperatorsTest/Equal/Equal123AndString123",
                "cql/CqlListOperatorsTest/Equivalent/Equivalent123AndABC",
                "cql/CqlListOperatorsTest/Equivalent/Equivalent123AndString123",
                "cql/CqlListOperatorsTest/Equivalent/EquivalentABCAnd123",
                "cql/CqlListOperatorsTest/NotEqual/NotEqual123AndABC",
                "cql/CqlListOperatorsTest/NotEqual/NotEqual123AndString123",
                "cql/CqlListOperatorsTest/NotEqual/NotEqualABCAnd123",
                "cql/CqlListOperatorsTest/ProperlyIncludedIn/ProperlyIncludedInNulRight",
                "cql/CqlListOperatorsTest/ProperlyIncludes/ProperlyIncludesNullLeft",
                "cql/CqlListOperatorsTest/Union/UnionListNullAndListNull",
                "cql/CqlStringOperatorsTest/toString tests/DateTimeToString3",
                "cql/CqlTypeOperatorsTest/As/AsQuantity",
                "cql/CqlTypeOperatorsTest/As/CastAsQuantity",
                "cql/CqlTypeOperatorsTest/ToTime/ToTime2",
                "cql/CqlTypeOperatorsTest/ToTime/ToTime3",
                "cql/CqlTypeOperatorsTest/ToTime/ToTime4",
                "cql/CqlTypesTest/DateTime/DateTimeUncertain",
                "cql/CqlTypesTest/Time/TimeUpperBoundMillis",
                "cql/ValueLiteralsAndSelectors/Decimal/Decimal10Pow20",
                "cql/ValueLiteralsAndSelectors/Decimal/DecimalNeg10Pow20",
                "cql/ValueLiteralsAndSelectors/Decimal/DecimalNegTenthStep",
                "cql/ValueLiteralsAndSelectors/Decimal/DecimalPos10Pow20",
                "cql/ValueLiteralsAndSelectors/Decimal/DecimalPosTenthStep",
                "cql/ValueLiteralsAndSelectors/Decimal/DecimalTenthStep",
                "r4/tests-fhir-r4/from-Zulip/(true and 'foo').empty()",
                "r4/tests-fhir-r4/from-Zulip/(true | 'foo').allTrue()",
                "r4/tests-fhir-r4/testAggregate/testAggregate1",
                "r4/tests-fhir-r4/testAggregate/testAggregate2",
                "r4/tests-fhir-r4/testAggregate/testAggregate3",
                "r4/tests-fhir-r4/testAggregate/testAggregate4",
                "r4/tests-fhir-r4/testAll/testAllTrue4",
                "r4/tests-fhir-r4/testBasics/testIdentifierContext",
                "r4/tests-fhir-r4/testBasics/testIdentifierContextIsNotResource",
                "r4/tests-fhir-r4/testBasics/testContainerAccess",
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
                "r4/tests-fhir-r4/testEquality/testEquality19",
                "r4/tests-fhir-r4/testEquality/testEquality21",
                "r4/tests-fhir-r4/testEquality/testEquality22",
                "r4/tests-fhir-r4/testEquality/testEquality23",
                "r4/tests-fhir-r4/testEquivalent/testEquivalent11",
                "r4/tests-fhir-r4/testEquivalent/testEquivalent17",
                "r4/tests-fhir-r4/testEquivalent/testEquivalent21",
                "r4/tests-fhir-r4/testExclude/testExclude4",
                "r4/tests-fhir-r4/testExtension/testExtension1",
                "r4/tests-fhir-r4/testExtension/testExtension2",
                "r4/tests-fhir-r4/testExtension/testExtension3",
                "r4/tests-fhir-r4/testGreaterThan/testGreaterThan26",
                "r4/tests-fhir-r4/testGreaterThan/testGreaterThan27",
                "r4/tests-fhir-r4/testGreatorOrEqual/testGreatorOrEqual26",
                "r4/tests-fhir-r4/testGreatorOrEqual/testGreatorOrEqual27",
                "r4/tests-fhir-r4/testIif/testIif1",
                "r4/tests-fhir-r4/testIif/testIif2",
                "r4/tests-fhir-r4/testIif/testIif3",
                "r4/tests-fhir-r4/testIif/testIif4",
                "r4/tests-fhir-r4/testInvariants/extension('http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-scoring').exists() and extension('http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-scoring').value = 'ratio' implies group.population.where(code.coding.where(system = 'http://terminology.hl7.org/CodeSystem/measure-population').code = 'initial-population').count() in (1 | 2)",
                "r4/tests-fhir-r4/testLessOrEqual/testLessOrEqual26",
                "r4/tests-fhir-r4/testLessOrEqual/testLessOrEqual27",
                "r4/tests-fhir-r4/testLessThan/testLessThan26",
                "r4/tests-fhir-r4/testLessThan/testLessThan27",
                "r4/tests-fhir-r4/testLiterals/testDateGreaterThanDate",
                "r4/tests-fhir-r4/testLiterals/testDateNotEqual",
                "r4/tests-fhir-r4/testLiterals/testDateNotEqualTimeMinute",
                "r4/tests-fhir-r4/testLiterals/testDateNotEqualTimeSecond",
                "r4/tests-fhir-r4/testLiterals/testDateNotEqualToday",
                "r4/tests-fhir-r4/testLiterals/testDateTimeGreaterThanDate1",
                "r4/tests-fhir-r4/testLiterals/testDateTimeGreaterThanDate2",
                "r4/tests-fhir-r4/testLiterals/testLiteralDateTimeTZGreater",
                "r4/tests-fhir-r4/testLiterals/testLiteralDateTimeTZLess",
                "r4/tests-fhir-r4/testLiterals/testLiteralDecimalGreaterThanIntegerTrue",
                "r4/tests-fhir-r4/testLiterals/testLiteralDecimalLessThanInteger",
                "r4/tests-fhir-r4/testLiterals/testLiteralDecimalLessThanInvalid",
                "r4/tests-fhir-r4/testMatches/testMatchesSingleLineMode1",
                "r4/tests-fhir-r4/testMatches/testMatchesWithinUrl1",
                "r4/tests-fhir-r4/testNEquality/testNEquality13",
                "r4/tests-fhir-r4/testNEquality/testNEquality15",
                "r4/tests-fhir-r4/testNEquality/testNEquality16",
                "r4/tests-fhir-r4/testNEquality/testNEquality17",
                "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent13",
                "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent17",
                "r4/tests-fhir-r4/testNotEquivalent/testNotEquivalent21",
                "r4/tests-fhir-r4/testPower/testPower3",
                "r4/tests-fhir-r4/testPrecedence/testPrecedence3",
                "r4/tests-fhir-r4/testPrecedence/testPrecedence4",
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
                "r4/tests-fhir-r4/testSqrt/testSqrt2",
                "r4/tests-fhir-r4/testSubSetOf/testSubSetOf1",
                "r4/tests-fhir-r4/testSubSetOf/testSubSetOf2",
                "r4/tests-fhir-r4/testSuperSetOf/testSuperSetOf1",
                "r4/tests-fhir-r4/testSuperSetOf/testSuperSetOf2",
                "r4/tests-fhir-r4/testTimeOfDay/testTimeOfDay1",
                "r4/tests-fhir-r4/testToChars/testToChars1",
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
                "r4/tests-fhir-r4/testVariables/testVariables1",
                "r4/tests-fhir-r4/testVariables/testVariables2",
                "r4/tests-fhir-r4/testVariables/testVariables3",
                "r4/tests-fhir-r4/testVariables/testVariables4",
                "r4/tests-fhir-r4/testWhere/testWhere2",
                "r4/tests-fhir-r4/testWhere/testWhere3",
                "r4/tests-fhir-r4/testWhere/testWhere4",
            )

        fun getTestName(file: String, group: Group, test: Test): String {
            return (file.replace(".xml".toRegex(), "") +
                "/" +
                group.getName() +
                "/" +
                (if (test.getName() != null) test.getName() else test.getExpression().getValue()))
        }
    }
}
