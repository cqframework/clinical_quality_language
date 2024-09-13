package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;

class CqlComparisonOperatorsTest extends CqlTestBase {

    @Test
    void all_comparison_operators_tests() {
        assertThrows(CqlException.class, () -> {
            GreaterEvaluator.greater(1, "one", engine.getState());
        });
    }
}
