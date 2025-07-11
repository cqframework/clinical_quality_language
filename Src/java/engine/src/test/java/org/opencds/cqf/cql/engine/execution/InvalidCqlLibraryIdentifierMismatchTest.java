package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.cqframework.cql.cql2elm.CqlIncludeException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class InvalidCqlLibraryIdentifierMismatchTest extends CqlTestBase {

    private static Stream<Arguments> evaluateWithMatchedLibraryIdsParams() {
        return Stream.of(Arguments.of(
                new VersionedIdentifier().withId("ValidCqlLibraryIdentifierMatches"),
                new VersionedIdentifier().withId("ValidCqlLibraryIdentifierMatchesWithVersion"),
                new VersionedIdentifier()
                        .withId("ValidCqlLibraryIdentifierMatchesWithVersion")
                        .withVersion("1.0.0")));
    }

    private static Stream<Arguments> evaluateWithMismatchedLibraryIdsParams() {
        return Stream.of(
                Arguments.of(
                        new VersionedIdentifier().withId("InvalidCqlLibraryIdentifierMismatch"),
                        "Library InvalidCqlLibraryIdentifierMismatch was included with version null, but id: Mismatched and version null of the library was found."),
                Arguments.of(
                        new VersionedIdentifier()
                                .withId("InvalidCqlLibraryIdentifierMismatch")
                                .withVersion("1.0.0"),
                        "Library InvalidCqlLibraryIdentifierMismatch was included with version 1.0.0, but id: Mismatched and version null of the library was found."),
                Arguments.of(
                        new VersionedIdentifier().withId("InvalidCqlLibraryIdentifierMismatchWithVersion"),
                        "Library InvalidCqlLibraryIdentifierMismatchWithVersion was included with version null, but id: MismatchedWithVersion and version 1.0.0 of the library was found."),
                Arguments.of(
                        // If we match only on version, we should still fail
                        new VersionedIdentifier()
                                .withId("InvalidCqlLibraryIdentifierMismatchWithVersion")
                                .withVersion("1.0.0"),
                        "Library InvalidCqlLibraryIdentifierMismatchWithVersion was included with version 1.0.0, but id: MismatchedWithVersion and version 1.0.0 of the library was found."));
    }

    @ParameterizedTest
    @MethodSource("evaluateWithMatchedLibraryIdsParams")
    void evaluateWithMismatchedLibraryIds(VersionedIdentifier libraryIdentifierToSearch) {
        // We're simply asserting that we do not fail
        assertNotNull(engine.evaluate(libraryIdentifierToSearch));
    }

    @ParameterizedTest
    @MethodSource("evaluateWithMismatchedLibraryIdsParams")
    void evaluateWithMismatchedLibraryIds(
            VersionedIdentifier libraryIdentifierToSearch, String expectedExceptionMessage) {
        var exception = assertThrows(CqlIncludeException.class, () -> engine.evaluate(libraryIdentifierToSearch));

        assertEquals(expectedExceptionMessage, exception.getMessage());
    }
}
