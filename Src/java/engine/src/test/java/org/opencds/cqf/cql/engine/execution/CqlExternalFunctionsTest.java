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

        var results = engine.evaluate(identifier);
        var value = results.forExpression("CallMyPlus").value();
        assertThat(value, is(10));

        value = results.forExpression("CallMyMinus").value();
        assertThat(value, is(-2));
    }
}
