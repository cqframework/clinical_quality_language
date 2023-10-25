package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.TimeZone;

@SuppressWarnings("removal")
public class CqlTimezoneTests {
    private static final String NORTH_AMERICA_MOUNTAIN = "America/Denver"; // This is the baseline:  Normal hour on the hour timezone
    private static final String NEWFOUNDLAND = "America/St_Johns";
    private static final String INDIA = "Asia/Kolkata";
    private static final String AUSTRALIA_NORTHERN_TERRITORY = "Australia/Darwin";
    private static final String AUSTRALIA_EUCLA= "Australia/Eucla";
    private static final String AUSTRALIA_BROKEN_HILL = "Australia/Broken_Hill";
    private static final String AUSTRALIA_LORD_HOWE = "Australia/Lord_Howe";
    private static final String AUSTRALIA_SOUTH = "Australia/Adelaide";

    private static final String INDIAN_COCOS = "Indian/Cocos";
    private static final String PACIFIC_CHATHAM = "Pacific/Chatham";

    private static ModelManager modelManager;

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlTimezoneTests");

    @DataProvider
    private static Object[][] timezones() {
        return new Object[][] {{NORTH_AMERICA_MOUNTAIN},{NEWFOUNDLAND},{INDIA},{AUSTRALIA_NORTHERN_TERRITORY},{AUSTRALIA_EUCLA},{AUSTRALIA_BROKEN_HILL},{AUSTRALIA_LORD_HOWE},{AUSTRALIA_SOUTH},{INDIAN_COCOS},{PACIFIC_CHATHAM}};
    }

    @Test(dataProvider = "timezones")
    public void testExpressionsProblematicForWeirdTimezones(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        try {
            final Environment localEnvironment = new Environment(getLibraryManager());
            final CqlEngine localEngine = new CqlEngine(localEnvironment);
            final SoftAssert softAssert = new SoftAssert();

            evaluateExpression(localEngine, "After_SameHour", false, softAssert);
            evaluateExpression(localEngine, "SameAs_SameHour", true, softAssert);
            evaluateExpression(localEngine, "SameOrAfter_HourBefore", false, softAssert);
            evaluateExpression(localEngine, "SameOrBefore_SameHour", true, softAssert);

            softAssert.assertAll();
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }
    protected static LibraryManager getLibraryManager() {
        return getLibraryManager(createOptionsMin());
    }

    protected static LibraryManager getLibraryManager(CqlCompilerOptions compilerOptions) {
        var manager = new LibraryManager(getModelManager(), compilerOptions);
        manager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        return manager;
    }
    protected static ModelManager getModelManager() {
        if (modelManager == null) {
            modelManager = new ModelManager();
        }

        return modelManager;
    }

    private static CqlCompilerOptions createOptionsMin() {
        CqlCompilerOptions result = new CqlCompilerOptions();
        result.setOptions(CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation);

        return result;
    }

    private static void evaluateExpression(CqlEngine localEngine, String DateTimeSameOrBeforeTodayTrue1, boolean expectedResult, SoftAssert softAssert) {
        Object result = localEngine.expression(library, DateTimeSameOrBeforeTodayTrue1).value();
        softAssert.assertEquals(result, expectedResult);
    }
}
