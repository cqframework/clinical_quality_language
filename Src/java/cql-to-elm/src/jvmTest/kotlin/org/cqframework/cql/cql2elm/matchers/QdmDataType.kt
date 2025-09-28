package org.cqframework.cql.cql2elm.matchers

import javax.xml.namespace.QName
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

/** Created by Bryn on 9/26/2018. */
class QdmDataType(fullName: String) : TypeSafeDiagnosingMatcher<QName>() {
    private val expectedValue: QName = QName("urn:healthit-gov:qdm:v5_3", fullName, "")

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
            mismatchDescription.appendText("had wrong prefix: ").appendText(item.getPrefix())
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
        fun qdmDataType(fullName: String): Matcher<QName?> {
            return QdmDataType(fullName)
        }

        @JvmStatic
        @Factory
        fun qdmDataType(topic: String?, modality: String?): Matcher<QName?> {
            return QdmDataType(topic + modality + "Occurrence")
        }

        @JvmStatic
        @Factory
        fun qdmDataType(topic: String?, modality: String?, occurrence: String?): Matcher<QName?> {
            return QdmDataType(topic + modality + occurrence)
        }
    }
}
