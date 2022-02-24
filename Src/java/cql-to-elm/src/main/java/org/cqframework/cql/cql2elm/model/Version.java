package org.cqframework.cql.cql2elm.model;

/**
 * Created by Bryn on 3/2/2017.
 */

// NOTE: Would like to use Apache Maven ComparableVersion, but don't want the dependency of the entire Maven 3.3.1 API...
public class Version implements Comparable<Version> {

    private String version;

    private boolean isComparable;

    public Version(String version) {
        if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        this.version = version;
        this.setIsComparable();
    }

    @Override
    public int compareTo(Version that) {
        if(that == null)
            return 1;
        validateComparability(that);

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

        if(!isComparable  || !that.isComparable) {
            return matchStrictly(that);
        }

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

    public boolean matchStrictly(Version that) {
        if (that != null) {
            return this.version.equals(that.version);
        }
        return false;
    }

    public boolean isComparable() {
        return this.isComparable;
    }

    private void setIsComparable() {
        this.isComparable = this.version.matches("[0-9]+(\\.[0-9]+)*");
    }

    private void validateComparability(Version that) {
        if(!this.isComparable || (that != null && !that.isComparable)) {
            throw new IllegalArgumentException("The versions are not comparable");
        }
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