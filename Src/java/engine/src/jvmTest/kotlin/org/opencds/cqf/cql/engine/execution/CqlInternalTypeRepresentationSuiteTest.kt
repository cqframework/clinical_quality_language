package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlInternalTypeRepresentationSuiteTest : CqlTestBase() {
    @ParameterizedTest
    @MethodSource("timeZones")
    fun all_internal_type_representation(zoneId: ZoneId, now: LocalDateTime) {
        val results =
            engine
                .evaluate {
                    library("CqlInternalTypeRepresentationSuite")
                    evaluationDateTime = ZonedDateTime.of(now, zoneId)
                }
                .onlyResultOrThrow

        val bigDecimalZoneOffset = bigDecimalZoneOffset

        var value = results["BoolTrue"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        value = results["BoolFalse"]!!.value
        assertIs<Boolean>(value)
        assertFalse(value.value)

        value = results["IntOne"]!!.value
        assertIs<Integer>(value)
        assertEquals(1, value.value)

        value = results["DecimalTenth"]!!.value
        assertIs<Decimal>(value)
        assertEquals(0, (value.value).compareTo(BigDecimal("0.1")))

        value = results["StringTrue"]!!.value
        assertIs<String>(value)
        assertEquals("true", value.value)

        value = results["DateTimeX"]!!.value
        assertIs<DateTime>(value)
        assertTrue(
            equal(value, DateTime(BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456))?.value == true
        )

        value = results["DateTimeFX"]!!.value
        assertIs<DateTime>(value)
        assertTrue(
            equal(value, DateTime(BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456))?.value == true
        )

        value = results["TimeX"]!!.value
        assertIs<Time>(value)
        assertTrue(equal(value, Time(12, 10, 59, 456))?.value == true)

        value = results["DateTime_Year"]!!.value
        assertIs<DateTime>(value)
        assertTrue(equal(value, DateTime(bigDecimalZoneOffset, 2012))?.value == true)

        value = results["DateTime_Month"]!!.value
        assertIs<DateTime>(value)
        assertTrue(equal(value, DateTime(bigDecimalZoneOffset, 2012, 2))?.value == true)

        value = results["DateTime_Day"]!!.value
        assertIs<DateTime>(value)
        assertTrue(equal(value, DateTime(bigDecimalZoneOffset, 2012, 2, 15))?.value == true)

        value = results["DateTime_Hour"]!!.value
        assertIs<DateTime>(value)
        assertTrue(equal(value, DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12))?.value == true)

        value = results["DateTime_Minute"]!!.value
        assertIs<DateTime>(value)
        assertTrue(equal(value, DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10))?.value == true)

        value = results["DateTime_Second"]!!.value
        assertIs<DateTime>(value)
        assertTrue(
            equal(value, DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59))?.value == true
        )

        value = results["DateTime_Millisecond"]!!.value
        assertIs<DateTime>(value)
        assertTrue(
            equal(value, DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59, 456))?.value ==
                true
        )

        value = results["DateTime_TimezoneOffset"]!!.value
        assertIs<DateTime>(value)
        assertTrue(
            equal(value, DateTime(BigDecimal("-8.0"), 2012, 2, 15, 12, 10, 59, 456))?.value == true
        )

        value = results["Time_Hour"]!!.value
        assertIs<Time>(value)
        assertTrue(equal(value, Time(12))?.value == true)

        value = results["Time_Minute"]!!.value
        assertIs<Time>(value)
        assertTrue(equal(value, Time(12, 10))?.value == true)

        value = results["Time_Second"]!!.value
        assertIs<Time>(value)
        assertTrue(equal(value, Time(12, 10, 59))?.value == true)

        value = results["Time_Millisecond"]!!.value
        assertIs<Time>(value)
        assertTrue(equal(value, Time(12, 10, 59, 456))?.value == true)

        value = results["Clinical_quantity"]!!.value
        assertIs<Quantity>(value)
        assertTrue(equal(value, Quantity().withValue(BigDecimal(12)).withUnit("a"))?.value == true)

        value = results["Clinical_QuantityA"]!!.value
        assertIs<Quantity>(value)
        assertTrue(equal(value, Quantity().withValue(BigDecimal(12)).withUnit("a"))?.value == true)

        value = results["Clinical_CodeA"]!!.value
        assertIs<Code>(value)
        assertTrue(
            equal(
                    value,
                    Code()
                        .withCode("12345")
                        .withSystem("http://loinc.org")
                        .withVersion("1")
                        .withDisplay("Test Code"),
                )
                ?.value == true
        )

        value = results["Clinical_ConceptA"]!!.value
        assertIs<Concept>(value)
        assertTrue(
            equal(
                    value,
                    Concept()
                        .withCode(
                            Code()
                                .withCode("12345")
                                .withSystem("http://loinc.org")
                                .withVersion("1")
                                .withDisplay("Test Code")
                        )
                        .withDisplay("Test Concept"),
                )
                ?.value == true
        )

        val elements = LinkedHashMap<kotlin.String, Value?>()
        elements["a"] = 1.toCqlInteger()
        elements["b"] = 2.toCqlInteger()
        value = results["Structured_tuple"]!!.value
        assertIs<Tuple>(value)
        assertTrue(equal(value, Tuple().withElements(elements))?.value == true)

        elements.clear()
        elements["class"] = "Portable CQL Test Suite".toCqlString()
        elements["versionNum"] = BigDecimal("1.0").toCqlDecimal()
        elements["date"] = DateTime(bigDecimalZoneOffset, 2018, 7, 18)
        elements["developer"] = "Christopher Schuler".toCqlString()

        value = results["Structured_TupleA"]!!.value
        assertIs<Tuple>(value)
        assertTrue(equal(value, Tuple().withElements(elements))?.value == true)

        value = results["Interval_Open"]!!.value
        assertIs<Interval>(value)
        assertTrue(
            equal(
                    value,
                    Interval(
                        DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                        false,
                        DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                        false,
                    ),
                )
                ?.value == true
        )

        value = results["Interval_LeftOpen"]!!.value
        assertIs<Interval>(value)
        assertTrue(
            equal(
                    value,
                    Interval(
                        DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                        false,
                        DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["Interval_RightOpen"]!!.value
        assertIs<Interval>(value)
        assertTrue(
            equal(
                    value,
                    Interval(
                        DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                        true,
                        DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                        false,
                    ),
                )
                ?.value == true
        )

        value = results["Interval_Closed"]!!.value
        assertIs<Interval>(value)
        assertTrue(
            equal(
                    value,
                    Interval(
                        DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                        true,
                        DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["List_BoolList"]!!.value
        assertIs<List>(value)
        var listComp =
            equal(
                value,
                listOf(
                        org.opencds.cqf.cql.engine.runtime.Boolean.TRUE,
                        org.opencds.cqf.cql.engine.runtime.Boolean.FALSE,
                        org.opencds.cqf.cql.engine.runtime.Boolean.TRUE,
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_IntList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(9.toCqlInteger(), 7.toCqlInteger(), 8.toCqlInteger()).toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_DecimalList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        BigDecimal("1.0").toCqlDecimal(),
                        BigDecimal("2.1").toCqlDecimal(),
                        BigDecimal("3.2").toCqlDecimal(),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_StringList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf("a".toCqlString(), "bee".toCqlString(), "see".toCqlString()).toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_DateTimeList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        DateTime(BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456),
                        DateTime(BigDecimal("0.0"), 2012, 3, 15, 12, 10, 59, 456),
                        DateTime(BigDecimal("0.0"), 2012, 4, 15, 12, 10, 59, 456),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_TimeList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(Time(12, 10, 59, 456), Time(13, 10, 59, 456), Time(14, 10, 59, 456))
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_QuantityList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        Quantity().withValue(BigDecimal("1.0")).withUnit("m"),
                        Quantity().withValue(BigDecimal("2.1")).withUnit("m"),
                        Quantity().withValue(BigDecimal("3.2")).withUnit("m"),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_CodeList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        Code()
                            .withCode("12345")
                            .withSystem("http://loinc.org")
                            .withVersion("1")
                            .withDisplay("Test Code"),
                        Code()
                            .withCode("123456")
                            .withSystem("http://loinc.org")
                            .withVersion("1")
                            .withDisplay("Another Test Code"),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_ConceptList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        Concept()
                            .withCode(
                                Code()
                                    .withCode("12345")
                                    .withSystem("http://loinc.org")
                                    .withVersion("1")
                                    .withDisplay("Test Code")
                            )
                            .withDisplay("Test Concept"),
                        Concept()
                            .withCode(
                                Code()
                                    .withCode("123456")
                                    .withSystem("http://loinc.org")
                                    .withVersion("1")
                                    .withDisplay("Another Test Code")
                            )
                            .withDisplay("Another Test Concept"),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        elements.clear()
        elements["a"] = 1.toCqlInteger()
        elements["b"] = "2".toCqlString()
        val elements2 = mutableMapOf<kotlin.String, Value?>()
        elements2["x"] = 2.toCqlInteger()
        elements2["z"] = "3".toCqlString()
        value = results["List_TupleList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(Tuple().withElements(elements), Tuple().withElements(elements2)).toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_ListList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        mutableListOf(1.toCqlInteger(), 2.toCqlInteger(), 3.toCqlInteger())
                            .toCqlList(),
                        mutableListOf("a".toCqlString(), "b".toCqlString(), "c".toCqlString())
                            .toCqlList(),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_IntervalList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(
                        Interval(1.toCqlInteger(), true, 5.toCqlInteger(), true),
                        Interval(5.toCqlInteger(), false, 9.toCqlInteger(), false),
                        Interval(8.toCqlInteger(), true, 10.toCqlInteger(), false),
                    )
                    .toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_MixedList"]!!.value
        assertIs<List>(value)
        listComp =
            equal(
                value,
                listOf(1.toCqlInteger(), "two".toCqlString(), 3.toCqlInteger()).toCqlList(),
                engine.state,
            )
        assertTrue(listComp != null && listComp.value)

        value = results["List_EmptyList"]!!.value
        assertIs<List>(value)
        listComp = equal(value, List.EMPTY_LIST, engine.state)
        assertNull(listComp)
    }

    companion object {

        private val UTC: ZoneId = ZoneId.of("UTC")
        private val MONTREAL: ZoneId = ZoneId.of("America/Montreal")
        private val REGINA: ZoneId =
            ZoneId.of(
                "America/Regina"
            ) // Saskatchewan does not have standard time (non-DST) all year round
        private val DST_2023_11_01: LocalDateTime =
            LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0)
        private val NON_DST_2023_11_13: LocalDateTime =
            LocalDateTime.of(2023, Month.NOVEMBER, 13, 0, 0, 0)
        private val NON_DST_2018_01_01: LocalDateTime =
            LocalDateTime.of(2018, Month.JANUARY, 1, 7, 0, 0)

        @JvmStatic
        private fun timeZones(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(UTC, DST_2023_11_01),
                arrayOf(MONTREAL, DST_2023_11_01),
                arrayOf(REGINA, DST_2023_11_01),
                arrayOf(UTC, NON_DST_2023_11_13),
                arrayOf(MONTREAL, NON_DST_2023_11_13),
                arrayOf(REGINA, NON_DST_2023_11_13),
                arrayOf(UTC, NON_DST_2018_01_01),
                arrayOf(MONTREAL, NON_DST_2018_01_01),
                arrayOf(REGINA, NON_DST_2018_01_01),
            )
        }
    }
}
