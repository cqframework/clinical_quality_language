package org.cqframework.cql.cql2elm.matchers

import java.math.BigDecimal
import javax.xml.namespace.QName
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.ObjectFactory

class LiteralFor : TypeSafeDiagnosingMatcher<Expression> {
    private val expectedValue: Literal

    constructor(b: Boolean?) : super() {
        expectedValue =
            ObjectFactory()
                .createLiteral()
                .withValueType(QName("urn:hl7-org:elm-types:r1", "Boolean"))
                .withValue(b.toString())
    }

    constructor(s: String?) : super() {
        expectedValue =
            ObjectFactory()
                .createLiteral()
                .withValueType(QName("urn:hl7-org:elm-types:r1", "String"))
                .withValue(s)
    }

    constructor(i: Int?) : super() {
        expectedValue =
            ObjectFactory()
                .createLiteral()
                .withValueType(QName("urn:hl7-org:elm-types:r1", "Integer"))
                .withValue(i.toString())
    }

    constructor(d: BigDecimal?) : super() {
        expectedValue =
            ObjectFactory()
                .createLiteral()
                .withValueType(QName("urn:hl7-org:elm-types:r1", "Decimal"))
                .withValue(d.toString())
    }

    @Suppress("ReturnCount")
    override fun matchesSafely(item: Expression, mismatchDescription: Description): Boolean {
        if (item !is Literal) {
            mismatchDescription
                .appendText("had wrong ELM class type: ")
                .appendText(item.javaClass.getName())
            return false
        }

        if (expectedValue.valueType!! != item.valueType) {
            mismatchDescription.appendText("had wrong type: ").appendValue(item.valueType)
            return false
        }

        if (!expectedValue.value.equals(item.value)) {
            mismatchDescription
                .appendText("had wrong value: <")
                .appendText(item.value)
                .appendText(">")
            return false
        }

        return true
    }

    override fun describeTo(description: Description) {
        description
            .appendText("Literal w/ value: <")
            .appendText(expectedValue.value)
            .appendText("> and type: ")
            .appendValue(expectedValue.valueType)
    }

    @Suppress("MemberNameEqualsClassName")
    companion object {
        @JvmStatic
        @Factory
        fun literalFor(b: Boolean): Matcher<Expression> {
            return LiteralFor(b)
        }

        @JvmStatic
        @Factory
        fun literalFor(s: String): Matcher<Expression> {
            return LiteralFor(s)
        }

        @JvmStatic
        @Factory
        fun literalFor(i: Int): Matcher<Expression> {
            return LiteralFor(i)
        }

        @JvmStatic
        @Factory
        fun literalFor(d: Double): Matcher<Expression> {
            return LiteralFor(BigDecimal.valueOf(d))
        }
    }
}
