package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
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
        this.getLibrary(toElmIdentifier("CqlTestSuite"), errors, testCompilerOptionsWithBlacklist());
        assertFalse(CqlCompilerException.hasErrors(errors), String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    public void test_all_portable_cql_engine_tests() {
        var e = getEngine(testCompilerOptionsWithBlacklist());
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

    private static final String CQL_OVERLOAD_TESTS_FILE = "CqlOverloadTests";
    private static final String CQL_GENERIC_OVERLOAD_TESTS_FILE = "CqlGenericOverloadTests";

    @DataProvider(name = "testCqlAndCompilerOptions")
    public static Object[][] testCqlAndCompilerOptions() {
        return new Object[][] {
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion)},
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion)},
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion)},
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.EnableAnnotations)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.EnableAnnotations)},
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.EnableResultTypes)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.EnableResultTypes)},
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.EnableAnnotations, CqlCompilerOptions.Options.EnableResultTypes)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.EnableAnnotations, CqlCompilerOptions.Options.EnableResultTypes)},
                {CQL_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion, CqlCompilerOptions.Options.DisableDefaultModelInfoLoad)},
                {CQL_GENERIC_OVERLOAD_TESTS_FILE, Set.of(CqlCompilerOptions.Options.DisableListDemotion, CqlCompilerOptions.Options.DisableListPromotion, CqlCompilerOptions.Options.DisableDefaultModelInfoLoad)}
            };
    }

    @Test(dataProvider = "testCqlAndCompilerOptions")
    public void testOverloadsNoEnableAnnotations(String testFile, Collection<CqlCompilerOptions.Options> options) {
        final CqlEngine engine = getEngine(testCompilerOptionsWithWhitelist(options));

        if (options.contains(CqlCompilerOptions.Options.EnableAnnotations) || options.contains(CqlCompilerOptions.Options.EnableResultTypes)) {
            // No Exception thrown
            engine.evaluate(toElmIdentifier(testFile), evalTime);

        } else {
            assertThrows(CqlException.class, () -> engine.evaluate(toElmIdentifier(testFile), evalTime));
        }
    }

    protected CqlCompilerOptions testCompilerOptionsWithWhitelist(Collection<CqlCompilerOptions.Options> options) {
        return new CqlCompilerOptions(options.toArray(new CqlCompilerOptions.Options[0]));
    }

   protected CqlCompilerOptions testCompilerOptionsWithBlacklist() {
       return testCompilerOptionsWithBlacklist(CqlCompilerOptions.Options.DisableListDemotion,
               CqlCompilerOptions.Options.DisableListPromotion);
    }

    protected CqlCompilerOptions testCompilerOptionsWithBlacklist(CqlCompilerOptions.Options... optionsToRemove) {
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
