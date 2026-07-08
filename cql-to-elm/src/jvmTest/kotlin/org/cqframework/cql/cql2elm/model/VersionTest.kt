package org.cqframework.cql.cql2elm.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class VersionTest {
    @Test
    fun versionComparable() {
        val versionComparable = Version("0.0.1")
        Assertions.assertTrue(versionComparable.isComparable)

        val versionNonComparable = Version("0.0a.0b1")
        Assertions.assertFalse(versionNonComparable.isComparable)

        val version = Version("0.0&.1")
        Assertions.assertFalse(version.isComparable)
    }

    @Test
    fun versionValidateCompareToThrows() {
        val version = Version("0.0.1")
        val versionThat = Version("0.0a.1")
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            version.compareTo(versionThat)
        }
    }

    @Test
    fun versionValidateCompareTo() {
        val version = Version("0.0.1")
        val versionThat = Version("0.0.1")
        Assertions.assertEquals(0, version.compareTo(versionThat))

        val versionThatLater = Version("0.0.2")
        Assertions.assertTrue(version < versionThatLater)
        Assertions.assertTrue(versionThatLater > version)
    }

    @Test
    fun versionValidateCompatibility() {
        val version = Version("0.0.1")
        val versionThat = Version("0.0b.1")
        val versionThatSame = Version("0.0b.1")
        Assertions.assertFalse(version.compatibleWith(versionThat))
        Assertions.assertFalse(versionThat.compatibleWith(version))
        Assertions.assertTrue(versionThat.compatibleWith(versionThatSame))
    }

    @Test
    fun matVersions() {
        val version = Version("v1-0-0-QDM-5-6")
        Assertions.assertEquals(1, version.majorVersion)
        Assertions.assertEquals(0, version.minorVersion)
        Assertions.assertEquals(0, version.patchVersion)
        Assertions.assertEquals("QDM-5-6", version.buildVersion)
    }

    @Test
    fun matVersionsCompatible() {
        val version = Version("7.0.0")
        val matVersion = Version("v7-0-0-QDM-5-6")
        Assertions.assertTrue(matVersion.compatibleWith(version))
    }
}
