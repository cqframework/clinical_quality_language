package org.cqframework.cql.cql2elm.model;

import java.util.regex.Pattern;

/**
 * Created by Bryn on 3/2/2017.
 */

/**
 * Implements a comparable version for use in comparing CQL artifact versions.
 * Supports versions specified in filename strings according to the following pattern:
 *     [v]{{major}}(.|-){{minor}}(.|-){{patch}}(.|-){{build}}
 * where major, minor, and patch are all required to be unsigned integers, and build is any string
 *
 * Examples:
 *     1.0.0 -&gt; major: 1, minor: 0, patch: 0
 *     v1-0-0 -&gt; major: 1, minor: 0, patch: 0
 *     v1-0-0-SNAPSHOT -&gt; major: 1, minor: 0, patch: 0, build: snapshot
 *
 * NOTE: Deliberately not using Apache ComparableVersion to a) avoid dependencies on Maven and b) allow for more
 * flexible version strings used by MAT file naming conventions.
 */
public class Version implements Comparable<Version> {

    private String version;

    private Integer majorVersion;
    private Integer minorVersion;
    private Integer patchVersion;
    private String buildVersion;

    private static Pattern isUnsignedInteger = Pattern.compile("[0-9]+");

    private void setVersion(String version) {
        this.version = version;
        String[] parts = this.version.split("\\.|-");
        for (int i = 0; i < Math.max(parts.length, 4); i++) {
            String part = i < parts.length ? parts[i] : "";
            if (part.startsWith("v")) {
                part = part.substring(1);
            }
            switch (i) {
                case 0:
                    if (isUnsignedInteger.matcher(part).matches()) {
                        majorVersion = Integer.parseInt(part);
                    }
                break;
                case 1:
                    if (isUnsignedInteger.matcher(part).matches()) {
                        minorVersion = Integer.parseInt(part);
                    }
                    else {
                        return;
                    }
                break;
                case 2:
                    if (isUnsignedInteger.matcher(part).matches()) {
                        patchVersion = Integer.parseInt(part);
                    }
                    else {
                        return;
                    }
                break;
                case 3:
                    buildVersion = part;
                break;
                default:
                    buildVersion += "-" + part;
                break;
            }
        }
    }

    public Version(String version) {
        if (version == null)
            throw new IllegalArgumentException("Version required");
        setVersion(version);
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public Integer getPatchVersion() {
        return patchVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    private int compareTo(Version that, int level) {
        if (that == null)
            return 1;
        validateComparability(that);

        for (int i = 0; i < Math.max(level, 4); i++) {
            switch (i) {
                case 0: {
                    int result = Integer.compare(this.majorVersion, that.majorVersion);
                    if (result != 0) {
                        return result;
                    }
                }
                break;

                case 1: {
                    if (this.minorVersion == null && that.minorVersion == null) {
                        return 0;
                    }
                    if (this.minorVersion == null) {
                        return -1;
                    }
                    if (that.minorVersion == null) {
                        return 1;
                    }
                    int result = Integer.compare(this.minorVersion, that.minorVersion);
                    if (result != 0) {
                        return result;
                    }
                }
                break;

                case 2: {
                    if (this.patchVersion == null && that.patchVersion == null) {
                        return 0;
                    }
                    if (this.patchVersion == null) {
                        return -1;
                    }
                    if (that.patchVersion == null) {
                        return 1;
                    }
                    int result = Integer.compare(this.patchVersion, that.patchVersion);
                    if (result != 0) {
                        return result;
                    }
                }
                break;

                case 3: {
                    if (this.buildVersion == null && that.buildVersion == null) {
                        return 0;
                    }
                    if (this.buildVersion == null) {
                        return -1;
                    }
                    if (that.buildVersion == null) {
                        return 1;
                    }
                    return this.buildVersion.compareToIgnoreCase(that.buildVersion);
                }
            }

        }
        return 0;
    }

    @Override
    public int compareTo(Version that) {
        return compareTo(that, 4);
    }

    public boolean compatibleWith(Version that) {
        if (that == null)
            return false;

        if (!isComparable() || !that.isComparable()) {
            return matchStrictly(that);
        }

        return this.majorVersion == that.majorVersion && compareTo(that, 2) >= 0;
    }

    public boolean matchStrictly(Version that) {
        if (that != null) {
            return this.version.equals(that.version);
        }
        return false;
    }

    public boolean isComparable(int level) {
        switch (level) {
            case 0: {
                return majorVersion != null;
            }
            case 1: {
                return minorVersion != null;
            }
            case 2: {
                return patchVersion != null;
            }
            case 3: {
                return buildVersion != null;
            }
        }
        return false;
    }

    public boolean isComparable() {
        return this.isComparable(2);
    }

    private void validateComparability(Version that) {
        if (!this.isComparable() || (that != null && !that.isComparable())) {
            throw new IllegalArgumentException("The versions are not comparable");
        }
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        return this.compareTo((Version)that) == 0;
    }

    @Override
    public int hashCode() {
        return this.version.hashCode();
    }

    @Override
    public String toString() {
        return this.version;
    }
}