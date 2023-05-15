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

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlStringOperatorsTest"), null, null, null, null, null);
        Object result;

        result = evaluationResult.expressionResults.get("CombineNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("CombineEmptyList").value();
        assertThat(result, is(""));

        result = evaluationResult.expressionResults.get("CombineABC").value();
        assertThat(result, is("abc"));

        result = evaluationResult.expressionResults.get("CombineABCSepDash").value();
        assertThat(result, is("a-b-c"));

        result = evaluationResult.expressionResults.get("ConcatenateNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConcatenateANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConcatenateNullB").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConcatenateAB").value();
        assertThat(result, is("ab"));

        result = evaluationResult.expressionResults.get("ConcatenateABWithAdd").value();
        assertThat(result, is("ab"));

        result = evaluationResult.expressionResults.get("EndsWithNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("EndsWithTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EndsWithFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IndexerNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexerANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexerNull1String").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexerAB0").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("IndexerAB1").value();
        assertThat(result, is("b"));

        result = evaluationResult.expressionResults.get("IndexerAB2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexerABNeg1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("MatchesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("MatchesNumberFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("MatchesNumberTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("MatchesAllTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("MatchesWordsAndSpacesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("MatchesWordsAndSpacesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("MatchesNotWords").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("MatchesWhiteSpace").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LastPositionOfNull").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("LastPositionOfNull1").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("LastPositionOfNull2").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("LastPositionOf1").value();
        Assert.assertTrue((Integer) result == 1);

        result = evaluationResult.expressionResults.get("LastPositionOf2").value();
        Assert.assertTrue((Integer) result == 11);

        result = evaluationResult.expressionResults.get("LengthNullString").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("LengthEmptyString").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("LengthA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("LengthAB").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("LowerNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("LowerEmpty").value();
        assertThat(result, is(""));

        result = evaluationResult.expressionResults.get("LowerA").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("LowerB").value();
        assertThat(result, is("b"));

        result = evaluationResult.expressionResults.get("LowerAB").value();
        assertThat(result, is("ab"));

        result = evaluationResult.expressionResults.get("PositionOfNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("PositionOfANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("PositionOfNullA").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("PositionOfAInAB").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("PositionOfBInAB").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("PositionOfCInAB").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("ReplaceMatchesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ReplaceMatchesAll").value();
        assertThat(result, is("But still waters run deep"));

        result = evaluationResult.expressionResults.get("ReplaceMatchesMany").value();
        assertThat(result, is("Who put the bang in the bang she bang she bang?"));

        result = evaluationResult.expressionResults.get("ReplaceMatchesSpaces").value();
        assertThat(result, is("All$that$glitters$is$not$gold"));

        result = evaluationResult.expressionResults.get("SplitNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SplitNullComma").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SplitABNull").value();
        assertThat(result, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        result = evaluationResult.expressionResults.get("SplitABDash").value();
        assertThat(result, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        result = evaluationResult.expressionResults.get("SplitABSpace").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.expressionResults.get("SplitABComma").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.expressionResults.get("SplitMatchesNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SplitMatchesNullComma").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SplitMatchesABNull").value();
        assertThat(result, is(new ArrayList<Object>(Collections.singletonList("a,b"))));

        result = evaluationResult.expressionResults.get("SplitMatchesABSpaceRegex").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.expressionResults.get("SplitMatchesABComma").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList("a", "b"))));

        result = evaluationResult.expressionResults.get("StartsWithNull").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("StartsWithNull1").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("StartsWithNull2").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("StartsWithTrue1").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("StartsWithFalse1").value();
        Assert.assertTrue(!(Boolean) result);

        result = evaluationResult.expressionResults.get("SubstringNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SubstringANull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SubstringNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SubstringAB0").value();
        assertThat(result, is("ab"));

        result = evaluationResult.expressionResults.get("SubstringAB1").value();
        assertThat(result, is("b"));

        result = evaluationResult.expressionResults.get("SubstringAB2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SubstringABNeg1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SubstringAB0To1").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("SubstringABC1To1").value();
        assertThat(result, is("b"));

        result = evaluationResult.expressionResults.get("SubstringAB0To3").value();
        assertThat(result, is("ab"));

        result = evaluationResult.expressionResults.get("UpperNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("UpperSpace").value();
        assertThat(result, is(" "));

        result = evaluationResult.expressionResults.get("UpperEmpty").value();
        assertThat(result, is(""));

        result = evaluationResult.expressionResults.get("UpperA").value();
        assertThat(result, is("A"));

        result = evaluationResult.expressionResults.get("UpperB").value();
        assertThat(result, is("B"));

        result = evaluationResult.expressionResults.get("UpperAB").value();
        assertThat(result, is("AB"));

        result = evaluationResult.expressionResults.get("QuantityToString").value();
        assertThat(result, is("125 'cm'"));

        result = evaluationResult.expressionResults.get("DateTimeToString1").value();
        assertThat(result, is("2000-01-01"));

        result = evaluationResult.expressionResults.get("DateTimeToString2").value();
        assertThat(result, is("2000-01-01T15:25:25.300"));

        result = evaluationResult.expressionResults.get("DateTimeToString3").value();
        assertThat(result, is("2000-01-01T08:25:25.300"));

        result = evaluationResult.expressionResults.get("TimeToString1").value();
        assertThat(result, is("09:30:01.003"));
    }
}
