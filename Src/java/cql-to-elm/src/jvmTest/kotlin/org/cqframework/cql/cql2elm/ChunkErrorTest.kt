package org.cqframework.cql.cql2elm

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.SystemModelInfoProvider
import org.junit.jupiter.api.Test

/**
 * Regression test for a bug where the translator silently drops statements after the first
 * expression definition when processing CQL without a `library` declaration.
 *
 * The root cause is in `LibraryBuilder`'s chunk tracking: after processing the first expression
 * definition, subsequent definitions trigger "Child chunk cannot be added because it is not
 * contained within the parent chunk", causing them to be silently dropped from the ELM output.
 *
 * This file reproduces the issue with a minimal 3-statement CQL file using DateTime constructors.
 * The same bug affects `OperatorTests/DateTimeOperators.cql` (1 of 71 statements emitted).
 */
class ChunkErrorTest {

    @Test
    fun allStatementsEmittedWithoutLibraryDeclaration() {
        val modelManager =
            ModelManager().apply {
                modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            }
        val libraryManager = LibraryManager(modelManager)
        val translator =
            CqlTranslator.fromSource(
                ChunkErrorTest::class
                    .java
                    .getResourceAsStream("ChunkErrorTest.cql")!!
                    .asSource()
                    .buffered(),
                libraryManager,
            )
        val library = translator.toELM()!!
        val stmtCount = library.statements?.def?.size ?: 0

        assertTrue(
            translator.errors.isEmpty(),
            "Expected no errors, got: ${translator.errors.map { it.message }}",
        )
        assertEquals(
            3,
            stmtCount,
            "Expected all 3 statements to be emitted, but only $stmtCount were. " +
                "Statements: ${library.statements?.def?.map { it.name }}",
        )
    }
}
