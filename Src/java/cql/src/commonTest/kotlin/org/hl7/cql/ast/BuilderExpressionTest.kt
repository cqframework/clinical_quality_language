package org.hl7.cql.ast

import kotlin.test.Test

class BuilderExpressionTest {

    private val builder = Builder()

    private fun assertExpression(text: String, expected: ExpressionSpec) {
        builder.parseExpression(text).assertMatches(expected)
    }

    @Test
    fun parsesArithmeticExpressions() {
        assertExpression("1 + 2") { binary(BinaryOperator.ADD, { int(1) }, { int(2) }) }
        assertExpression("1 - 2") { binary(BinaryOperator.SUBTRACT, { int(1) }, { int(2) }) }
        assertExpression("2 * 3") { binary(BinaryOperator.MULTIPLY, { int(2) }, { int(3) }) }
        assertExpression("4 / 2") { binary(BinaryOperator.DIVIDE, { int(4) }, { int(2) }) }
        assertExpression("5 div 2") { binary(BinaryOperator.DIVIDE, { int(5) }, { int(2) }) }
        assertExpression("5 mod 2") { binary(BinaryOperator.MODULO, { int(5) }, { int(2) }) }
        assertExpression("2 ^ 3") { binary(BinaryOperator.POWER, { int(2) }, { int(3) }) }
        assertExpression("'foo' & 'bar'") {
            binary(BinaryOperator.CONCAT, { string("foo") }, { string("bar") })
        }
    }

    @Test
    fun parsesEqualityAndComparisonExpressions() {
        assertExpression("1 = 1") { binary(BinaryOperator.EQUALS, { int(1) }, { int(1) }) }
        assertExpression("1 != 2") { binary(BinaryOperator.NOT_EQUALS, { int(1) }, { int(2) }) }
        assertExpression("'abc' ~ 'abc'") {
            binary(BinaryOperator.EQUIVALENT, { string("abc") }, { string("abc") })
        }
        assertExpression("'abc' !~ 'def'") {
            binary(BinaryOperator.NOT_EQUIVALENT, { string("abc") }, { string("def") })
        }
        assertExpression("1 < 2") { binary(BinaryOperator.LT, { int(1) }, { int(2) }) }
        assertExpression("1 <= 2") { binary(BinaryOperator.LTE, { int(1) }, { int(2) }) }
        assertExpression("2 > 1") { binary(BinaryOperator.GT, { int(2) }, { int(1) }) }
        assertExpression("2 >= 1") { binary(BinaryOperator.GTE, { int(2) }, { int(1) }) }
    }

    @Test
    fun parsesBooleanExpressions() {
        assertExpression("true and false") {
            binary(BinaryOperator.AND, { boolean(true) }, { boolean(false) })
        }
        assertExpression("true or false") {
            binary(BinaryOperator.OR, { boolean(true) }, { boolean(false) })
        }
        assertExpression("true xor false") {
            binary(BinaryOperator.XOR, { boolean(true) }, { boolean(false) })
        }
        assertExpression("true implies false") {
            binary(BinaryOperator.IMPLIES, { boolean(true) }, { boolean(false) })
        }
        assertExpression("not false") { unary(UnaryOperator.NOT) { boolean(false) } }
    }

    @Test
    fun parsesSetAndMembershipExpressions() {
        assertExpression("exists { 1, 2 }") { exists { list({ int(1) }, { int(2) }) } }
        assertExpression("{ 1, 2 } union { 3 }") {
            binary(BinaryOperator.UNION, { list({ int(1) }, { int(2) }) }, { list({ int(3) }) })
        }
        assertExpression("{ 1, 2 } intersect { 2 }") {
            binary(BinaryOperator.INTERSECT, { list({ int(1) }, { int(2) }) }, { list({ int(2) }) })
        }
        assertExpression("{ 1, 2 } except { 2 }") {
            binary(BinaryOperator.EXCEPT, { list({ int(1) }, { int(2) }) }, { list({ int(2) }) })
        }
        assertExpression("1 in { 1, 2 }") {
            membership(
                MembershipOperator.IN,
                left = { int(1) },
                right = { list({ int(1) }, { int(2) }) },
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
                thenBranch = { int(1) },
                elseBranch = { int(0) },
            )
        }
        assertExpression("case when 1 = 1 then 'a' else 'b' end") {
            case {
                whenThen({ binary(BinaryOperator.EQUALS, { int(1) }, { int(1) }) }) { string("a") }
                elseResult { string("b") }
            }
        }
    }

    @Test
    fun parsesBetweenExpressions() {
        assertExpression("1 between 0 and 2") {
            between(input = { int(1) }, lower = { int(0) }, upper = { int(2) })
        }
        assertExpression("duration in days between @2020-01-01T00:00:00 and @2020-01-05T00:00:00") {
            durationBetween(
                precision = "days",
                lower = { dateTime("@2020-01-01T00:00:00") },
                upper = { dateTime("@2020-01-05T00:00:00") },
            )
        }
        assertExpression(
            "difference in days between @2020-01-05T00:00:00 and @2020-01-01T00:00:00"
        ) {
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
            durationOf("days") { interval({ int(1) }, { int(5) }) }
        }
        assertExpression("difference in days of Interval[1, 5]") {
            differenceOf("days") { interval({ int(1) }, { int(5) }) }
        }
        assertExpression("width of Interval[1, 5]") { widthOf { interval({ int(1) }, { int(5) }) } }
        assertExpression(
            "duration in days of Interval[@2020-01-01T00:00:00, @2020-01-05T00:00:00]"
        ) {
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
        assertExpression("successor of 1") { unary(UnaryOperator.SUCCESSOR) { int(1) } }
        assertExpression("predecessor of 2") { unary(UnaryOperator.PREDECESSOR) { int(2) } }
        assertExpression("singleton from { 1 }") {
            elementExtractor(ElementExtractorKind.SINGLETON) { list({ int(1) }) }
        }
        assertExpression("point from Interval[1, 5]") {
            elementExtractor(ElementExtractorKind.POINT) { interval({ int(1) }, { int(5) }) }
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
            convert(operand = { int(5) }, destinationType = namedType("String"))
        }
        assertExpression("start of Interval[1, 5]") {
            timeBoundary(TimeBoundaryKind.START) { interval({ int(1) }, { int(5) }) }
        }
        assertExpression("end of Interval[1, 5]") {
            timeBoundary(TimeBoundaryKind.END) { interval({ int(1) }, { int(5) }) }
        }
    }

    @Test
    fun parsesListTransformExpressions() {
        assertExpression("distinct { 1, 1, 2 }") {
            listTransform(ListTransformKind.DISTINCT) { list({ int(1) }, { int(1) }, { int(2) }) }
        }
        assertExpression("flatten { { 1 }, { 2 } }") {
            listTransform(ListTransformKind.FLATTEN) {
                list({ list({ int(1) }) }, { list({ int(2) }) })
            }
        }
    }

    @Test
    fun parsesIntervalRelationships() {
        assertExpression("Interval[1, 5] starts on or before start of Interval[5, 10]") {
            intervalRelation(
                left = { interval({ int(1) }, { int(5) }) },
                right = {
                    timeBoundary(TimeBoundaryKind.START) { interval({ int(5) }, { int(10) }) }
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
                left = { interval({ int(1) }, { int(5) }) },
                right = {
                    timeBoundary(TimeBoundaryKind.END) { interval({ int(5) }, { int(10) }) }
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
                left = { interval({ int(1) }, { int(5) }) },
                right = {
                    timeBoundary(TimeBoundaryKind.START) { interval({ int(5) }, { int(10) }) }
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
                left = { interval({ int(1) }, { int(5) }) },
                right = {
                    timeBoundary(TimeBoundaryKind.END) { interval({ int(5) }, { int(10) }) }
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
            list({ int(1) }, { int(2) }, { int(3) }, elementType = namedType("Integer"))
        }
        assertExpression("Tuple { value: 1, flag: true }") {
            tuple("value" to { int(1) }, "flag" to { boolean(true) })
        }
        assertExpression("System.Code{ code: '140', system: 'http://snomed.info/sct' }") {
            instance(
                type = namedType("System", "Code"),
                "code" to { string("140") },
                "system" to { string("http://snomed.info/sct") },
            )
        }
        assertExpression("List<Choice<Integer, String>>{ 1, 'two' }") {
            list(
                { int(1) },
                { string("two") },
                elementType = choiceType(namedType("Integer"), namedType("String")),
            )
        }
    }

    @Test
    fun parsesQueryExpressions() {
        val expected: ExpressionSpec = {
            val retrieve =
                RetrieveExpression(
                    typeSpecifier = namedType("Observation"),
                    terminology = null,
                    context = null,
                    codePath = null,
                    comparator = null,
                )
            QueryExpression(
                sources = listOf(AliasedQuerySource(source = retrieve, alias = Identifier("O"))),
                lets = emptyList(),
                inclusions = emptyList(),
                where = null,
                aggregate = null,
                result =
                    ReturnClause(
                        expression = IdentifierExpression(QualifiedIdentifier(listOf("O")))
                    ),
                sort = null,
            )
        }

        assertExpression("[Observation] O return O", expected)
        assertExpression("from [Observation] O return O", expected)
    }

    @Test
    fun parsesAggregateQueryExpressions() {
        val queryText =
            """
            from [Observation] O
              let Value: O.value
              where Value != null
              aggregate distinct Count starting 0: Count + 1
            """
                .trimIndent()

        assertExpression(queryText) {
            val retrieve =
                RetrieveExpression(
                    typeSpecifier = namedType("Observation"),
                    terminology = null,
                    context = null,
                    codePath = null,
                    comparator = null,
                )
            val aliasSource = AliasedQuerySource(source = retrieve, alias = Identifier("O"))
            val valueBinding =
                LetClauseItem(
                    identifier = Identifier("Value"),
                    expression =
                        PropertyAccessExpression(
                            target = IdentifierExpression(QualifiedIdentifier(listOf("O"))),
                            property = Identifier("value"),
                        ),
                )
            val whereExpression =
                OperatorBinaryExpression(
                    operator = BinaryOperator.NOT_EQUALS,
                    left = IdentifierExpression(QualifiedIdentifier(listOf("Value"))),
                    right = LiteralExpression(NullLiteral()),
                )
            val aggregateClause =
                AggregateClause(
                    distinct = true,
                    identifier = Identifier("Count"),
                    starting = LiteralExpression(IntLiteral(0)),
                    expression =
                        OperatorBinaryExpression(
                            operator = BinaryOperator.ADD,
                            left = IdentifierExpression(QualifiedIdentifier(listOf("Count"))),
                            right = LiteralExpression(IntLiteral(1)),
                        ),
                )

            QueryExpression(
                sources = listOf(aliasSource),
                lets = listOf(valueBinding),
                inclusions = emptyList(),
                where = whereExpression,
                aggregate = aggregateClause,
                result = null,
                sort = null,
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
        assertExpression("Code '140' from \"SNOMED\"") { code("140", system = "SNOMED") }
    }

    @Test
    fun parsesTypeExpressions() {
        assertExpression("1 is Integer") {
            typeIs(operand = { int(1) }, type = namedType("Integer"))
        }
        assertExpression("1 is not null") {
            binary(BinaryOperator.EQUALS, left = { int(1) }, right = { nullValue() })
        }
        assertExpression("1 as Integer") {
            typeAs(operand = { int(1) }, type = namedType("Integer"))
        }
        assertExpression("cast 1 as Integer") {
            cast(operand = { int(1) }, type = namedType("Integer"))
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
            "collapse { Interval[@2020-01-01T00:00:00, @2020-01-02T00:00:00], Interval[@2020-01-02T00:00:00, @2020-01-03T00:00:00] }"
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
