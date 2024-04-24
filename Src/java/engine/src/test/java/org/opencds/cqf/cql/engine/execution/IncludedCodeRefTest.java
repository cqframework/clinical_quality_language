package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Code;

class IncludedCodeRefTest extends CqlTestBase {

    @Test
    void included_code_ref() {
        var results = engine.evaluate(toElmIdentifier("IncludedCodeRefTest"));
        var value = results.forExpression("IncludedCode").value();
        assertNotNull(value);
        assertThat(value, is(instanceOf(Code.class)));
    }
}
