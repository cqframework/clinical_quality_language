package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.external.MyMath;
import org.testng.annotations.Test;

public class CqlExternalFunctionsTest extends CqlTestBase {

    @Test
    public void testExternalFunctions() {
        VersionedIdentifier identifier = toElmIdentifier("CqlExternalFunctionsTest");

        engine.getState()
                .getEnvironment()
                .registerExternalFunctionProvider(
                        identifier,
                        new SystemExternalFunctionProvider(Arrays.asList(MyMath.class.getDeclaredMethods())));

        EvaluationResult evaluationResult = engine.evaluate(identifier);

        Object result;

        result = evaluationResult.forExpression("CallMyPlus").value();
        assertThat(result, is(10));

        result = evaluationResult.forExpression("CallMyMinus").value();
        assertThat(result, is(-2));
    }
}
