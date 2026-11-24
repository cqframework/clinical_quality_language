package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.Instance
import org.hl7.elm.r1.InstanceElement
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.SubsumedBy
import org.hl7.elm.r1.Subsumes
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.runtime.Boolean

/**
 * Direct-ELM tests for the visitSubsumes and visitSubsumedBy overrides.
 *
 * cql-to-elm currently resolves `Subsumes(a, b)` / `SubsumedBy(a, b)` to a bare FunctionRef without
 * libraryName=System, so a plain CQL source text cannot reach the visitSubsumes / visitSubsumedBy
 * branch of the EvaluationVisitor dispatcher. These tests construct Subsumes and SubsumedBy ELM
 * nodes directly and invoke the visitor against them, which is the dispatcher path the override
 * exists to cover (ELM authored by non-cql-to-elm tooling, or by a future translator that emits the
 * direct operator node).
 */
internal class SubsumesNullPropagationTest : CqlTestBase() {

    private fun stateWithEmptyLibrary(): State {
        // Compile and evaluate a trivial library so the engine wires up
        // a State with a current library; we then reach the visitor
        // directly for Subsumes/SubsumedBy, which the engine's own
        // translator path does not reach today.
        engine.evaluate {
            library(VersionedIdentifier().withId("SubsumesNullPropagation")) {
                expressions("Trivial")
            }
        }
        return engine.state
    }

    private fun codeInstance(code: String, system: String): Instance {
        val cqlType = QName("urn:hl7-org:elm-types:r1", "Code")
        val stringType = QName("urn:hl7-org:elm-types:r1", "String")
        return Instance().apply {
            classType = cqlType
            element.add(
                InstanceElement()
                    .withName("code")
                    .withValue(Literal().withValueType(stringType).withValue(code))
            )
            element.add(
                InstanceElement()
                    .withName("system")
                    .withValue(Literal().withValueType(stringType).withValue(system))
            )
        }
    }

    private fun nullElm(): Null = Null()

    private fun subsumes(
        left: org.hl7.elm.r1.Expression,
        right: org.hl7.elm.r1.Expression,
    ): Subsumes =
        Subsumes().apply {
            operand.add(left)
            operand.add(right)
        }

    private fun subsumedBy(
        left: org.hl7.elm.r1.Expression,
        right: org.hl7.elm.r1.Expression,
    ): SubsumedBy =
        SubsumedBy().apply {
            operand.add(left)
            operand.add(right)
        }

    override val cqlSubdirectory: String = "SubsumesNullPropagationTest"

    @Test
    fun subsumes_returns_null_when_left_operand_is_null() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm = subsumes(nullElm(), codeInstance("A", "http://example.org/cs"))
        assertNull(visitor.visitSubsumes(elm, state))
    }

    @Test
    fun subsumes_returns_null_when_right_operand_is_null() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm = subsumes(codeInstance("A", "http://example.org/cs"), nullElm())
        assertNull(visitor.visitSubsumes(elm, state))
    }

    @Test
    fun subsumes_returns_null_when_both_operands_are_null() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm = subsumes(nullElm(), nullElm())
        assertNull(visitor.visitSubsumes(elm, state))
    }

    @Test
    fun subsumes_returns_true_for_equivalent_codes() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm =
            subsumes(
                codeInstance("A", "http://example.org/cs"),
                codeInstance("A", "http://example.org/cs"),
            )
        assertEquals(Boolean.TRUE, visitor.visitSubsumes(elm, state))
    }

    @Test
    fun subsumes_returns_false_for_non_equivalent_codes() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm =
            subsumes(
                codeInstance("A", "http://example.org/cs"),
                codeInstance("B", "http://example.org/cs"),
            )
        assertEquals(Boolean.FALSE, visitor.visitSubsumes(elm, state))
    }

    @Test
    fun subsumedBy_returns_null_when_left_operand_is_null() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm = subsumedBy(nullElm(), codeInstance("A", "http://example.org/cs"))
        assertNull(visitor.visitSubsumedBy(elm, state))
    }

    @Test
    fun subsumedBy_returns_null_when_right_operand_is_null() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm = subsumedBy(codeInstance("A", "http://example.org/cs"), nullElm())
        assertNull(visitor.visitSubsumedBy(elm, state))
    }

    @Test
    fun subsumedBy_returns_null_when_both_operands_are_null() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm = subsumedBy(nullElm(), nullElm())
        assertNull(visitor.visitSubsumedBy(elm, state))
    }

    @Test
    fun subsumedBy_returns_true_for_equivalent_codes() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm =
            subsumedBy(
                codeInstance("A", "http://example.org/cs"),
                codeInstance("A", "http://example.org/cs"),
            )
        assertEquals(Boolean.TRUE, visitor.visitSubsumedBy(elm, state))
    }

    @Test
    fun subsumedBy_returns_false_for_non_equivalent_codes() {
        val state = stateWithEmptyLibrary()
        val visitor = EvaluationVisitor()
        val elm =
            subsumedBy(
                codeInstance("A", "http://example.org/cs"),
                codeInstance("B", "http://example.org/cs"),
            )
        assertEquals(Boolean.FALSE, visitor.visitSubsumedBy(elm, state))
    }
}
