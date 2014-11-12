package org.cqframework.cql.cql2js.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.math.BigDecimal;

public class LiteralFor extends TypeSafeDiagnosingMatcher<JsonNode> {
    private JsonNode expectedValue;

    public LiteralFor(Boolean b) {
        super();

        expectedValue = new ObjectMapper().createObjectNode()
                .put("type", "Literal")
                .put("valueType", "{http://www.w3.org/2001/XMLSchema}bool")
                .put("value", String.valueOf(b));
    }

    public LiteralFor(String s) {
        super();

        expectedValue = new ObjectMapper().createObjectNode()
                .put("type", "Literal")
                .put("valueType", "{http://www.w3.org/2001/XMLSchema}string")
                .put("value", String.valueOf(s));
    }

    public LiteralFor(Integer i) {
        super();

        expectedValue = new ObjectMapper().createObjectNode()
                .put("type", "Literal")
                .put("valueType", "{http://www.w3.org/2001/XMLSchema}int")
                .put("value", String.valueOf(i));
    }

    public LiteralFor(BigDecimal d) {
        super();

        expectedValue = new ObjectMapper().createObjectNode()
                .put("type", "Literal")
                .put("valueType", "{http://www.w3.org/2001/XMLSchema}decimal")
                .put("value", String.valueOf(d));
    }

    @Override
    protected boolean matchesSafely(JsonNode item, Description mismatchDescription) {
        if (! (expectedValue.equals(item))) {
            mismatchDescription.appendValue(item);
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedValue);
    }



    @Factory
    public static <T> Matcher<JsonNode> literalFor(Boolean b) {
        return new LiteralFor(b);
    }

    @Factory
    public static <T> Matcher<JsonNode> literalFor(String s) {
        return new LiteralFor(s);
    }

    @Factory
    public static <T> Matcher<JsonNode> literalFor(Integer i) {
        return new LiteralFor(i);
    }

    @Factory
    public static <T> Matcher<JsonNode> literalFor(Double d) {
        return new LiteralFor(BigDecimal.valueOf(d));
    }
}
