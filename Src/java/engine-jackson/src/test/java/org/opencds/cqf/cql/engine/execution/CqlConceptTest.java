package org.opencds.cqf.cql.engine.execution;

import java.util.Arrays;
import java.util.List;

import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlConceptTest extends CqlExecutionTestBase {

    @Test
    public void testConceptRef() {
        Context ctx = new Context(library);

        List<Code> codes = Arrays.asList(
                createCode("123", "1"),
                createCode("234", "1"),
                createCode("abc", "a")
        );
        Concept expected = new Concept()
                .withDisplay("test-concept-display")
                .withCodes(codes);

        CqlType actual = (CqlType)ctx.resolveExpressionRef("testConceptRef").getExpression().evaluate(ctx);

        assertEqual(expected, actual);
    }

    @Test
    public void testResolveConceptRef_noConceptFound() {
        Context context = new Context(library);

        try {
            context.resolveConceptRef("invalid-concept");
            Assert.fail("Did not hit expected exception");
        }
        catch (CqlException e) {
            if (!e.getMessage().startsWith("Could not resolve concept reference")) {
                Assert.fail("Unexpected exception message");
            }
        }
    }

    private static Code createCode(String prefix, String systemVal) {
        return new Code()
                .withCode(prefix + "-value")
                .withSystem("http://system-" + systemVal + ".org")
                .withVersion(systemVal)
                .withDisplay(prefix + "-display");
    }

    static void assertEqual(CqlType expected, CqlType actual) {
        if (!expected.equal(actual)) {
            String message = "Expected " + expected + " but got " + actual;
            Assert.fail(message);
        }
    }

}
