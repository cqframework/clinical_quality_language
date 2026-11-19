package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.toCqlString

class ToDateEvaluatorTest {
    private fun toDate(value: String) = ToDateEvaluator.toDate(value.toCqlString())

    @Test
    fun toDate() {
        assertNull(ToDateEvaluator.toDate(null))
        assertEquals(Date(2014, 1, 1), toDate("2014-01-01"))
        assertEquals(Date(2014, 1), toDate("2014-01"))
        assertEquals(Date(2014), toDate("2014"))
        assertNull(toDate("2014-13-01"))
        assertNull(toDate("not a date"))
    }

    /**
     * [#1918](https://github.com/cqframework/clinical_quality_language/issues/1918): The time
     * portion of a datetime-formatted string is ignored
     */
    @Test
    fun toDateIgnoresTime() {
        assertEquals(Date(2014, 1, 1), toDate("2014-01-01T12:30:00"))
        assertEquals(Date(2014, 1, 1), toDate("2014-01-01T12:30"))
        assertEquals(Date(2014, 1, 1), toDate("2014-01-01T12:05:05.955"))
        assertEquals(Date(2014, 1, 1), toDate("2014-01-01T12:30:00Z"))
        assertEquals(Date(2014, 1, 1), toDate("2014-01-01T23:30:00-05:00"))
        assertNull(toDate("2014-01-01T25:00:00"))
        assertNull(toDate("2014-01-01Tnot a time"))
    }

    /** ConvertsToDate supports the same conversions as ToDate. */
    @Test
    fun convertsToDateMatchesToDate() {
        for (value in
            listOf("2014-01-01", "2014-01", "2014-01-01T12:30:00", "2014-01-01T12:30:00Z")) {
            assertEquals(Boolean.TRUE, ConvertsToDateEvaluator.convertsToDate(value.toCqlString()))
        }
        for (value in listOf("2014-13-01", "2014-01-01T25:00:00", "not a date")) {
            assertEquals(Boolean.FALSE, ConvertsToDateEvaluator.convertsToDate(value.toCqlString()))
        }
        assertNull(ConvertsToDateEvaluator.convertsToDate(null))
    }
}
