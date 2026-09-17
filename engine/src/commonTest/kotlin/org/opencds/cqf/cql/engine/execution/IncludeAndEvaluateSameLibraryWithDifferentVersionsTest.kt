package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.io.Source
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.utils.asSource
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class IncludeAndEvaluateSameLibraryWithDifferentVersionsTest {

    /**
     * Expression x is defined in LibD version 1.0.0 and version 2.0.0 and evaluates to 1 and 2,
     * respectively.
     *
     * LibB re-exports expression x from LibD version 1.0.0.
     *
     * LibC re-exports expression x from LibD version 2.0.0.
     *
     * LibA accesses LibB.x and LibC.x.
     *
     * LibE directly includes both versions of LibD.
     */
    val librarySourceProvider =
        object : LibrarySourceProvider {
            @Suppress("LongMethod")
            override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
                return when (libraryIdentifier) {
                    VersionedIdentifier().apply { id = "LibA" } ->
                        """
                            library LibA
                            
                            include LibB
                            include LibC
                            
                            define xFromLibB: LibB.x
                            define xFromLibC: LibC.x
                        """
                            .trimIndent()
                            .asSource()
                    VersionedIdentifier().apply { id = "LibB" } ->
                        """
                            library LibB
                            
                            include LibD version '1.0.0'
                            
                            define x: LibD.x
                        """
                            .trimIndent()
                            .asSource()
                    VersionedIdentifier().apply { id = "LibC" } ->
                        """
                            library LibC
                            
                            include LibD version '2.0.0'
                            
                            define x: LibD.x
                        """
                            .trimIndent()
                            .asSource()
                    VersionedIdentifier().apply {
                        id = "LibD"
                        version = "1.0.0"
                    } ->
                        """
                            library LibD version '1.0.0'
                            
                            define x: 1
                        """
                            .trimIndent()
                            .asSource()
                    VersionedIdentifier().apply {
                        id = "LibD"
                        version = "2.0.0"
                    } ->
                        """
                            library LibD version '2.0.0'
                            
                            define x: 2
                        """
                            .trimIndent()
                            .asSource()
                    VersionedIdentifier().apply { id = "LibE" } ->
                        """
                            library LibE
                            
                            include LibD version '1.0.0' called LibDVersion1
                            include LibD version '2.0.0' called LibDVersion2
                            
                            define xFromLibDVersion1: LibDVersion1.x
                            define xFromLibDVersion2: LibDVersion2.x
                        """
                            .trimIndent()
                            .asSource()
                    else -> null
                }
            }
        }

    fun createEngine(): CqlEngine {
        val modelManager = ModelManager()
        modelManager.modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
        val libraryManager = LibraryManager(modelManager)
        libraryManager.librarySourceLoader.registerProvider(librarySourceProvider)
        val environment = Environment(libraryManager)
        return CqlEngine(environment)
    }

    @Test
    fun includeSameLibraryWithDifferentVersionsTransitivelyTest() {
        val resultsForLibA = createEngine().evaluate { library("LibA") }.onlyResultOrThrow

        assertEquals(1.toCqlInteger(), resultsForLibA["xFromLibB"]?.value)
        assertEquals(2.toCqlInteger(), resultsForLibA["xFromLibC"]?.value)
    }

    @Test
    fun includeSameLibraryWithDifferentVersionsDirectlyTest() {
        val resultsForLibE = createEngine().evaluate { library("LibE") }.onlyResultOrThrow

        assertEquals(1.toCqlInteger(), resultsForLibE["xFromLibDVersion1"]?.value)
        assertEquals(2.toCqlInteger(), resultsForLibE["xFromLibDVersion2"]?.value)
    }

    @Test
    fun evaluateSameLibraryWithDifferentVersionsConsecutivelyTest() {
        val engine = createEngine()
        val resultsForLibDVersion1 =
            engine
                .evaluate {
                    library(
                        VersionedIdentifier().apply {
                            id = "LibD"
                            version = "1.0.0"
                        }
                    )
                }
                .onlyResultOrThrow
        val resultsForLibDVersion2 =
            engine
                .evaluate {
                    library(
                        VersionedIdentifier().apply {
                            id = "LibD"
                            version = "2.0.0"
                        }
                    )
                }
                .onlyResultOrThrow

        assertEquals(1.toCqlInteger(), resultsForLibDVersion1["x"]?.value)
        assertEquals(2.toCqlInteger(), resultsForLibDVersion2["x"]?.value)
    }

    @Test
    fun evaluateSameLibraryWithDifferentVersionsSimultaneouslyTest() {
        val results =
            createEngine().evaluate {
                library(
                    VersionedIdentifier().apply {
                        id = "LibD"
                        version = "1.0.0"
                    }
                )
                library(
                    VersionedIdentifier().apply {
                        id = "LibD"
                        version = "2.0.0"
                    }
                )
            }

        assertEquals(
            1.toCqlInteger(),
            results.results[
                    VersionedIdentifier().apply {
                        id = "LibD"
                        version = "1.0.0"
                    }]
                ?.get("x")
                ?.value,
        )
        assertEquals(
            2.toCqlInteger(),
            results.results[
                    VersionedIdentifier().apply {
                        id = "LibD"
                        version = "2.0.0"
                    }]
                ?.get("x")
                ?.value,
        )
    }
}
