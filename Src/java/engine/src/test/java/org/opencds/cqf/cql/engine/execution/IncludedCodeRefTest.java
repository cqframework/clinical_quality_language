package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.testng.annotations.Test;

public class IncludedCodeRefTest extends CqlTestBase {

    @Test
    public void test_included_code_ref() {
        var results = engine.evaluate(toElmIdentifier("IncludedCodeRefTest"));
        var value = results.forExpression("IncludedCode").value();
        assertNotNull(value);
        assertThat(value, is(instanceOf(Code.class)));
    }
}
