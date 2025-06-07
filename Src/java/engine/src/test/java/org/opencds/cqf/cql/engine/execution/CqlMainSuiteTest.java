package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.ZonedDateTime;
import java.util.*;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.junit.jupiter.api.Test;

class CqlMainSuiteTest extends CqlTestBase {

    private static final ZonedDateTime evalTime =
            ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId());

    @Test
    void cql_main_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        this.getLibrary(toElmIdentifier("CqlTestSuite"), errors, testCompilerOptions());
        assertFalse(
                CqlCompilerException.hasErrors(errors),
                String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    void all_portable_cql_engine_tests() {
        var e = getEngine(testCompilerOptions());
        // TODO: It'd be interesting to be able to inspect the
        // possible set of expressions from the CQL engine API
        // prior to evaluating them all
        var result = e.evaluate(toElmIdentifier("CqlTestSuite"), evalTime);

        for (var entry : result.expressionResults.entrySet()) {
            if (entry.getKey().toString().startsWith("test")) {
                if (((ExpressionResult) entry.getValue()).value() != null) {
                    assertEquals(
                            (String) ((ExpressionResult) entry.getValue()).value(),
                            entry.getKey().toString().replaceAll("test_", "") + " TEST PASSED");
                }
            }
        }
    }

    @Test
    void cql_timezone_tests() {
        var e = getEngine(testCompilerOptions());
        // TODO: It'd be interesting to be able to inspect the
        // possible set of expressions from the CQL engine API
        // prior to evaluating them all

        var result = e.evaluate(toElmIdentifier("CqlTimeZoneTestSuite"), evalTime);

        for (var entry : result.expressionResults.entrySet()) {
            if (entry.getKey().toString().startsWith("test")) {
                if (((ExpressionResult) entry.getValue()).value() != null) {
                    assertEquals(
                            (String) ((ExpressionResult) entry.getValue()).value(),
                            entry.getKey().toString().replaceAll("test_", "") + " TEST PASSED");
                }
            }
        }
    }

    protected CqlCompilerOptions testCompilerOptions() {
        var options = CqlCompilerOptions.defaultOptions();
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListDemotion);
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListPromotion);

        // When called with the null argument, the toString function in the CqlTestSuite
        // library can only be unambiguously resolved at runtime if the library is
        // compiled with signature level set to Overloads or All.
        options.withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads);

        return options;
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
