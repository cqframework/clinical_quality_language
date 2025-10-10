package org.hl7.cql.ast

import kotlin.test.Test

class BuilderExpressionTest {

    private val builder = Builder()

    private fun assertExpression(text: String, expected: ExpressionSpec) {
        builder.parseExpression(text).assertMatches(expected)
    }

    @Test
    fun parsesArithmeticExpressions() {
        assertExpression("1 + 2") { binary(BinaryOperator.ADD, { number("1") }, { number("2") }) }
        assertExpression("1 - 2") { binary(BinaryOperator.SUBTRACT, { number("1") }, { number("2") }) }
        assertExpression("2 * 3") { binary(BinaryOperator.MULTIPLY, { number("2") }, { number("3") }) }
        assertExpression("4 / 2") { binary(BinaryOperator.DIVIDE, { number("4") }, { number("2") }) }
        assertExpression("5 div 2") { binary(BinaryOperator.DIVIDE, { number("5") }, { number("2") }) }
        assertExpression("5 mod 2") { binary(BinaryOperator.MODULO, { number("5") }, { number("2") }) }
        assertExpression("2 ^ 3") { binary(BinaryOperator.POWER, { number("2") }, { number("3") }) }
        assertExpression("'foo' & 'bar'") { binary(BinaryOperator.CONCAT, { string("foo") }, { string("bar") }) }
    }

    @Test
    fun parsesEqualityAndComparisonExpressions() {
        assertExpression("1 = 1") { binary(BinaryOperator.EQUALS, { number("1") }, { number("1") }) }
        assertExpression("1 != 2") { binary(BinaryOperator.NOT_EQUALS, { number("1") }, { number("2") }) }
        assertExpression("'abc' ~ 'abc'") { binary(BinaryOperator.EQUIVALENT, { string("abc") }, { string("abc") }) }
        assertExpression("'abc' !~ 'def'") { binary(BinaryOperator.NOT_EQUIVALENT, { string("abc") }, { string("def") }) }
        assertExpression("1 < 2") { binary(BinaryOperator.LT, { number("1") }, { number("2") }) }
        assertExpression("1 <= 2") { binary(BinaryOperator.LTE, { number("1") }, { number("2") }) }
        assertExpression("2 > 1") { binary(BinaryOperator.GT, { number("2") }, { number("1") }) }
        assertExpression("2 >= 1") { binary(BinaryOperator.GTE, { number("2") }, { number("1") }) }
    }

    @Test
    fun parsesBooleanExpressions() {
        assertExpression("true and false") { binary(BinaryOperator.AND, { boolean(true) }, { boolean(false) }) }
        assertExpression("true or false") { binary(BinaryOperator.OR, { boolean(true) }, { boolean(false) }) }
        assertExpression("true xor false") { binary(BinaryOperator.XOR, { boolean(true) }, { boolean(false) }) }
        assertExpression("true implies false") { binary(BinaryOperator.IMPLIES, { boolean(true) }, { boolean(false) }) }
        assertExpression("not false") { unary(UnaryOperator.NOT) { boolean(false) } }
    }

    @Test
    fun parsesSetAndMembershipExpressions() {
        assertExpression("exists { 1, 2 }") { exists { list({ number("1") }, { number("2") }) } }
        assertExpression("{ 1, 2 } union { 3 }") {
            binary(
                BinaryOperator.UNION,
                { list({ number("1") }, { number("2") }) },
                { list({ number("3") }) },
            )
        }
        assertExpression("{ 1, 2 } intersect { 2 }") {
            binary(
                BinaryOperator.INTERSECT,
                { list({ number("1") }, { number("2") }) },
                { list({ number("2") }) },
            )
        }
        assertExpression("{ 1, 2 } except { 2 }") {
            binary(
                BinaryOperator.EXCEPT,
                { list({ number("1") }, { number("2") }) },
                { list({ number("2") }) },
            )
        }
        assertExpression("1 in { 1, 2 }") {
            membership(
                MembershipOperator.IN,
                left = { number("1") },
                right = { list({ number("1") }, { number("2") }) },
            )
        }
        assertExpression("{ 'a' } contains 'a'") {
            membership(
                MembershipOperator.CONTAINS,
                left = { list({ string("a") }) },
                right = { string("a") },
            )
        }
    }

    @Test
    fun parsesConditionalExpressions() {
        assertExpression("if true then 1 else 0") {
            ifThenElse(
                condition = { boolean(true) },
                thenBranch = { number("1") },
                elseBranch = { number("0") },
            )
        }
        assertExpression("case when 1 = 1 then 'a' else 'b' end") {
            case {
                whenThen({ binary(BinaryOperator.EQUALS, { number("1") }, { number("1") }) }) { string("a") }
                elseResult { string("b") }
            }
        }
    }

    @Test
    fun parsesBetweenExpressions() {
        assertExpression("1 between 0 and 2") {
            between(
                input = { number("1") },
                lower = { number("0") },
                upper = { number("2") },
            )
        }
        assertExpression("duration in days between @2020-01-01T00:00:00 and @2020-01-05T00:00:00") {
            durationBetween(
                precision = "days",
                lower = { dateTime("@2020-01-01T00:00:00") },
                upper = { dateTime("@2020-01-05T00:00:00") },
            )
        }
        assertExpression("difference in days between @2020-01-05T00:00:00 and @2020-01-01T00:00:00") {
            differenceBetween(
                precision = "days",
                lower = { dateTime("@2020-01-05T00:00:00") },
                upper = { dateTime("@2020-01-01T00:00:00") },
            )
        }
    }

    @Test
    fun parsesTemporalUnaryExpressions() {
        assertExpression("duration in days of Interval[1, 5]") {
            durationOf("days") { interval({ number("1") }, { number("5") }) }
        }
        assertExpression("difference in days of Interval[1, 5]") {
            differenceOf("days") { interval({ number("1") }, { number("5") }) }
        }
        assertExpression("width of Interval[1, 5]") {
            widthOf { interval({ number("1") }, { number("5") }) }
        }
        assertExpression("duration in days of Interval[@2020-01-01T00:00:00, @2020-01-05T00:00:00]") {
            durationOf("days") {
                interval(
                    lower = { dateTime("@2020-01-01T00:00:00") },
                    upper = { dateTime("@2020-01-05T00:00:00") },
                )
            }
        }
    }

    @Test
    fun parsesElementExtractorExpressions() {
        assertExpression("successor of 1") { unary(UnaryOperator.SUCCESSOR) { number("1") } }
        assertExpression("predecessor of 2") { unary(UnaryOperator.PREDECESSOR) { number("2") } }
        assertExpression("singleton from { 1 }") {
            elementExtractor(ElementExtractorKind.SINGLETON) { list({ number("1") }) }
        }
        assertExpression("point from Interval[1, 5]") {
            elementExtractor(ElementExtractorKind.POINT) { interval({ number("1") }, { number("5") }) }
        }
    }

    @Test
    fun parsesTypeExtentAndConversionExpressions() {
        assertExpression("minimum Integer") {
            typeExtent(TypeExtentKind.MINIMUM, namedType("Integer"))
        }
        assertExpression("maximum Integer") {
            typeExtent(TypeExtentKind.MAXIMUM, namedType("Integer"))
        }
        assertExpression("convert 5 to String") {
            convert(
                operand = { number("5") },
                destinationType = namedType("String"),
            )
        }
        assertExpression("start of Interval[1, 5]") {
            timeBoundary(TimeBoundaryKind.START) { interval({ number("1") }, { number("5") }) }
        }
        assertExpression("end of Interval[1, 5]") {
            timeBoundary(TimeBoundaryKind.END) { interval({ number("1") }, { number("5") }) }
        }
    }

    @Test
    fun parsesListTransformExpressions() {
        assertExpression("distinct { 1, 1, 2 }") {
            listTransform(ListTransformKind.DISTINCT) {
                list({ number("1") }, { number("1") }, { number("2") })
            }
        }
        assertExpression("flatten { { 1 }, { 2 } }") {
            listTransform(ListTransformKind.FLATTEN) {
                list({ list({ number("1") }) }, { list({ number("2") }) })
            }
        }
    }

    @Test
    fun parsesIntervalRelationships() {
        assertExpression("Interval[1, 5] starts on or before start of Interval[5, 10]") {
            intervalRelation(
                left = { interval({ number("1") }, { number("5") }) },
                right = {
                    timeBoundary(TimeBoundaryKind.START) {
                        interval({ number("5") }, { number("10") })
                    }
                },
                phrase = {
                    beforeOrAfter(
                        direction = TemporalRelationshipDirection.BEFORE,
                        leftBoundary = IntervalBoundarySelector.START,
                        inclusive = true,
                    )
                },
            )
        }
        assertExpression("Interval[1, 5] starts before or on end of Interval[5, 10]") {
            intervalRelation(
                left = { interval({ number("1") }, { number("5") }) },
                right = {
                    timeBoundary(TimeBoundaryKind.END) {
                        interval({ number("5") }, { number("10") })
                    }
                },
                phrase = {
                    beforeOrAfter(
                        direction = TemporalRelationshipDirection.BEFORE,
                        leftBoundary = IntervalBoundarySelector.START,
                        inclusive = true,
                    )
                },
            )
        }
        assertExpression("Interval[1, 5] starts on or after start of Interval[5, 10]") {
            intervalRelation(
                left = { interval({ number("1") }, { number("5") }) },
                right = {
                    timeBoundary(TimeBoundaryKind.START) {
                        interval({ number("5") }, { number("10") })
                    }
                },
                phrase = {
                    beforeOrAfter(
                        direction = TemporalRelationshipDirection.AFTER,
                        leftBoundary = IntervalBoundarySelector.START,
                        inclusive = true,
                    )
                },
            )
        }
        assertExpression("Interval[1, 5] starts after or on end of Interval[5, 10]") {
            intervalRelation(
                left = { interval({ number("1") }, { number("5") }) },
                right = {
                    timeBoundary(TimeBoundaryKind.END) {
                        interval({ number("5") }, { number("10") })
                    }
                },
                phrase = {
                    beforeOrAfter(
                        direction = TemporalRelationshipDirection.AFTER,
                        leftBoundary = IntervalBoundarySelector.START,
                        inclusive = true,
                    )
                },
            )
        }
    }

    @Test
    fun parsesLiteralsAndStructures() {
        assertExpression("List<Integer>{ 1, 2, 3 }") {
            list(
                { number("1") },
                { number("2") },
                { number("3") },
                elementType = namedType("Integer"),
            )
        }
        assertExpression("Tuple { value: 1, flag: true }") {
            tuple(
                "value" to { number("1") },
                "flag" to { boolean(true) },
            )
        }
        assertExpression("System.Code{ code: '140', system: 'http://snomed.info/sct' }") {
            instance(
                type = namedType("System", "Code"),
                "code" to { string("140") },
                "system" to { string("http://snomed.info/sct") },
            )
        }
    }

    @Test
    fun parsesQuantityAndRatioLiterals() {
        assertExpression("5 'mg'") { quantity("5", "'mg'") }
        assertExpression("5 'mg':10 'mL'") {
            ratio(
                numerator = quantityLiteral("5", "'mg'"),
                denominator = quantityLiteral("10", "'mL'"),
            )
        }
    }

    @Test
    fun parsesDateTimeAndBooleanLiterals() {
        assertExpression("@2020-01-01") { dateTime("@2020-01-01") }
        assertExpression("@2020-01-01T00:00:00") { dateTime("@2020-01-01T00:00:00") }
        assertExpression("@T10:30:00") { time("@T10:30:00") }
        assertExpression("null") { nullValue() }
        assertExpression("true") { boolean(true) }
    }

    @Test
    fun parsesCodeLiteral() {
        assertExpression("Code '140' from \"SNOMED\"") {
            code("140", system = "SNOMED")
        }
    }

    @Test
    fun parsesTypeExpressions() {
        assertExpression("1 is Integer") {
            typeIs(
                operand = { number("1") },
                type = namedType("Integer"),
            )
        }
        assertExpression("1 is not null") {
            binary(
                BinaryOperator.EQUALS,
                left = { number("1") },
                right = { nullValue() },
            )
        }
        assertExpression("1 as Integer") {
            typeAs(
                operand = { number("1") },
                type = namedType("Integer"),
            )
        }
        assertExpression("cast 1 as Integer") {
            cast(
                operand = { number("1") },
                type = namedType("Integer"),
            )
        }
    }

    @Test
    fun parsesExpandAndCollapseExpressions() {
        assertExpression("expand Interval[@2020-01-01T00:00:00, @2020-01-03T00:00:00]") {
            expandCollapse(
                kind = ExpandCollapseKind.EXPAND,
                operand = {
                    interval(
                        lower = { dateTime("@2020-01-01T00:00:00") },
                        upper = { dateTime("@2020-01-03T00:00:00") },
                    )
                },
            )
        }
        assertExpression(
            "collapse { Interval[@2020-01-01T00:00:00, @2020-01-02T00:00:00], Interval[@2020-01-02T00:00:00, @2020-01-03T00:00:00] }",
        ) {
            expandCollapse(
                kind = ExpandCollapseKind.COLLAPSE,
                operand = {
                    list(
                        {
                            interval(
                                lower = { dateTime("@2020-01-01T00:00:00") },
                                upper = { dateTime("@2020-01-02T00:00:00") },
                            )
                        },
                        {
                            interval(
                                lower = { dateTime("@2020-01-02T00:00:00") },
                                upper = { dateTime("@2020-01-03T00:00:00") },
                            )
                        },
                    )
                },
            )
        }
    }
}
