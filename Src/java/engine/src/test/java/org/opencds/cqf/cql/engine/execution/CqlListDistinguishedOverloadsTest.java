package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;

import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

@SuppressWarnings("removal")
public class CqlListDistinguishedOverloadsTest extends CqlTestBase {

    private static final VersionedIdentifier library =
            new VersionedIdentifier().withId("CqlListDistinguishedOverloads");

    @Test
    @Ignore("There's a bug in the cql engine that is causing it to select the wrong function overload at runtime")
    public void test_list_overload() {
        var value = engine.expression(library, "Test").value();
        assertEquals(value, "1, 2, 3, 4, 5");
    }
}
