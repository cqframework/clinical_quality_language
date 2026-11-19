package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Queries, their clauses, and retrieves. */
class BuilderQueryTest {

    private val builder = Builder()

    private fun assertExpression(text: String, expected: ExpressionSpec) {
        builder.parseExpression(text).assertMatches(expected)
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
                result = ReturnClause(expression = IdentifierExpression(qualified("O"))),
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
                            target = IdentifierExpression(qualified("O")),
                            property = Identifier("value"),
                        ),
                )
            val whereExpression =
                OperatorBinaryExpression(
                    operator = BinaryOperator.NOT_EQUALS,
                    left = IdentifierExpression(qualified("Value")),
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
                            left = IdentifierExpression(qualified("Count")),
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
    fun parsesSortDirections() {
        fun sortOf(text: String): SortClause {
            val result = builder.parseExpression("[Observation] O $text")
            assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
            return (result.expression as QueryExpression).sort!!
        }

        assertEquals(SortDirection.DESC, sortOf("sort desc").direction)
        assertEquals(SortDirection.ASCENDING, sortOf("sort ascending").direction)
        assertTrue(sortOf("sort desc").items.isEmpty())

        val items = sortOf("sort by id, status asc, value descending").items
        assertEquals(
            listOf(null, SortDirection.ASC, SortDirection.DESCENDING),
            items.map { it.direction },
        )
        assertEquals(null, sortOf("sort by id").direction)
    }

    @Test
    fun parsesACodePathWithIndexes() {
        val retrieve =
            Builder()
                .parseExpression("[Encounter: reason.coding[0]['code'] in \"V\"]")
                .expression
                .withoutSourceDetails() as RetrieveExpression
        assertEquals(
            SimplePath(
                listOf(
                    PropertySegment(Identifier("reason")),
                    PropertySegment(Identifier("coding")),
                    IndexSegment(LiteralExpression(IntLiteral(0))),
                    IndexSegment(LiteralExpression(StringLiteral("code"))),
                )
            ),
            retrieve.codePath,
        )
    }

    @Test
    fun parsesAQuantityAsTheStartOfAnAggregate() {
        val query =
            Builder().parseExpression("({ 1 }) X aggregate R starting 1 'mg': R").expression
                as QueryExpression
        assertEquals(
            LiteralExpression(QuantityLiteral("1", "mg")),
            query.aggregate!!.starting!!.withoutSourceDetails(),
        )
    }
}
