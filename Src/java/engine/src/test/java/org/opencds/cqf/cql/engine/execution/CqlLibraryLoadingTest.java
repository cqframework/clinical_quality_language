package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.cqframework.cql.cql2elm.CqlIncludeException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlLibraryLoadingTest extends CqlTestBase {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void missing_identifier_throws_error() {
        engine.evaluate((VersionedIdentifier) null);
    }

    @Test
    public void missing_library_throws_error() {
        try {
            engine.evaluate(new VersionedIdentifier().withId("Not a library"));
            Assert.fail();
        } catch (CqlIncludeException e) {
            assertThat(e.getMessage(), containsString("not load source"));
        }
    }

    @Test
    public void bad_library_throws_error() {
        try {
            engine.evaluate(new VersionedIdentifier().withId("CqlLibraryLoadingTest"));
            Assert.fail();
        } catch (CqlException e) {
            assertThat(e.getMessage(), containsString("loaded, but had errors"));
        }
    }
}
