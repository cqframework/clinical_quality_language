package org.opencds.cqf.cql.engine.fhir.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.opencds.cqf.cql.engine.elm.executing.PropertyEvaluator
import org.opencds.cqf.cql.engine.runtime.ClassInstance

/**
 * Covers opt-in lazy composite conversion in [FhirModelResolver.toCqlValue] (the `lazyComposites`
 * flag, backed by [LazyFhirElementMap]). The central guarantee is that a lazy-backed instance is
 * indistinguishable from the eager (dense-map) one it replaces — same key set, equality, hash code,
 * and per-field reads — while converting only the children that are actually read.
 */
class LazyCompositeConversionTest {

    private val resolver = CachedR4FhirModelResolver()

    private fun patient(): Patient =
        Patient().apply {
            gender = Enumerations.AdministrativeGender.MALE
            active = true
            birthDateElement = DateType("1980-01-01")
            addName(HumanName().setFamily("Smith").addGiven("John"))
        }

    @Test
    fun lazyInstanceMatchesEagerInstance() {
        val subject = patient()
        val eager = assertIs<ClassInstance>(resolver.toCqlValue(subject, false, false))
        val lazy = assertIs<ClassInstance>(resolver.toCqlValue(subject, false, true))

        // Key-set identity: every declared child is a present key in both, absent ones included.
        assertEquals(eager.elements.keys, lazy.elements.keys)
        assertEquals(eager.elements.size, lazy.elements.size)

        // The point of extending AbstractMap: equality and hashCode match the eager map (recursing
        // through the nested `name` list), so set membership / distinct / `=` / `~` are identical.
        assertEquals(eager, lazy)
        assertEquals(eager.hashCode(), lazy.hashCode())

        // Per-field reads agree for scalar, date, and nested list-of-composite leaf paths.
        for (path in
            listOf("gender", "active", "birthDate", "name[0].family", "name[0].given[0]")) {
            val fromEager = PropertyEvaluator.resolvePath(eager, path)
            assertNotNull(fromEager, "expected a value for '$path'")
            assertEquals(fromEager, PropertyEvaluator.resolvePath(lazy, path))
        }

        // A declared-but-absent child is a present key whose value is null, matching eager.
        assertTrue(lazy.elements.containsKey("maritalStatus"))
        assertNull(lazy.elements["maritalStatus"])
        assertNull(PropertyEvaluator.resolvePath(lazy, "maritalStatus"))
    }

    @Test
    fun onlyReadChildrenAreConverted() {
        val lazy = assertIs<ClassInstance>(resolver.toCqlValue(patient(), false, true))
        val map = assertIs<LazyFhirElementMap>(lazy.elements)

        assertEquals(0, map.materializedChildCount())

        // Reading a single top-level child converts exactly that child.
        assertNotNull(PropertyEvaluator.resolvePath(lazy, "gender"))
        assertEquals(1, map.materializedChildCount())

        // A whole-instance operation (hashCode, as `=` / `~` / set ops also would) materializes
        // all.
        lazy.hashCode()
        assertEquals(map.size, map.materializedChildCount())
    }
}
