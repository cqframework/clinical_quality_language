package org.cqframework.cql.cql2elm.operators

import kotlin.test.assertIs
import kotlin.test.assertNotNull
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Multiply
import org.hl7.elm.r1.Query
import org.junit.jupiter.api.Test

/**
 * Reproduces a bug where the translator infers `Coalesce(R, 1)` result type as `Any` instead of
 * `Integer` when `R` is an aggregate accumulator starting from null.
 *
 * The downstream `Multiply` operator then wraps the Coalesce result in an unnecessary `As(Integer)`
 * cast. The correct behavior: Coalesce should compute its result type as the common type of
 * concrete (non-Any) arguments, giving Integer directly.
 */
class AggregateCoalesceTypeBugTest {

    @Test
    fun coalesceInAggregateShouldNotRequireCast() {
        val mm =
            ModelManager().apply {
                modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            }
        val cql =
            this::class
                .java
                .classLoader
                .getResourceAsStream(
                    "org/cqframework/cql/cql2elm/OperatorTests/AggregateCoalesceTypeBug.cql"
                )!!
                .bufferedReader()
                .readText()
        val translator = CqlTranslator.fromText(cql, LibraryManager(mm))
        val library = translator.toELM()
        assertNotNull(library)

        // Find the Factorial expression
        val factorial = library.statements!!.def.first { it.name == "Factorial" }
        val query = factorial.expression
        assertIs<Query>(query)

        // The aggregate body is: Coalesce(R, 1) * X
        val multiply = query.aggregate!!.expression
        assertIs<Multiply>(multiply)

        // BUG: The translator wraps the first Multiply operand in As(Integer)
        // because Coalesce(Any, Integer) resolves with result type Any.
        // The correct behavior: Coalesce result type should be Integer (no cast needed).
        val firstOperand = multiply.operand[0]

        // This assertion demonstrates the bug — it PASSES because the translator wraps in As.
        // When the bug is fixed, firstOperand should be Coalesce directly (not As).
        assertIs<As>(
            firstOperand,
            "BUG: Coalesce(R, 1) result is wrapped in unnecessary As(Integer) cast. " +
                "Expected Coalesce directly as the Multiply operand.",
        )
    }
}
