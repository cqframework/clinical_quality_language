package org.opencds.cqf.cql.engine.execution;

import java.util.List;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LetClauseOutsideQueryContextTest extends CqlTestBase {

    @Test
    public void testEvaluate() {
        var results = engine.evaluate(toElmIdentifier("LetClauseOutsideQueryContextTest"));
        var value = results.forExpression("First Position of list").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(0), 1));

        value = results.forExpression("Third Position of list With Same Name of Let As First")
                .value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(0), 3));
    }
}
