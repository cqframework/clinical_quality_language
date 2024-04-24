package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("removal")
class CqlListDistinguishedOverloadsTest extends CqlTestBase {

    private static final VersionedIdentifier library =
            new VersionedIdentifier().withId("CqlListDistinguishedOverloads");

    @Test
    @Disabled("There's a bug in the cql engine that is causing it to select the wrong function overload at runtime")
    void list_overload() {
        var value = engine.expression(library, "Test").value();
        assertEquals("1, 2, 3, 4, 5", value);
    }
}
