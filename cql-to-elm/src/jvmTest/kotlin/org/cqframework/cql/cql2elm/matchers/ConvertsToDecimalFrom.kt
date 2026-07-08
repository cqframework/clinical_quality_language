package org.cqframework.cql.cql2elm.matchers

import javax.xml.namespace.QName
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ObjectFactory
import org.hl7.elm.r1.ToDecimal

class ConvertsToDecimalFrom : TypeSafeDiagnosingMatcher<Expression> {
    private val expectedArg: Any?
    private val expectedValue: ToDecimal

    constructor(i: Int?) : super() {
        expectedArg = i

        val of = ObjectFactory()
        val integerLiteral =
            of.createLiteral()
                .withValueType(QName("urn:hl7-org:elm-types:r1", "Integer"))
                .withValue(i.toString())

        expectedValue = of.createToDecimal().withOperand(integerLiteral)
    }

    constructor(a: AliasRef?) : super() {
        expectedArg = a
        val of = ObjectFactory()
        expectedValue = of.createToDecimal().withOperand(a)
    }

    @Suppress("ReturnCount")
    override fun matchesSafely(item: Expression, mismatchDescription: Description): Boolean {
        if (item !is ToDecimal) {
            mismatchDescription
                .appendText("had wrong ELM class type: ")
                .appendText(item.javaClass.simpleName)
            return false
        }

        if (expectedValue != item) {
            mismatchDescription.appendText("had wrong conversion: ").appendValue(item)
            return false
        }

        return true
    }

    override fun describeTo(description: Description) {
        description.appendText("Conversion to Decimal w/ value: <").appendValue(expectedArg)
    }

    @SuppressWarnings("MemberNameEqualsClassName")
    companion object {
        @JvmStatic
        @Factory
        fun convertsToDecimalFrom(i: Int): Matcher<Expression> {
            return ConvertsToDecimalFrom(i)
        }

        @JvmStatic
        fun convertsToDecimalFromAlias(alias: String): Matcher<Expression> {
            val a = ObjectFactory().createAliasRef().withName(alias)
            return ConvertsToDecimalFrom(a)
        }
    }
}
