package org.opencds.cqf.cql.engine.execution;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlStringOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_string_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlStringOperatorsTest"));
        Object result;

        result = evaluationResult.forExpression("CombineNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("CombineEmptyList").value();
        assertThat(result, is(""));

        result = evaluationResult.forExpression("CombineABC").value();
        assertThat(result, is("abc"));

        result = evaluationResult.forExpression("CombineABCSepDash").value();
        assertThat(result, is("a-b-c"));

        result = evaluationResult.forExpression("ConcatenateNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("ConcatenateANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("ConcatenateNullB").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("ConcatenateAB").value();
        assertThat(result, is("ab"));

        result = evaluationResult.forExpression("ConcatenateABWithAdd").value();
        assertThat(result, is("ab"));

        result = evaluationResult.forExpression("EndsWithNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("EndsWithTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EndsWithFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IndexerNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexerANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexerNull1String").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexerAB0").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("IndexerAB1").value();
        assertThat(result, is("b"));

        result = evaluationResult.forExpression("IndexerAB2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexerABNeg1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("MatchesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("MatchesNumberFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("MatchesNumberTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("MatchesAllTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("MatchesWordsAndSpacesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("MatchesWordsAndSpacesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("MatchesNotWords").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("MatchesWhiteSpace").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LastPositionOfNull").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("LastPositionOfNull1").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("LastPositionOfNull2").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("LastPositionOf1").value();
        Assert.assertTrue((Integer) result == 1);

        result = evaluationResult.forExpression("LastPositionOf2").value();
        Assert.assertTrue((Integer) result == 11);

        result = evaluationResult.forExpression("LengthNullString").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("LengthEmptyString").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("LengthA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("LengthAB").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("LowerNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("LowerEmpty").value();
        assertThat(result, is(""));

        result = evaluationResult.forExpression("LowerA").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("LowerB").value();
        assertThat(result, is("b"));

        result = evaluationResult.forExpression("LowerAB").value();
        assertThat(result, is("ab"));

        result = evaluationResult.forExpression("PositionOfNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("PositionOfANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("PositionOfNullA").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("PositionOfAInAB").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("PositionOfBInAB").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("PositionOfCInAB").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("ReplaceMatchesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("ReplaceMatchesAll").value();
        assertThat(result, is("But still waters run deep"));

        result = evaluationResult.forExpression("ReplaceMatchesMany").value();
        assertThat(result, is("Who put the bang in the bang she bang she bang?"));

        result = evaluationResult.forExpression("ReplaceMatchesSpaces").value();
        assertThat(result, is("All$that$glitters$is$not$gold"));

        result = evaluationResult.forExpression("SplitNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SplitNullComma").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SplitABNull").value();
        assertThat(result, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        result = evaluationResult.forExpression("SplitABDash").value();
        assertThat(result, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        result = evaluationResult.forExpression("SplitABSpace").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.forExpression("SplitABComma").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.forExpression("SplitMatchesNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SplitMatchesNullComma").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SplitMatchesABNull").value();
        assertThat(result, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        result = evaluationResult.forExpression("SplitMatchesABSpaceRegex").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.forExpression("SplitMatchesABComma").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.forExpression("StartsWithNull").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("StartsWithNull1").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("StartsWithNull2").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("StartsWithTrue1").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.forExpression("StartsWithFalse1").value();
        Assert.assertTrue(!(Boolean) result);

        result = evaluationResult.forExpression("SubstringNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SubstringANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SubstringNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SubstringAB0").value();
        assertThat(result, is("ab"));

        result = evaluationResult.forExpression("SubstringAB1").value();
        assertThat(result, is("b"));

        result = evaluationResult.forExpression("SubstringAB2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SubstringABNeg1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SubstringAB0To1").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("SubstringABC1To1").value();
        assertThat(result, is("b"));

        result = evaluationResult.forExpression("SubstringAB0To3").value();
        assertThat(result, is("ab"));

        result = evaluationResult.forExpression("UpperNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("UpperSpace").value();
        assertThat(result, is(" "));

        result = evaluationResult.forExpression("UpperEmpty").value();
        assertThat(result, is(""));

        result = evaluationResult.forExpression("UpperA").value();
        assertThat(result, is("A"));

        result = evaluationResult.forExpression("UpperB").value();
        assertThat(result, is("B"));

        result = evaluationResult.forExpression("UpperAB").value();
        assertThat(result, is("AB"));

        result = evaluationResult.forExpression("QuantityToString").value();
        assertThat(result, is("125 'cm'"));

        result = evaluationResult.forExpression("DateTimeToString1").value();
        assertThat(result, is("2000-01-01"));

        result = evaluationResult.forExpression("DateTimeToString2").value();
        assertThat(result, is("2000-01-01T15:25:25.300"));

        result = evaluationResult.forExpression("DateTimeToString3").value();
        assertThat(result, is("2000-01-01T08:25:25.300"));

        result = evaluationResult.forExpression("TimeToString1").value();
        assertThat(result, is("09:30:01.003"));
    }
}
