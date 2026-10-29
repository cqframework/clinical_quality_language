package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cqframework.cql.shared.QName

internal class ClassInstanceTest {
    private val type = QName("http://example.org", "Coding", "ExampleModel")
    private val layout = ClassLayout(listOf("system", "code", "display"))

    private fun layoutInstance(
        system: kotlin.String?,
        code: kotlin.String?,
        display: kotlin.String?,
    ) =
        ClassInstance(
            type,
            layout,
            arrayOf(system?.toCqlString(), code?.toCqlString(), display?.toCqlString()),
        )

    @Test
    fun elementsOmitNullSlotsInLayoutOrder() {
        val instance = layoutInstance("http://loinc.org", null, "Systolic")

        assertEquals(listOf("system", "display"), instance.elements.keys.toList())
        assertEquals(2, instance.elements.size)
        assertEquals("Systolic".toCqlString(), instance["display"])
        assertNull(instance["code"])
        assertNull(instance["unknown"])
        assertTrue(instance.has("system"))
        assertFalse(instance.has("code"))
        assertFalse(instance.has("unknown"))
    }

    @Test
    fun mutatingElementsWritesSlots() {
        val instance = layoutInstance("http://loinc.org", null, null)

        instance.elements["code"] = "8480-6".toCqlString()
        assertEquals("8480-6".toCqlString(), instance["code"])

        assertEquals("http://loinc.org".toCqlString(), instance.elements.remove("system"))
        assertFalse(instance.has("system"))

        instance.elements["code"] = null
        assertTrue(instance.elements.isEmpty())
    }

    @Test
    fun elementsTheLayoutDoesNotNameAreKept() {
        val instance = layoutInstance(null, "8480-6", null)

        instance.elements["userSelected"] = Boolean.TRUE

        assertEquals(Boolean.TRUE, instance["userSelected"])
        assertTrue(instance.has("userSelected"))
        assertEquals(listOf("code", "userSelected"), instance.elements.keys.toList())
        assertEquals(mapInstance("code" to "8480-6", "userSelected" to Boolean.TRUE), instance)
    }

    @Test
    fun iteratorRemovesSlots() {
        val instance = layoutInstance("http://loinc.org", "8480-6", "Systolic")

        val entries = instance.elements.entries.iterator()
        entries.next()
        entries.remove()
        entries.next().setValue("1234-5".toCqlString())

        assertEquals(listOf("code", "display"), instance.elements.keys.toList())
        assertEquals("1234-5".toCqlString(), instance["code"])
    }

    @Test
    fun layoutAndMapInstancesCompareEqual() {
        val layoutBacked = layoutInstance("http://loinc.org", "8480-6", null)
        val mapBacked = mapInstance("system" to "http://loinc.org", "code" to "8480-6")

        assertEquals(mapBacked, layoutBacked)
        assertEquals(layoutBacked, mapBacked)
        assertEquals(mapBacked.hashCode(), layoutBacked.hashCode())
        assertEquals(layoutInstance("http://loinc.org", "8480-6", null), layoutBacked)

        assertNotEquals(layoutInstance("http://loinc.org", "8480-6", "Systolic"), layoutBacked)
        assertNotEquals(mapInstance("system" to "http://loinc.org"), layoutBacked)
    }

    @Test
    fun slotsMustMatchLayout() {
        assertFailsWith<IllegalArgumentException> { ClassInstance(type, layout, arrayOfNulls(2)) }
        assertFailsWith<IllegalArgumentException> { ClassLayout(listOf("code", "code")) }
    }

    private fun mapInstance(vararg elements: Pair<kotlin.String, Any>) =
        ClassInstance(
            type,
            elements
                .associate { (key, value) ->
                    key to if (value is kotlin.String) value.toCqlString() else value as Value
                }
                .toMutableMap(),
        )
}
