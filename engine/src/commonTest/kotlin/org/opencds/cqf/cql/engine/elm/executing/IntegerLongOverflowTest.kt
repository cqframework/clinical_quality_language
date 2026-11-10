package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/**
 * [#1912](https://github.com/cqframework/clinical_quality_language/issues/1912): Integer and Long
 * arithmetic returns null instead of wrapping around when the result cannot be represented.
 */
class IntegerLongOverflowTest {
    private val maxInt = Int.MAX_VALUE.toCqlInteger()
    private val minInt = Int.MIN_VALUE.toCqlInteger()
    private val maxLong = Long.MAX_VALUE.toCqlLong()
    private val minLong = Long.MIN_VALUE.toCqlLong()

    private fun int(value: Int) = value.toCqlInteger()

    private fun long(value: Long) = value.toCqlLong()

    @Test
    fun add() {
        assertEquals(maxInt, AddEvaluator.add(int(Int.MAX_VALUE - 1), int(1), null))
        assertEquals(minInt, AddEvaluator.add(int(Int.MIN_VALUE + 1), int(-1), null))
        assertEquals(int(-1), AddEvaluator.add(maxInt, minInt, null))
        assertNull(AddEvaluator.add(maxInt, int(1), null))
        assertNull(AddEvaluator.add(minInt, int(-1), null))
        assertNull(AddEvaluator.add(maxInt, maxInt, null))

        assertEquals(maxLong, AddEvaluator.add(long(Long.MAX_VALUE - 1), long(1), null))
        assertEquals(minLong, AddEvaluator.add(long(Long.MIN_VALUE + 1), long(-1), null))
        assertEquals(long(-1), AddEvaluator.add(maxLong, minLong, null))
        assertNull(AddEvaluator.add(maxLong, long(1), null))
        assertNull(AddEvaluator.add(minLong, long(-1), null))
        assertNull(AddEvaluator.add(minLong, minLong, null))
    }

    @Test
    fun subtract() {
        assertEquals(minInt, SubtractEvaluator.subtract(int(Int.MIN_VALUE + 1), int(1), null))
        assertEquals(maxInt, SubtractEvaluator.subtract(int(Int.MAX_VALUE - 1), int(-1), null))
        assertEquals(int(0), SubtractEvaluator.subtract(minInt, minInt, null))
        assertNull(SubtractEvaluator.subtract(minInt, int(1), null))
        assertNull(SubtractEvaluator.subtract(maxInt, int(-1), null))
        assertNull(SubtractEvaluator.subtract(int(0), minInt, null))

        assertEquals(minLong, SubtractEvaluator.subtract(long(Long.MIN_VALUE + 1), long(1), null))
        assertEquals(maxLong, SubtractEvaluator.subtract(long(Long.MAX_VALUE - 1), long(-1), null))
        assertEquals(long(0), SubtractEvaluator.subtract(minLong, minLong, null))
        assertNull(SubtractEvaluator.subtract(minLong, long(1), null))
        assertNull(SubtractEvaluator.subtract(maxLong, long(-1), null))
        assertNull(SubtractEvaluator.subtract(long(0), minLong, null))
    }

    @Test
    fun multiply() {
        assertEquals(int(2147483646), MultiplyEvaluator.multiply(int(1073741823), int(2), null))
        assertEquals(minInt, MultiplyEvaluator.multiply(int(-1073741824), int(2), null))
        assertEquals(int(-Int.MAX_VALUE), MultiplyEvaluator.multiply(maxInt, int(-1), null))
        assertNull(MultiplyEvaluator.multiply(maxInt, int(2), null))
        assertNull(MultiplyEvaluator.multiply(minInt, int(-1), null))
        assertNull(MultiplyEvaluator.multiply(int(65536), int(65536), null))

        assertEquals(minLong, MultiplyEvaluator.multiply(long(Long.MIN_VALUE / 2), long(2), null))
        assertEquals(long(-Long.MAX_VALUE), MultiplyEvaluator.multiply(maxLong, long(-1), null))
        assertNull(MultiplyEvaluator.multiply(maxLong, long(2), null))
        assertNull(MultiplyEvaluator.multiply(minLong, long(-1), null))
        assertNull(MultiplyEvaluator.multiply(long(4294967296), long(4294967296), null))
    }

    @Test
    fun abs() {
        assertEquals(maxInt, AbsEvaluator.abs(int(-Int.MAX_VALUE)))
        assertNull(AbsEvaluator.abs(minInt))

        assertEquals(maxLong, AbsEvaluator.abs(long(-Long.MAX_VALUE)))
        assertNull(AbsEvaluator.abs(minLong))
    }

    @Test
    fun negate() {
        assertEquals(int(Int.MIN_VALUE + 1), NegateEvaluator.negate(maxInt))
        assertNull(NegateEvaluator.negate(minInt))

        assertEquals(long(Long.MIN_VALUE + 1), NegateEvaluator.negate(maxLong))
        assertNull(NegateEvaluator.negate(minLong))
    }

    @Test
    fun power() {
        assertEquals(int(1073741824), PowerEvaluator.power(int(2), int(30)))
        assertEquals(minInt, PowerEvaluator.power(int(-2), int(31)))
        assertNull(PowerEvaluator.power(int(2), int(31)))
        assertNull(PowerEvaluator.power(int(-2), int(32)))

        assertEquals(long(4611686018427387904), PowerEvaluator.power(long(2), long(62)))
        assertEquals(minLong, PowerEvaluator.power(long(-2), long(63)))
        assertNull(PowerEvaluator.power(long(2), long(63)))
        assertNull(PowerEvaluator.power(long(-2), long(64)))
    }

    @Test
    fun truncatedDivide() {
        assertEquals(int(-Int.MAX_VALUE), TruncatedDivideEvaluator.div(maxInt, int(-1), null))
        assertEquals(int(-1073741824), TruncatedDivideEvaluator.div(minInt, int(2), null))
        assertNull(TruncatedDivideEvaluator.div(minInt, int(-1), null))

        assertEquals(long(-Long.MAX_VALUE), TruncatedDivideEvaluator.div(maxLong, long(-1), null))
        assertNull(TruncatedDivideEvaluator.div(minLong, long(-1), null))
    }
}
