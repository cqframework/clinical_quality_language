package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("removal")
class CqlConditionalOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlConditionalOperatorsTest");

    @Test
    void all_conditional_operators_tests() throws IOException {
        var value = engine.expression(library, "IfTrue1").value();
        assertThat(value, is(5));

        value = engine.expression(library, "IfFalse1").value();
        assertThat(value, is(5));

        value = engine.expression(library, "IfNull1").value();
        assertThat(value, is(10));
        value = engine.expression(library, "StandardCase1").value();
        assertThat(value, is(5));

        value = engine.expression(library, "StandardCase2").value();
        assertThat(value, is(5));

        value = engine.expression(library, "StandardCase3").value();
        assertThat(value, is(15));
    }
}
