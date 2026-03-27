package org.cqframework.cql.cql2elm.analysis

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.TestLibrarySourceProvider
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
import org.hl7.cql.ast.Builder
import org.hl7.cql.model.SystemModelInfoProvider

/**
 * Tests that FHIR choice-typed properties resolve correctly through the analysis pipeline.
 * Specifically: model conversions (FHIRHelpers.ToDateTime etc.) must be recorded for
 * choice-typed operands in operators like In/Contains.
 */
class FhirChoiceResolutionTest {

    private fun analyze(cql: String): SemanticAnalyzer.Result {
        val mm = ModelManager().apply {
            modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            modelInfoLoader.registerModelInfoProvider(FhirModelInfoProvider())
        }
        val lm = LibraryManager(mm).apply {
            librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        }
        val ast = Builder().parseLibrary(cql)
        assertTrue(ast.problems.isEmpty(), "Parse problems: ${ast.problems}")
        return SemanticAnalyzer(modelManager = mm, libraryManager = lm).analyze(ast.library)
    }

    @Test
    fun choicePropertyInMembershipResolvesWithModelConversion() {
        // P.performed is a FHIR choice type (dateTime|Age|Period|Range|string).
        // When used in "P.performed in Interval<DateTime>", the resolution should
        // include choice narrowing (As FHIR.dateTime) + model conversion (FHIRHelpers.ToDateTime).
        val result = analyze("""
            library TestChoiceResolution
            using FHIR version '4.0.1'
            include FHIRHelpers version '4.0.1'
            parameter "MP" Interval<DateTime>
            context Patient
            define Test: [Procedure] P where P.performed in MP
        """.trimIndent())

        val typeTable = result.semanticModel.typeTable
        val conversionTable = result.semanticModel.conversionTable

        // Find the membership expression (In)
        val library = result.library
        val testDef = library.statements.filterIsInstance<org.hl7.cql.ast.ExpressionDefinition>()
            .first { it.name.value == "Test" }
        val queryExpr = testDef.expression as org.hl7.cql.ast.QueryExpression
        val whereExpr = queryExpr.where!!

        // The where clause should have an operator resolution recorded
        val resolution = typeTable.getOperatorResolution(whereExpr)
        assertNotNull(resolution, "Where clause should have an operator resolution")

        // The resolution should have conversions (choice narrowing + model conversion)
        assertTrue(resolution.hasConversions(), "Resolution should have conversions for choice type")

        // Check that the conversion includes a model conversion operator (FHIRHelpers.ToDateTime)
        val leftConversion = resolution.conversions.firstOrNull()
        assertNotNull(leftConversion, "Left operand should have a conversion")
        println("Left conversion: isCast=${leftConversion.isCast}, operator=${leftConversion.operator?.name}, inner=${leftConversion.conversion?.operator?.name}")
    }
}
