package org.opencds.cqf.cql.engine.execution;

import java.util.List;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SortDescendingTest extends CqlTestBase {

    @Test
    public void testEvaluate() {
        var results = engine.evaluate(toElmIdentifier("SortDescendingTest"));
        var value = results.forExpression("sorted list of numbers descending").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(0), 9));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(1), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(2), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(3), 1));
    }
}
