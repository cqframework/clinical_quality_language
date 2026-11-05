package org.opencds.cqf.cql.engine.util

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class IdentitySetTest {

    /**
     * Two instances that are `==` but not `===`. [ClassInstance] compares structurally, so these
     * are exactly what a [HashSet] would collapse and an [IdentitySet] must not — and exactly what
     * the FHIR retrieve path produces, since it builds a fresh value per retrieve.
     */
    private fun classInstance(id: String): ClassInstance =
        ClassInstance(
            QName("http://example.org", "ExampleClass", "ExampleModel"),
            mutableMapOf("id" to id.toCqlString()),
        )

    @Test
    fun retainsEqualButDistinctInstances() {
        val first = classInstance("Encounter1")
        val second = classInstance("Encounter1")

        // Precondition: the two really are equal-but-distinct, or this test proves nothing.
        assertEquals(first, second, "fixture should be structurally equal")
        assertFalse(first === second, "fixture should be two distinct instances")

        val set = IdentitySet<Value?>()
        assertTrue(set.add(first))
        assertTrue(set.add(second))

        assertEquals(2, set.size)
    }

    @Test
    fun collapsesRepeatedAddsOfTheSameInstance() {
        val only = classInstance("Encounter1")

        val set = IdentitySet<Value?>()
        assertTrue(set.add(only))
        assertFalse(set.add(only), "adding the same reference twice should not grow the set")

        assertEquals(1, set.size)
    }

    @Test
    fun containsAnswersByReference() {
        val present = classInstance("Encounter1")
        val equalButAbsent = classInstance("Encounter1")

        val set = IdentitySet<Value?>()
        set.add(present)

        assertTrue(set.contains(present))
        assertFalse(
            set.contains(equalButAbsent),
            "contains should not match on structural equality",
        )
    }

    @Test
    fun removeAnswersByReference() {
        val present = classInstance("Encounter1")
        val equalButAbsent = classInstance("Encounter1")

        val set = IdentitySet<Value?>()
        set.add(present)

        assertFalse(
            set.remove(equalButAbsent),
            "an equal instance should not remove the stored one",
        )
        assertEquals(1, set.size)

        assertTrue(set.remove(present))
        assertEquals(0, set.size)
    }

    @Test
    fun iteratorRemovalDropsOnlyTheVisitedElement() {
        val first = classInstance("Encounter1")
        val second = classInstance("Encounter1")

        val set = IdentitySet<Value?>()
        set.add(first)
        set.add(second)

        val iterator = set.iterator()
        iterator.next()
        iterator.remove()

        assertEquals(1, set.size)
    }

    @Test
    fun iterationYieldsEveryStoredInstance() {
        val first = classInstance("Encounter1")
        val second = classInstance("Encounter1")

        val set = IdentitySet<Value?>()
        set.add(first)
        set.add(second)

        // Ordering is not part of the contract, so compare as a reference-identity multiset.
        val seen = set.toList()
        assertEquals(2, seen.size)
        assertTrue(seen.any { it === first })
        assertTrue(seen.any { it === second })
    }

    @Test
    fun clearEmptiesTheSet() {
        val set = IdentitySet<Value?>()
        set.add(classInstance("Encounter1"))
        set.add(classInstance("Encounter2"))

        set.clear()

        assertEquals(0, set.size)
        assertContentEquals(emptyList(), set.toList())
    }

    @Test
    fun holdsNull() {
        val set = IdentitySet<Value?>()
        assertTrue(set.add(null))
        assertFalse(set.add(null))

        assertEquals(1, set.size)
        assertTrue(set.contains(null))
    }
}
