package org.cqframework.cql.cql2elm.matchers;

import org.cqframework.cql.cql2elm.tracking.Trackable;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hl7.elm.r1.ExpressionDef;

public class HasTypeAndResult extends TypeSafeDiagnosingMatcher<ExpressionDef> {
    private Class expectedType;
    private String expectedResult;

    public HasTypeAndResult(Class t, String r) {
        super();

        expectedType = t;
        expectedResult = r;
    }

    @Override
    protected boolean matchesSafely(ExpressionDef item, Description mismatchDescription) {
        if (!(expectedType.isInstance(item.getExpression()))) {
            mismatchDescription
                    .appendText("had wrong type: ")
                    .appendText(item.getExpression().getClass().getName());
            return false;
        }

        var type = Trackable.INSTANCE.getResultType(item);
        if (type == null) {
            mismatchDescription.appendText("had null result type");
            return false;
        } else if (!type.toString().equals(expectedResult)) {
            mismatchDescription.appendText("had wrong result: ").appendText(type.toString());
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("ExpressionDef w/ type: <")
                .appendText(expectedType.getName())
                .appendText("> and result: <")
                .appendText(expectedResult)
                .appendText(">");
    }

    @Factory
    public static <T> Matcher<ExpressionDef> hasTypeAndResult(Class t, Class r) {
        return new HasTypeAndResult(t, r.getName());
    }

    @Factory
    public static <T> Matcher<ExpressionDef> hasTypeAndResult(Class t, String r) {
        return new HasTypeAndResult(t, r);
    }
}
