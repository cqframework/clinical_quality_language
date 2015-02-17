package org.cqframework.cql.cql2elm.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Literal;
import org.hl7.elm.r1.ObjectFactory;

import javax.xml.namespace.QName;
import java.math.BigDecimal;

public class LiteralFor extends TypeSafeDiagnosingMatcher<Expression> {
    private Literal expectedValue;

    public LiteralFor(Boolean b) {
        super();

        expectedValue = new ObjectFactory().createLiteral()
                .withValueType(new QName("urn:hl7-org:elm:r1", "Boolean"))
                .withValue(String.valueOf(b));
    }

    public LiteralFor(String s) {
        super();

        expectedValue = new ObjectFactory().createLiteral()
                .withValueType(new QName("urn:hl7-org:elm:r1", "String"))
                .withValue(s);
    }

    public LiteralFor(Integer i) {
        super();

        expectedValue = new ObjectFactory().createLiteral()
                .withValueType(new QName("urn:hl7-org:elm:r1", "Integer"))
                .withValue(String.valueOf(i));
    }

    public LiteralFor(BigDecimal d) {
        super();

        expectedValue = new ObjectFactory().createLiteral()
                .withValueType(new QName("urn:hl7-org:elm:r1", "Decimal"))
                .withValue(String.valueOf(d));
    }

    @Override
    protected boolean matchesSafely(Expression item, Description mismatchDescription) {
        if (! (item instanceof Literal)) {
            mismatchDescription.appendText("had wrong ELM class type: ").appendText(item.getClass().getName());
            return false;
        }

        Literal literal = (Literal) item;
        if (! expectedValue.getValueType().equals(literal.getValueType())) {
            mismatchDescription.appendText("had wrong type: ").appendValue(literal.getValueType());
            return false;
        }

        if (! expectedValue.getValue().equals(literal.getValue())) {
            mismatchDescription.appendText("had wrong value: <").appendText(literal.getValue()).appendText(">");
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Literal w/ value: <")
                .appendText(expectedValue.getValue())
                .appendText("> and type: ")
                .appendValue(expectedValue.getValueType());
    }



    @Factory
    public static <T> Matcher<Expression> literalFor(Boolean b) {
        return new LiteralFor(b);
    }

    @Factory
    public static <T> Matcher<Expression> literalFor(String s) {
        return new LiteralFor(s);
    }

    @Factory
    public static <T> Matcher<Expression> literalFor(Integer i) {
        return new LiteralFor(i);
    }

    @Factory
    public static <T> Matcher<Expression> literalFor(Double d) {
        return new LiteralFor(BigDecimal.valueOf(d));
    }
}
