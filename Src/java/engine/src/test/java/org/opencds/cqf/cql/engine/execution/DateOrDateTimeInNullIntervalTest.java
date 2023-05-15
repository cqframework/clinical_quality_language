package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DateOrDateTimeInNullIntervalTest extends CqlTestBase {

    @Test
    public void test_all_date_comparator() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("DateOrDateTimeInNullIntervalTest"), null, null, null, null, null);
        Object result;


    }
}
