package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

import java.time.ZonedDateTime;
import java.util.*;

public class CqlMainSuiteTest extends CqlTestBase {

    private static final ZonedDateTime evalTime = ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId());

    @Test
    public void test_cql_main_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        this.getLibrary(toElmIdentifier("CqlTestSuite"), errors, testCompilerOptions());
        assertFalse(CqlCompilerException.hasErrors(errors), String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    public void test_all_portable_cql_engine_tests() {
        var e = getEngine(testCompilerOptions());
        // TODO: It'd be interesting to be able to inspect the
        // possible set of expressions from the CQL engine API
        // prior to evaluating them all
        var result = e.evaluate(toElmIdentifier("CqlTestSuite"), evalTime);

        for (var entry : result.expressionResults.entrySet()) {
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

    @Test
    public void test_cql_timezone_tests() {
        var e = getEngine(testCompilerOptions());
        // TODO: It'd be interesting to be able to inspect the
        // possible set of expressions from the CQL engine API
        // prior to evaluating them all

        var result = e.evaluate(toElmIdentifier("CqlTimeZoneTestSuite"), evalTime);

        for (var entry : result.expressionResults.entrySet()) {
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

   protected CqlCompilerOptions testCompilerOptions() {
        var options = CqlCompilerOptions.defaultOptions();
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListDemotion);
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListPromotion);

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
