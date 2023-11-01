package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.TimeZone;

@SuppressWarnings("removal")
public class CqlTimezoneTests extends CqlTestBase {
    private static final Logger logger = LoggerFactory.getLogger(CqlTimezoneTests.class);

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlTimezoneTests");

    @Test(dataProvider = "timezones")
    public void testExpressionsProblematicForWeirdTimezones(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        try {
            final SoftAssert softAssert = new SoftAssert();

//            evaluateExpression("in interval 1", false, softAssert);
//            logExpression("the interval");
            // LUKETODO: This is a Date compared to an Interval<DateTime>:  Seems to be only a problem with some Australia timezones (-06:00 vs. -07:00 ?????)
            evaluateExpression("in interval 1", true, softAssert);
//            evaluateExpression("in interval 1.5", true, softAssert);
//            evaluateExpression("in interval 1.6", true, softAssert);
            evaluateExpression("in interval 2", true, softAssert); // good
//            evaluateExpression("in interval 3", false, softAssert);
            evaluateExpression("in interval 3", true, softAssert);
            // LUKETODO: This is a Date compared to an Interval<DateTime>:  Seems to be only a problem with some Australia timezones (+10:30 vs. +09:30 ?????)
            evaluateExpression("in interval 4", true, softAssert);
            // This is a DateTime compared to an Interval<DateTime>
            evaluateExpression("in interval 5", true, softAssert); // good
            evaluateExpression("in interval 6", true, softAssert); // good

            evaluateExpression("After_SameHour", false, softAssert);
            evaluateExpression("SameAs_SameHour", true, softAssert);
            evaluateExpression("SameOrAfter_HourBefore", false, softAssert);
            evaluateExpression("SameOrBefore_SameHour", true, softAssert);

            softAssert.assertAll();
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }

    private void logExpression(String functionName) {
        Object result = engine.expression(library, functionName).value();

        logger.info("result: timezone: [{}], functionName: [{}], type: {}, value: {}", TimeZone.getDefault().getDisplayName(), functionName, result.getClass(), result);
    }

    private void evaluateExpression(String functionName, boolean expectedResult, SoftAssert softAssert) {
        Object result = engine.expression(library, functionName).value();
        logger.info("result: timezone: [{}], functionName: [{}], type: {}, value: {}", TimeZone.getDefault().getDisplayName(), functionName, result.getClass(), result);
        softAssert.assertEquals(result, expectedResult, functionName);
    }

}
