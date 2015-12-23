package org.cqframework.cql.cql2elm.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hl7.elm.r1.AliasRef;
import org.hl7.elm.r1.Convert;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Literal;
import org.hl7.elm.r1.ObjectFactory;

import javax.xml.namespace.QName;

public class ConvertsToDecimalFrom extends TypeSafeDiagnosingMatcher<Expression> {
    private Object expectedArg;
    private Convert expectedValue;

    public ConvertsToDecimalFrom(Integer i) {
        super();

        expectedArg = i;

        ObjectFactory of = new ObjectFactory();
        Literal integerLiteral = of.createLiteral()
                .withValueType(new QName("urn:hl7-org:elm-types:r1", "Integer"))
                .withValue(String.valueOf(i));

<<<<<<< adeeb4811d63b61ed4945063119d72d1ce2a9fa2
        QName expectedTypeName = new QName("urn:hl7-org:elm-types:r1", "Decimal");
        expectedValue = of.createConvert()
                .withToType(expectedTypeName)
                .withToTypeSpecifier(of.createNamedTypeSpecifier().withName(expectedTypeName))
=======
        expectedValue = of.createConvert()
                .withToType(new QName("urn:hl7-org:elm-types:r1", "Decimal"))
>>>>>>> All conversions use Convert ELM operator (instead of 'ToX' functions).  Also add tests for other type operators.  NOTE: This breaks the execution engine because it doesn't currently support Convert.
                .withOperand(integerLiteral);
    }

    public ConvertsToDecimalFrom(AliasRef a) {
        super();

        expectedArg = a;
<<<<<<< adeeb4811d63b61ed4945063119d72d1ce2a9fa2
        ObjectFactory of = new ObjectFactory();
        QName expectedTypeName = new QName("urn:hl7-org:elm-types:r1", "Decimal");
        expectedValue = of.createConvert()
                .withToType(expectedTypeName)
                .withToTypeSpecifier(of.createNamedTypeSpecifier().withName(expectedTypeName))
=======
        expectedValue = new ObjectFactory().createConvert()
                .withToType(new QName("urn:hl7-org:elm-types:r1", "Decimal"))
>>>>>>> All conversions use Convert ELM operator (instead of 'ToX' functions).  Also add tests for other type operators.  NOTE: This breaks the execution engine because it doesn't currently support Convert.
                .withOperand(a);
    }

    @Override
    protected boolean matchesSafely(Expression item, Description mismatchDescription) {
        if (! (item instanceof Convert)) {
            mismatchDescription.appendText("had wrong ELM class type: ").appendText(item.getClass().getName());
            return false;
        }

        Convert convert = (Convert) item;
        if (! expectedValue.equals(convert)) {
            mismatchDescription.appendText("had wrong conversion: ").appendValue(convert);
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Conversion to Decimal w/ value: <")
                .appendValue(expectedArg);
    }

    @Factory
    public static <T> Matcher<Expression> convertsToDecimalFrom(Integer i) {
        return new ConvertsToDecimalFrom(i);
    }

    public static <T> Matcher<Expression> convertsToDecimalFromAlias(String alias) {
        AliasRef a = new ObjectFactory().createAliasRef().withName(alias);
        return new ConvertsToDecimalFrom(a);
    }
}
