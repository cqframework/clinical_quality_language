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
import java.util.ArrayList;
import java.util.List;

public class ListOfLiterals extends TypeSafeDiagnosingMatcher<Expression> {
    private List<Literal> expectedValue;

    public ListOfLiterals(Boolean... bools) {
        super();

        expectedValue = new ArrayList<>(bools.length);
        for (Boolean b : bools) {
            expectedValue.add(new ObjectFactory().createLiteral()
                    .withValueType(new QName("urn:hl7-org:elm-types:r1", "Boolean"))
                    .withValue(String.valueOf(b)));
        }
    }

    public ListOfLiterals(String... strings) {
        super();

        expectedValue = new ArrayList<>(strings.length);
        for (String s : strings) {
            expectedValue.add(new ObjectFactory().createLiteral()
                    .withValueType(new QName("urn:hl7-org:elm-types:r1", "String"))
                    .withValue(s));
        }
    }

    public ListOfLiterals(Integer... ints) {
        super();

        expectedValue = new ArrayList<>(ints.length);
        for (Integer i : ints) {
            expectedValue.add(new ObjectFactory().createLiteral()
                    .withValueType(new QName("urn:hl7-org:elm-types:r1", "Integer"))
                    .withValue(String.valueOf(i)));
        }
    }

    public ListOfLiterals(Double... decs) {
        super();

        expectedValue = new ArrayList<>(decs.length);
        for (Double d : decs) {
            expectedValue.add(new ObjectFactory().createLiteral()
                    .withValueType(new QName("urn:hl7-org:elm-types:r1", "Decimal"))
                    .withValue(String.valueOf(d)));
        }
    }

    @Override
    protected boolean matchesSafely(Expression item, Description mismatchDescription) {
        if (! (item instanceof org.hl7.elm.r1.List)) {
            mismatchDescription.appendText("had wrong ELM class type: ").appendText(item.getClass().getName());
            return false;
        }

        org.hl7.elm.r1.List list = (org.hl7.elm.r1.List) item;
        if (! expectedValue.equals(list.getElement())) {
            mismatchDescription.appendText("had wrong elements: ").appendValueList("[ ", " , ", " ]", list.getElement());
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("List w/ elements: ")
                .appendValueList("[ ", " , ", " ]", expectedValue);
    }



    @Factory
    public static <T> Matcher<Expression> listOfLiterals(Boolean... b) {
        return new ListOfLiterals(b);
    }

    @Factory
    public static <T> Matcher<Expression> listOfLiterals(String... s) {
        return new ListOfLiterals(s);
    }

    @Factory
    public static <T> Matcher<Expression> listOfLiterals(Integer... i) {
        return new ListOfLiterals(i);
    }

    @Factory
    public static <T> Matcher<Expression> listOfLiterals(Double... d) {
        return new ListOfLiterals(d);
    }
}
