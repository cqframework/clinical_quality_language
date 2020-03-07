package org.cqframework.cql.cql2elm.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.xml.namespace.QName;

public class Quick2DataType extends TypeSafeDiagnosingMatcher<QName> {
    private QName expectedValue;

    public Quick2DataType(String fullName) {
        super();

        expectedValue = new QName("http://hl7.org/fhir/us/qicore", fullName, "");
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
    public static <T> Matcher<QName> quick2DataType(String fullName) {
        return new Quick2DataType(fullName);
    }

    @Factory
    public static <T> Matcher<QName> quick2DataType(String topic, String modality) {
        return new Quick2DataType(topic + modality + "Occurrence");
    }

    @Factory
    public static <T> Matcher<QName> quick2DataType(String topic, String modality, String occurrence) {
        return new Quick2DataType(topic + modality + occurrence);
    }
}
