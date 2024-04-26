package org.cqframework.cql.cql2elm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VersionTest {

    @Test
    void versionComparable() {
        Version versionComparable = new Version("0.0.1");
        assertTrue(versionComparable.isComparable());

        Version versionNonComparable = new Version("0.0a.0b1");
        assertFalse(versionNonComparable.isComparable());

        Version version = new Version("0.0&.1");
        assertFalse(version.isComparable());
    }

    @Test
    void versionValidateCompareToThrows() {
        Version version = new Version("0.0.1");
        Version versionThat = new Version("0.0a.1");
        assertThrows(IllegalArgumentException.class, () -> {
            version.compareTo(versionThat);
        });
    }

    @Test
    void versionValidateCompareTo() {
        Version version = new Version("0.0.1");
        Version versionThat = new Version("0.0.1");
        assertEquals(0, version.compareTo(versionThat));

        Version versionThatLater = new Version("0.0.2");
        assertTrue(version.compareTo(versionThatLater) < 0);
        assertTrue(versionThatLater.compareTo(version) > 0);
    }

    @Test
    void versionValidateCompatibility() {
        Version version = new Version("0.0.1");
        Version versionThat = new Version("0.0b.1");
        Version versionThatSame = new Version("0.0b.1");
        assertFalse(version.compatibleWith(versionThat));
        assertFalse(versionThat.compatibleWith(version));
        assertTrue(versionThat.compatibleWith(versionThatSame));
    }

    @Test
    void matVersions() {
        Version version = new Version("v1-0-0-QDM-5-6");
        assertTrue(version.getMajorVersion() == 1);
        assertTrue(version.getMinorVersion() == 0);
        assertTrue(version.getPatchVersion() == 0);
        assertEquals("QDM-5-6", version.getBuildVersion());
    }

    @Test
    void matVersionsCompatible() {
        Version version = new Version("7.0.0");
        Version matVersion = new Version("v7-0-0-QDM-5-6");
        assertTrue(matVersion.compatibleWith(version));
    }
}
