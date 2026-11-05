package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.toCqlString

/**
 * The contract of [State.evaluatedResources]: it records what retrieves returned, and does not
 * deduplicate.
 *
 * Deduplicating would require the engine to know what makes two model values the same record.
 * Neither ELM nor the ModelInfo defines that, so the engine stays out of it and callers, which know
 * their model, collapse on the key that suits them.
 *
 * [retainsEqualButDistinctValues] is written to fail against a stack that deduplicates by value —
 * the previous `HashSet<Value?>` behaviour — rather than to pass either way.
 */
internal class StateEvaluatedResourcesTest {

    private fun classInstance(id: String): ClassInstance =
        ClassInstance(
            QName("http://hl7.org/fhir", "Encounter", "FHIR"),
            mutableMapOf("id" to id.toCqlString()),
        )

    /**
     * A freshly constructed [State] has no evaluated-resource frame; the engine pushes the root one
     * as it begins evaluating. [State.clearEvaluatedResources] establishes that same root frame.
     */
    private fun newState(): State = State(Environment(null)).also { it.clearEvaluatedResources() }

    @Test
    fun retainsEqualButDistinctValues() {
        // The FHIR retrieve path builds a fresh value per retrieve, so a record reached by several
        // expressions arrives as several structurally-equal instances. Under the previous
        // value-equality set these collapsed to one and the engine reported one evaluated resource
        // where two retrieves had happened.
        val firstRetrieve = classInstance("Encounter1")
        val secondRetrieve = classInstance("Encounter1")

        assertEquals(firstRetrieve, secondRetrieve, "fixture should be structurally equal")
        assertFalse(firstRetrieve === secondRetrieve, "fixture should be two distinct instances")

        val state = newState()
        state.evaluatedResources!!.add(firstRetrieve)
        state.evaluatedResources!!.add(secondRetrieve)

        assertEquals(
            2,
            state.evaluatedResources!!.size,
            "evaluated resources are collected, not deduplicated by value",
        )
    }

    @Test
    fun collapsesTheSameValueAddedTwice() {
        val onlyRetrieve = classInstance("Encounter1")

        val state = newState()
        state.evaluatedResources!!.add(onlyRetrieve)
        state.evaluatedResources!!.add(onlyRetrieve)

        assertEquals(1, state.evaluatedResources!!.size, "identity still collapses a repeated add")
    }

    @Test
    fun carriesEqualButDistinctValuesUpTheCallStack() {
        // Popping a frame folds its evaluated resources into the caller's. That merge must not
        // collapse them either, or the nesting depth of an expression would change the count.
        val outerRetrieve = classInstance("Encounter1")
        val innerRetrieve = classInstance("Encounter1")

        val state = newState()
        state.evaluatedResources!!.add(outerRetrieve)

        state.pushEvaluatedResourceStack()
        state.evaluatedResources!!.add(innerRetrieve)
        state.popEvaluatedResourceStack()

        assertEquals(2, state.evaluatedResources!!.size)
        assertTrue(state.evaluatedResources!!.any { it === outerRetrieve })
        assertTrue(state.evaluatedResources!!.any { it === innerRetrieve })
    }

    @Test
    fun clearEvaluatedResourcesResetsToAnEmptyStack() {
        val state = newState()
        state.evaluatedResources!!.add(classInstance("Encounter1"))

        state.clearEvaluatedResources()

        assertEquals(0, state.evaluatedResources!!.size)
    }
}
