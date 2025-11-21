package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import java.util.*
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class CqlTimeZoneTests : CqlTestBase() {
    @MethodSource("timezones")
    @ParameterizedTest
    fun expressionsProblematicForWeirdTimezones(timezone: String?) {
        val oldTz = System.getProperty("user.timezone")
        /* spell-checker: disable */
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT
        // work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone))
        engine.state.setEvaluationDateTime(ZonedDateTime.now())

        try {
            evaluateExpression("in interval 1", null)
            evaluateExpression("in interval 2", true)
            evaluateExpression("in interval 3", true)
            evaluateExpression("in interval 4", null)
            // This is a DateTime compared to an Interval<DateTime>
            evaluateExpression("in interval 5", true)
            evaluateExpression("in interval 6", true)

            evaluateExpression("After_SameHour", false)
            evaluateExpression("SameAs_SameHour", true)
            evaluateExpression("SameOrAfter_HourBefore", false)
            evaluateExpression("SameOrBefore_SameHour", true)
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz))
        }
    }

    private fun evaluateExpression(functionName: String, expectedResult: Boolean?) {
        val value = engine.expression(library, functionName)
        Assertions.assertEquals(expectedResult, value, functionName)
    }

    companion object {
        private val library = VersionedIdentifier().withId("CqlTimeZoneTests")
    }
}
