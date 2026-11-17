package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.ucum.UcumService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class ArbitraryUnitsTest {
    private fun translate(cql: String): CqlTranslator {
        val libraryManager = LibraryManager(ModelManager(), CqlCompilerOptions())
        return CqlTranslator.fromText(cql, libraryManager)
    }

    @Test
    fun arbitraryUnitQuantityProducesWarning() {
        // #1602: authoring systems must warn that arbitrary units are context-dependent.
        val translator =
            translate(
                """
                library Test version '1.0.0'
                define ArbitraryComparison: 10000 '[IU]/mL' > 5000 '[IU]/mL'
                """
                    .trimIndent()
            )

        assertThat(
            translator.errors.map { it.message }.toString(),
            translator.errors,
            Matchers.empty(),
        )

        val arbitraryWarnings =
            translator.warnings.map { it.message!! }.filter { it.contains("arbitrary") }
        // One warning per arbitrary-unit literal, naming the offending unit.
        assertThat(arbitraryWarnings.toString(), arbitraryWarnings, Matchers.hasSize(2))
        assertThat(arbitraryWarnings[0], Matchers.containsString("[IU]/mL"))
    }

    @Test
    fun nonArbitraryUnitQuantityProducesNoWarning() {
        val translator =
            translate(
                """
                library Test version '1.0.0'
                define PlainComparison: 5 'mg' > 3 'mg'
                """
                    .trimIndent()
            )

        assertThat(
            translator.errors.map { it.message }.toString(),
            translator.errors,
            Matchers.empty(),
        )

        val arbitraryWarnings =
            translator.warnings.map { it.message!! }.filter { it.contains("arbitrary") }
        assertThat(arbitraryWarnings.toString(), arbitraryWarnings, Matchers.empty())
    }

    @Test
    fun calendarDurationQuantityCompilesWithoutUcumService() {
        // #1869: a calendar duration such as `4 weeks` is not a UCUM unit, so compiling it must
        // not need a UCUM service (none is available by default on JS and Wasm).
        val noUcumService = lazy<UcumService> { error("No UCUM service available") }
        val libraryManager =
            LibraryManager(ModelManager(), CqlCompilerOptions(), null, noUcumService)
        val translator =
            CqlTranslator.fromText(
                """
                library Test version '1.0.0'
                define FourWeeks: 4 weeks
                define OneYear: 1 year
                define TwoMilliseconds: 2 milliseconds
                """
                    .trimIndent(),
                libraryManager,
            )

        assertThat(
            translator.errors.map { it.message }.toString(),
            translator.errors,
            Matchers.empty(),
        )
        assertThat(noUcumService.isInitialized(), Matchers.`is`(false))
    }
}
