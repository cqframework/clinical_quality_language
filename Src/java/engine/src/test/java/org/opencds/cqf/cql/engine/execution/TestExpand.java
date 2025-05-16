package org.opencds.cqf.cql.engine.execution;

import org.junit.jupiter.api.Test;

class TestExpand extends CqlTestBase {

    @Test
    void expand() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"));
    }
}
