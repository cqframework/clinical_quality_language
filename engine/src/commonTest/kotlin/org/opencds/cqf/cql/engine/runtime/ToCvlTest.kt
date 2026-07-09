package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName

class ToCvlTest {
    @Test
    fun simpleValueToCvlTest() {
        assertEquals("true", Boolean.TRUE.toString())
        assertEquals("123", 123.toCqlInteger().toString())
        assertEquals("123L", 123L.toCqlLong().toString())
        assertEquals("1.0", BigDecimal("1.0").toCqlDecimal().toString())
    }

    @Test
    fun stringToCvlTest() {
        assertEquals("'abc'", "abc".toCqlString().toString())
        assertEquals("'abc\\'def'", "abc'def".toCqlString().toString())
    }

    @Test
    fun dateTimeToCvlTest() {
        assertEquals("@2012-06-01", Date(2012, 6, 1).toString())
        assertEquals(
            "@2012-06-01T12:34:56.789+00:00",
            DateTime(BigDecimal("0.0"), 2012, 6, 1, 12, 34, 56, 789).toString(),
        )
        assertEquals("@T12:34:56.789", Time(12, 34, 56, 789).toString())
    }

    @Test
    fun systemStructuredValueToCvlTest() {
        assertEquals(
            "5.0 'g'",
            Quantity()
                .apply {
                    value = BigDecimal("5.0")
                    unit = "g"
                }
                .toString(),
        )
        assertEquals(
            "1.0 'mg':2.0 'mg'",
            Ratio()
                .apply {
                    numerator =
                        Quantity().apply {
                            value = BigDecimal("1.0")
                            unit = "mg"
                        }
                    denominator =
                        Quantity().apply {
                            value = BigDecimal("2.0")
                            unit = "mg"
                        }
                }
                .toString(),
        )
        assertEquals(
            """
            ValueSet {
              id: 'exampleVsId',
              version: '1.0.0',
              name: 'exampleVsName',
              codesystems: {
                CodeSystem {
                  id: 'exampleCsId',
                  version: '2.0.0',
                  name: 'exampleCsName'
                }
              }
            }
        """
                .trimIndent(),
            ValueSet()
                .apply {
                    id = "exampleVsId"
                    version = "1.0.0"
                    name = "exampleVsName"
                    codeSystems =
                        mutableListOf(
                            CodeSystem().apply {
                                id = "exampleCsId"
                                version = "2.0.0"
                                name = "exampleCsName"
                            }
                        )
                }
                .toString(),
        )
    }

    @Test
    fun classInstanceToCvlTest() {
        assertEquals(
            "ExampleModel.ExampleClass { : }",
            ClassInstance(
                    QName("http://example.org", "ExampleClass", "ExampleModel"),
                    mutableMapOf(),
                )
                .toString(),
        )
        assertEquals(
            """
            ExampleModel.ExampleClass {
              field1: 123,
              field2: 'abc',
              field3: null
            }
        """
                .trimIndent(),
            ClassInstance(
                    QName("http://example.org", "ExampleClass", "ExampleModel"),
                    mutableMapOf(
                        "field1" to 123.toCqlInteger(),
                        "field2" to "abc".toCqlString(),
                        "field3" to null,
                    ),
                )
                .toString(),
        )
    }

    @Test
    fun tupleToCvlTest() {
        assertEquals("Tuple { : }", Tuple().toString())
        assertEquals(
            """
            Tuple {
              field1: 123,
              field2: 'abc',
              field3: null
            }
        """
                .trimIndent(),
            Tuple()
                .withElements(
                    mutableMapOf(
                        "field1" to 123.toCqlInteger(),
                        "field2" to "abc".toCqlString(),
                        "field3" to null,
                    )
                )
                .toString(),
        )
    }

    @Test
    fun listToCvlTest() {
        assertEquals("{}", List.EMPTY_LIST.toString())
        assertEquals(
            """
            {
              123,
              'abc',
              null
            }
        """
                .trimIndent(),
            listOf(123.toCqlInteger(), "abc".toCqlString(), null).toCqlList().toString(),
        )
    }

    @Test
    fun intervalToCvlTest() {
        assertEquals(
            "Interval[1, 2)",
            Interval(1.toCqlInteger(), true, 2.toCqlInteger(), false).toString(),
        )
        assertEquals(
            "Interval[5.0 'g', null)",
            Interval(
                    Quantity().apply {
                        value = BigDecimal("5.0")
                        unit = "g"
                    },
                    true,
                    null,
                    false,
                )
                .toString(),
        )
    }
}
