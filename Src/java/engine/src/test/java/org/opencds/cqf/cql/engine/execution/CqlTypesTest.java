package org.opencds.cqf.cql.engine.execution;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.is;
// import static org.hamcrest.Matchers.nullValue;

public class CqlTypesTest extends CqlTestBase {

    @Test
    public void test_all_types() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();
        final SoftAssert softAssert = new SoftAssert();

        var results = engine.evaluate(toElmIdentifier("CqlTypesTest"));
        var value = results.forExpression("AnyInteger").value();
        softAssert.assertEquals(value, 5, "AnyInteger");

        value = results.forExpression("AnyLong").value();
        softAssert.assertEquals(value, Long.valueOf("12"), "AnyLong");

        value = results.forExpression("AnyDecimal").value();
        softAssert.assertEquals(value, new BigDecimal("5.0"), "AnyDecimal");

        value = results.forExpression("AnyQuantity").value();
        softAssert.assertTrue(
                ((Quantity) value)
                        .equal(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g")),
                "AnyQuantity");

        value = results.forExpression("AnyDateTime").value();
        softAssert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 4, 4)), "AnyDateTime");

        value = results.forExpression("AnyTime").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(9, 0, 0, 0)), "AnyTime");

        value = results.forExpression("AnyInterval").value();
        softAssert.assertEquals(((Interval) value), new Interval(2, true, 7, true), "AnyInterval");

        value = results.forExpression("AnyList").value();
        softAssert.assertEquals(value, Arrays.asList(1, 2, 3), "AnyList");

        value = results.forExpression("AnyTuple").value();
        softAssert.assertEquals(
                ((Tuple) value).getElements(),
                new HashMap<String, Object>() {
                    {
                        put("id", 5);
                        put("name", "Chris");
                    }
                },
                "AnyTuple");

        value = results.forExpression("BooleanTestTrue").value();
        softAssert.assertEquals(value.getClass().getSimpleName(), "Boolean", "BooleanTestTrue");
        softAssert.assertEquals(value, true, "BooleanTestTrue");

        value = results.forExpression("BooleanTestFalse").value();
        softAssert.assertEquals(value.getClass().getSimpleName(), "Boolean", "BooleanTestFalse");
        softAssert.assertEquals(value, false, "BooleanTestFalse");

        value = results.forExpression("CodeLiteral").value();
        softAssert.assertTrue(
                ((Code) value)
                        .equal(new Code()
                                .withCode("8480-6")
                                .withSystem("http://loinc.org")
                                .withVersion("1.0")
                                .withDisplay("Systolic blood pressure")),
                "CodeLiteral");

        value = results.forExpression("CodeLiteral2").value();
        softAssert.assertTrue(
                ((Code) value)
                        .equal(new Code()
                                .withCode("1234-5")
                                .withSystem("http://example.org")
                                .withVersion("1.05")
                                .withDisplay("Test Code")),
                "CodeLiteral2");

        value = results.forExpression("ConceptTest").value();
        softAssert.assertTrue(
                ((Concept) value)
                        .equal(new Concept()
                                .withCodes(Arrays.asList(
                                        new Code()
                                                .withCode("8480-6")
                                                .withSystem("http://loinc.org")
                                                .withVersion("1.0")
                                                .withDisplay("Systolic blood pressure"),
                                        new Code()
                                                .withCode("1234-5")
                                                .withSystem("http://example.org")
                                                .withVersion("1.05")
                                                .withDisplay("Test Code")))
                                .withDisplay("Type B viral hepatitis")),
                "ConceptTest");

        value = results.forExpression("DateTimeNull").value();
        softAssert.assertNull(value, "DateTimeNull");

        value = results.forExpression("DateTimeProper").value();
        softAssert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 7, 7, 6, 25, 33, 910)),
                "DateTimeProper");

        value = results.forExpression("DateTimeIncomplete").value();
        softAssert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2015, 2, 10)),
                "DateTimeIncomplete");

        value = results.forExpression("DateTimeUncertain").value();
        softAssert.assertEquals(((Interval) value).getStart(), 19, "DateTimeUncertain");
        softAssert.assertEquals(((Interval) value).getEnd(), 49, "DateTimeUncertain");

        value = results.forExpression("DateTimeMin").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(
                value,
                new DateTime(OffsetDateTime.of(
                        1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().getOffset()))));

        value = results.forExpression("DateTimeMax").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(
                value, new DateTime(bigDecimalZoneOffset, 9999, 12, 31, 23, 59, 59, 999)));

        value = results.forExpression("DecimalUpperBoundExcept").value();
        softAssert.assertEquals(
                value, new BigDecimal("10000000000000000000000000000000000.00000000"), "DecimalUpperBoundExcept");

        value = results.forExpression("DecimalLowerBoundExcept").value();
        softAssert.assertEquals(
                value, new BigDecimal("-10000000000000000000000000000000000.00000000"), "DecimalLowerBoundExcept");

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results.forExpression("DecimalFractionalTooBig").value();
        softAssert.assertEquals(value, new BigDecimal("5.999999999"), "DecimalFractionalTooBig");

        value = results.forExpression("DecimalPi").value();
        softAssert.assertEquals(value, new BigDecimal("3.14159265"), "DecimalPi");

        value = results.forExpression("IntegerProper").value();
        softAssert.assertEquals(value, 5000, "IntegerProper");

        value = results.forExpression("QuantityTest").value();
        softAssert.assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));

        value = results.forExpression("QuantityTest2").value();
        softAssert.assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results.forExpression("QuantityFractionalTooBig").value();
        softAssert.assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("5.99999999")).withUnit("g")));

        value = results.forExpression("RatioTest").value();
        softAssert.assertTrue(((Ratio) value)
                .getNumerator()
                .equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));
        softAssert.assertTrue(((Ratio) value)
                .getDenominator()
                .equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        value = results.forExpression("StringTestEscapeQuotes").value();
        softAssert.assertEquals(
                value, "\'I start with a single quote and end with a double quote\"", "StringTestEscapeQuotes");

        value = results.forExpression("TimeProper").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(10, 25, 12, 863)), "TimeProper");

        value = results.forExpression("TimeAllMax").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(23, 59, 59, 999)), "TimeAllMax");

        value = results.forExpression("TimeAllMin").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(0, 0, 0, 0)), "TimeAllMin");

        softAssert.assertAll();
    }
}
