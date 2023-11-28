package org.opencds.cqf.cql.engine.execution;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import org.cqframework.cql.cql2elm.*;
import org.hl7.elm.r1.Library;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

public class CqlTestBase {
    static final String NORTH_AMERICA_MOUNTAIN = "America/Denver"; // This is the baseline:  Normal hour on the hour timezone
    static final String NEWFOUNDLAND = "America/St_Johns";
    static final String INDIA = "Asia/Kolkata";
    static final String AUSTRALIA_NORTHERN_TERRITORY = "Australia/Darwin";
    static final String AUSTRALIA_EUCLA= "Australia/Eucla";
    static final String AUSTRALIA_BROKEN_HILL = "Australia/Broken_Hill";
    static final String AUSTRALIA_LORD_HOWE = "Australia/Lord_Howe";
    static final String AUSTRALIA_SOUTH = "Australia/Adelaide";
    static final String INDIAN_COCOS = "Indian/Cocos";
    static final String PACIFIC_CHATHAM = "Pacific/Chatham";

    @DataProvider
    static Object[][] timezones() {
        return new Object[][] {{NORTH_AMERICA_MOUNTAIN},{NEWFOUNDLAND},{INDIA},{AUSTRALIA_NORTHERN_TERRITORY},{AUSTRALIA_EUCLA},{AUSTRALIA_BROKEN_HILL},{AUSTRALIA_LORD_HOWE},{AUSTRALIA_SOUTH},{INDIAN_COCOS},{PACIFIC_CHATHAM}};
    }

    private static ModelManager modelManager;
    protected static ModelManager getModelManager() {
        if (modelManager == null) {
            modelManager = new ModelManager();
        }

        return modelManager;
    }

    protected static LibraryManager getLibraryManager() {
        return getLibraryManager(createOptionsMin());
    }

    protected static LibraryManager getLibraryManager(CqlCompilerOptions compilerOptions) {
        var manager = new LibraryManager(getModelManager(), compilerOptions);
        manager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        return manager;
    }

    public Library getLibrary(org.hl7.elm.r1.VersionedIdentifier libraryId, List<CqlCompilerException> errors, CqlCompilerOptions options) {
        return getLibraryManager(options).resolveLibrary(libraryId, errors).getLibrary();
    }


    public Library getLibrary(org.hl7.elm.r1.VersionedIdentifier libraryId, List<CqlCompilerException> errors) {
        return environment.getLibraryManager().resolveLibrary(libraryId, errors).getLibrary();
    }

    public Library getLibrary(org.hl7.elm.r1.VersionedIdentifier libraryId) {
        return this.getLibrary(libraryId, null);
    }

    public Library toLibrary(String text) {
        return toLibrary(text, getLibraryManager());
    }

    public Library toLibrary(String text, LibraryManager libraryManager) {
        CqlTranslator translator = CqlTranslator.fromText(text, libraryManager);
        return translator.toELM();
    }

    public static  org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    public static CqlEngine getEngine(CqlCompilerOptions cqlCompilerOptions) {
        var env = new Environment(getLibraryManager(cqlCompilerOptions));
        return new CqlEngine(env);
    }

    Environment environment;
    CqlEngine engine;
    @BeforeMethod
    protected void beforeEachMethod(){
        environment = new Environment(getLibraryManager());
        engine = new CqlEngine(environment);
    }

    public static CqlCompilerOptions createOptionsMin() {
        CqlCompilerOptions result = new CqlCompilerOptions();
        result.setOptions(CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation);

        return result;
    }

    public BigDecimal getBigDecimalZoneOffset() {
        return getBigDecimalZoneOffset(engine.getState().getEvaluationZonedDateTime().getOffset());
    }

    public BigDecimal getBigDecimalZoneOffset(ZoneId zoneId) {
        return getBigDecimalZoneOffset(zoneId.getRules().getOffset(Instant.now()));
    }

    private BigDecimal getBigDecimalZoneOffset(ZoneOffset zoneOffset) {
        return TemporalHelper.zoneToOffset(zoneOffset);
    }
}
