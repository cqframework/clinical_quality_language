package org.opencds.cqf.cql.engine.execution;

import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("removal")
class CqlTimeZoneTests extends CqlTestBase {
    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlTimeZoneTests");

    @MethodSource("timezones")
    @ParameterizedTest
    void expressionsProblematicForWeirdTimezones(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        /* spell-checker: disable */
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        engine.getState().setEvaluationDateTime(ZonedDateTime.now());

        try {

            evaluateExpression("in interval 1", null);
            evaluateExpression("in interval 2", true);
            evaluateExpression("in interval 3", true);
            evaluateExpression("in interval 4", null);
            // This is a DateTime compared to an Interval<DateTime>
            evaluateExpression("in interval 5", true);
            evaluateExpression("in interval 6", true);

            evaluateExpression("After_SameHour", false);
            evaluateExpression("SameAs_SameHour", true);
            evaluateExpression("SameOrAfter_HourBefore", false);
            evaluateExpression("SameOrBefore_SameHour", true);
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }

    private void evaluateExpression(String functionName, Boolean expectedResult) {
        var value = engine.expression(library, functionName).value();
        assertEquals(expectedResult, value, functionName);
    }
}
