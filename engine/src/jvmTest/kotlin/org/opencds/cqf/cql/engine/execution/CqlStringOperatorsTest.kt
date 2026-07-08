package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlStringOperatorsTest : CqlTestBase() {
    @Test
    fun all_string_operators() {
        val results = engine.evaluate { library("CqlStringOperatorsTest") }.onlyResultOrThrow
        var value = results["CombineNull"]!!.value
        assertNull(value)

        value = results["CombineEmptyList"]!!.value
        assertEquals(String.EMPTY_STRING, value)

        value = results["CombineABC"]!!.value
        assertEquals("abc".toCqlString(), value)

        value = results["CombineABCSepDash"]!!.value
        assertEquals("a-b-c".toCqlString(), value)

        value = results["ConcatenateNullNull"]!!.value
        assertNull(value)

        value = results["ConcatenateANull"]!!.value
        assertNull(value)

        value = results["ConcatenateNullB"]!!.value
        assertNull(value)

        value = results["ConcatenateAB"]!!.value
        assertEquals("ab".toCqlString(), value)

        value = results["ConcatenateABWithAdd"]!!.value
        assertEquals("ab".toCqlString(), value)

        value = results["EndsWithNull"]!!.value
        assertNull(value)

        value = results["EndsWithTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EndsWithFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IndexerNullNull"]!!.value
        assertNull(value)

        value = results["IndexerANull"]!!.value
        assertNull(value)

        value = results["IndexerNull1String"]!!.value
        assertNull(value)

        value = results["IndexerAB0"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["IndexerAB1"]!!.value
        assertEquals("b".toCqlString(), value)

        value = results["IndexerAB2"]!!.value
        assertNull(value)

        value = results["IndexerABNeg1"]!!.value
        assertNull(value)

        value = results["MatchesNull"]!!.value
        assertNull(value)

        value = results["MatchesNumberFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["MatchesNumberTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["MatchesAllTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["MatchesWordsAndSpacesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["MatchesWordsAndSpacesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["MatchesNotWords"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["MatchesWhiteSpace"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["LastPositionOfNull"]!!.value
        assertNull(value)

        value = results["LastPositionOfNull1"]!!.value
        assertNull(value)

        value = results["LastPositionOfNull2"]!!.value
        assertNull(value)

        value = results["LastPositionOf1"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["LastPositionOf2"]!!.value
        assertEquals(11.toCqlInteger(), value)

        value = results["LengthNullString"]!!.value
        assertNull(value)

        value = results["LengthEmptyString"]!!.value
        assertEquals(Integer.ZERO, value)

        value = results["LengthA"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["LengthAB"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["LowerNull"]!!.value
        assertNull(value)

        value = results["LowerEmpty"]!!.value
        assertEquals(String.EMPTY_STRING, value)

        value = results["LowerA"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["LowerB"]!!.value
        assertEquals("b".toCqlString(), value)

        value = results["LowerAB"]!!.value
        assertEquals("ab".toCqlString(), value)

        value = results["PositionOfNullNull"]!!.value
        assertNull(value)

        value = results["PositionOfANull"]!!.value
        assertNull(value)

        value = results["PositionOfNullA"]!!.value
        assertNull(value)

        value = results["PositionOfAInAB"]!!.value
        assertEquals(Integer.ZERO, value)

        value = results["PositionOfBInAB"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["PositionOfCInAB"]!!.value
        assertEquals((-1).toCqlInteger(), value)

        value = results["ReplaceMatchesNull"]!!.value
        assertNull(value)

        value = results["ReplaceMatchesAll"]!!.value
        assertEquals("But still waters run deep".toCqlString(), value)

        value = results["ReplaceMatchesMany"]!!.value
        assertEquals("Who put the bang in the bang she bang she bang?".toCqlString(), value)

        value = results["ReplaceMatchesSpaces"]!!.value
        assertEquals($$"All$that$glitters$is$not$gold".toCqlString(), value)

        value = results["SplitNullNull"]!!.value
        assertNull(value)

        value = results["SplitNullComma"]!!.value
        assertNull(value)

        value = results["SplitABNull"]!!.value
        assertEquals(listOf("a,b".toCqlString()).toCqlList(), value)

        value = results["SplitABDash"]!!.value
        assertEquals(listOf("a,b".toCqlString()).toCqlList(), value)

        value = results["SplitABSpace"]!!.value
        assertEquals(listOf("a".toCqlString(), "b".toCqlString()).toCqlList(), value)

        value = results["SplitABComma"]!!.value
        assertEquals(listOf("a".toCqlString(), "b".toCqlString()).toCqlList(), value)

        value = results["SplitMatchesNullNull"]!!.value
        assertNull(value)

        value = results["SplitMatchesNullComma"]!!.value
        assertNull(value)

        value = results["SplitMatchesABNull"]!!.value
        assertEquals(listOf("a,b".toCqlString()).toCqlList(), value)

        value = results["SplitMatchesABSpaceRegex"]!!.value
        assertEquals(listOf("a".toCqlString(), "b".toCqlString()).toCqlList(), value)

        value = results["SplitMatchesABComma"]!!.value
        assertEquals(listOf("a".toCqlString(), "b".toCqlString()).toCqlList(), value)

        value = results["StartsWithNull"]!!.value
        assertNull(value)

        value = results["StartsWithNull1"]!!.value
        assertNull(value)

        value = results["StartsWithNull2"]!!.value
        assertNull(value)

        value = results["StartsWithTrue1"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["StartsWithFalse1"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["SubstringNullNull"]!!.value
        assertNull(value)

        value = results["SubstringANull"]!!.value
        assertNull(value)

        value = results["SubstringNull1"]!!.value
        assertNull(value)

        value = results["SubstringAB0"]!!.value
        assertEquals("ab".toCqlString(), value)

        value = results["SubstringAB1"]!!.value
        assertEquals("b".toCqlString(), value)

        value = results["SubstringAB2"]!!.value
        assertNull(value)

        value = results["SubstringABNeg1"]!!.value
        assertNull(value)

        value = results["SubstringAB0To1"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["SubstringABC1To1"]!!.value
        assertEquals("b".toCqlString(), value)

        value = results["SubstringAB0To3"]!!.value
        assertEquals("ab".toCqlString(), value)

        value = results["UpperNull"]!!.value
        assertNull(value)

        value = results["UpperSpace"]!!.value
        assertEquals(" ".toCqlString(), value)

        value = results["UpperEmpty"]!!.value
        assertEquals(String.EMPTY_STRING, value)

        value = results["UpperA"]!!.value
        assertEquals("A".toCqlString(), value)

        value = results["UpperB"]!!.value
        assertEquals("B".toCqlString(), value)

        value = results["UpperAB"]!!.value
        assertEquals("AB".toCqlString(), value)

        value = results["QuantityToString"]!!.value
        assertEquals("125 'cm'".toCqlString(), value)

        value = results["DateTimeToString1"]!!.value
        assertEquals("2000-01-01".toCqlString(), value)

        value = results["DateTimeToString2"]!!.value
        // The DateTime uses the local timezone. Strip that for the assertion.
        val withoutTimezone = (value as String).replace("[+-][0-9]{2}:[0-9]{2}$".toRegex(), "")
        assertEquals("2000-01-01T15:25:25.300", withoutTimezone)

        value = results["DateTimeToString3"]!!.value
        assertEquals("2000-01-01T08:25:25.300-07:00".toCqlString(), value)

        value = results["TimeToString1"]!!.value
        assertEquals("09:30:01.003".toCqlString(), value)
    }
}
