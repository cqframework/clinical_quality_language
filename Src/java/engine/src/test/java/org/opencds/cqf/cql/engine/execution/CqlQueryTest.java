package org.opencds.cqf.cql.engine.execution;

import java.util.*;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlQueryTest extends CqlTestBase {

    @Test
    public void test_all_query_operators() {
        var results = engine.evaluate(toElmIdentifier("CqlQueryTests"));
        var value = results.forExpression("RightShift").value();
        Assert.assertEquals(value, Arrays.asList(null, "A", "B", "C"));
        value = results.forExpression("LeftShift").value();
        Assert.assertEquals(value, Arrays.asList("B", "C", "D", null));
        value = results.forExpression("LeftShift2").value();
        Assert.assertEquals(value, Arrays.asList("B", "C", "D", null));

        value = results.forExpression("Multisource").value();
        Assert.assertTrue(value instanceof List);
        List<?> list = (List<?>) value;
        Assert.assertTrue(list.size() == 1);
        Assert.assertTrue(list.get(0) instanceof Tuple);
        Tuple resultTuple = (Tuple) list.get(0);
        Assert.assertTrue(resultTuple.getElements().containsKey("A")
                && resultTuple.getElements().containsKey("B"));

        value = results.forExpression("Complex Multisource").value();
        Assert.assertTrue(value instanceof List);
        list = (List<?>) value;
        Assert.assertTrue(list.size() == 4);

        value = results.forExpression("Let Test Fails").value();

        value = results.forExpression("Triple Source Query").value();
        Assert.assertTrue(value instanceof List);
        list = (List<?>) value;
        Assert.assertTrue(list.size() == 27);

        value = results.forExpression("Let Expression in Multi Source Query").value();
        Assert.assertTrue(value instanceof List);
        list = (List<?>) value;
        Assert.assertTrue(list.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(list.get(0), 3));

        value = results.forExpression("Accessing Third Element of Triple Source Query")
                .value();
        Assert.assertTrue(value instanceof List);
        list = (List<?>) value;
        Assert.assertTrue(list.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(list.get(0), 3));
    }
}
