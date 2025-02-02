package org.cqframework.cql.cql2elm;

import static kotlinx.io.SourcesJvmKt.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import kotlin.text.Charsets;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;

class StringLibrarySourceProviderTest {

    // CQL allows for Library identifiers to be quoted or unquoted, so we test both scenarios
    private static final String QUOTED = "library \"Test\"\n define \"Value\": 2 + 2";
    private static final String NOT_QUOTED = "library Test\n define \"Value\": 2 + 2";
    private static final String QUOTED_VERSION_1 = "library \"Test\" version '1.0.0'\n define \"Value\": 2 + 2";
    private static final String NOT_QUOTED_VERSION_1 = "library Test version '1.0.0'\n define \"Value\": 2 + 2";
    private static final String NOT_QUOTED_VERSION_2 = "library Test version '2.0.0'\n define \"Value\": 2 + 2";
    private static final String GARBAGE = "NotALibrary";

    @Test
    void ambiguous_id_throws_error() throws IOException {
        var list = List.of(QUOTED, NOT_QUOTED);

        var provider = new StringLibrarySourceProvider(list);
        var id = new VersionedIdentifier().withId("Test");

        assertThrows(IllegalArgumentException.class, () -> provider.getLibrarySource(id));
        ;
    }

    @Test
    void ambiguous_id_without_version_throws_error() throws IOException {
        var list = List.of(QUOTED_VERSION_1, NOT_QUOTED_VERSION_1);

        var provider = new StringLibrarySourceProvider(list);

        var id = new VersionedIdentifier().withId("Test");

        assertThrows(IllegalArgumentException.class, () -> {
            provider.getLibrarySource(id);
        });
    }

    @Test
    void ambiguous_id_with_version_throws_error() throws IOException {
        var list = List.of(QUOTED_VERSION_1, NOT_QUOTED_VERSION_1);

        var provider = new StringLibrarySourceProvider(list);
        var id = new VersionedIdentifier().withId("Test").withVersion("1.0.0");

        assertThrows(IllegalArgumentException.class, () -> {
            provider.getLibrarySource(id);
        });
    }

    @Test
    void quoted_match_returns_library() throws IOException {
        var list = List.of(QUOTED);

        var provider = new StringLibrarySourceProvider(list);

        var result = provider.getLibrarySource(new VersionedIdentifier().withId("Test"));

        assertNotNull(result);
        assertEquals(QUOTED, readString(result, Charsets.UTF_8));
    }

    @Test
    void unquoted_match_returns_library() throws IOException {
        var list = List.of(NOT_QUOTED);

        var provider = new StringLibrarySourceProvider(list);

        var result = provider.getLibrarySource(new VersionedIdentifier().withId("Test"));

        assertNotNull(result);
        assertEquals(NOT_QUOTED, readString(result, Charsets.UTF_8));
    }

    @Test
    void quoted_versioned_match_returns_library() throws IOException {
        var list = List.of(QUOTED_VERSION_1);

        var provider = new StringLibrarySourceProvider(list);

        var result = provider.getLibrarySource(
                new VersionedIdentifier().withId("Test").withVersion("1.0.0"));

        assertNotNull(result);
        assertEquals(QUOTED_VERSION_1, readString(result, Charsets.UTF_8));
    }

    @Test
    void unquoted_versioned_match_returns_library() throws IOException {
        var list = List.of(NOT_QUOTED_VERSION_1, NOT_QUOTED_VERSION_2);

        var provider = new StringLibrarySourceProvider(list);

        var result = provider.getLibrarySource(
                new VersionedIdentifier().withId("Test").withVersion("2.0.0"));

        assertNotNull(result);
        assertEquals(NOT_QUOTED_VERSION_2, readString(result, Charsets.UTF_8));
    }

    @Test
    void garbage_returns_nothing() throws IOException {
        var list = List.of(GARBAGE);

        var provider = new StringLibrarySourceProvider(list);

        var result = provider.getLibrarySource(new VersionedIdentifier().withId("Test"));

        assertNull(result);
    }
}
