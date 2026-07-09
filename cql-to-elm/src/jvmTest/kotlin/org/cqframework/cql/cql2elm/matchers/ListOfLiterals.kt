package org.cqframework.cql.cql2elm.matchers

import javax.xml.namespace.QName
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.List
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.ObjectFactory

class ListOfLiterals : TypeSafeDiagnosingMatcher<Expression> {
    private val expectedValue: MutableList<Literal?>

    constructor(vararg bools: Boolean?) : super() {
        expectedValue = ArrayList<Literal?>(bools.size)
        for (b in bools) {
            expectedValue.add(
                ObjectFactory()
                    .createLiteral()
                    .withValueType(QName("urn:hl7-org:elm-types:r1", "Boolean"))
                    .withValue(b.toString())
            )
        }
    }

    constructor(vararg strings: String?) : super() {
        expectedValue = ArrayList<Literal?>(strings.size)
        for (s in strings) {
            expectedValue.add(
                ObjectFactory()
                    .createLiteral()
                    .withValueType(QName("urn:hl7-org:elm-types:r1", "String"))
                    .withValue(s)
            )
        }
    }

    constructor(vararg ints: Int?) : super() {
        expectedValue = ArrayList<Literal?>(ints.size)
        for (i in ints) {
            expectedValue.add(
                ObjectFactory()
                    .createLiteral()
                    .withValueType(QName("urn:hl7-org:elm-types:r1", "Integer"))
                    .withValue(i.toString())
            )
        }
    }

    constructor(vararg decs: Double?) : super() {
        expectedValue = ArrayList<Literal?>(decs.size)
        for (d in decs) {
            expectedValue.add(
                ObjectFactory()
                    .createLiteral()
                    .withValueType(QName("urn:hl7-org:elm-types:r1", "Decimal"))
                    .withValue(d.toString())
            )
        }
    }

    @Suppress("ReturnCount")
    override fun matchesSafely(item: Expression, mismatchDescription: Description): Boolean {
        if (item !is List) {
            mismatchDescription
                .appendText("had wrong ELM class type: ")
                .appendText(item.javaClass.getName())
            return false
        }

        if (expectedValue != item.element) {
            mismatchDescription
                .appendText("had wrong elements: ")
                .appendValueList("[ ", " , ", " ]", item.element)
            return false
        }

        return true
    }

    override fun describeTo(description: Description) {
        description
            .appendText("List w/ elements: ")
            .appendValueList<Literal?>("[ ", " , ", " ]", expectedValue)
    }

    @Suppress("MemberNameEqualsClassName")
    companion object {
        @JvmStatic
        @Factory
        fun listOfLiterals(vararg b: Boolean?): Matcher<Expression?> {
            return ListOfLiterals(*b)
        }

        @JvmStatic
        @Factory
        fun listOfLiterals(vararg s: String?): Matcher<Expression?> {
            return ListOfLiterals(*s)
        }

        @JvmStatic
        @Factory
        fun listOfLiterals(vararg i: Int?): Matcher<Expression?> {
            return ListOfLiterals(*i)
        }

        @JvmStatic
        @Factory
        fun listOfLiterals(vararg d: Double?): Matcher<Expression?> {
            return ListOfLiterals(*d)
        }
    }
}
