package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class Issue223 extends CqlTestBase {

    @Test
    public void testInterval() {

        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("Issue223"));
        Object result = evaluationResult.expressionResults.get("Access Flattened List of List Items").value();
        List<?> list = (List<?>)result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));

        result = evaluationResult.expressionResults.get("Access Flattened List of List Items in a Single Query").value();
        list = (List<?>)result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));

    }
}
