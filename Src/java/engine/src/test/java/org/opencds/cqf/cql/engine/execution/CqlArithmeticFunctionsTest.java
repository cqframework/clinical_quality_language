package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.*;
import org.opencds.cqf.cql.engine.exception.CqlException;

@SuppressWarnings("removal")
class CqlArithmeticFunctionsTest extends CqlTestBase {

    @Test
    void abs() {

        // error testing
        try {
            var value = AbsEvaluator.abs("This is an error");
            fail();
        } catch (CqlException e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AddEvaluator#evaluate(Context)}
     */
    @Test
    void add() {

        // error testing
        try {
            var value = AddEvaluator.add("This is an error", 404);
            fail();
        } catch (CqlException e) {
            // pass
        }
    }
}
