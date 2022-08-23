package org.cqframework.cql.cql2elm.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class VersionTest {

    @Test
    public void testVersionComparable() {
        Version versionComparable = new Version("0.0.1");
        Assert.assertTrue(versionComparable.isComparable());

        Version versionNonComparable = new Version("0.0a.0b1");
        Assert.assertFalse(versionNonComparable.isComparable());

        Version version = new Version("0.0&.1");
        Assert.assertFalse(version.isComparable());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testVersionValidateCompareToThrows() {
        Version version = new Version("0.0.1");
        Version versionThat = new Version("0.0a.1");
        version.compareTo(versionThat);
    }

    @Test
    public void testVersionValidateCompareTo() {
        Version version = new Version("0.0.1");
        Version versionThat = new Version("0.0.1");
        Assert.assertTrue(version.compareTo(versionThat) == 0);

        Version versionThatLater = new Version("0.0.2");
        Assert.assertTrue(version.compareTo(versionThatLater) < 0);
        Assert.assertTrue(versionThatLater.compareTo(version) > 0);
    }

    @Test
    public void testVersionValidateCompatibility() {
        Version version = new Version("0.0.1");
        Version versionThat = new Version("0.0b.1");
        Version versionThatSame = new Version("0.0b.1");
        Assert.assertFalse(version.compatibleWith(versionThat));
        Assert.assertFalse(versionThat.compatibleWith(version));
        Assert.assertTrue(versionThat.compatibleWith(versionThatSame));
    }

    @Test
    public void testMATVersions() {
        Version version = new Version("v1-0-0-QDM-5-6");
        Assert.assertTrue(version.getMajorVersion() == 1);
        Assert.assertTrue(version.getMinorVersion() == 0);
        Assert.assertTrue(version.getPatchVersion() == 0);
        Assert.assertTrue(version.getBuildVersion().equals("QDM-5-6"));
    }

    @Test
    public void testMATVersionsCompatible() {
        Version version = new Version("7.0.0");
        Version matVersion = new Version("v7-0-0-QDM-5-6");
        Assert.assertTrue(matVersion.compatibleWith(version));
    }
}
