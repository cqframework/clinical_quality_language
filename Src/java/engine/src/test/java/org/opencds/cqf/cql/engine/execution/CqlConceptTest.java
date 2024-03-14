package org.opencds.cqf.cql.engine.execution;

import java.io.IOException;
import java.util.*;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlConceptTest extends CqlTestBase {
    @Test
    public void test_all_cql_concept_tests() throws IOException {

        Environment environment = new Environment(getLibraryManager());
        CqlEngine engine = new CqlEngine(environment);

        Set<String> set = new HashSet<>();

        var results = engine.evaluate(toElmIdentifier("CqlConceptTest"));

        List<Code> codes = Arrays.asList(createCode("123", "1"), createCode("234", "1"), createCode("abc", "a"));
        Concept expected = new Concept().withDisplay("test-concept-display").withCodes(codes);

        CqlType actual = (CqlType) results.forExpression("testConceptRef").value();

        assertEqual(expected, actual);
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
