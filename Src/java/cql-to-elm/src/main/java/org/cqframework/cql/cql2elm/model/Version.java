package org.cqframework.cql.cql2elm.model;

/**
 * Created by Bryn on 3/2/2017.
 */

// NOTE: Would like to use Apache Maven ComparableVersion, but don't want the dependency of the entire Maven 3.3.1 API...
public class Version implements Comparable<Version> {

    private String version;

    public Version(String version) {
        if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        if(!version.matches("[0-9A-Za-z]+(\\.[0-9A-Za-z]+)*"))
            throw new IllegalArgumentException("Invalid version format");
        this.version = version;
    }

    @Override
    public int compareTo(Version that) {
        if(that == null)
            return 1;
        String[] thisParts = this.version.split("\\.");
        String[] thatParts = that.version.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    public boolean compatibleWith(Version that) {
        if (that == null)
            return false;

        // this version is compatible with that version if:
            // this.major = that.major
            // this.minor >= that.minor
        String[] thisParts = this.version.split("\\.");
        String[] thatParts = that.version.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            switch (i) {
                case 0: {
                    // Major version
                    if (thisPart != thatPart)
                        return false;
                }
                break;

                case 1: {
                    if (thisPart < thatPart)
                        return false;
                }
                break;

                case 2: {
                    // ignore patch version....
                }
                break;
            }
        }

        return true;
    }

    public boolean isComparable(Version thatVersion) {
        return this.isComparable() && thatVersion.isComparable();
    }

    public boolean isComparable() {
        return this.version.matches("(\\.[0-9])+");
    }

    @Override
    public boolean equals(Object that) {
        if(this == that)
            return true;
        if(that == null)
            return false;
        if(this.getClass() != that.getClass())
            return false;
        return this.compareTo((Version) that) == 0;
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