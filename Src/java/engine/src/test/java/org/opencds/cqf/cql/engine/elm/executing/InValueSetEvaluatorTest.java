package org.opencds.cqf.cql.engine.elm.executing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

public class InValueSetEvaluatorTest {

    @Test
    void issue1469FalseOnNullCode() {
        var env = new Environment(null);
        var state = new State(env);
        var valueSet = new ValueSet();

        Object actual = InValueSetEvaluator.inValueSet(null, valueSet, state);
        assertInstanceOf(Boolean.class, actual);
        assertFalse((Boolean) actual);
    }
}
