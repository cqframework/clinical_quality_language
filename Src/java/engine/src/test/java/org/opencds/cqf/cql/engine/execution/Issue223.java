package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.testng.annotations.Test;

public class Issue223 extends CqlTestBase {

    @Test
    public void testInterval() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toIdentifier("Issue223"));
        Object result = evaluationResult
                .forExpression("Access Flattened List of List Items")
                .value();
        List<?> list = (List<?>) result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));

        result = evaluationResult
                .forExpression("Access Flattened List of List Items in a Single Query")
                .value();
        list = (List<?>) result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));
    }
}
