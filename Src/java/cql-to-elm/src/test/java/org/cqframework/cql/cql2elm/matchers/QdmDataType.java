package org.cqframework.cql.cql2elm.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.xml.namespace.QName;

/**
 * Created by Bryn on 9/26/2018.
 */
public class QdmDataType extends TypeSafeDiagnosingMatcher<QName> {
    private QName expectedValue;

    public QdmDataType(String fullName) {
        super();

        expectedValue = new QName("urn:healthit-gov:qdm:v5_3", fullName, "");
    }

    @Override
    protected boolean matchesSafely(QName item, Description mismatchDescription) {
        if (! expectedValue.getNamespaceURI().equals(item.getNamespaceURI())) {
            mismatchDescription.appendText("had wrong namespace: ").appendValue(item.getNamespaceURI());
            return false;
        }

        if (! expectedValue.getLocalPart().equals(item.getLocalPart())) {
            mismatchDescription.appendText("had wrong local part: ").appendText(item.getLocalPart());
            return false;
        }

        if (! expectedValue.getPrefix().equals(item.getPrefix())) {
            mismatchDescription.appendText("had wrong prefix: ").appendText(item.getPrefix());
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedValue);
    }

    @Factory
    public static <T> Matcher<QName> qdmDataType(String fullName) {
        return new QdmDataType(fullName);
    }

    @Factory
    public static <T> Matcher<QName> qdmDataType(String topic, String modality) {
        return new QdmDataType(topic + modality + "Occurrence");
    }

    @Factory
    public static <T> Matcher<QName> qdmDataType(String topic, String modality, String occurrence) {
        return new QdmDataType(topic + modality + occurrence);
    }
}
