package org.cqframework.cql.cql2elm.matchers

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.hl7.elm.r1.ExpressionDef

class HasTypeAndResult(private val expectedType: Class<*>, private val expectedResult: String?) :
    TypeSafeDiagnosingMatcher<ExpressionDef>() {
    @SuppressWarnings("ReturnCount")
    override fun matchesSafely(item: ExpressionDef, mismatchDescription: Description): Boolean {
        if (!(expectedType.isInstance(item.expression))) {
            mismatchDescription
                .appendText("had wrong type: ")
                .appendText(item.expression?.javaClass?.simpleName)
            return false
        }

        val type = item.resultType
        if (type == null) {
            mismatchDescription.appendText("had null result type")
            return false
        } else if (type.toString() != expectedResult) {
            mismatchDescription.appendText("had wrong result: ").appendText(type.toString())
            return false
        }

        return true
    }

    override fun describeTo(description: Description) {
        description
            .appendText("ExpressionDef w/ type: <")
            .appendText(expectedType.getName())
            .appendText("> and result: <")
            .appendText(expectedResult)
            .appendText(">")
    }

    @SuppressWarnings("MemberNameEqualsClassName")
    companion object {
        @JvmStatic
        @Factory
        fun hasTypeAndResult(t: Class<*>, r: Class<*>): Matcher<ExpressionDef?> {
            return HasTypeAndResult(t, r.getName())
        }

        @JvmStatic
        @Factory
        fun hasTypeAndResult(t: Class<*>, r: String?): Matcher<ExpressionDef?> {
            return HasTypeAndResult(t, r)
        }
    }
}
