package org.cqframework.cql.cql2js.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ElmType extends TypeSafeDiagnosingMatcher<JsonNode> {
    private String expectedType;
    private Integer expectedNumOperands;

    public ElmType(String type, Integer numOperands) {
        super();

        expectedType = type;
        expectedNumOperands = numOperands;
    }

    public ElmType(String type) {
        this(type, null);
    }

    @Override
    protected boolean matchesSafely(JsonNode item, Description mismatchDescription) {
        String type = item.path("type").asText();
        boolean hasOpArray = item.path("operand").isArray();
        int numOps = item.path("operand").size();

        if (! expectedType.equals(type) || expectedNumOperands != null && (!hasOpArray || expectedNumOperands != numOps)) {
            mismatchDescription.appendText("type: " + type);
            mismatchDescription.appendText("\nnumOperands: " + (hasOpArray ? numOps : "n/a"));
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("type: " + expectedType);
        if (expectedNumOperands != null) {
            description.appendText("\nnumOperands: " + expectedNumOperands);
        }
    }

    @Factory
    public static <T> Matcher<JsonNode> hasElmType(String type) {
        return new ElmType(type);
    }

    public static <T> Matcher<JsonNode> hasElmTypeWithOps(String type, int numOps) {
        return new ElmType(type, numOps);
    }
}
