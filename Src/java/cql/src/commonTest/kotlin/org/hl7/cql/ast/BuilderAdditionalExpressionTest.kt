package org.hl7.cql.ast

import kotlin.test.Test

class BuilderAdditionalExpressionTest {

    private val builder = Builder()

    private fun assertExpression(text: String, expected: ExpressionSpec) {
        builder.parseExpression(text).assertMatches(expected)
    }

    @Test
    fun parsesIndexedExpressions() {
        assertExpression("{ 1, 2 }[0]") {
            index(target = { list({ int(1) }, { int(2) }) }, index = { int(0) })
        }
        assertExpression("(1 + 2)[1]") {
            index(
                target = { binary(BinaryOperator.ADD, { int(1) }, { int(2) }) },
                index = { int(1) },
            )
        }
    }

    @Test
    fun parsesDateTimeComponentExtraction() {
        assertExpression("year from @2020-01-01T00:00:00") {
            dateTimeComponent(DateTimeComponent.YEAR) { dateTime("@2020-01-01T00:00:00") }
        }
        assertExpression("timezoneoffset from @2020-01-01T00:00:00") {
            dateTimeComponent(DateTimeComponent.TIMEZONE_OFFSET) {
                dateTime("@2020-01-01T00:00:00")
            }
        }
    }

    @Test
    fun parsesExternalConstants() {
        assertExpression("%MeasurementPeriod") { externalConstant("MeasurementPeriod") }
        assertExpression("%default") { externalConstant("default") }
        assertExpression("%'Quoted Constant'") { externalConstant("Quoted Constant") }
    }
}
