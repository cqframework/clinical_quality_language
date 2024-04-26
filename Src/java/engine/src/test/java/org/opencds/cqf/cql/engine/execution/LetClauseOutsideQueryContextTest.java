package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;

class LetClauseOutsideQueryContextTest extends CqlTestBase {

    @Test
    void evaluate() {
        var results = engine.evaluate(toElmIdentifier("LetClauseOutsideQueryContextTest"));
        var value = results.forExpression("First Position of list").value();
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(0), 1));

        value = results.forExpression("Third Position of list With Same Name of Let As First")
                .value();
        assertTrue(EquivalentEvaluator.equivalent(((List<?>) value).get(0), 3));
    }
}
