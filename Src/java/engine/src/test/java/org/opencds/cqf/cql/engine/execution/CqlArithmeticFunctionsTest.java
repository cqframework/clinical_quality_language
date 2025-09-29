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
     * {@link org.opencds.cqf.cql.engine.elm.executing.AddEvaluator#add}
     */
    @Test
    void add() {

        // error testing
        try {
            // Passing null as the state argument to the subtract method is fine here since that method
            // only uses the state when it has to convert Quantities with different units which cannot
            // happen here.
            var value = AddEvaluator.add("This is an error", 404, null);
            fail();
        } catch (CqlException e) {
            // pass
        }
    }
}
