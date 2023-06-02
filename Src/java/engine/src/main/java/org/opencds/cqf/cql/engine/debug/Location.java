package org.opencds.cqf.cql.engine.debug;

/*
Identifies a location in a source file
 */
public class Location {
    private final int startLine;
    private final int startChar;
    private final int endLine;
    private final int endChar;

    public Location(int startLine, int startChar, int endLine, int endChar) {
        this.startLine = startLine;
        this.startChar = startChar;
        this.endLine = endLine;
        this.endChar = endChar;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartChar() {
        return startChar;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndChar() {
        return endChar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location other = (Location) o;

        if (endChar != other.endChar) {
            return false;
        }
        if (endLine != other.endLine) {
            return false;
        }
        if (startChar != other.startChar) {
            return false;
        }
        if (startLine != other.startLine) {
            return false;
        }

        return true;
    }

    /*
    Returns true if this location includes the other location (i.e. starts on or before and ends on or after)
     */
    public boolean includes(Location other) {
        if (other == null) {
            throw new IllegalArgumentException("other required");
        }

        if (this.startLine > other.startLine) {
            return false;
        }

        if (this.startLine == other.startLine && this.startChar > other.startChar) {
            return false;
        }

        if (this.endLine < other.endLine) {
            return false;
        }

        if (this.endLine == other.endLine && this.endChar < other.endChar) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 31 * result + startLine;
        result = 31 * result + startChar;
        result = 31 * result + endLine;
        result = 31 * result + endChar;
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                " startLine=" + startLine +
                ", startChar=" + startChar +
                ", endLine=" + endLine +
                ", endChar=" + endChar +
                '}';
    }

    public String toLocator() {
        return
                startLine == endLine && startChar == endChar
                        ? String.format("%s:%s", startLine, startChar)
                        : String.format("%s:%s-%s:%s", startLine, startChar, endLine, endChar);
    }

    public static Location fromLocator(String locator) {
        if (locator == null || locator.trim().isEmpty()) {
            throw new IllegalArgumentException("locator required");
        }

        int startLine = 0;
        int startChar = 0;
        int endLine = 0;
        int endChar = 0;
        String[] locations = locator.split("-");
        for (int i = 0; i < locations.length; i++) {
            String[] ranges = locations[i].split(":");
            if (ranges.length != 2) {
                throw new IllegalArgumentException(String.format("Invalid locator format: %s", locator));
            }
            if (i == 0) {
                startLine = Integer.parseInt(ranges[0]);
                startChar = Integer.parseInt(ranges[1]);
            }
            else {
                endLine = Integer.parseInt(ranges[0]);
                endChar = Integer.parseInt(ranges[1]);
            }
        }

        if (locations.length == 1) {
            endLine = startLine;
            endChar = startChar;
        }

        return new Location(startLine, startChar, endLine, endChar);
    }
}
