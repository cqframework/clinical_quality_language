package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.CqlException

/**
 * Tests that [Libraries] resolver methods throw [CqlException] (not NPE) when the library lacks the
 * corresponding ELM section (e.g., no parameters, no statements).
 */
internal class LibrariesNullSafetyTest : CqlTestBase() {

    private val emptyLibrary by lazy { getLibrary(toElmIdentifier("EmptyLibrary"))!! }

    private val libraryWithParams by lazy {
        getLibrary(toElmIdentifier("ResolveParameterDefaultTest"))!!
    }

    @Test
    fun resolveParameterRef_throws_CqlException_for_library_without_parameters() {
        val e =
            assertThrows(CqlException::class.java) {
                Libraries.resolveParameterRef("SomeParam", emptyLibrary)
            }
        assertTrue(e.message!!.contains("Could not resolve parameter reference"))
    }

    @Test
    fun resolveExpressionRef_throws_CqlException_for_library_without_statements() {
        val e =
            assertThrows(CqlException::class.java) {
                Libraries.resolveExpressionRef("SomeExpr", emptyLibrary)
            }
        assertTrue(e.message!!.contains("Could not resolve expression reference"))
    }

    @Test
    fun resolveCodeSystemRef_throws_CqlException_for_library_without_codeSystems() {
        val e =
            assertThrows(CqlException::class.java) {
                Libraries.resolveCodeSystemRef("SomeCodeSystem", emptyLibrary)
            }
        assertTrue(e.message!!.contains("Could not resolve code system reference"))
    }

    @Test
    fun resolveValueSetRef_throws_CqlException_for_library_without_valueSets() {
        val e =
            assertThrows(CqlException::class.java) {
                Libraries.resolveValueSetRef("SomeValueSet", emptyLibrary)
            }
        assertTrue(e.message!!.contains("Could not resolve value set reference"))
    }

    @Test
    fun resolveCodeRef_throws_CqlException_for_library_without_codes() {
        val e =
            assertThrows(CqlException::class.java) {
                Libraries.resolveCodeRef("SomeCode", emptyLibrary)
            }
        assertTrue(e.message!!.contains("Could not resolve code reference"))
    }

    @Test
    fun resolveConceptRef_throws_CqlException_for_library_without_concepts() {
        val e =
            assertThrows(CqlException::class.java) {
                Libraries.resolveConceptRef("SomeConcept", emptyLibrary)
            }
        assertTrue(e.message!!.contains("Could not resolve concept reference"))
    }

    @Test
    fun resolveParameterDefault_throws_CqlException_for_library_without_parameters() {
        val e =
            assertThrows(CqlException::class.java) {
                engine.resolveParameterDefault(toElmIdentifier("EmptyLibrary"), "SomeParam")
            }
        assertTrue(e.message!!.contains("Could not resolve parameter reference"))
    }

    // -- hasParameterDef tests --

    @Test
    fun hasParameterDef_returns_false_for_library_without_parameters() {
        assertFalse(Libraries.hasParameterDef("SomeParam", emptyLibrary))
    }

    @Test
    fun hasParameterDef_returns_true_for_existing_parameter() {
        assertTrue(Libraries.hasParameterDef("Measurement Period", libraryWithParams))
    }

    @Test
    fun hasParameterDef_returns_false_for_nonexistent_parameter() {
        assertFalse(Libraries.hasParameterDef("Nonexistent", libraryWithParams))
    }

    // -- hasLibrary tests --

    @Test
    fun hasLibrary_returns_true_for_existing_library() {
        assertTrue(engine.hasLibrary(toElmIdentifier("EmptyLibrary")))
    }

    @Test
    fun hasLibrary_returns_false_for_nonexistent_library() {
        assertFalse(engine.hasLibrary(toElmIdentifier("NonexistentLibrary")))
    }

    // -- hasParameter tests --

    @Test
    fun hasParameter_returns_true_for_existing_parameter() {
        assertTrue(
            engine.hasParameter(
                toElmIdentifier("ResolveParameterDefaultTest"),
                "Measurement Period",
            )
        )
    }

    @Test
    fun hasParameter_returns_false_for_nonexistent_parameter() {
        assertFalse(
            engine.hasParameter(toElmIdentifier("ResolveParameterDefaultTest"), "Nonexistent")
        )
    }

    @Test
    fun hasParameter_throws_for_nonexistent_library() {
        assertThrows(Exception::class.java) {
            engine.hasParameter(toElmIdentifier("NonexistentLibrary"), "SomeParam")
        }
    }

    @Test
    fun hasParameter_returns_false_for_library_without_parameters() {
        assertFalse(engine.hasParameter(toElmIdentifier("EmptyLibrary"), "SomeParam"))
    }
}
