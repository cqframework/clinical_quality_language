package org.cqframework.cql.poc.translator.model.logger;

public class TrackBack {
    private final String library;
    private final String version;
    private final int startLine;
    private final int startChar;
    private final int endLine;
    private final int endChar;

    public TrackBack(String library, String version, int startLine, int startChar, int endLine, int endChar) {
        this.library = library;
        this.version = version;
        this.startLine = startLine;
        this.startChar = startChar;
        this.endLine = endLine;
        this.endChar = endChar;
    }

    public String getLibrary() {
        return library;
    }

    public String getVersion() {
        return version;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackBack trackBack = (TrackBack) o;

        if (endChar != trackBack.endChar) return false;
        if (endLine != trackBack.endLine) return false;
        if (startChar != trackBack.startChar) return false;
        if (startLine != trackBack.startLine) return false;
        if (!library.equals(trackBack.library)) return false;
        if (version != null ? !version.equals(trackBack.version) : trackBack.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = library.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + startLine;
        result = 31 * result + startChar;
        result = 31 * result + endLine;
        result = 31 * result + endChar;
        return result;
    }

    @Override
    public String toString() {
        return "TrackBack{" +
                "library='" + library + '\'' +
                ", version='" + version + '\'' +
                ", startLine=" + startLine +
                ", startChar=" + startChar +
                ", endLine=" + endLine +
                ", endChar=" + endChar +
                '}';
    }
}
