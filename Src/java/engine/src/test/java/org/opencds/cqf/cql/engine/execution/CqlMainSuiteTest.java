package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

import java.time.ZonedDateTime;
import java.util.*;

public class CqlMainSuiteTest extends CqlTestBase {

    @Test
    public void test_cql_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        var options = CqlCompilerOptions.defaultOptions();
        // The test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListDemotion);
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListPromotion);
        this.getLibrary(toElmIdentifier("CqlTestSuite"), errors, options);
        assertFalse(CqlCompilerException.hasErrors(errors), String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    public void test_all_portable_cql_engine_tests() {
        var result = engine.evaluate(toElmIdentifier("CqlTestSuite"), ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));

        for (Map.Entry entry : result.expressionResults.entrySet()) {
            if(entry.getKey().toString().startsWith("test")) {
                if(((ExpressionResult)entry.getValue()).value() != null) {
                Assert.assertEquals(
                        (String) ((ExpressionResult) entry.getValue()).value(),
                        entry.getKey().toString().replaceAll("test_", "") + " TEST PASSED"
                );
                }
            }
        }

    }

    String toString(List<CqlCompilerException> errors) {
        StringBuilder builder = new StringBuilder();

        for (var e : errors) {
            builder.append(e.toString() + System.lineSeparator());
            if (e.getLocator() != null) {
                builder.append("at" + System.lineSeparator());
                builder.append(e.getLocator().toLocator() + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
