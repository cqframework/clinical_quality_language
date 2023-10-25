package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.VersionedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.TimeZone;

@SuppressWarnings("removal")
public class CqlTimezoneTests {
    private static final Logger logger = LoggerFactory.getLogger(CqlTimezoneTests.class);
    private static final String NEWFOUNDLAND = "America/St_Johns";

    private static ModelManager modelManager;

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlDateTimeOperatorsTest");

    private Environment environment;
    private CqlEngine engine;
    private String oldTz;

    // LUKETODO:  parameterized
    // LUKETODO:  India, Australia Northern Terrirot, Eucia, South Australia, Broken Hill, Lord Howe Island, Coco Islands, Chatham Islands, Sri Lanka, Afghanistan, Iran, Myanmar, Nepal, French Polynesia, Marquesas Islands

    @BeforeMethod
    void beforeEachMethod(){
        oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Dusertimezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(NEWFOUNDLAND));

        environment = new Environment(getLibraryManager());
        engine = new CqlEngine(environment);

    }

    @AfterMethod
    void afterEachMethod(){
        environment = new Environment(getLibraryManager());
        engine = new CqlEngine(environment);

        TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
    }

    // LUKETODO: better name
    @Test
    public void testNewfoundland() {
        final Environment localEnvironment = new Environment(getLibraryManager());
        final CqlEngine localEngine = new CqlEngine(localEnvironment);
        final SoftAssert softAssert = new SoftAssert();

        evaluateExpression(localEngine, "After_SameHour", false, softAssert);
        evaluateExpression(localEngine, "SameAs_SameHour", true, softAssert);
        evaluateExpression(localEngine, "SameOrAfter_HourBefore", false, softAssert);
        evaluateExpression(localEngine, "SameOrBefore_SameHour", true, softAssert);

        softAssert.assertAll();
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
