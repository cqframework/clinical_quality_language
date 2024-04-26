package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.external.MyMath2;

class CqlExternalFunctionsTest2 extends CqlTestBase {

    @Test
    void externalFunctions() {
        VersionedIdentifier identifier = toElmIdentifier("CqlExternalFunctionsTest2");

        engine.getEnvironment()
                .registerExternalFunctionProvider(
                        identifier,
                        new SystemExternalFunctionProvider(Arrays.asList(MyMath2.class.getDeclaredMethods())));

        var results = engine.evaluate(identifier);
        var value = results.forExpression("CallMyTimes").value();
        assertThat(value, is(54));

        value = results.forExpression("CallMyDividedBy").value();
        assertThat(value, is(6));
    }
}
