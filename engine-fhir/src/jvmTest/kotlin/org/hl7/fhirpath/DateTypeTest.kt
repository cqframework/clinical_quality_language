package org.hl7.fhirpath

import java.util.*
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.fhir.r4.model.DateType
import org.junit.jupiter.api.Test

internal class DateTypeTest {
    @Test
    fun dateType() {
        // DateType Month is zero-based (11 == December)
        val birthDate = DateType(1974, 11, 25)
        MatcherAssert.assertThat(birthDate.year, Matchers.`is`(1974))
        MatcherAssert.assertThat(birthDate.month, Matchers.`is`(11))
        MatcherAssert.assertThat(birthDate.day, Matchers.`is`(25))
    }

    @Test
    fun date() {
        // NOTE: DateType uses default GMT
        val birthDate = DateType(1974, 11, 25).value
        val calendar = GregorianCalendar(TimeZone.getTimeZone("GMT"))
        calendar.setTime(birthDate)
        MatcherAssert.assertThat(calendar.get(Calendar.YEAR), Matchers.`is`(1974))
        MatcherAssert.assertThat(calendar.get(Calendar.MONTH), Matchers.`is`(11))
        MatcherAssert.assertThat(calendar.get(Calendar.DAY_OF_MONTH), Matchers.`is`(25))
    }
}
