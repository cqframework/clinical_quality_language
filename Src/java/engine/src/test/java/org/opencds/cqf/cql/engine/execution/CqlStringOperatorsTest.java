package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import org.junit.jupiter.api.Test;

class CqlStringOperatorsTest extends CqlTestBase {

    @Test
    void all_string_operators() {
        var results = engine.evaluate(toElmIdentifier("CqlStringOperatorsTest"));
        var value = results.forExpression("CombineNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("CombineEmptyList").value();
        assertThat(value, is(""));

        value = results.forExpression("CombineABC").value();
        assertThat(value, is("abc"));

        value = results.forExpression("CombineABCSepDash").value();
        assertThat(value, is("a-b-c"));

        value = results.forExpression("ConcatenateNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ConcatenateANull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ConcatenateNullB").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ConcatenateAB").value();
        assertThat(value, is("ab"));

        value = results.forExpression("ConcatenateABWithAdd").value();
        assertThat(value, is("ab"));

        value = results.forExpression("EndsWithNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("EndsWithTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("EndsWithFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IndexerNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexerANull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexerNull1String").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexerAB0").value();
        assertThat(value, is("a"));

        value = results.forExpression("IndexerAB1").value();
        assertThat(value, is("b"));

        value = results.forExpression("IndexerAB2").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexerABNeg1").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("MatchesNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("MatchesNumberFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("MatchesNumberTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("MatchesAllTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("MatchesWordsAndSpacesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("MatchesWordsAndSpacesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("MatchesNotWords").value();
        assertThat(value, is(true));

        value = results.forExpression("MatchesWhiteSpace").value();
        assertThat(value, is(true));

        value = results.forExpression("LastPositionOfNull").value();
        assertTrue(value == null);

        value = results.forExpression("LastPositionOfNull1").value();
        assertTrue(value == null);

        value = results.forExpression("LastPositionOfNull2").value();
        assertTrue(value == null);

        value = results.forExpression("LastPositionOf1").value();
        assertTrue((Integer) value == 1);

        value = results.forExpression("LastPositionOf2").value();
        assertTrue((Integer) value == 11);

        value = results.forExpression("LengthNullString").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("LengthEmptyString").value();
        assertThat(value, is(0));

        value = results.forExpression("LengthA").value();
        assertThat(value, is(1));

        value = results.forExpression("LengthAB").value();
        assertThat(value, is(2));

        value = results.forExpression("LowerNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("LowerEmpty").value();
        assertThat(value, is(""));

        value = results.forExpression("LowerA").value();
        assertThat(value, is("a"));

        value = results.forExpression("LowerB").value();
        assertThat(value, is("b"));

        value = results.forExpression("LowerAB").value();
        assertThat(value, is("ab"));

        value = results.forExpression("PositionOfNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("PositionOfANull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("PositionOfNullA").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("PositionOfAInAB").value();
        assertThat(value, is(0));

        value = results.forExpression("PositionOfBInAB").value();
        assertThat(value, is(1));

        value = results.forExpression("PositionOfCInAB").value();
        assertThat(value, is(-1));

        value = results.forExpression("ReplaceMatchesNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ReplaceMatchesAll").value();
        assertThat(value, is("But still waters run deep"));

        value = results.forExpression("ReplaceMatchesMany").value();
        assertThat(value, is("Who put the bang in the bang she bang she bang?"));

        value = results.forExpression("ReplaceMatchesSpaces").value();
        assertThat(value, is("All$that$glitters$is$not$gold"));

        value = results.forExpression("SplitNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SplitNullComma").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SplitABNull").value();
        assertThat(value, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        value = results.forExpression("SplitABDash").value();
        assertThat(value, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        value = results.forExpression("SplitABSpace").value();
        assertThat(value, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        value = results.forExpression("SplitABComma").value();
        assertThat(value, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        value = results.forExpression("SplitMatchesNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SplitMatchesNullComma").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SplitMatchesABNull").value();
        assertThat(value, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        value = results.forExpression("SplitMatchesABSpaceRegex").value();
        assertThat(value, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        value = results.forExpression("SplitMatchesABComma").value();
        assertThat(value, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        value = results.forExpression("StartsWithNull").value();
        assertTrue(value == null);

        value = results.forExpression("StartsWithNull1").value();
        assertTrue(value == null);

        value = results.forExpression("StartsWithNull2").value();
        assertTrue(value == null);

        value = results.forExpression("StartsWithTrue1").value();
        assertTrue((Boolean) value);

        value = results.forExpression("StartsWithFalse1").value();
        assertFalse((Boolean) value);

        value = results.forExpression("SubstringNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SubstringANull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SubstringNull1").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SubstringAB0").value();
        assertThat(value, is("ab"));

        value = results.forExpression("SubstringAB1").value();
        assertThat(value, is("b"));

        value = results.forExpression("SubstringAB2").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SubstringABNeg1").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SubstringAB0To1").value();
        assertThat(value, is("a"));

        value = results.forExpression("SubstringABC1To1").value();
        assertThat(value, is("b"));

        value = results.forExpression("SubstringAB0To3").value();
        assertThat(value, is("ab"));

        value = results.forExpression("UpperNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("UpperSpace").value();
        assertThat(value, is(" "));

        value = results.forExpression("UpperEmpty").value();
        assertThat(value, is(""));

        value = results.forExpression("UpperA").value();
        assertThat(value, is("A"));

        value = results.forExpression("UpperB").value();
        assertThat(value, is("B"));

        value = results.forExpression("UpperAB").value();
        assertThat(value, is("AB"));

        value = results.forExpression("QuantityToString").value();
        assertThat(value, is("125 'cm'"));

        value = results.forExpression("DateTimeToString1").value();
        assertThat(value, is("2000-01-01"));

        value = results.forExpression("DateTimeToString2").value();
        // The DateTime uses the local timezone. Strip that for the assertion.
        final var withoutTimezone = ((String) value).replaceAll("[+-][0-9]{2}:[0-9]{2}$", "");
        assertThat(withoutTimezone, is("2000-01-01T15:25:25.300"));

        value = results.forExpression("DateTimeToString3").value();
        assertThat(value, is("2000-01-01T08:25:25.300-07:00"));

        value = results.forExpression("TimeToString1").value();
        assertThat(value, is("09:30:01.003"));
    }
}
