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
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testVersionThrows() {
        Version version = new Version("0.0&.1");
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
}
