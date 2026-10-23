package org.cqframework.cql.cql2elm.operators

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Multiply
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef

/**
 * [#1862](https://github.com/cqframework/clinical_quality_language/issues/1862): An operand of type
 * Any must not make the result type of Coalesce Any when other operands have a concrete type.
 *
 * Each expression is translated in its own library, because operator signatures resolved earlier in
 * a library can affect how later calls resolve.
 */
internal class CoalesceTypeInferenceTest {
    private fun translate(expression: String): Expression {
        val cql = "library CoalesceTest\n\ndefine Sequence: { 1, 2, 3 }\ndefine Test: $expression\n"
        val modelManager =
            ModelManager().apply {
                modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            }
        val translator = CqlTranslator.fromText(cql, LibraryManager(modelManager))
        assertEquals(0, translator.errors.size, translator.errors.joinToString { "${it.message}" })
        return translator.toELM()!!.statements!!.def.single { it.name == "Test" }.expression!!
    }

    @Test
    fun coalesceInAggregateWithoutStartingValue() {
        val query = assertIs<Query>(translate("Sequence X aggregate R: Coalesce(R, 1) * X"))
        val multiply = assertIs<Multiply>(query.aggregate!!.expression)
        val coalesce = assertIs<Coalesce>(multiply.operand[0])
        assertEquals("System.Integer", coalesce.resultType.toString())
        val cast = assertIs<As>(coalesce.operand[0])
        assertIs<QueryLetRef>(cast.operand)
    }

    @Test
    fun coalesceWithNullFirst() {
        val coalesce = assertIs<Coalesce>(translate("Coalesce(null, 1)"))
        assertEquals("System.Integer", coalesce.resultType.toString())
        assertIs<As>(coalesce.operand[0])
    }

    @Test
    fun coalesceWithNullFirstUsesCommonTypeOfOtherOperands() {
        assertEquals("System.Decimal", translate("Coalesce(null, 1, 2.5)").resultType.toString())
    }

    @Test
    fun coalesceWithOnlyNullOperandsStaysAny() {
        assertEquals("System.Any", translate("Coalesce(null, null)").resultType.toString())
    }
}
