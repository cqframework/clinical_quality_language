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

            evaluateExpression("After_SameHour", false, softAssert);
            evaluateExpression("SameAs_SameHour", true, softAssert);
            evaluateExpression("SameOrAfter_HourBefore", false, softAssert);
            evaluateExpression("SameOrBefore_SameHour", true, softAssert);

            softAssert.assertAll();
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }

    /*

define After_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameAs_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameOrAfter_HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameOrBefore_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
     */

    private void evaluateExpression(String functionName, boolean expectedResult, SoftAssert softAssert) {
        Object result = engine.expression(library, functionName).value();
        softAssert.assertEquals(result, expectedResult, functionName);
        logger.info("functionName: {}, expected: {}, actual: {}", functionName, expectedResult, result);
        logger.info("---------------------");
    }

    /*
    WITH FIX:


2000-03-15T13:30:25.200+01:30
2000-03-15T13:14:47.500+01:30
     */

    /*
    WITHOUT FIX:

2000-03-15T13:30:25.200+01:00
2000-03-15T13:14:47.500+01:00
     */
}
