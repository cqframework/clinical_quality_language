package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.Test;

@SuppressWarnings("removal")
public class CqlConditionalOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlConditionalOperatorsTest");

    @Test
    public void test_all_conditional_operators_tests() throws IOException {
        var result = engine.expression(library, "IfTrue1").value();
        assertThat(result, is(5));

        result = engine.expression(library, "IfFalse1").value();
        assertThat(result, is(5));

        result = engine.expression(library, "IfNull1").value();
        assertThat(result, is(10));
        result = engine.expression(library, "StandardCase1").value();
        assertThat(result, is(5));

        result = engine.expression(library, "StandardCase2").value();
        assertThat(result, is(5));

        result = engine.expression(library, "StandardCase3").value();
        assertThat(result, is(15));
    }
}
