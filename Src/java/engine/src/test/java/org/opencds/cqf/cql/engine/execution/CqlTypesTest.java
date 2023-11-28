package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.In;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.nullValue;

public class CqlTypesTest extends CqlTestBase {

    @Test
    public void test_all_types() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();
        final SoftAssert softAssert = new SoftAssert();

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlTypesTest"));
        Object result;

        result = evaluationResult.forExpression("AnyInteger").value();
        softAssert.assertEquals(result, 5, "AnyInteger");

        result = evaluationResult.forExpression("AnyLong").value();
        softAssert.assertEquals(result, Long.valueOf("12"), "AnyLong");

        result = evaluationResult.forExpression("AnyDecimal").value();
        softAssert.assertEquals(result, new BigDecimal("5.0"), "AnyDecimal");

        result = evaluationResult.forExpression("AnyQuantity").value();
        softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g")), "AnyQuantity");

        result = evaluationResult.forExpression("AnyDateTime").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 4, 4)), "AnyDateTime");

        result = evaluationResult.forExpression("AnyTime").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(9, 0, 0, 0)), "AnyTime");

        result = evaluationResult.forExpression("AnyInterval").value();
        softAssert.assertEquals(((Interval) result), new Interval(2, true, 7, true), "AnyInterval");

        result = evaluationResult.forExpression("AnyList").value();
        softAssert.assertEquals(result, Arrays.asList(1, 2, 3), "AnyList");

        result = evaluationResult.forExpression("AnyTuple").value();
        softAssert.assertEquals(((Tuple)result).getElements(), new HashMap<String, Object>() {{put("id", 5); put("name", "Chris");}}, "AnyTuple");

        result = evaluationResult.forExpression("BooleanTestTrue").value();
        softAssert.assertEquals(result.getClass().getSimpleName(), "Boolean", "BooleanTestTrue");
        softAssert.assertEquals(result, true, "BooleanTestTrue");

        result = evaluationResult.forExpression("BooleanTestFalse").value();
        softAssert.assertEquals(result.getClass().getSimpleName(), "Boolean", "BooleanTestFalse");
        softAssert.assertEquals(result, false, "BooleanTestFalse");

        result = evaluationResult.forExpression("CodeLiteral").value();
        softAssert.assertTrue(((Code) result).equal(new Code().withCode("8480-6").withSystem("http://loinc.org").withVersion("1.0").withDisplay("Systolic blood pressure")), "CodeLiteral");

        result = evaluationResult.forExpression("CodeLiteral2").value();
        softAssert.assertTrue(((Code) result).equal(new Code().withCode("1234-5").withSystem("http://example.org").withVersion("1.05").withDisplay("Test Code")), "CodeLiteral2");

        result = evaluationResult.forExpression("ConceptTest").value();
        softAssert.assertTrue(((Concept) result).equal(new Concept().withCodes(Arrays.asList(new Code().withCode("8480-6").withSystem("http://loinc.org").withVersion("1.0").withDisplay("Systolic blood pressure"), new Code().withCode("1234-5").withSystem("http://example.org").withVersion("1.05").withDisplay("Test Code"))).withDisplay("Type B viral hepatitis")), "ConceptTest");

        result = evaluationResult.forExpression("DateTimeNull").value();
        softAssert.assertNull(result, "DateTimeNull");

        result = evaluationResult.forExpression("DateTimeProper").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 7, 7, 6, 25, 33, 910)), "DateTimeProper");

        result = evaluationResult.forExpression("DateTimeIncomplete").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2015, 2, 10)), "DateTimeIncomplete");

        result = evaluationResult.forExpression("DateTimeUncertain").value();
        softAssert.assertEquals(((Interval) result).getStart(), 19, "DateTimeUncertain");
        softAssert.assertEquals(((Interval) result).getEnd(), 49, "DateTimeUncertain");

        result = evaluationResult.forExpression("DateTimeMin").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().getOffset()))));

        result = evaluationResult.forExpression("DateTimeMax").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 9999, 12, 31, 23, 59, 59, 999)));

        result = evaluationResult.forExpression("DecimalUpperBoundExcept").value();
        softAssert.assertEquals(result, new BigDecimal("10000000000000000000000000000000000.00000000"), "DecimalUpperBoundExcept");

        result = evaluationResult.forExpression("DecimalLowerBoundExcept").value();
        softAssert.assertEquals(result, new BigDecimal("-10000000000000000000000000000000000.00000000"), "DecimalLowerBoundExcept");

        // NOTE: This should also return an error as the fractional precision is greater than 8
        result = evaluationResult.forExpression("DecimalFractionalTooBig").value();
        softAssert.assertEquals(result, new BigDecimal("5.999999999"), "DecimalFractionalTooBig");

        result = evaluationResult.forExpression("DecimalPi").value();
        softAssert.assertEquals(result, new BigDecimal("3.14159265"), "DecimalPi");

        result = evaluationResult.forExpression("IntegerProper").value();
        softAssert.assertEquals(result, 5000, "IntegerProper");

        result = evaluationResult.forExpression("QuantityTest").value();
        softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));

        result = evaluationResult.forExpression("QuantityTest2").value();
        softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        result = evaluationResult.forExpression("QuantityFractionalTooBig").value();
        softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.99999999")).withUnit("g")));

        result = evaluationResult.forExpression("RatioTest").value();
        softAssert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));
        softAssert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        result = evaluationResult.forExpression("StringTestEscapeQuotes").value();
        softAssert.assertEquals(result, "\'I start with a single quote and end with a double quote\"", "StringTestEscapeQuotes");


        result = evaluationResult.forExpression("TimeProper").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 25, 12, 863)), "TimeProper");

        result = evaluationResult.forExpression("TimeAllMax").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)), "TimeAllMax");

        result = evaluationResult.forExpression("TimeAllMin").value();
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)), "TimeAllMin");

        softAssert.assertAll();
    }
}
