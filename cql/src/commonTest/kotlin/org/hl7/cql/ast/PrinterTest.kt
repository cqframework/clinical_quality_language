package org.hl7.cql.ast

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Printing a tree and parsing the result gives the same tree, and printing that gives the same
 * text.
 */
class PrinterTest {

    @Test
    fun printsEveryKindOfExpressionSoItParsesTheSame() {
        val failures = EXPRESSIONS.mapNotNull { roundTripFailure(it) }
        if (failures.isNotEmpty()) fail(failures.joinToString("\n\n"))
    }

    @Test
    fun printsALibrarySoItParsesTheSame() {
        assertLibraryRoundTrips(
            """
            library Example.Measures version '1.0.0'

            using FHIR version '4.0.1'

            include FHIRHelpers version '4.0.1' called FHIRHelpers
            include Common called C

            codesystem "LOINC": 'http://loinc.org' version '2.72'
            valueset "Visits": 'http://example.org/visits' version '1' codesystems { "LOINC", C."SNOMED" }
            private code "Glucose": '2345-7' from "LOINC" display 'Glucose'
            concept "Labs": { "Glucose", C."Other" } display 'Labs'
            parameter "Measurement Period" Interval<DateTime>
              default Interval[@2024-01-01T00:00:00, @2025-01-01T00:00:00)
            parameter Threshold Integer
            public parameter Flag default true

            context Patient

            define "Visits In Period":
              [Encounter: type in "Visits"] E
                where E.period during "Measurement Period"

            define private Ordered:
              from "Visits In Period" V, [Condition] C
                let first: First(V.reason), count: Count(C.code)
                with [Procedure] P such that P.encounter = V.id
                without [Observation: code = "Glucose"] O such that O.encounter = V.id
                where C.subject = V.subject
                return all Tuple { visit: V, condition: C }
                sort by visit.id desc, condition.id

            define Total:
              ({ 1, 2, 3 }) X aggregate distinct R starting 0: R + X

            define fluent function Clean(value List<FHIR.Coding>, t Tuple { system String, code String }) returns Boolean:
              value.distinct().Count() > 0

            define function Lookup(code Choice<String, Integer>) returns String: external
            """
        )
    }

    @Test
    fun printsCommentsWithTheirDeclarations() {
        assertLibraryRoundTrips(
            """
            // Before the header
            library L // after the header

            /*
             * @author: Jane
             * @description: Using FHIR
             */
            using FHIR

            // Before A
            define A:
              1 + /* within A */ 2 // after A

            define B:
              // before the operand
              3

            define C: if X // after the condition
              then 1 else 2
            // At the end
            /* and after that */
            """
        )
    }

    @Test
    fun keepsCommentsBeforeAClosingDelimiterWithinTheirDeclaration() {
        // Written after the closing delimiter, the second line comment would go to the next line,
        // and so to the next declaration.
        assertLibraryRoundTrips(
            """
            define A:
              (X and Y
                // one
                // two
              )
            define B: F(1 // one
              // two
            )
            define C: { 1, 2 // one
              // two
            }
            define D: Tuple { a: 1 // one
              // two
            }
            define E: [Encounter: "V" // one
              // two
            ]
            define F: 1
            """
        )
    }

    @Test
    fun keepsTheTextOfACommentExactly() {
        assertLibraryRoundTrips("define A:\n  1 + // trailing spaces   \n  2 /* block */\n")
    }

    @Test
    fun printsALibraryOfOnlyComments() {
        assertLibraryRoundTrips("// one\n/* two */\n")
    }

    @Test
    fun printsHowIdentifiersAreQuoted() {
        val text = "define \"Quoted Name\":\n  `delimited`.plain + \"a\\\"b\".\"c\"\n"
        assertEquals(text, Printer.print(parseLibrary(text)))
    }

    @Test
    fun printsTheSameLayoutEveryTime() {
        val printed =
            Printer.print(
                parseLibrary(
                    "library L using FHIR\ndefine A: [Encounter] E where E.x = 1 return E\n" +
                        "define B: case X when 1 then 'a' else 'b' end"
                )
            )
        assertEquals(
            """
            library L

            using FHIR

            define A:
              [Encounter] E
                where E.x = 1
                return E

            define B:
              case X
                when 1 then 'a'
                else 'b'
              end

            """
                .trimIndent(),
            printed,
        )
    }

    @Test
    fun addsTheParenthesesATreeBuiltInCodeNeeds() {
        val sum = OperatorBinaryExpression(BinaryOperator.ADD, int(1), int(2))
        assertEquals(
            "(1 + 2) * 3",
            Printer.print(OperatorBinaryExpression(BinaryOperator.MULTIPLY, sum, int(3))),
        )
        assertEquals(
            "3 - (1 + 2)",
            Printer.print(OperatorBinaryExpression(BinaryOperator.SUBTRACT, int(3), sum)),
        )
        val choice = IfExpression(identifier("A"), int(1), int(2))
        assertEquals(
            "(if A then 1 else 2) + 3",
            Printer.print(OperatorBinaryExpression(BinaryOperator.ADD, choice, int(3))),
        )
        // `if` is a term, so it can follow an operator; only what follows it needs parentheses
        assertEquals(
            "3 + if A then 1 else 2",
            Printer.print(OperatorBinaryExpression(BinaryOperator.ADD, int(3), choice)),
        )
        assertEquals(
            "X and (if A then 1 else 2) or Y",
            Printer.print(
                OperatorBinaryExpression(
                    BinaryOperator.OR,
                    OperatorBinaryExpression(BinaryOperator.AND, identifier("X"), choice),
                    identifier("Y"),
                )
            ),
        )
    }

    /**
     * Trees built from random operators, without parentheses, must print so that parsing them gives
     * the same tree once the added parentheses are removed. This checks the printer's precedence
     * and open-ended forms against the grammar.
     */
    @Test
    fun printsGeneratedTreesSoTheyParseTheSame() {
        val random = Random(SEED)
        val failures =
            (1..GENERATED_TREES).mapNotNull {
                val tree = TreeGenerator(random).expression(depth = 4)
                val printed = Printer.print(tree)
                val result = Builder().parseExpression(printed)
                when {
                    result.problems.isNotEmpty() ->
                        "$printed\n  did not parse: ${result.problems.map { it.message }}"
                    result.expression.withoutParentheses().structure() !=
                        tree.withNegativeLiterals().structure() ->
                        "$printed\n  parsed as ${Printer.print(result.expression.withoutParentheses())}"
                    else -> null
                }
            }
        if (failures.isNotEmpty())
            fail("${failures.size} trees:\n" + failures.take(20).joinToString("\n"))
    }

    private fun roundTripFailure(text: String): String? {
        val first = Builder().parseExpression(text)
        val printed = runCatching { Printer.print(first.expression) }
        val second = printed.getOrNull()?.let { Builder().parseExpression(it) }
        return when {
            first.problems.isNotEmpty() ->
                "`$text` did not parse: ${first.problems.map { it.message }}"
            second == null -> "`$text` did not print: ${printed.exceptionOrNull()}"
            second.problems.isNotEmpty() ->
                "`$text` printed as `${printed.getOrNull()}`, which did not parse: " +
                    "${second.problems.map { it.message }}"
            second.expression.structure() != first.expression.structure() ->
                "`$text` printed as `${printed.getOrNull()}`, which parsed differently"
            Printer.print(second.expression) != printed.getOrNull() ->
                "`$text` printed as `${printed.getOrNull()}`, then as " +
                    "`${Printer.print(second.expression)}`"
            else -> null
        }
    }

    private fun assertLibraryRoundTrips(text: String) {
        val first = parseLibrary(text.trimIndent())
        val printed = Printer.print(first)
        val second = Builder().parseLibrary(printed)
        assertTrue(second.problems.isEmpty(), "$printed\n${second.problems.map { it.message }}")
        assertEquals(first.structure(), second.library.structure(), printed)
        assertEquals(printed, Printer.print(second.library))
    }

    private fun parseLibrary(text: String): Library {
        val result = Builder().parseLibrary(text)
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
        return result.library
    }

    /** The tree without source positions, but with identifier quoting, comments and tags. */
    private fun Expression.structure(): JsonElement =
        Json.encodeToJsonElement(Expression.serializer(), this).withoutLocators()

    private fun Library.structure(): JsonElement =
        Json.encodeToJsonElement(Library.serializer(), this).withoutLocators()

    private fun JsonElement.withoutLocators(): JsonElement =
        when (this) {
            is JsonObject ->
                JsonObject(filterKeys { it != "locator" }.mapValues { it.value.withoutLocators() })
            is JsonArray -> JsonArray(map { it.withoutLocators() })
            else -> this
        }

    private fun Expression.withoutParentheses(): Expression =
        transform(
            object : Transformer() {
                override fun visitParenthesizedExpression(
                    expression: ParenthesizedExpression
                ): Expression = visitExpression(expression.expression)
            }
        )

    /**
     * [this] with `-` applied directly to a number as the negative number, as the Builder reads it.
     */
    private fun Expression.withNegativeLiterals(): Expression =
        transform(
            object : Transformer() {
                override fun visitOperatorUnaryExpression(
                    expression: OperatorUnaryExpression
                ): Expression {
                    val literal = (expression.operand as? LiteralExpression)?.literal as? IntLiteral
                    return if (expression.operator == UnaryOperator.NEGATE && literal != null)
                        LiteralExpression(IntLiteral(-literal.value))
                    else super.visitOperatorUnaryExpression(expression)
                }
            }
        )

    private fun int(value: Int): Expression = LiteralExpression(IntLiteral(value))

    private fun identifier(name: String): Expression =
        IdentifierExpression(QualifiedIdentifier(listOf(Identifier(name))))

    /** Random trees over operators of every precedence, and the open-ended forms. */
    private class TreeGenerator(private val random: Random) {
        private val binary =
            listOf(
                BinaryOperator.IMPLIES,
                BinaryOperator.OR,
                BinaryOperator.AND,
                BinaryOperator.EQUALS,
                BinaryOperator.LT,
                BinaryOperator.UNION,
                BinaryOperator.ADD,
                BinaryOperator.SUBTRACT,
                BinaryOperator.MULTIPLY,
                BinaryOperator.POWER,
            )

        @Suppress("CyclomaticComplexMethod")
        fun expression(depth: Int): Expression {
            if (depth == 0) return leaf()
            fun sub() = expression(depth - 1)
            return when (random.nextInt(FORMS)) {
                0,
                1,
                2,
                3 -> OperatorBinaryExpression(binary.random(random), sub(), sub())
                4 -> OperatorUnaryExpression(UnaryOperator.NOT, sub())
                5 -> OperatorUnaryExpression(UnaryOperator.NEGATE, sub())
                6 -> ExistsExpression(sub())
                7 -> IfExpression(sub(), sub(), sub())
                8 -> BooleanTestExpression(sub(), BooleanTestKind.NULL)
                9 -> MembershipExpression(MembershipOperator.IN, left = sub(), right = sub())
                10 -> BetweenExpression(sub(), sub(), sub())
                11 -> TimeBoundaryExpression(TimeBoundaryKind.START, sub())
                12 -> PropertyAccessExpression(sub(), Identifier("p"))
                13 -> IndexExpression(sub(), sub())
                14 -> ListTransformExpression(ListTransformKind.DISTINCT, sub())
                15 -> FunctionCallExpression(null, Identifier("F"), listOf(sub()))
                16 -> CastExpression(sub(), namedType())
                17 -> IsExpression(sub(), namedType())
                18 ->
                    QueryExpression(
                        sources =
                            listOf(
                                AliasedQuerySource(
                                    ExpressionQuerySource(identifierExpression("S")),
                                    Identifier("s"),
                                )
                            ),
                        lets = emptyList(),
                        inclusions = emptyList(),
                        where = sub(),
                    )
                else -> leaf()
            }
        }

        private fun leaf(): Expression =
            when (random.nextInt(3)) {
                0 -> LiteralExpression(IntLiteral(random.nextInt(10)))
                1 -> identifierExpression(listOf("A", "B", "C").random(random))
                else -> LiteralExpression(BooleanLiteral(random.nextBoolean()))
            }

        private fun identifierExpression(name: String): Expression =
            IdentifierExpression(QualifiedIdentifier(listOf(Identifier(name))))

        private fun namedType(): NamedTypeSpecifier =
            NamedTypeSpecifier(QualifiedIdentifier(listOf(Identifier("T"))))

        companion object {
            private const val FORMS = 22
        }
    }

    companion object {
        private const val SEED = 1902
        private const val GENERATED_TREES = 3000

        private val EXPRESSIONS =
            listOf(
                // Operators
                "1 + 2",
                "1 - 2",
                "2 * 3",
                "4 / 2",
                "5 div 2",
                "5 mod 2",
                "2 ^ 3",
                "'foo' & 'bar'",
                "1 = 1",
                "1 != 2",
                "'abc' ~ 'abc'",
                "'abc' !~ 'def'",
                "1 < 2",
                "1 <= 2",
                "2 > 1",
                "2 >= 1",
                "true and false",
                "true or false",
                "true xor false",
                "true implies false",
                "not false",
                "exists { 1, 2 }",
                "{ 1, 2 } union { 3 }",
                "{ 1, 2 } | { 3 }",
                "{ 1, 2 } intersect { 2 }",
                "{ 1, 2 } except { 2 }",
                "1 in { 1, 2 }",
                "{ 'a' } contains 'a'",
                "X in day of Interval[@2020-01-01, @2020-02-01]",
                "Interval[1, 5] contains day of X",
                "1 + 2 * 3",
                "(1 + 2) * 3",
                "1 - (2 - 3)",
                "((1))",
                "-(1)",
                "-x",
                "- -1",
                "+1",
                "-x.y",
                "(-x).y",
                "-1 ^ 2",
                "not X is null",
                "(not X) is null",
                "not A = B",
                "exists X and Y",
                "exists (X and Y)",
                "A or B and C implies D",
                // Conditionals
                "if true then 1 else 0",
                "(if A then 1 else 2) + 3",
                "X and if A then B else C",
                "case when 1 = 1 then 'a' else 'b' end",
                "case X when 1 then 'a' when 2 then 'b' else 'c' end",
                "case when A then 1 else 2 end + 1",
                // Temporal
                "1 between 0 and 2",
                "X properly between A and B + 1",
                "duration in days between @2020-01-01T00:00:00 and @2020-01-05T00:00:00",
                "years between A and B",
                "difference in days between @2020-01-05T00:00:00 and @2020-01-01T00:00:00",
                "duration in days of Interval[1, 5]",
                "difference in days of Interval[1, 5]",
                "width of Interval[1, 5]",
                "successor of 1",
                "predecessor of 2",
                "singleton from { 1 }",
                "point from Interval[1, 5]",
                "minimum Integer",
                "maximum Integer",
                "convert 5 to String",
                "convert X to 'mg'",
                "convert X to days",
                "start of Interval[1, 5]",
                "end of Interval[1, 5]",
                "start of X + 1",
                "year from @2020-01-01T00:00:00",
                "timezoneoffset from @2020-01-01T00:00:00",
                "date from X",
                "A starts on or before start of B",
                "A starts before or on end of B",
                "A starts on or after start of B",
                "A ends after end of B",
                "A occurs 3 days or more before B",
                "A starts less than 1 year after start of B",
                "A before day of B",
                "A same as B",
                "A starts same day or before start of B",
                "A ends same month or after B",
                "A includes B",
                "A properly includes day of start B",
                "A during B",
                "A starts properly included in day of B",
                "A within 3 days of B",
                "A ends properly within 3 days of start B",
                "A meets B",
                "A meets before day of B",
                "A overlaps after B",
                "A starts B",
                "A ends day of B",
                // Lists
                "distinct { 1, 1, 2 }",
                "flatten { { 1 }, { 2 } }",
                "(distinct X) union Y",
                "expand Interval[@2020-01-01T00:00:00, @2020-01-03T00:00:00]",
                "expand X per day",
                "collapse X per 2 days",
                "(expand X) union Y",
                "{ 1, 2 }[0]",
                "(1 + 2)[1]",
                "{ }",
                "List<Integer>{ 1, 2, 3 }",
                "List<Choice<Integer, String>>{ 1, 'two' }",
                // Selectors and literals
                "Tuple { value: 1, flag: true }",
                "{ value: 1 }",
                "Tuple { : }",
                "System.Code{ code: '140', system: 'http://snomed.info/sct' }",
                "FHIR.Patient { : }",
                "Interval[1, 5]",
                "Interval(1, 5)",
                "Interval[1, 5)",
                "Code '140' from \"SNOMED\"",
                "Code '1\\'2' from Lib.\"SNOMED\" display 'Two'",
                "Concept { Code '1' from S, Code '2' from S } display 'Both'",
                "5 'mg'",
                "3 days",
                "1 year",
                "5 'mg':10 'mL'",
                "5 'mg/dL'",
                "10L",
                "-10L",
                "-9223372036854775808L",
                "2147483647",
                "-2147483648",
                "-1.5",
                "1.50",
                "'it\\'s'",
                "'a\\tb\\nc'",
                "'\\u0041\\\\'",
                "'a/b'",
                "\"a\\\"b\"",
                "`a\\`b`",
                "\"\"",
                "@2020",
                "@2020-01-01",
                "@2020-01-01T00:00:00.000Z",
                "@T10:30:00",
                "null",
                "true",
                "%MeasurementPeriod",
                "%default",
                "%'Quoted Constant'",
                "%\"Quoted Identifier\"",
                // Types
                "1 is Integer",
                "1 as Integer",
                "cast 1 as Integer",
                "X as List<FHIR.Coding>",
                "X is Interval<DateTime>",
                "X is Tuple { a Integer, b String }",
                "X is null",
                "X is not null",
                "X is true",
                "X is not false",
                // Invocation
                "X.y.z",
                "X.f(1, 2)",
                "F()",
                "F(X, Y)",
                "\$this",
                "X.where(\$this > 1)",
                "Lib.\"Some Definition\"",
                // Queries and retrieves
                "[Encounter]",
                "[Encounter: \"Visits\"]",
                "[Encounter: type in \"Visits\"]",
                "[Encounter: type ~ \"Visit\"]",
                "[Encounter: Lib.\"Visits\"]",
                "[Patient -> Encounter: type in \"Visits\"]",
                "[Encounter: reason.coding in \"Visits\"]",
                "[Observation] O return O",
                "from [Observation] O return O",
                "from A a, B b return a",
                "[Encounter] E where E.status = 'finished'",
                "[Encounter] E let x: 1, y: 2 where x < y",
                "[Encounter] E with [Condition] C such that C.id = E.id",
                "[Encounter] E without [Condition] C such that C.id = E.id return E",
                "[Encounter] E return distinct E.id",
                "[Encounter] E return all E.id",
                "[Encounter] E sort asc",
                "[Encounter] E sort descending",
                "[Encounter] E sort by id desc, period.start",
                "({ 1, 2 }) X aggregate R starting 0: R + X",
                "({ 1, 2 }) X aggregate distinct R starting (1 + 1): R + X",
                "({ 1, 2 }) X aggregate R starting 'a': R & X",
                "({ 1, 2 }) X aggregate R: R",
                "exists [Encounter] E where E.x",
                "exists ([Encounter] E where E.x) and Y",
                "Count([Encounter] E where E.x)",
                "([Encounter] E where E.x) union Y",
                "[Encounter] E where E.x in ([Condition] C where C.y return C.x)",
                "[Encounter] E where exists (E.x X where X.y) return E",
                "([Encounter]).id",
                // Prefix operators are read whole, so one can be another's operand
                "not exists X",
                "not exists X and not exists Y",
                "exists not X",
                "- start of X",
                // A query without clauses ends at its alias
                "[Observation] a union [Procedure] b",
                // A query in a clause takes a following clause only if it could come next
                "Patient P let a: X x where x.y, b: Y y where y.z where a = b",
                "X x where Y y where y.z return x",
                "X x where (Y y return y.z) return x",
                "X x let a: Y y where y.z with Z z such that z.a return x",
            )
    }
}
