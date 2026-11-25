package org.hl7.cql.ast

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        val failures = CqlSamples.EXPRESSIONS.mapNotNull { roundTripFailure(it) }
        if (failures.isNotEmpty()) fail(failures.joinToString("\n\n"))
    }

    @Test
    fun printsALibrarySoItParsesTheSame() {
        assertLibraryRoundTrips(CqlSamples.LIBRARY)
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
    fun refusesToPrintWhatCouldNotBeParsed() {
        for (text in listOf("using", "define A", "define A: if", "define A: 2147483648")) {
            val library = Builder().parseLibrary(text).library
            assertFailsWith<IllegalArgumentException>(text) { Printer.print(library) }
        }
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
    }
}
