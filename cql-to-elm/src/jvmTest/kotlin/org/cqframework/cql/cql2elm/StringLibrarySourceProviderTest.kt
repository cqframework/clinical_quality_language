package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.readString
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class StringLibrarySourceProviderTest {
    @Test
    @Throws(IOException::class)
    fun ambiguous_id_throws_error() {
        val list = listOf(QUOTED, NOT_QUOTED)

        val provider = StringLibrarySourceProvider(list)
        val id = VersionedIdentifier().withId("Test")

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            provider.getLibrarySource(id)
        }
    }

    @Test
    @Throws(IOException::class)
    fun ambiguous_id_without_version_throws_error() {
        val list = listOf(QUOTED_VERSION_1, NOT_QUOTED_VERSION_1)

        val provider = StringLibrarySourceProvider(list)

        val id = VersionedIdentifier().withId("Test")

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            provider.getLibrarySource(id)
        }
    }

    @Test
    @Throws(IOException::class)
    fun ambiguous_id_with_version_throws_error() {
        val list = listOf(QUOTED_VERSION_1, NOT_QUOTED_VERSION_1)

        val provider = StringLibrarySourceProvider(list)
        val id = VersionedIdentifier().withId("Test").withVersion("1.0.0")

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            provider.getLibrarySource(id)
        }
    }

    @Test
    @Throws(IOException::class)
    fun quoted_match_returns_library() {
        val list = listOf(QUOTED)

        val provider = StringLibrarySourceProvider(list)

        val result = provider.getLibrarySource(VersionedIdentifier().withId("Test"))

        Assertions.assertNotNull(result)
        Assertions.assertEquals(QUOTED, result?.readString())
    }

    @Test
    @Throws(IOException::class)
    fun unquoted_match_returns_library() {
        val list = listOf(NOT_QUOTED)

        val provider = StringLibrarySourceProvider(list)

        val result = provider.getLibrarySource(VersionedIdentifier().withId("Test"))

        Assertions.assertNotNull(result)
        Assertions.assertEquals(NOT_QUOTED, result?.readString())
    }

    @Test
    @Throws(IOException::class)
    fun quoted_versioned_match_returns_library() {
        val list = listOf(QUOTED_VERSION_1)

        val provider = StringLibrarySourceProvider(list)

        val result =
            provider.getLibrarySource(VersionedIdentifier().withId("Test").withVersion("1.0.0"))

        Assertions.assertNotNull(result)
        Assertions.assertEquals(QUOTED_VERSION_1, result?.readString())
    }

    @Test
    @Throws(IOException::class)
    fun unquoted_versioned_match_returns_library() {
        val list = listOf(NOT_QUOTED_VERSION_1, NOT_QUOTED_VERSION_2)

        val provider = StringLibrarySourceProvider(list)

        val result =
            provider.getLibrarySource(VersionedIdentifier().withId("Test").withVersion("2.0.0"))

        Assertions.assertNotNull(result)
        Assertions.assertEquals(NOT_QUOTED_VERSION_2, result?.readString())
    }

    @Test
    @Throws(IOException::class)
    fun garbage_returns_nothing() {
        val list = listOf(GARBAGE)

        val provider = StringLibrarySourceProvider(list)

        val result = provider.getLibrarySource(VersionedIdentifier().withId("Test"))

        Assertions.assertNull(result)
    }

    companion object {
        // CQL allows for Library identifiers to be quoted or unquoted, so we test both scenarios
        private const val QUOTED = "library \"Test\"\n define \"Value\": 2 + 2"
        private const val NOT_QUOTED = "library Test\n define \"Value\": 2 + 2"
        private const val QUOTED_VERSION_1 =
            "library \"Test\" version '1.0.0'\n define \"Value\": 2 + 2"
        private const val NOT_QUOTED_VERSION_1 =
            "library Test version '1.0.0'\n define \"Value\": 2 + 2"
        private const val NOT_QUOTED_VERSION_2 =
            "library Test version '2.0.0'\n define \"Value\": 2 + 2"
        private const val GARBAGE = "NotALibrary"
    }
}
