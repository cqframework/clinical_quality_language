package org.hl7.cql.model;

public class NamespaceInfo {
    public NamespaceInfo(String name, String uri) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("Uri is required");
        }

        this.name = name;
        this.uri = uri;
    }

    private String name;
    public String getName() {
        return name;
    }

    private String uri;
    public String getUri() {
        return uri;
    }

    @Override
    public int hashCode() {
        return 17 * name.hashCode() ^ 39 * uri.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof NamespaceInfo) {
            NamespaceInfo thatInfo = (NamespaceInfo)that;
            return this.name.equals(thatInfo.getName()) && this.uri.equals(thatInfo.getUri());
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, uri);
    }
}
