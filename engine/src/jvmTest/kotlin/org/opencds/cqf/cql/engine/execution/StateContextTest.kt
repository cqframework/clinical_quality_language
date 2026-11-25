package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager

internal class StateContextTest {
    private fun newState(): State {
        val environment = Environment(LibraryManager(ModelManager()))
        return State(environment)
    }

    @Test
    fun exitContextRestoresOuterValue() {
        val state = newState()

        val outer = state.enterContext("Patient", "a")
        val inner = state.enterContext("Patient", "b")
        assertEquals("Patient", state.getCurrentContext())
        assertEquals("b", state.currentContextValue)

        state.exitContext(inner)
        assertEquals("a", state.currentContextValue)

        state.exitContext(outer)
        assertNull(state.getCurrentContext())
        assertNull(state.currentContextValue)
    }

    @Test
    fun inheritedValueUsesStackNotStaleMapEntry() {
        val state = newState()

        val outer = state.enterContext("Patient", "a")
        val inner = state.enterContext("Patient", "b")
        state.exitContext(inner)

        assertEquals("a", state.inheritedContextValue("Patient"))
        state.exitContext(outer)
    }

    @Test
    fun setContextValueIsFallbackWhenNoEntryIsActive() {
        val state = newState()

        state.setContextValue("Patient", "x")

        assertEquals("x", state.inheritedContextValue("Patient"))
    }
}
