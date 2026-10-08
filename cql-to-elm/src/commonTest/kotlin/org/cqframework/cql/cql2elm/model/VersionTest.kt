package org.cqframework.cql.cql2elm.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionTest {
    @Test
    fun versionComparable() {
        val versionComparable = Version("0.0.1")
        assertTrue(versionComparable.isComparable)

        val versionNonComparable = Version("0.0a.0b1")
        assertFalse(versionNonComparable.isComparable)

        val version = Version("0.0&.1")
        assertFalse(version.isComparable)
    }

    @Test
    fun versionValidateCompareToThrows() {
        val version = Version("0.0.1")
        val versionThat = Version("0.0a.1")
        assertFailsWith<IllegalArgumentException> { version.compareTo(versionThat) }
    }

    @Test
    fun versionValidateCompareTo() {
        val version = Version("0.0.1")
        val versionThat = Version("0.0.1")
        assertEquals(0, version.compareTo(versionThat))

        val versionThatLater = Version("0.0.2")
        assertTrue(version < versionThatLater)
        assertTrue(versionThatLater > version)
    }

    @Test
    fun versionValidateCompatibility() {
        val version = Version("0.0.1")
        val versionThat = Version("0.0b.1")
        val versionThatSame = Version("0.0b.1")
        assertFalse(version.compatibleWith(versionThat))
        assertFalse(versionThat.compatibleWith(version))
        assertTrue(versionThat.compatibleWith(versionThatSame))
    }

    @Test
    fun matVersions() {
        val version = Version("v1-0-0-QDM-5-6")
        assertEquals(1, version.majorVersion)
        assertEquals(0, version.minorVersion)
        assertEquals(0, version.patchVersion)
        assertEquals("QDM-5-6", version.buildVersion)
    }

    @Test
    fun matVersionsCompatible() {
        val version = Version("7.0.0")
        val matVersion = Version("v7-0-0-QDM-5-6")
        assertTrue(matVersion.compatibleWith(version))
    }
}
