package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.testng.annotations.Test;

public class IncludedCodeRefTest extends CqlExecutionTestBase {
    @Test
    public void testCodeRef() {
        Context context = new Context(library);
        context.registerLibraryLoader(new TestLibraryLoader(getLibraryManager()));

        Object result = context.resolveExpressionRef("IncludedCode").getExpression().evaluate(context);
        assertNotNull(result);
        assertThat(result, is(instanceOf(Code.class)));
    }
}