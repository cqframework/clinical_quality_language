package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CqlStringOperatorsTest : CqlTestBase() {
    @Test
    fun all_string_operators() {
        val results = engine.evaluate { library("CqlStringOperatorsTest") }.onlyResultOrThrow
        var value = results["CombineNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["CombineEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(""))

        value = results["CombineABC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("abc"))

        value = results["CombineABCSepDash"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a-b-c"))

        value = results["ConcatenateNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ConcatenateANull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ConcatenateNullB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ConcatenateAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results["ConcatenateABWithAdd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results["EndsWithNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["EndsWithTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EndsWithFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IndexerNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexerANull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexerNull1String"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexerAB0"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["IndexerAB1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results["IndexerAB2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexerABNeg1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["MatchesNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["MatchesNumberFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["MatchesNumberTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["MatchesAllTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["MatchesWordsAndSpacesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["MatchesWordsAndSpacesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["MatchesNotWords"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["MatchesWhiteSpace"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["LastPositionOfNull"]!!.value
        Assertions.assertTrue(value == null)

        value = results["LastPositionOfNull1"]!!.value
        Assertions.assertTrue(value == null)

        value = results["LastPositionOfNull2"]!!.value
        Assertions.assertTrue(value == null)

        value = results["LastPositionOf1"]!!.value
        Assertions.assertTrue(value as Int? == 1)

        value = results["LastPositionOf2"]!!.value
        Assertions.assertTrue(value as Int? == 11)

        value = results["LengthNullString"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["LengthEmptyString"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results["LengthA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["LengthAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["LowerNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["LowerEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(""))

        value = results["LowerA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["LowerB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results["LowerAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results["PositionOfNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["PositionOfANull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["PositionOfNullA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["PositionOfAInAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results["PositionOfBInAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["PositionOfCInAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-1))

        value = results["ReplaceMatchesNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ReplaceMatchesAll"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("But still waters run deep"))

        value = results["ReplaceMatchesMany"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`("Who put the bang in the bang she bang she bang?"),
        )

        value = results["ReplaceMatchesSpaces"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`($$"All$that$glitters$is$not$gold"))

        value = results["SplitNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SplitNullComma"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SplitABNull"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a,b"))),
        )

        value = results["SplitABDash"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a,b"))),
        )

        value = results["SplitABSpace"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results["SplitABComma"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results["SplitMatchesNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SplitMatchesNullComma"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SplitMatchesABNull"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a,b"))),
        )

        value = results["SplitMatchesABSpaceRegex"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results["SplitMatchesABComma"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results["StartsWithNull"]!!.value
        Assertions.assertTrue(value == null)

        value = results["StartsWithNull1"]!!.value
        Assertions.assertTrue(value == null)

        value = results["StartsWithNull2"]!!.value
        Assertions.assertTrue(value == null)

        value = results["StartsWithTrue1"]!!.value
        Assertions.assertTrue((value as Boolean?)!!)

        value = results["StartsWithFalse1"]!!.value
        Assertions.assertFalse((value as Boolean?)!!)

        value = results["SubstringNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SubstringANull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SubstringNull1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SubstringAB0"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results["SubstringAB1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results["SubstringAB2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SubstringABNeg1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SubstringAB0To1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["SubstringABC1To1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results["SubstringAB0To3"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results["UpperNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["UpperSpace"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(" "))

        value = results["UpperEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(""))

        value = results["UpperA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("A"))

        value = results["UpperB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("B"))

        value = results["UpperAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("AB"))

        value = results["QuantityToString"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("125 'cm'"))

        value = results["DateTimeToString1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("2000-01-01"))

        value = results["DateTimeToString2"]!!.value
        // The DateTime uses the local timezone. Strip that for the assertion.
        val withoutTimezone = (value as String).replace("[+-][0-9]{2}:[0-9]{2}$".toRegex(), "")
        MatcherAssert.assertThat(withoutTimezone, Matchers.`is`("2000-01-01T15:25:25.300"))

        value = results["DateTimeToString3"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("2000-01-01T08:25:25.300-07:00"))

        value = results["TimeToString1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("09:30:01.003"))
    }
}
