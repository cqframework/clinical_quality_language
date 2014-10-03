package org.cqframework.cql.cql2js.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class FunctionRef extends ElmType {
    private String expectedName;

    public FunctionRef(String name, Integer numOperands) {
        super("FunctionRef", numOperands);

        expectedName = name;
    }

    public FunctionRef(String name) {
        this(name, null);
    }

    @Override
    protected boolean matchesSafely(JsonNode item, Description mismatchDescription) {
        if (! super.matchesSafely(item, mismatchDescription)) {
            return false;
        } else if (! expectedName.equals(item.path("name").asText())) {
            mismatchDescription.appendText("name: " + item.path("name").asText());
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        super.describeTo(description);
        description.appendText("\nname: " + expectedName);
    }

    @Factory
    public static <T> Matcher<JsonNode> isFunctionRefFor(String type) {
        return new FunctionRef(type);
    }
    @Factory
    public static <T> Matcher<JsonNode> isFunctionRefWithOpsFor(String type, int numOps) {
        return new FunctionRef(type, numOps);
    }
}
