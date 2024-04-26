package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;

class SortDescendingTest extends CqlTestBase {

    @Test
    void evaluate() {
        var results = engine.evaluate(toElmIdentifier("SortDescendingTest"));
        var value = results.forExpression("sorted list of numbers descending").value();
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(0), 9));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(1), 4));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(2), 2));
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(3), 1));
    }
}
