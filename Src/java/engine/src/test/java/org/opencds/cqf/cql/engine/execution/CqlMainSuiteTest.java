package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

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
            if(entry.getKey().startsWith("test")) {
                if(entry.getValue().value() != null) {
                Assert.assertEquals(
                        (String) entry.getValue().value(),
                        entry.getKey().replaceAll("test_", "") + " TEST PASSED"
                );
                }
            }
        }
    }

    @Test
    public void testOverloadsNoEnableAnnotations() {
        var e = getEngine(testCompilerOptionsNoEnableAnnotations());

        assertThrows(CqlCompilerException.class, () -> e.evaluate(toElmIdentifier("CqlOverloadTests"), evalTime));
    }

    @Test
    public void testGenericOverloadsNoEnableAnnotations() {
        var e = getEngine(testCompilerOptionsNoEnableAnnotations());

        assertThrows(CqlCompilerException.class, () -> e.evaluate(toElmIdentifier("CqlGenericOverloadTests"), evalTime));
    }

    // LUKETODO:  play around with different compile options such as removing EnableAnnotations, and adding EnableResultTypes
   protected CqlCompilerOptions testCompilerOptions() {
       return testCompilerOptions(CqlCompilerOptions.Options.DisableListDemotion,
               CqlCompilerOptions.Options.DisableListPromotion);
    }

    protected CqlCompilerOptions testCompilerOptionsNoEnableAnnotations() {
        return testCompilerOptions(CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.EnableAnnotations);
    }

    protected CqlCompilerOptions testCompilerOptions(CqlCompilerOptions.Options... optionsToRemove) {
        final CqlCompilerOptions options = CqlCompilerOptions.defaultOptions();
        final Set<CqlCompilerOptions.Options> optionsOptions = options.getOptions();

        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        Arrays.stream(optionsToRemove).forEach(optionsOptions::remove);

        return options;
    }


    private String toString(List<CqlCompilerException> errors) {
        StringBuilder builder = new StringBuilder();

        for (var e : errors) {
            builder.append(e.toString()).append(System.lineSeparator());
            if (e.getLocator() != null) {
                builder.append("at").append(System.lineSeparator());
                builder.append(e.getLocator().toLocator()).append(System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
