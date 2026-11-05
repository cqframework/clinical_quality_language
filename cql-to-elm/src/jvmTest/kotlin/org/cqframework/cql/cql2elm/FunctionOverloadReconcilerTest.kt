package org.cqframework.cql.cql2elm

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Covers [FunctionOverloadReconciler]: same-named function overloads whose declared (profile) types
 * collapse to the same runtime ELM signature are either collapsed (equivalent bodies) or reported
 * as a translation error (differing bodies), instead of compiling to ambiguous ELM that throws at
 * runtime. See issues #1435 and #1855.
 */
internal class FunctionOverloadReconcilerTest {
    private class ProfileCollisionModelInfoProvider : ModelInfoProvider {
        override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
            if (modelIdentifier.id == "ProfileCollision") {
                val stream =
                    ProfileCollisionModelInfoProvider::class
                        .java
                        .getResourceAsStream("ModelTests/profile-collision-modelinfo.xml")
                return parseModelInfoXml(stream!!.asSource().buffered())
            }
            return null
        }
    }

    private fun translate(cql: String): CqlTranslator {
        val modelManager = ModelManager()
        modelManager.modelInfoLoader.registerModelInfoProvider(ProfileCollisionModelInfoProvider())
        val translator = CqlTranslator.fromText(cql, LibraryManager(modelManager))
        translator.toELM()
        return translator
    }

    private fun recordedFunctionDefs(translator: CqlTranslator): List<FunctionDef> {
        return translator.toELM()!!.statements!!.def.filterIsInstance<FunctionDef>().filter {
            it.name == "recorded"
        }
    }

    @Test
    fun collapsesOverloadsWithEquivalentBodiesAndDifferingOperandNames() {
        // The #1855 shape: the two bodies are structurally identical but name the operand
        // differently. They must collapse to a single FunctionDef with no errors.
        val cql =
            """
            library ProfileCollisionTest version '1.0.0'

            using ProfileCollision version '1.0.0'

            parameter p ProcedureNotDone

            define fluent function recorded(procedure Procedure):
              procedure.status

            define fluent function recorded(procedureNotDone ProcedureNotDone):
              procedureNotDone.status

            define "Test": p.recorded()
            """
                .trimIndent()
        val translator = translate(cql)
        assertThat(translator.errors.toString(), translator.errors.size, `is`(0))
        assertEquals(1, recordedFunctionDefs(translator).size, "overloads should collapse to one")
    }

    @Test
    fun collapsesOverloadsWithIdenticalBodies() {
        // The #1435 shape: identical bodies including the operand name.
        val cql =
            """
            library ProfileCollisionTest version '1.0.0'

            using ProfileCollision version '1.0.0'

            define fluent function recorded(p Procedure):
              p.status

            define fluent function recorded(p ProcedureNotDone):
              p.status
            """
                .trimIndent()
        val translator = translate(cql)
        assertThat(translator.errors.toString(), translator.errors.size, `is`(0))
        assertEquals(1, recordedFunctionDefs(translator).size, "overloads should collapse to one")
    }

    @Test
    fun reportsErrorForCollidingOverloadsWithDifferentBodies() {
        // Same runtime signature, different behavior: the translator cannot represent both, so it
        // must report an error rather than emit ambiguous ELM.
        val cql =
            """
            library ProfileCollisionTest version '1.0.0'

            using ProfileCollision version '1.0.0'

            define fluent function recorded(procedure Procedure):
              procedure.status

            define fluent function recorded(procedureNotDone ProcedureNotDone):
              'not recorded'
            """
                .trimIndent()
        val translator = translate(cql)
        assertEquals(1, translator.errors.size, "expected one collision error")
        assertThat(translator.errors[0].message, containsString("indistinguishable at runtime"))
        assertThat(translator.errors[0].message, containsString("recorded"))
    }

    @Test
    fun leavesDistinctRuntimeSignaturesUntouched() {
        // Overloads on genuinely distinct runtime types must not be collapsed or flagged.
        val cql =
            """
            library ProfileCollisionTest version '1.0.0'

            using ProfileCollision version '1.0.0'

            define fluent function recorded(procedure Procedure):
              procedure.status

            define fluent function recorded(s String):
              s
            """
                .trimIndent()
        val translator = translate(cql)
        assertThat(translator.errors.toString(), translator.errors.size, `is`(0))
        assertEquals(
            2,
            recordedFunctionDefs(translator).size,
            "distinct overloads must be preserved",
        )
    }
}
