package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple

internal class CqlInternalTypeRepresentationSuiteTest : CqlTestBase() {
    @ParameterizedTest
    @MethodSource("timeZones")
    fun all_internal_type_representation(zoneId: ZoneId, now: LocalDateTime) {
        val results =
            engine.evaluate(
                toElmIdentifier("CqlInternalTypeRepresentationSuite"),
                ZonedDateTime.of(now, zoneId),
            )

        val bigDecimalZoneOffset = bigDecimalZoneOffset

        var value = results.forExpression("BoolTrue").value()
        Assertions.assertTrue(value is Boolean)
        Assertions.assertTrue(value as Boolean)

        value = results.forExpression("BoolFalse").value()
        Assertions.assertTrue(value is Boolean)
        Assertions.assertFalse(value as Boolean)

        value = results.forExpression("IntOne").value()
        Assertions.assertTrue(value is Int)
        Assertions.assertTrue(value as Int == 1)

        value = results.forExpression("DecimalTenth").value()
        Assertions.assertTrue(value is BigDecimal)
        Assertions.assertEquals(0, (value as BigDecimal).compareTo(BigDecimal("0.1")))

        value = results.forExpression("StringTrue").value()
        Assertions.assertTrue(value is String)
        Assertions.assertEquals("true", value)

        value = results.forExpression("DateTimeX").value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456))
        )

        value = results.forExpression("DateTimeFX").value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456))
        )

        value = results.forExpression("TimeX").value()
        Assertions.assertTrue(value is Time)
        Assertions.assertTrue((value as Time).equal(Time(12, 10, 59, 456)))

        value = results.expressionResults["DateTime_Year"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue((value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012)))

        value = results.expressionResults["DateTime_Month"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue((value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012, 2)))

        value = results.expressionResults["DateTime_Day"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012, 2, 15))
        )

        value = results.expressionResults["DateTime_Hour"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12))
        )

        value = results.expressionResults["DateTime_Minute"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10))
        )

        value = results.expressionResults["DateTime_Second"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59))
        )

        value = results.expressionResults["DateTime_Millisecond"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59, 456))
        )

        value = results.expressionResults["DateTime_TimezoneOffset"]!!.value()
        Assertions.assertTrue(value is DateTime)
        Assertions.assertTrue(
            (value as DateTime).equal(DateTime(BigDecimal("-8.0"), 2012, 2, 15, 12, 10, 59, 456))
        )

        value = results.expressionResults["Time_Hour"]!!.value()
        Assertions.assertTrue(value is Time)
        Assertions.assertTrue((value as Time).equal(Time(12)))

        value = results.expressionResults["Time_Minute"]!!.value()
        Assertions.assertTrue(value is Time)
        Assertions.assertTrue((value as Time).equal(Time(12, 10)))

        value = results.expressionResults["Time_Second"]!!.value()
        Assertions.assertTrue(value is Time)
        Assertions.assertTrue((value as Time).equal(Time(12, 10, 59)))

        value = results.expressionResults["Time_Millisecond"]!!.value()
        Assertions.assertTrue(value is Time)
        Assertions.assertTrue((value as Time).equal(Time(12, 10, 59, 456)))

        value = results.expressionResults["Clinical_quantity"]!!.value()
        Assertions.assertTrue(value is Quantity)
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal(12)).withUnit("a"))
        )

        value = results.expressionResults["Clinical_QuantityA"]!!.value()
        Assertions.assertTrue(value is Quantity)
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal(12)).withUnit("a"))
        )

        value = results.expressionResults["Clinical_CodeA"]!!.value()
        Assertions.assertTrue(value is Code)
        Assertions.assertTrue(
            (value as Code).equal(
                Code()
                    .withCode("12345")
                    .withSystem("http://loinc.org")
                    .withVersion("1")
                    .withDisplay("Test Code")
            )
        )

        value = results.expressionResults["Clinical_ConceptA"]!!.value()
        Assertions.assertTrue(value is Concept)
        Assertions.assertTrue(
            (value as Concept).equal(
                Concept()
                    .withCode(
                        Code()
                            .withCode("12345")
                            .withSystem("http://loinc.org")
                            .withVersion("1")
                            .withDisplay("Test Code")
                    )
                    .withDisplay("Test Concept")
            )
        )

        val elements = LinkedHashMap<String?, Any?>()
        elements["a"] = 1
        elements["b"] = 2
        value = results.expressionResults["Structured_tuple"]!!.value()
        Assertions.assertTrue(value is Tuple)
        Assertions.assertTrue((value as Tuple).equal(Tuple(engine.state).withElements(elements)))

        elements.clear()
        elements["class"] = "Portable CQL Test Suite"
        elements["versionNum"] = BigDecimal("1.0")
        elements["date"] = DateTime(bigDecimalZoneOffset, 2018, 7, 18)
        elements["developer"] = "Christopher Schuler"

        value = results.expressionResults["Structured_TupleA"]!!.value()
        Assertions.assertTrue(value is Tuple)
        Assertions.assertTrue((value as Tuple).equal(Tuple(engine.state).withElements(elements)))

        value = results.expressionResults["Interval_Open"]!!.value()
        Assertions.assertTrue(value is Interval)
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                    false,
                    DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                    false,
                )
            )
        )

        value = results.expressionResults["Interval_LeftOpen"]!!.value()
        Assertions.assertTrue(value is Interval)
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                    false,
                    DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                    true,
                )
            )
        )

        value = results.expressionResults["Interval_RightOpen"]!!.value()
        Assertions.assertTrue(value is Interval)
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                    true,
                    DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                    false,
                )
            )
        )

        value = results.expressionResults["Interval_Closed"]!!.value()
        Assertions.assertTrue(value is Interval)
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                    true,
                    DateTime(bigDecimalZoneOffset, 2013, 1, 1),
                    true,
                )
            )
        )

        value = results.expressionResults["List_BoolList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        var listComp =
            CqlList.equal(
                value as Iterable<*>,
                mutableListOf<Boolean?>(true, false, true),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_IntList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp = CqlList.equal(value as Iterable<*>, mutableListOf<Int?>(9, 7, 8), engine.state)
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_DecimalList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(BigDecimal("1.0"), BigDecimal("2.1"), BigDecimal("3.2")),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_StringList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                mutableListOf<String?>("a", "bee", "see"),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_DateTimeList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(
                    DateTime(BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456),
                    DateTime(BigDecimal("0.0"), 2012, 3, 15, 12, 10, 59, 456),
                    DateTime(BigDecimal("0.0"), 2012, 4, 15, 12, 10, 59, 456),
                ),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_TimeList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(Time(12, 10, 59, 456), Time(13, 10, 59, 456), Time(14, 10, 59, 456)),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_QuantityList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("m"),
                    Quantity().withValue(BigDecimal("2.1")).withUnit("m"),
                    Quantity().withValue(BigDecimal("3.2")).withUnit("m"),
                ),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_CodeList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
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
                ),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_ConceptList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
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
                ),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        elements.clear()
        elements["a"] = 1
        elements["b"] = "2"
        val elements2 = LinkedHashMap<String?, Any?>()
        elements2["x"] = 2
        elements2["z"] = "3"
        value = results.expressionResults["List_TupleList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(
                    Tuple(engine.state).withElements(elements),
                    Tuple(engine.state).withElements(elements2),
                ),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_ListList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(mutableListOf<Int?>(1, 2, 3), mutableListOf<String?>("a", "b", "c")),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_IntervalList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp =
            CqlList.equal(
                value as Iterable<*>,
                listOf(
                    Interval(1, true, 5, true),
                    Interval(5, false, 9, false),
                    Interval(8, true, 10, false),
                ),
                engine.state,
            )
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_MixedList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp = CqlList.equal(value as Iterable<*>, mutableListOf(1, "two", 3), engine.state)
        Assertions.assertTrue(listComp != null && listComp)

        value = results.expressionResults["List_EmptyList"]!!.value()
        Assertions.assertTrue(value is Iterable<*>)
        listComp = CqlList.equal(value as Iterable<*>, Collections.EMPTY_LIST, engine.state)
        Assertions.assertNull(listComp)
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
