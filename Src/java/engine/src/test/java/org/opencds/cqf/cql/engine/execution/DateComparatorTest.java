package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DateComparatorTest extends CqlTestBase {

    @Test
    public void test_all_date_comparator() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("DateComparatorTest"), null, null, null, null, null);
        Object result = evaluationResult.expressionResults.get("Date Comparator Test").value();
        assertThat(result, is(true));


    }
}
