package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.TimeZone;

@SuppressWarnings("removal")
public class CqlTimezoneTests extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlTimezoneTests");

    @Test(dataProvider = "timezones")
    public void testExpressionsProblematicForWeirdTimezones(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        try {
            final SoftAssert softAssert = new SoftAssert();

            evaluateExpression("After_SameHour", false, softAssert);
            evaluateExpression("SameAs_SameHour", true, softAssert);
            evaluateExpression("SameOrAfter_HourBefore", false, softAssert);
            evaluateExpression("SameOrBefore_SameHour", true, softAssert);

            softAssert.assertAll();
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }

    private void evaluateExpression(String DateTimeSameOrBeforeTodayTrue1, boolean expectedResult, SoftAssert softAssert) {
        Object result = engine.expression(library, DateTimeSameOrBeforeTodayTrue1).value();
        softAssert.assertEquals(result, expectedResult);
    }
}
