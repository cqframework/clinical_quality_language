package org.cqframework.cql.cql2elm.matchers

import javax.xml.namespace.QName
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

class Quick2DataType(fullName: String) : TypeSafeDiagnosingMatcher<QName>() {
    private val expectedValue: QName = QName("http://hl7.org/fhir/us/qicore", fullName, "")

    @Suppress("ReturnCount")
    override fun matchesSafely(item: QName, mismatchDescription: Description): Boolean {
        if (expectedValue.namespaceURI != item.namespaceURI) {
            mismatchDescription.appendText("had wrong namespace: ").appendValue(item.namespaceURI)
            return false
        }

        if (expectedValue.localPart != item.localPart) {
            mismatchDescription.appendText("had wrong local part: ").appendText(item.localPart)
            return false
        }

        if (expectedValue.prefix != item.prefix) {
            mismatchDescription.appendText("had wrong prefix: ").appendText(item.prefix)
            return false
        }

        return true
    }

    override fun describeTo(description: Description) {
        description.appendValue(expectedValue)
    }

    @Suppress("MemberNameEqualsClassName")
    companion object {
        @JvmStatic
        @Factory
        fun quick2DataType(fullName: String): Matcher<QName?> {
            return Quick2DataType(fullName)
        }

        @JvmStatic
        @Factory
        fun quick2DataType(topic: String?, modality: String?): Matcher<QName?> {
            return Quick2DataType(topic + modality + "Occurrence")
        }

        @JvmStatic
        @Factory
        fun quick2DataType(
            topic: String?,
            modality: String?,
            occurrence: String?
        ): Matcher<QName?> {
            return Quick2DataType(topic + modality + occurrence)
        }
    }
}
