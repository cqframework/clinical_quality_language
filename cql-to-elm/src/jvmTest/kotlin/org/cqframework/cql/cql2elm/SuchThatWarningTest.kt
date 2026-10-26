package org.cqframework.cql.cql2elm

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class SuchThatWarningTest {
    private fun suchThatWarnings(define: String): List<String> {
        val libraryManager = LibraryManager(ModelManager(), CqlCompilerOptions())
        val translator =
            CqlTranslator.fromText("library Test version '1.0.0'\n$define", libraryManager)
        assertThat(
            translator.errors.map { it.message }.toString(),
            translator.errors,
            Matchers.empty(),
        )
        return translator.warnings.map { it.message!! }.filter { it.contains("such that") }
    }

    @Test
    fun withSuchThatNotReferencingSourceWarns() {
        // #1631
        val warnings = suchThatWarnings("define Valid: ({1, 2}) A with ({3}) B such that true")
        assertThat(warnings.toString(), warnings, Matchers.hasSize(1))
        assertThat(warnings[0], Matchers.containsString("included source B"))
    }

    @Test
    fun withoutSuchThatNotReferencingSourceWarns() {
        val warnings = suchThatWarnings("define Valid: ({1, 2}) A without ({3}) B such that A > 1")
        assertThat(warnings.toString(), warnings, Matchers.hasSize(1))
    }

    @Test
    fun suchThatReferencingSourceDoesNotWarn() {
        assertThat(
            suchThatWarnings("define Valid: ({1, 2}) A with ({3}) B such that B > A"),
            Matchers.empty(),
        )
        assertThat(
            suchThatWarnings(
                "define Valid: ({Tuple { x: 1 }}) A without ({Tuple { y: 1 }}) B such that B.y = A.x"
            ),
            Matchers.empty(),
        )
    }
}
