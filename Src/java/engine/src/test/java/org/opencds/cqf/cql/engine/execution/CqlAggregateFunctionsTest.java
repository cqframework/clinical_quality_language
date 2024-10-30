package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

class CqlAggregateFunctionsTest extends CqlTestBase {

    @Test
    void all_aggregate_function_tests() {
        Object value;

        try {
            value = AnyTrueEvaluator.anyTrue(Arrays.asList("this", "is", "error"));
            fail();
        } catch (InvalidOperatorArgument e) {
            // pass
        }

        try {
            value = AvgEvaluator.avg(Arrays.asList("this", "is", "error"), engine.getState());
            fail();
        } catch (InvalidOperatorArgument e) {
            // pass
        }
    }
}
