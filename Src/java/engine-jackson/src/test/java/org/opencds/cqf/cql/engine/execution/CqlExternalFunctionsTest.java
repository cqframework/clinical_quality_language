package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.external.MyMath;
import org.testng.annotations.Test;

public class CqlExternalFunctionsTest extends CqlExecutionTestBase {

    @Test
    public void testExternalFunctions() {
        Context context = new Context(library);

        context.registerExternalFunctionProvider(
            library.getIdentifier(),
            new SystemExternalFunctionProvider(Arrays.asList(MyMath.class.getDeclaredMethods()))
        );

        Object result;

        result = context.resolveExpressionRef("CallMyPlus").getExpression().evaluate(context);
        assertThat(result, is(10));

        result = context.resolveExpressionRef("CallMyMinus").getExpression().evaluate(context);
        assertThat(result, is(-2));
    }
}
