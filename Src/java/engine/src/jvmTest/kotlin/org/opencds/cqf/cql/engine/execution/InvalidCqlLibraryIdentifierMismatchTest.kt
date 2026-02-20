package org.opencds.cqf.cql.engine.execution

import java.util.stream.Stream
import org.cqframework.cql.cql2elm.CqlIncludeException
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class InvalidCqlLibraryIdentifierMismatchTest : CqlTestBase() {
    @ParameterizedTest
    @MethodSource("evaluateWithMatchedLibraryIdsParams")
    fun evaluateWithMismatchedLibraryIds(libraryIdentifierToSearch: VersionedIdentifier) {
        // We're simply asserting that we do not fail
        Assertions.assertNotNull(
            engine.evaluate { library(libraryIdentifierToSearch) }.onlyResultOrThrow
        )
    }

    @ParameterizedTest
    @MethodSource("evaluateWithMismatchedLibraryIdsParams")
    fun evaluateWithMismatchedLibraryIds(
        libraryIdentifierToSearch: VersionedIdentifier,
        expectedExceptionMessage: String?,
    ) {
        val exception =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                engine.evaluate { library(libraryIdentifierToSearch) }.onlyResultOrThrow
            }

        assertEquals(expectedExceptionMessage, exception.message)
    }

    companion object {
        @JvmStatic
        private fun evaluateWithMatchedLibraryIdsParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    VersionedIdentifier().withId("ValidCqlLibraryIdentifierMatches"),
                    VersionedIdentifier().withId("ValidCqlLibraryIdentifierMatchesWithVersion"),
                    VersionedIdentifier()
                        .withId("ValidCqlLibraryIdentifierMatchesWithVersion")
                        .withVersion("1.0.0"),
                )
            )
        }

        @JvmStatic
        private fun evaluateWithMismatchedLibraryIdsParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    VersionedIdentifier().withId("InvalidCqlLibraryIdentifierMismatch"),
                    "Library InvalidCqlLibraryIdentifierMismatch was included with version null, but id: Mismatched and version null of the library was found.",
                ),
                Arguments.of(
                    VersionedIdentifier()
                        .withId("InvalidCqlLibraryIdentifierMismatch")
                        .withVersion("1.0.0"),
                    "Library InvalidCqlLibraryIdentifierMismatch was included with version 1.0.0, but id: Mismatched and version null of the library was found.",
                ),
                Arguments.of(
                    VersionedIdentifier().withId("InvalidCqlLibraryIdentifierMismatchWithVersion"),
                    "Library InvalidCqlLibraryIdentifierMismatchWithVersion was included with version null, but id: MismatchedWithVersion and version 1.0.0 of the library was found.",
                ),
                Arguments.of( // If we match only on version, we should still fail
                    VersionedIdentifier()
                        .withId("InvalidCqlLibraryIdentifierMismatchWithVersion")
                        .withVersion("1.0.0"),
                    "Library InvalidCqlLibraryIdentifierMismatchWithVersion was included with version 1.0.0, but id: MismatchedWithVersion and version 1.0.0 of the library was found.",
                ),
            )
        }
    }
}
