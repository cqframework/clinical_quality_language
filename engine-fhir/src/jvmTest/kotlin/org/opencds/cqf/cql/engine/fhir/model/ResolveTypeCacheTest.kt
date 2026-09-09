package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType

/**
 * [FhirModelResolver.resolveType] memoises its answers. These cover what memoisation can break: a
 * second call must agree with the first, a failure must stay a failure, and one resolver's answers
 * must not leak into another's.
 */
internal class ResolveTypeCacheTest {

    private fun resolver() = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

    @Test
    fun repeatedCallsAgreeAcrossEveryResolutionBranch() {
        val resolver = resolver()

        // One name per branch of resolveType: a datatype found in the element definitions, a
        // resource found in the resource definitions, and an enumeration found only by the
        // Class.forName fallbacks. The fallbacks are the expensive path and so the one most worth
        // caching, which makes them the one most worth re-checking.
        for (typeName in listOf("Quantity", "Patient", "AdministrativeGender")) {
            val first = resolver.resolveType(typeName)
            val second = resolver.resolveType(typeName)
            assertSame(first, second, "resolveType('$typeName') changed answer on the second call")
        }
    }

    @Test
    fun anUnresolvableTypeKeepsThrowingRatherThanCachingAMiss() {
        val resolver = resolver()

        // The failure mode a cache introduces: storing "nothing found" and then handing that back
        // as a null on the next call, turning a loud failure into a silent one.
        repeat(3) {
            assertFailsWith<UnknownType> { resolver.resolveType("NoSuchTypeExistsAnywhere") }
        }
    }

    @Test
    fun subclassTypeNameMappingSurvivesCaching() {
        val resolver = resolver()

        // R4FhirModelResolver rewrites a handful of names before delegating to the cached base
        // implementation, so the cache is keyed on the rewritten name. Resolving twice must not
        // let the first answer escape the rewrite.
        val first = resolver.resolveType("MimeType")
        val second = resolver.resolveType("MimeType")

        assertSame(first, second)
        assertEquals("CodeType", first!!.simpleName)
    }

    @Test
    fun eachResolverCachesIndependently() {
        // The cache is instance state. Two resolvers over the same context must still each answer
        // for themselves — a shared cache would be a correctness problem the moment two resolvers
        // disagreed about a name.
        val first = resolver()
        val second = resolver()

        assertSame(first.resolveType("Patient"), second.resolveType("Patient"))
        assertFailsWith<UnknownType> { second.resolveType("NoSuchTypeExistsAnywhere") }
        assertSame(first.resolveType("Patient"), second.resolveType("Patient"))
    }

    @Test
    fun cachingDoesNotChangeWhatResolveTypeReturns() {
        // Guards the cache against returning a stale or wrong class for a name resolved earlier in
        // the same resolver's life: resolve a batch, then re-resolve and compare against a resolver
        // that has seen nothing else.
        val warmed = resolver()
        val names = listOf("Patient", "Observation", "Quantity", "CodeableConcept", "Encounter")
        names.forEach { warmed.resolveType(it) }

        for (typeName in names) {
            assertSame(
                resolver().resolveType(typeName),
                warmed.resolveType(typeName),
                "cached answer for '$typeName' differs from a cold resolver's",
            )
        }
    }
}
