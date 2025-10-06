package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CqlStringOperatorsTest : CqlTestBase() {
    @Test
    fun all_string_operators() {
        val results = engine.evaluate(toElmIdentifier("CqlStringOperatorsTest"))
        var value = results.forExpression("CombineNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("CombineEmptyList")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(""))

        value = results.forExpression("CombineABC")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("abc"))

        value = results.forExpression("CombineABCSepDash")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a-b-c"))

        value = results.forExpression("ConcatenateNullNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ConcatenateANull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ConcatenateNullB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ConcatenateAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results.forExpression("ConcatenateABWithAdd")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results.forExpression("EndsWithNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("EndsWithTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EndsWithFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IndexerNullNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexerANull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexerNull1String")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexerAB0")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("IndexerAB1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results.forExpression("IndexerAB2")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexerABNeg1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("MatchesNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("MatchesNumberFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("MatchesNumberTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("MatchesAllTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("MatchesWordsAndSpacesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("MatchesWordsAndSpacesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("MatchesNotWords")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("MatchesWhiteSpace")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("LastPositionOfNull")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("LastPositionOfNull1")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("LastPositionOfNull2")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("LastPositionOf1")!!.value()
        Assertions.assertTrue(value as Int? == 1)

        value = results.forExpression("LastPositionOf2")!!.value()
        Assertions.assertTrue(value as Int? == 11)

        value = results.forExpression("LengthNullString")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("LengthEmptyString")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results.forExpression("LengthA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("LengthAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("LowerNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("LowerEmpty")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(""))

        value = results.forExpression("LowerA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("LowerB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results.forExpression("LowerAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results.forExpression("PositionOfNullNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("PositionOfANull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("PositionOfNullA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("PositionOfAInAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results.forExpression("PositionOfBInAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("PositionOfCInAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(-1))

        value = results.forExpression("ReplaceMatchesNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ReplaceMatchesAll")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("But still waters run deep"))

        value = results.forExpression("ReplaceMatchesMany")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`("Who put the bang in the bang she bang she bang?"),
        )

        value = results.forExpression("ReplaceMatchesSpaces")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`($$"All$that$glitters$is$not$gold"))

        value = results.forExpression("SplitNullNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SplitNullComma")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SplitABNull")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a,b"))),
        )

        value = results.forExpression("SplitABDash")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a,b"))),
        )

        value = results.forExpression("SplitABSpace")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results.forExpression("SplitABComma")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results.forExpression("SplitMatchesNullNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SplitMatchesNullComma")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SplitMatchesABNull")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a,b"))),
        )

        value = results.forExpression("SplitMatchesABSpaceRegex")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results.forExpression("SplitMatchesABComma")!!.value()
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<String?>("a", "b"))),
        )

        value = results.forExpression("StartsWithNull")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("StartsWithNull1")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("StartsWithNull2")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("StartsWithTrue1")!!.value()
        Assertions.assertTrue((value as Boolean?)!!)

        value = results.forExpression("StartsWithFalse1")!!.value()
        Assertions.assertFalse((value as Boolean?)!!)

        value = results.forExpression("SubstringNullNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SubstringANull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SubstringNull1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SubstringAB0")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results.forExpression("SubstringAB1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results.forExpression("SubstringAB2")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SubstringABNeg1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SubstringAB0To1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("SubstringABC1To1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("b"))

        value = results.forExpression("SubstringAB0To3")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("ab"))

        value = results.forExpression("UpperNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("UpperSpace")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(" "))

        value = results.forExpression("UpperEmpty")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(""))

        value = results.forExpression("UpperA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("A"))

        value = results.forExpression("UpperB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("B"))

        value = results.forExpression("UpperAB")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("AB"))

        value = results.forExpression("QuantityToString")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("125 'cm'"))

        value = results.forExpression("DateTimeToString1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("2000-01-01"))

        value = results.forExpression("DateTimeToString2")!!.value()
        // The DateTime uses the local timezone. Strip that for the assertion.
        val withoutTimezone = (value as String).replace("[+-][0-9]{2}:[0-9]{2}$".toRegex(), "")
        MatcherAssert.assertThat(withoutTimezone, Matchers.`is`("2000-01-01T15:25:25.300"))

        value = results.forExpression("DateTimeToString3")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("2000-01-01T08:25:25.300-07:00"))

        value = results.forExpression("TimeToString1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("09:30:01.003"))
    }
}
