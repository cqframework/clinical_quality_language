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
    fun elementsHaveAKeyPerSlotInLayoutOrder() {
        val instance = layoutInstance("http://loinc.org", null, "Systolic")

        assertEquals(listOf("system", "code", "display"), instance.elements.keys.toList())
        assertEquals(3, instance.elements.size)
        assertTrue(instance.elements.containsKey("code"))
        assertNull(instance.elements["code"])
        assertEquals("Systolic".toCqlString(), instance["display"])
        assertNull(instance["code"])
        assertNull(instance["unknown"])
        assertTrue(instance.has("system"))
        assertTrue(instance.has("code"))
        assertFalse(instance.has("unknown"))
    }

    @Test
    fun layoutAndMapInstancesCompareEqual() {
        val layoutBacked = layoutInstance("http://loinc.org", "8480-6", null)
        val mapBacked =
            ClassInstance(
                type,
                mapOf(
                    "system" to "http://loinc.org".toCqlString(),
                    "code" to "8480-6".toCqlString(),
                    "display" to null,
                ),
            )

        assertEquals(mapBacked, layoutBacked)
        assertEquals(layoutBacked, mapBacked)
        assertEquals(mapBacked.hashCode(), layoutBacked.hashCode())
        assertEquals(layoutInstance("http://loinc.org", "8480-6", null), layoutBacked)

        assertNotEquals(layoutInstance("http://loinc.org", "8480-6", "Systolic"), layoutBacked)
        assertNotEquals(mapInstance("system" to "http://loinc.org"), layoutBacked)
        // A key that maps to null is not the same as no key
        assertNotEquals(
            mapInstance("system" to "http://loinc.org", "code" to "8480-6"),
            layoutBacked,
        )
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
                .toMap(),
        )
}
