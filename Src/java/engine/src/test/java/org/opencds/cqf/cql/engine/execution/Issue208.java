package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;

class Issue208 extends CqlTestBase {

    @Test
    void interval() {
        var results = engine.evaluate(toElmIdentifier("Issue208"));
        var value = results.forExpression("Let Test 1").value();
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(0))).get(0), 1));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(0))).get(1), 2));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(0))).get(2), 3));

        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(1))).get(0), 4));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(1))).get(1), 5));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(1))).get(2), 6));

        value = results.forExpression("Let Test 2").value();
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(0))).get(0), 1));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(0))).get(1), 2));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(0))).get(2), 3));

        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(1))).get(0), 4));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(1))).get(1), 5));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) (((List<?>) value).get(1))).get(2), 6));
    }
}
