package org.opencds.cqf.cql.engine.execution;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlList;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlInternalTypeRepresentationSuiteTest {

    private static final Logger logger = LoggerFactory.getLogger(CqlInternalTypeRepresentationSuiteTest.class);

    // This test is to check the validity of the internal representation of the CQL types (OPTIONAL)
    @Test
    public void testInternalTypeRepresentationSuite() throws IOException, UcumException {
        Library library = translate("portable/CqlInternalTypeRepresentationSuite.cql");
        Context context = new Context(library, ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));
        Object result;

        result = context.resolveExpressionRef("BoolTrue").evaluate(context);
        Assert.assertTrue(result instanceof Boolean);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("BoolFalse").evaluate(context);
        Assert.assertTrue(result instanceof Boolean);
        Assert.assertTrue(!(Boolean) result);

        result = context.resolveExpressionRef("IntOne").evaluate(context);
        Assert.assertTrue(result instanceof Integer);
        Assert.assertTrue((Integer) result == 1);

        result = context.resolveExpressionRef("DecimalTenth").evaluate(context);
        Assert.assertTrue(result instanceof BigDecimal);
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("0.1")) == 0);

        result = context.resolveExpressionRef("StringTrue").evaluate(context);
        Assert.assertTrue(result instanceof String);
        Assert.assertTrue(result.equals("true"));

        result = context.resolveExpressionRef("DateTimeX").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456)));

        result = context.resolveExpressionRef("DateTimeFX").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456)));

        result = context.resolveExpressionRef("TimeX").evaluate(context);
        Assert.assertTrue(result instanceof Time);
        Assert.assertTrue(((Time) result).equal(new Time(12, 10, 59, 456)));

        result = context.resolveExpressionRef("DateTime_Year").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012)));

        result = context.resolveExpressionRef("DateTime_Month").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012, 2)));

        result = context.resolveExpressionRef("DateTime_Day").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012, 2, 15)));

        result = context.resolveExpressionRef("DateTime_Hour").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012, 2, 15, 12)));

        result = context.resolveExpressionRef("DateTime_Minute").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012, 2, 15, 12, 10)));

        result = context.resolveExpressionRef("DateTime_Second").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012, 2, 15, 12, 10, 59)));

        result = context.resolveExpressionRef("DateTime_Millisecond").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(null, 2012, 2, 15, 12, 10, 59, 456)));

        result = context.resolveExpressionRef("DateTime_TimezoneOffset").evaluate(context);
        Assert.assertTrue(result instanceof DateTime);
        Assert.assertTrue(((DateTime) result).equal(new DateTime(new BigDecimal("-8.0"), 2012, 2, 15, 12, 10, 59, 456)));

        result = context.resolveExpressionRef("Time_Hour").evaluate(context);
        Assert.assertTrue(result instanceof Time);
        Assert.assertTrue(((Time) result).equal(new Time(12)));

        result = context.resolveExpressionRef("Time_Minute").evaluate(context);
        Assert.assertTrue(result instanceof Time);
        Assert.assertTrue(((Time) result).equal(new Time(12, 10)));

        result = context.resolveExpressionRef("Time_Second").evaluate(context);
        Assert.assertTrue(result instanceof Time);
        Assert.assertTrue(((Time) result).equal(new Time(12, 10, 59)));

        result = context.resolveExpressionRef("Time_Millisecond").evaluate(context);
        Assert.assertTrue(result instanceof Time);
        Assert.assertTrue(((Time) result).equal(new Time(12, 10, 59, 456)));

        result = context.resolveExpressionRef("Clinical_quantity").evaluate(context);
        Assert.assertTrue(result instanceof Quantity);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal(12)).withUnit("a")));

        result = context.resolveExpressionRef("Clinical_QuantityA").evaluate(context);
        Assert.assertTrue(result instanceof Quantity);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal(12)).withUnit("a")));

        result = context.resolveExpressionRef("Clinical_CodeA").evaluate(context);
        Assert.assertTrue(result instanceof Code);
        Assert.assertTrue(((Code) result).equal(new Code().withCode("12345").withSystem("http://loinc.org").withVersion("1").withDisplay("Test Code")));

        result = context.resolveExpressionRef("Clinical_ConceptA").evaluate(context);
        Assert.assertTrue(result instanceof Concept);
        Assert.assertTrue(((Concept) result).equal(
                new Concept()
                        .withCode(
                                new Code().withCode("12345").withSystem("http://loinc.org").withVersion("1").withDisplay("Test Code")
                        ).withDisplay("Test Concept")
                )
        );

        LinkedHashMap<String, Object> elements = new LinkedHashMap<>();
        elements.put("a", 1);
        elements.put("b", 2);
        result = context.resolveExpressionRef("Structured_tuple").evaluate(context);
        Assert.assertTrue(result instanceof Tuple);
        Assert.assertTrue(((Tuple) result).equal(new Tuple(context).withElements(elements)));

        elements.clear();
        elements.put("class", "Portable CQL Test Suite");
        elements.put("versionNum", new BigDecimal("1.0"));
        elements.put("date", new DateTime(null, 2018, 7, 18));
        elements.put("developer", "Christopher Schuler");

        result = context.resolveExpressionRef("Structured_TupleA").evaluate(context);
        Assert.assertTrue(result instanceof Tuple);
        Assert.assertTrue(((Tuple) result).equal(new Tuple(context).withElements(elements)));

        result = context.resolveExpressionRef("Interval_Open").evaluate(context);
        Assert.assertTrue(result instanceof Interval);
        Assert.assertTrue(
                ((Interval) result).equal(
                        new Interval(
                                new DateTime(null, 2012, 1, 1), false,
                                new DateTime(null, 2013, 1, 1), false
                        )
                )
        );

        result = context.resolveExpressionRef("Interval_LeftOpen").evaluate(context);
        Assert.assertTrue(result instanceof Interval);
        Assert.assertTrue(
                ((Interval) result).equal(
                        new Interval(
                                new DateTime(null, 2012, 1, 1), false,
                                new DateTime(null, 2013, 1, 1), true
                        )
                )
        );

        result = context.resolveExpressionRef("Interval_RightOpen").evaluate(context);
        Assert.assertTrue(result instanceof Interval);
        Assert.assertTrue(
                ((Interval) result).equal(
                        new Interval(
                                new DateTime(null, 2012, 1, 1), true,
                                new DateTime(null, 2013, 1, 1), false
                        )
                )
        );

        result = context.resolveExpressionRef("Interval_Closed").evaluate(context);
        Assert.assertTrue(result instanceof Interval);
        Assert.assertTrue(
                ((Interval) result).equal(
                        new Interval(
                                new DateTime(null, 2012, 1, 1), true,
                                new DateTime(null, 2013, 1, 1), true
                        )
                )
        );

        result = context.resolveExpressionRef("List_BoolList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        Boolean listComp = CqlList.equal((Iterable<?>) result, Arrays.asList(true, false, true), context);
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_IntList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) result, Arrays.asList(9, 7, 8), context);
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_DecimalList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) result, Arrays.asList(new BigDecimal("1.0"), new BigDecimal("2.1"), new BigDecimal("3.2")), context);
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_StringList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) result, Arrays.asList("a", "bee", "see"), context);
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_DateTimeList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456),
                        new DateTime(new BigDecimal("0.0"), 2012, 3, 15, 12, 10, 59, 456),
                        new DateTime(new BigDecimal("0.0"), 2012, 4, 15, 12, 10, 59, 456)
                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_TimeList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new Time(12, 10, 59, 456),
                        new Time(13, 10, 59, 456),
                        new Time(14, 10, 59, 456)
                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_QuantityList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new Quantity().withValue(new BigDecimal("1.0")).withUnit("m"),
                        new Quantity().withValue(new BigDecimal("2.1")).withUnit("m"),
                        new Quantity().withValue(new BigDecimal("3.2")).withUnit("m")
                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_CodeList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new Code().withCode("12345").withSystem("http://loinc.org").withVersion("1").withDisplay("Test Code"),
                        new Code().withCode("123456").withSystem("http://loinc.org").withVersion("1").withDisplay("Another Test Code")
                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_ConceptList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new Concept().withCode(
                                new Code().withCode("12345").withSystem("http://loinc.org").withVersion("1").withDisplay("Test Code")
                        ).withDisplay("Test Concept"),
                        new Concept().withCode(
                                new Code().withCode("123456").withSystem("http://loinc.org").withVersion("1").withDisplay("Another Test Code")
                        ).withDisplay("Another Test Concept")

                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        elements.clear();
        elements.put("a", 1);
        elements.put("b", "2");
        LinkedHashMap<String, Object> elements2 = new LinkedHashMap<>();
        elements2.put("x", 2);
        elements2.put("z", "3");
        result = context.resolveExpressionRef("List_TupleList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new Tuple(context).withElements(elements), new Tuple(context).withElements(elements2)

                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_ListList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(Arrays.asList(1,2,3), Arrays.asList("a", "b", "c")), context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_IntervalList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) result,
                Arrays.asList(
                        new Interval(1, true, 5, true), new Interval(5, false, 9, false), new Interval(8, true, 10, false)
                ),
                context
        );
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_MixedList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) result, Arrays.asList(1, "two", 3), context);
        Assert.assertTrue(listComp != null && listComp);

        result = context.resolveExpressionRef("List_EmptyList").evaluate(context);
        Assert.assertTrue(result instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) result, Collections.EMPTY_LIST, context);
        Assert.assertTrue(listComp != null && listComp);
    }

    private Library translate(String file) throws UcumException, IOException {
        return new TranslatorHelper().translate(file, LibraryBuilder.SignatureLevel.All);
    }
}
