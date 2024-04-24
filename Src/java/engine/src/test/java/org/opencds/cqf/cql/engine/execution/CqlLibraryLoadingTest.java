package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.cqframework.cql.cql2elm.CqlIncludeException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.exception.CqlException;

class CqlLibraryLoadingTest extends CqlTestBase {

    @Test
    void missing_identifier_throws_error() {
        assertThrows(IllegalArgumentException.class, () -> {
            engine.evaluate((VersionedIdentifier) null);
        });
    }

    @Test
    void missing_library_throws_error() {
        try {
            engine.evaluate(new VersionedIdentifier().withId("Not a library"));
            fail();
        } catch (CqlIncludeException e) {
            assertThat(e.getMessage(), containsString("not load source"));
        }
    }

    @Test
    void bad_library_throws_error() {
        try {
            engine.evaluate(new VersionedIdentifier().withId("CqlLibraryLoadingTest"));
            fail();
        } catch (CqlException e) {
            assertThat(e.getMessage(), containsString("loaded, but had errors"));
        }
    }
}
