package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;

// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.is;
// import static org.hamcrest.Matchers.nullValue;

class CqlTypesTest extends CqlTestBase {

    @Test
    void all_types() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var results = engine.evaluate(toElmIdentifier("CqlTypesTest"));
        var value = results.forExpression("AnyInteger").value();
        assertEquals(5, value, "AnyInteger");

        value = results.forExpression("AnyLong").value();
        assertEquals(value, Long.valueOf("12"), "AnyLong");

        value = results.forExpression("AnyDecimal").value();
        assertEquals(value, new BigDecimal("5.0"), "AnyDecimal");

        value = results.forExpression("AnyQuantity").value();
        assertTrue(
                ((Quantity) value)
                        .equal(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g")),
                "AnyQuantity");

        value = results.forExpression("AnyDateTime").value();
        assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 4, 4)), "AnyDateTime");

        value = results.forExpression("AnyTime").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(9, 0, 0, 0)), "AnyTime");

        value = results.forExpression("AnyInterval").value();
        assertEquals(((Interval) value), new Interval(2, true, 7, true), "AnyInterval");

        value = results.forExpression("AnyList").value();
        assertEquals(value, Arrays.asList(1, 2, 3), "AnyList");

        value = results.forExpression("AnyTuple").value();
        assertEquals(
                ((Tuple) value).getElements(),
                new HashMap<String, Object>() {
                    {
                        put("id", 5);
                        put("name", "Chris");
                    }
                },
                "AnyTuple");

        value = results.forExpression("BooleanTestTrue").value();
        assertEquals("Boolean", value.getClass().getSimpleName(), "BooleanTestTrue");
        assertTrue((Boolean) value, "BooleanTestTrue");

        value = results.forExpression("BooleanTestFalse").value();
        assertEquals("Boolean", value.getClass().getSimpleName(), "BooleanTestFalse");
        assertFalse((Boolean) value, "BooleanTestFalse");

        value = results.forExpression("CodeLiteral").value();
        assertTrue(
                ((Code) value)
                        .equal(new Code()
                                .withCode("8480-6")
                                .withSystem("http://loinc.org")
                                .withVersion("1.0")
                                .withDisplay("Systolic blood pressure")),
                "CodeLiteral");

        value = results.forExpression("CodeLiteral2").value();
        assertTrue(
                ((Code) value)
                        .equal(new Code()
                                .withCode("1234-5")
                                .withSystem("http://example.org")
                                .withVersion("1.05")
                                .withDisplay("Test Code")),
                "CodeLiteral2");

        value = results.forExpression("ConceptTest").value();
        assertTrue(
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
        assertNull(value, "DateTimeNull");

        value = results.forExpression("DateTimeProper").value();
        assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 7, 7, 6, 25, 33, 910)),
                "DateTimeProper");

        value = results.forExpression("DateTimeIncomplete").value();
        assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2015, 2, 10)),
                "DateTimeIncomplete");

        value = results.forExpression("DateTimeUncertain").value();
        assertEquals(19, ((Interval) value).getStart(), "DateTimeUncertain");
        assertEquals(49, ((Interval) value).getEnd(), "DateTimeUncertain");

        value = results.forExpression("DateTimeMin").value();
        assertTrue(EquivalentEvaluator.equivalent(
                value,
                new DateTime(OffsetDateTime.of(
                        1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().getOffset()))));

        value = results.forExpression("DateTimeMax").value();
        assertTrue(EquivalentEvaluator.equivalent(
                value, new DateTime(bigDecimalZoneOffset, 9999, 12, 31, 23, 59, 59, 999)));

        value = results.forExpression("DecimalUpperBoundExcept").value();
        assertEquals(value, new BigDecimal("10000000000000000000000000000000000.00000000"), "DecimalUpperBoundExcept");

        value = results.forExpression("DecimalLowerBoundExcept").value();
        assertEquals(value, new BigDecimal("-10000000000000000000000000000000000.00000000"), "DecimalLowerBoundExcept");

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results.forExpression("DecimalFractionalTooBig").value();
        assertEquals(value, new BigDecimal("5.999999999"), "DecimalFractionalTooBig");

        value = results.forExpression("DecimalPi").value();
        assertEquals(value, new BigDecimal("3.14159265"), "DecimalPi");

        value = results.forExpression("IntegerProper").value();
        assertEquals(5000, value, "IntegerProper");

        value = results.forExpression("QuantityTest").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));

        value = results.forExpression("QuantityTest2").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results.forExpression("QuantityFractionalTooBig").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("5.99999999")).withUnit("g")));

        value = results.forExpression("RatioTest").value();
        assertTrue(((Ratio) value)
                .getNumerator()
                .equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));
        assertTrue(((Ratio) value)
                .getDenominator()
                .equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        value = results.forExpression("StringTestEscapeQuotes").value();
        assertEquals("\'I start with a single quote and end with a double quote\"", value, "StringTestEscapeQuotes");

        value = results.forExpression("TimeProper").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(10, 25, 12, 863)), "TimeProper");

        value = results.forExpression("TimeAllMax").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(23, 59, 59, 999)), "TimeAllMax");

        value = results.forExpression("TimeAllMin").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(0, 0, 0, 0)), "TimeAllMin");
    }
}
