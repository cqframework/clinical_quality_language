package org.cqframework.cql.cql2elm.matchers;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.NamedType;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Literal;
import org.hl7.elm.r1.ObjectFactory;

import javax.xml.namespace.QName;
import java.math.BigDecimal;

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
        if (! (expectedType.isInstance(item.getExpression()))) {
            mismatchDescription.appendText("had wrong type: ").appendText(item.getExpression().getClass().getName());
            return false;
        }

        DataType type = item.getResultType();
        if (type instanceof NamedType) {
            String name = ((NamedType) type).getName();
            if (name == null || ! name.equals(expectedResult)) {
                mismatchDescription.appendText("had wrong result: ").appendText(name);
                return false;
            }
        } else {
            mismatchDescription.appendText("had un-named result type");
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ExpressionDef w/ type: <")
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
